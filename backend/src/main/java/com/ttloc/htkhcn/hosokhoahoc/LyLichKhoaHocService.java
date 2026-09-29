package com.ttloc.htkhcn.hosokhoahoc;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ttloc.htkhcn.common.ModuleKey;
import com.ttloc.htkhcn.common.exception.ResourceNotFoundException;
import com.ttloc.htkhcn.security.SecurityUser;
import com.ttloc.htkhcn.user.NguoiDungRepository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

/** Ly lich khoa hoc la thong tin ca nhan cua chinh can bo - CHO PHEP chinh
 * chu tu sua (khong bat buoc quyen Editor module), Admin/Editor(DE_TAI_NCKH)
 * duoc sua ho bat ky ai (vd nhap ho ly lich can bo khac). */
@Service
@RequiredArgsConstructor
public class LyLichKhoaHocService {

    private final LyLichKhoaHocRepository repository;
    private final NguoiDungRepository nguoiDungRepository;
    private final EntityManager entityManager;

    @Transactional(readOnly = true)
    public LyLichKhoaHocResponse layTheoNguoiDung(UUID nguoiDungId) {
        return repository.findByNguoiDungId(nguoiDungId).map(LyLichKhoaHocResponse::from).orElse(null);
    }

    @Transactional
    public LyLichKhoaHocResponse luu(UUID nguoiDungId, LyLichKhoaHocRequest request) {
        kiemTraQuyen(nguoiDungId);
        if (!nguoiDungRepository.existsById(nguoiDungId)) {
            throw new ResourceNotFoundException("Khong tim thay nguoi dung: " + nguoiDungId);
        }
        LyLichKhoaHoc l = repository.findByNguoiDungId(nguoiDungId).orElseGet(() -> {
            LyLichKhoaHoc moi = new LyLichKhoaHoc();
            moi.setNguoiDung(entityManager.getReference(com.ttloc.htkhcn.user.NguoiDung.class, nguoiDungId));
            return moi;
        });
        l.setHocHam(request.hocHam());
        l.setHocVi(request.hocVi());
        l.setChuyenNganh(request.chuyenNganh());
        l.setQuaTrinhCongTac(request.quaTrinhCongTac());
        l.setGhiChu(request.ghiChu());
        l.setNguoiSuaId(currentUserId());
        l.setNgaySua(OffsetDateTime.now());
        LyLichKhoaHoc saved = repository.save(l);
        entityManager.flush();
        entityManager.refresh(saved);
        return LyLichKhoaHocResponse.from(saved);
    }

    private void kiemTraQuyen(UUID nguoiDungId) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth != null && auth.getPrincipal() instanceof SecurityUser user)) {
            throw new AccessDeniedException("Chua dang nhap");
        }
        if (user.isAdmin() || user.coTheSuaModule(ModuleKey.DE_TAI_NCKH) || user.getId().equals(nguoiDungId)) {
            return;
        }
        throw new AccessDeniedException("Ban chi duoc sua ly lich khoa hoc cua chinh minh");
    }

    private UUID currentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof SecurityUser user) {
            return user.getId();
        }
        return null;
    }
}
