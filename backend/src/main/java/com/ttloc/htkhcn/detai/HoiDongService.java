package com.ttloc.htkhcn.detai;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ttloc.htkhcn.common.ModuleKey;
import com.ttloc.htkhcn.common.exception.ResourceNotFoundException;
import com.ttloc.htkhcn.danhmuc.TuDien;
import com.ttloc.htkhcn.danhmuc.TuDienRepository;
import com.ttloc.htkhcn.security.SecurityUser;
import com.ttloc.htkhcn.user.NguoiDung;
import com.ttloc.htkhcn.user.NguoiDungRepository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

/**
 * "Workflow duyet theo vai tro" cho hoi dong (SPEC bo sung Muc 8): moi thanh
 * vien hoi dong (dong hoi_dong_thanh_vien, gan qua nguoi_dung_id) CHI duoc tu
 * cap nhat y kien cua CHINH MINH - kiem tra o day (khong the bieu dien bang
 * @PreAuthorize SpEL don gian vi phai so sanh voi 1 dong du lieu cu the), Admin/
 * Editor(DE_TAI_NCKH) duoc ghi de bat ky dong nao (vd nhap ho thanh vien
 * ngoai truong khong co tai khoan).
 */
@Service
@RequiredArgsConstructor
public class HoiDongService {

    private final HoiDongRepository hoiDongRepository;
    private final HoiDongThanhVienRepository thanhVienRepository;
    private final DeTaiService deTaiService;
    private final NguoiDungRepository nguoiDungRepository;
    private final TuDienRepository tuDienRepository;
    private final EntityManager entityManager;

    @Transactional(readOnly = true)
    public List<HoiDongResponse> danhSach(UUID deTaiId) {
        return hoiDongRepository.findByDeTaiIdOrderByNgayTaoAsc(deTaiId).stream()
                .map(HoiDongResponse::from)
                .toList();
    }

    @Transactional
    public HoiDongResponse tao(UUID deTaiId, HoiDongRequest request) {
        var deTai = deTaiService.timHoacLoi(deTaiId);
        HoiDong h = new HoiDong();
        h.setDeTai(deTai);
        ganMeta(h, request);
        h.setNguoiTaoId(currentUserId());
        h.setNgayTao(Instant.now());
        HoiDong saved = hoiDongRepository.save(h);
        entityManager.flush();
        entityManager.refresh(saved);
        return HoiDongResponse.from(saved);
    }

    @Transactional
    public HoiDongResponse sua(UUID id, HoiDongRequest request) {
        HoiDong h = timHoacLoi(id);
        ganMeta(h, request);
        h.setNguoiSuaId(currentUserId());
        h.setNgaySua(Instant.now());
        HoiDong saved = hoiDongRepository.save(h);
        entityManager.flush();
        entityManager.refresh(saved);
        return HoiDongResponse.from(saved);
    }

    @Transactional
    public void xoa(UUID id) {
        hoiDongRepository.delete(timHoacLoi(id));
    }

    @Transactional(readOnly = true)
    public List<HoiDongThanhVienResponse> danhSachThanhVien(UUID hoiDongId) {
        return thanhVienRepository.findByHoiDongIdOrderById(hoiDongId).stream()
                .map(HoiDongThanhVienResponse::from)
                .toList();
    }

    @Transactional
    public HoiDongThanhVienResponse themThanhVien(UUID hoiDongId, HoiDongThanhVienRequest request) {
        HoiDong h = timHoacLoi(hoiDongId);
        HoiDongThanhVien tv = new HoiDongThanhVien();
        tv.setHoiDong(h);
        if (request.nguoiDungId() != null) {
            if (!nguoiDungRepository.existsById(request.nguoiDungId())) {
                throw new ResourceNotFoundException("Khong tim thay nguoi dung: " + request.nguoiDungId());
            }
            tv.setNguoiDung(entityManager.getReference(NguoiDung.class, request.nguoiDungId()));
        }
        tv.setHoTenNgoai(request.hoTenNgoai());
        if (request.vaiTroTuDienId() != null) {
            if (!tuDienRepository.existsById(request.vaiTroTuDienId())) {
                throw new ResourceNotFoundException("Khong tim thay vai tro: " + request.vaiTroTuDienId());
            }
            tv.setVaiTro(entityManager.getReference(TuDien.class, request.vaiTroTuDienId()));
        }
        HoiDongThanhVien saved = thanhVienRepository.save(tv);
        entityManager.flush();
        entityManager.refresh(saved);
        return HoiDongThanhVienResponse.from(saved);
    }

    @Transactional
    public void xoaThanhVien(UUID thanhVienId) {
        HoiDongThanhVien tv = thanhVienRepository.findById(thanhVienId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay thanh vien hoi dong: " + thanhVienId));
        thanhVienRepository.delete(tv);
    }

    @Transactional
    public HoiDongThanhVienResponse ghiYKien(UUID thanhVienId, YKienHoiDongRequest request) {
        HoiDongThanhVien tv = thanhVienRepository.findById(thanhVienId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay thanh vien hoi dong: " + thanhVienId));
        kiemTraQuyenGhiYKien(tv);
        tv.setYKien(request.yKien());
        tv.setDiem(request.diem());
        tv.setDongY(request.dongY());
        tv.setNgayChoYKien(Instant.now());
        HoiDongThanhVien saved = thanhVienRepository.save(tv);
        entityManager.flush();
        entityManager.refresh(saved);
        return HoiDongThanhVienResponse.from(saved);
    }

    private void kiemTraQuyenGhiYKien(HoiDongThanhVien tv) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth != null && auth.getPrincipal() instanceof SecurityUser user)) {
            throw new AccessDeniedException("Chua dang nhap");
        }
        if (user.isAdmin() || user.coTheSuaModule(ModuleKey.DE_TAI_NCKH)) {
            return;
        }
        if (tv.getNguoiDung() != null && tv.getNguoiDung().getId().equals(user.getId())) {
            return;
        }
        throw new AccessDeniedException("Ban chi duoc ghi y kien cua chinh minh trong hoi dong nay");
    }

    private void ganMeta(HoiDong h, HoiDongRequest request) {
        h.setLoai(request.loai());
        h.setNgayHop(request.ngayHop());
        h.setDiaDiem(request.diaDiem());
        h.setKetQua(request.ketQua() != null ? request.ketQua() : KetQuaHoiDong.CHUA_CO_KET_QUA);
        h.setDiemTrungBinh(request.diemTrungBinh());
        h.setGhiChu(request.ghiChu());
    }

    private HoiDong timHoacLoi(UUID id) {
        return hoiDongRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay hoi dong: " + id));
    }

    private UUID currentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof SecurityUser user) {
            return user.getId();
        }
        return null;
    }
}
