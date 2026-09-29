package com.ttloc.htkhcn.hosokhoahoc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ttloc.htkhcn.common.ModuleKey;
import com.ttloc.htkhcn.common.exception.ResourceNotFoundException;
import com.ttloc.htkhcn.security.SecurityUser;
import com.ttloc.htkhcn.user.NguoiDung;
import com.ttloc.htkhcn.user.NguoiDungRepository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HoatDongNgoaiTruongService {

    private final HoatDongNgoaiTruongRepository repository;
    private final NguoiDungRepository nguoiDungRepository;
    private final EntityManager entityManager;

    @Transactional(readOnly = true)
    public List<HoatDongNgoaiTruongResponse> danhSach(UUID nguoiDungId) {
        return repository.findByNguoiDungIdOrderByThoiGianBatDauDesc(nguoiDungId).stream()
                .map(HoatDongNgoaiTruongResponse::from)
                .toList();
    }

    @Transactional
    public HoatDongNgoaiTruongResponse tao(UUID nguoiDungId, HoatDongNgoaiTruongRequest request) {
        kiemTraQuyen(nguoiDungId);
        if (!nguoiDungRepository.existsById(nguoiDungId)) {
            throw new ResourceNotFoundException("Khong tim thay nguoi dung: " + nguoiDungId);
        }
        HoatDongNgoaiTruong h = new HoatDongNgoaiTruong();
        h.setNguoiDung(entityManager.getReference(NguoiDung.class, nguoiDungId));
        gan(h, request);
        h.setNguoiTaoId(currentUserId());
        h.setNgayTao(Instant.now());
        HoatDongNgoaiTruong saved = repository.save(h);
        entityManager.flush();
        entityManager.refresh(saved);
        return HoatDongNgoaiTruongResponse.from(saved);
    }

    @Transactional
    public void xoa(UUID id) {
        HoatDongNgoaiTruong h = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay hoat dong: " + id));
        kiemTraQuyen(h.getNguoiDung().getId());
        repository.delete(h);
    }

    private void gan(HoatDongNgoaiTruong h, HoatDongNgoaiTruongRequest request) {
        h.setLoai(request.loai());
        h.setTen(request.ten());
        h.setDonViPhoiHop(request.donViPhoiHop());
        h.setVaiTro(request.vaiTro());
        h.setThoiGianBatDau(request.thoiGianBatDau());
        h.setThoiGianKetThuc(request.thoiGianKetThuc());
        h.setGhiChu(request.ghiChu());
    }

    private void kiemTraQuyen(UUID nguoiDungId) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth != null && auth.getPrincipal() instanceof SecurityUser user)) {
            throw new AccessDeniedException("Chua dang nhap");
        }
        if (user.isAdmin() || user.coTheSuaModule(ModuleKey.DE_TAI_NCKH) || user.getId().equals(nguoiDungId)) {
            return;
        }
        throw new AccessDeniedException("Ban chi duoc quan ly hoat dong ngoai truong cua chinh minh");
    }

    private UUID currentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof SecurityUser user) {
            return user.getId();
        }
        return null;
    }
}
