package com.ttloc.htkhcn.sukien;

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
import com.ttloc.htkhcn.security.SecurityUser;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SuKienService {

    private final SuKienRepository suKienRepository;
    private final TuDienRepository tuDienRepository;
    private final EntityManager entityManager;

    @Transactional(readOnly = true)
    public Page<SuKienResponse> danhSach(Integer nam, UUID loaiSuKienTuDienId, String tuKhoa, Pageable pageable) {
        Specification<SuKien> spec = SpecUtils.and(
                SuKienSpecifications.chuaBiXoa(),
                SuKienSpecifications.namBang(nam),
                SuKienSpecifications.loaiSuKienBang(loaiSuKienTuDienId),
                SuKienSpecifications.tuKhoaKhongDauTrong(tuKhoa));
        return suKienRepository.findAll(spec, pageable).map(SuKienResponse::from);
    }

    @Transactional(readOnly = true)
    public SuKienResponse layTheoId(UUID id) {
        return SuKienResponse.from(timHoacLoi(id));
    }

    @Transactional
    public SuKienResponse tao(SuKienRequest request) {
        validate(request);
        SuKien s = new SuKien();
        gan(s, request);
        SuKien saved = suKienRepository.save(s);
        entityManager.flush();
        entityManager.refresh(saved);
        return SuKienResponse.from(saved);
    }

    @Transactional
    public SuKienResponse sua(UUID id, SuKienRequest request) {
        validate(request);
        SuKien s = timHoacLoi(id);
        gan(s, request);
        SuKien saved = suKienRepository.save(s);
        entityManager.flush();
        entityManager.refresh(saved);
        return SuKienResponse.from(saved);
    }

    @Transactional
    public void xoa(UUID id) {
        SuKien s = timHoacLoi(id);
        s.setDeletedAt(Instant.now());
        s.setDeletedBy(currentUserId());
        suKienRepository.saveAndFlush(s);
    }

    private void validate(SuKienRequest request) {
        if (request.thoiGianKetThuc() != null && request.thoiGianKetThuc().isBefore(request.thoiGianBatDau())) {
            throw new BadRequestException("Thoi gian ket thuc khong duoc truoc thoi gian bat dau");
        }
    }

    private void gan(SuKien s, SuKienRequest request) {
        s.setTenSuKien(request.tenSuKien());
        s.setLoaiSuKien(thamChieu(request.loaiSuKienTuDienId(), "loai su kien (tu dien)"));
        s.setLinhVuc(thamChieu(request.linhVucTuDienId(), "linh vuc (tu dien)"));
        s.setThoiGianBatDau(request.thoiGianBatDau());
        s.setThoiGianKetThuc(request.thoiGianKetThuc());
        s.setDiaDiem(request.diaDiem());
        s.setDonViToChuc(request.donViToChuc());
        s.setSoLuongThamGia(request.soLuongThamGia());
        s.setNoiDung(request.noiDung());
        s.setGhiChu(request.ghiChu());
    }

    private TuDien thamChieu(UUID id, String ten) {
        if (id == null) {
            return null;
        }
        if (!tuDienRepository.existsById(id)) {
            throw new ResourceNotFoundException("Khong tim thay " + ten + ": " + id);
        }
        return entityManager.getReference(TuDien.class, id);
    }

    private SuKien timHoacLoi(UUID id) {
        return suKienRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay su kien: " + id));
    }

    private UUID currentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof SecurityUser user) {
            return user.getId();
        }
        return null;
    }
}
