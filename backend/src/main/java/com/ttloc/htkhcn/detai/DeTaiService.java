package com.ttloc.htkhcn.detai;

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
import com.ttloc.htkhcn.security.SecurityUser;
import com.ttloc.htkhcn.user.NguoiDung;
import com.ttloc.htkhcn.user.NguoiDungRepository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeTaiService {

    private final DeTaiRepository deTaiRepository;
    private final TuDienRepository tuDienRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final LinhVucNghienCuuRepository linhVucNghienCuuRepository;
    private final EntityManager entityManager;

    @Transactional(readOnly = true)
    public Page<DeTaiResponse> danhSach(Integer namDeXuat, TrangThaiDeTai trangThai, UUID chuNhiemId,
            UUID linhVucId, String tuKhoa, Pageable pageable) {
        Specification<DeTai> spec = SpecUtils.and(
                DeTaiSpecifications.chuaBiXoa(),
                DeTaiSpecifications.namDeXuatBang(namDeXuat),
                DeTaiSpecifications.trangThaiBang(trangThai),
                DeTaiSpecifications.chuNhiemBang(chuNhiemId),
                DeTaiSpecifications.linhVucBang(linhVucId),
                DeTaiSpecifications.tuKhoaKhongDauTrong(tuKhoa));
        return deTaiRepository.findAll(spec, pageable).map(DeTaiResponse::from);
    }

    @Transactional(readOnly = true)
    public DeTaiResponse layTheoId(UUID id) {
        return DeTaiResponse.from(timHoacLoi(id));
    }

    @Transactional
    public DeTaiResponse tao(DeTaiRequest request) {
        DeTai d = new DeTai();
        gan(d, request);
        DeTai saved = deTaiRepository.save(d);
        entityManager.flush();
        entityManager.refresh(saved);
        return DeTaiResponse.from(saved);
    }

    @Transactional
    public DeTaiResponse sua(UUID id, DeTaiRequest request) {
        DeTai d = timHoacLoi(id);
        gan(d, request);
        DeTai saved = deTaiRepository.save(d);
        entityManager.flush();
        entityManager.refresh(saved);
        return DeTaiResponse.from(saved);
    }

    @Transactional
    public DeTaiResponse capNhatTrangThai(UUID id, TrangThaiDeTai trangThaiMoi, String lyDoHuy) {
        DeTai d = timHoacLoi(id);
        d.setTrangThai(trangThaiMoi);
        if (trangThaiMoi == TrangThaiDeTai.BI_HUY) {
            d.setLyDoHuy(lyDoHuy);
        }
        DeTai saved = deTaiRepository.save(d);
        entityManager.flush();
        entityManager.refresh(saved);
        return DeTaiResponse.from(saved);
    }

    @Transactional
    public void xoa(UUID id) {
        DeTai d = timHoacLoi(id);
        d.setDeletedAt(Instant.now());
        d.setDeletedBy(currentUserId());
        deTaiRepository.saveAndFlush(d);
    }

    private void gan(DeTai d, DeTaiRequest request) {
        d.setMaDeTai(request.maDeTai());
        d.setTenDeTai(request.tenDeTai());
        d.setChuNhiem(thamChieu(NguoiDung.class, request.chuNhiemId(), nguoiDungRepository::existsById, "chu nhiem"));
        d.setChuNhiemNgoai(request.chuNhiemNgoai());
        d.setDonViThucHien(request.donViThucHien());
        d.setDonViChuQuan(request.donViChuQuan());
        d.setPhanLoai(thamChieu(TuDien.class, request.phanLoaiTuDienId(), tuDienRepository::existsById, "phan loai de tai"));
        d.setLoaiHinh(thamChieu(TuDien.class, request.loaiHinhTuDienId(), tuDienRepository::existsById, "loai hinh nghien cuu"));
        d.setNguonKinhPhi(thamChieu(TuDien.class, request.nguonKinhPhiTuDienId(), tuDienRepository::existsById, "nguon kinh phi"));
        d.setLinhVuc(thamChieu(LinhVucNghienCuu.class, request.linhVucId(), linhVucNghienCuuRepository::existsById, "linh vuc nghien cuu"));
        d.setNamDeXuat(request.namDeXuat());
        d.setThoiGianBatDau(request.thoiGianBatDau());
        d.setThoiGianKetThuc(request.thoiGianKetThuc());
        d.setKinhPhiDeXuat(request.kinhPhiDeXuat());
        d.setKinhPhiDuyet(request.kinhPhiDuyet());
        d.setMucTieu(request.mucTieu());
        d.setNoiDung(request.noiDung());
        d.setSanPhamDuKien(request.sanPhamDuKien());
        d.setMucXepLoai(thamChieu(TuDien.class, request.mucXepLoaiTuDienId(), tuDienRepository::existsById, "muc xep loai"));
        d.setGhiChu(request.ghiChu());
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

    public DeTai timHoacLoi(UUID id) {
        return deTaiRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay de tai: " + id));
    }

    private UUID currentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof SecurityUser user) {
            return user.getId();
        }
        return null;
    }
}
