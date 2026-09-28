package com.ttloc.htkhcn.doandiaphuong;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ttloc.htkhcn.common.SpecUtils;
import com.ttloc.htkhcn.common.exception.BadRequestException;
import com.ttloc.htkhcn.common.exception.ResourceNotFoundException;
import com.ttloc.htkhcn.danhmuc.TuDien;
import com.ttloc.htkhcn.danhmuc.TuDienRepository;
import com.ttloc.htkhcn.doitac.DoiTac;
import com.ttloc.htkhcn.doitac.DoiTacRepository;
import com.ttloc.htkhcn.security.SecurityUser;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DoanDiaPhuongService {

    private final DoanDiaPhuongRepository doanDiaPhuongRepository;
    private final DoiTacRepository doiTacRepository;
    private final TuDienRepository tuDienRepository;
    private final EntityManager entityManager;

    @Transactional(readOnly = true)
    public Page<DoanDiaPhuongResponse> danhSach(Integer nam, UUID doiTacId, String tuKhoa, Pageable pageable) {
        Specification<DoanDiaPhuong> spec = SpecUtils.and(
                DoanDiaPhuongSpecifications.chuaBiXoa(),
                DoanDiaPhuongSpecifications.namBang(nam),
                DoanDiaPhuongSpecifications.doiTacBang(doiTacId),
                DoanDiaPhuongSpecifications.tuKhoaKhongDauTrong(tuKhoa));
        return doanDiaPhuongRepository.findAll(spec, pageable).map(DoanDiaPhuongResponse::from);
    }

    @Transactional(readOnly = true)
    public DoanDiaPhuongResponse layTheoId(UUID id) {
        return DoanDiaPhuongResponse.from(timHoacLoi(id));
    }

    @Transactional
    public DoanDiaPhuongResponse tao(DoanDiaPhuongRequest request) {
        validate(request);
        DoanDiaPhuong d = new DoanDiaPhuong();
        gan(d, request);
        DoanDiaPhuong saved = doanDiaPhuongRepository.save(d);
        entityManager.flush();
        entityManager.refresh(saved);
        return DoanDiaPhuongResponse.from(saved);
    }

    @Transactional
    public DoanDiaPhuongResponse sua(UUID id, DoanDiaPhuongRequest request) {
        validate(request);
        DoanDiaPhuong d = timHoacLoi(id);
        gan(d, request);
        DoanDiaPhuong saved = doanDiaPhuongRepository.save(d);
        entityManager.flush();
        entityManager.refresh(saved);
        return DoanDiaPhuongResponse.from(saved);
    }

    @Transactional
    public void xoa(UUID id) {
        DoanDiaPhuong d = timHoacLoi(id);
        d.setDeletedAt(Instant.now());
        d.setDeletedBy(currentUserId());
        doanDiaPhuongRepository.saveAndFlush(d);
    }

    private void validate(DoanDiaPhuongRequest request) {
        if (request.thoiGianVe().isBefore(request.thoiGianDi())) {
            throw new BadRequestException("Thoi gian ve khong duoc truoc thoi gian di");
        }
    }

    private void gan(DoanDiaPhuong d, DoanDiaPhuongRequest request) {
        d.setTenDoan(request.tenDoan());
        d.setDoiTac(thamChieu(DoiTac.class, request.doiTacId(), doiTacRepository::existsById, "doi tac"));
        d.setThoiGianDi(request.thoiGianDi());
        d.setThoiGianVe(request.thoiGianVe());
        d.setDiaDiem(request.diaDiem());
        d.setMucTieu(thamChieu(TuDien.class, request.mucTieuTuDienId(), tuDienRepository::existsById, "muc tieu (tu dien)"));
        d.setNguonKinhPhi(thamChieu(TuDien.class, request.nguonKinhPhiTuDienId(), tuDienRepository::existsById, "nguon kinh phi (tu dien)"));
        d.setThanhPhan(request.thanhPhan());
        d.setNoiDungLamViec(request.noiDungLamViec());
        d.setGhiChu(request.ghiChu());
    }

    private <T> T thamChieu(Class<T> loai, UUID id, java.util.function.Predicate<UUID> tonTai, String ten) {
        if (id == null) {
            return null;
        }
        if (!tonTai.test(id)) {
            throw new ResourceNotFoundException("Khong tim thay " + ten + ": " + id);
        }
        return entityManager.getReference(loai, id);
    }

    private DoanDiaPhuong timHoacLoi(UUID id) {
        return doanDiaPhuongRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay doan di dia phuong: " + id));
    }

    private UUID currentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof SecurityUser user) {
            return user.getId();
        }
        return null;
    }
}
