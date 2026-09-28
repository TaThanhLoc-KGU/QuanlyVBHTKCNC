package com.ttloc.htkhcn.visa;

import java.time.Instant;
import java.util.UUID;
import java.util.function.Predicate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ttloc.htkhcn.common.SpecUtils;
import com.ttloc.htkhcn.common.exception.ResourceNotFoundException;
import com.ttloc.htkhcn.danhmuc.TuDien;
import com.ttloc.htkhcn.danhmuc.TuDienRepository;
import com.ttloc.htkhcn.doanvao.DoanVao;
import com.ttloc.htkhcn.doanvao.DoanVaoRepository;
import com.ttloc.htkhcn.security.SecurityUser;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VisaService {

    private final VisaRepository visaRepository;
    private final TuDienRepository tuDienRepository;
    private final DoanVaoRepository doanVaoRepository;
    private final EntityManager entityManager;

    @Transactional(readOnly = true)
    public Page<VisaResponse> danhSach(Integer nam, LoaiCapVisa loaiCap, String tuKhoa, Pageable pageable) {
        Specification<Visa> spec = SpecUtils.and(
                VisaSpecifications.chuaBiXoa(),
                VisaSpecifications.namBang(nam),
                VisaSpecifications.loaiCapBang(loaiCap),
                VisaSpecifications.tuKhoaKhongDauTrong(tuKhoa));
        return visaRepository.findAll(spec, pageable).map(VisaResponse::from);
    }

    @Transactional(readOnly = true)
    public VisaResponse layTheoId(UUID id) {
        return VisaResponse.from(timHoacLoi(id));
    }

    @Transactional
    public VisaResponse tao(VisaRequest request) {
        Visa v = new Visa();
        gan(v, request);
        Visa saved = visaRepository.save(v);
        entityManager.flush();
        entityManager.refresh(saved);
        return VisaResponse.from(saved);
    }

    @Transactional
    public VisaResponse sua(UUID id, VisaRequest request) {
        Visa v = timHoacLoi(id);
        gan(v, request);
        Visa saved = visaRepository.save(v);
        entityManager.flush();
        entityManager.refresh(saved);
        return VisaResponse.from(saved);
    }

    @Transactional
    public void xoa(UUID id) {
        Visa v = timHoacLoi(id);
        v.setDeletedAt(Instant.now());
        v.setDeletedBy(currentUserId());
        visaRepository.saveAndFlush(v);
    }

    private void gan(Visa v, VisaRequest request) {
        v.setHoTen(request.hoTen());
        v.setQuocTich(request.quocTich());
        v.setLoaiCap(request.loaiCap());
        v.setNgayCap(request.ngayCap());
        v.setNgayHetHan(request.ngayHetHan());
        v.setCoQuanCap(request.coQuanCap());
        v.setMucDich(thamChieu(TuDien.class, request.mucDichTuDienId(), tuDienRepository::existsById, "muc dich (tu dien)"));
        v.setDoanVao(thamChieu(DoanVao.class, request.doanVaoId(), doanVaoRepository::existsById, "doan vao"));
        v.setGhiChu(request.ghiChu());
    }

    private <T> T thamChieu(Class<T> loai, UUID id, Predicate<UUID> tonTai, String ten) {
        if (id == null) {
            return null;
        }
        if (!tonTai.test(id)) {
            throw new ResourceNotFoundException("Khong tim thay " + ten + ": " + id);
        }
        return entityManager.getReference(loai, id);
    }

    private Visa timHoacLoi(UUID id) {
        return visaRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay visa: " + id));
    }

    private UUID currentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof SecurityUser user) {
            return user.getId();
        }
        return null;
    }
}
