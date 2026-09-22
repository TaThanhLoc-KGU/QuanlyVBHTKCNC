package com.ttloc.htkhcn.vanbandhkg;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ttloc.htkhcn.common.SpecUtils;
import com.ttloc.htkhcn.common.TinhTrangHieuLuc;
import com.ttloc.htkhcn.common.exception.DuplicateResourceException;
import com.ttloc.htkhcn.common.exception.ResourceNotFoundException;
import com.ttloc.htkhcn.danhmuc.LoaiVanBan;
import com.ttloc.htkhcn.danhmuc.LoaiVanBanRepository;
import com.ttloc.htkhcn.security.SecurityUser;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VanBanDhkgService {

    private final VanBanDhkgRepository vanBanDhkgRepository;
    private final LoaiVanBanRepository loaiVanBanRepository;
    private final EntityManager entityManager;

    @Transactional(readOnly = true)
    public Page<VanBanDhkgResponse> danhSach(
            UUID loaiVanBanId, TinhTrangHieuLuc tinhTrang, LocalDate tu, LocalDate den, String tuKhoa,
            Pageable pageable) {
        Specification<VanBanDhkg> spec = SpecUtils.and(
                VanBanDhkgSpecifications.chuaBiXoa(),
                VanBanDhkgSpecifications.loaiVanBanBang(loaiVanBanId),
                VanBanDhkgSpecifications.tinhTrangBang(tinhTrang),
                VanBanDhkgSpecifications.ngayBanHanhTu(tu),
                VanBanDhkgSpecifications.ngayBanHanhDen(den),
                VanBanDhkgSpecifications.tuKhoaKhongDauTrong(tuKhoa));
        return vanBanDhkgRepository.findAll(spec, pageable).map(VanBanDhkgResponse::from);
    }

    @Transactional(readOnly = true)
    public VanBanDhkgResponse layTheoId(UUID id) {
        return VanBanDhkgResponse.from(timHoacLoi(id));
    }

    @Transactional
    public VanBanDhkgResponse tao(VanBanDhkgRequest request) {
        kiemTraTrungSoHieu(request.soHieu(), null);
        VanBanDhkg v = new VanBanDhkg();
        gan(v, request);
        return VanBanDhkgResponse.from(vanBanDhkgRepository.saveAndFlush(v));
    }

    @Transactional
    public VanBanDhkgResponse sua(UUID id, VanBanDhkgRequest request) {
        kiemTraTrungSoHieu(request.soHieu(), id);
        VanBanDhkg v = timHoacLoi(id);
        gan(v, request);
        return VanBanDhkgResponse.from(vanBanDhkgRepository.saveAndFlush(v));
    }

    @Transactional
    public void xoa(UUID id) {
        VanBanDhkg v = timHoacLoi(id);
        v.setDeletedAt(Instant.now());
        v.setDeletedBy(currentUserId());
        vanBanDhkgRepository.saveAndFlush(v);
    }

    private void kiemTraTrungSoHieu(String soHieu, UUID excludeId) {
        boolean trung = excludeId == null
                ? vanBanDhkgRepository.existsBySoHieu(soHieu)
                : vanBanDhkgRepository.existsBySoHieuAndIdNot(soHieu, excludeId);
        if (trung) {
            throw new DuplicateResourceException("So hieu da ton tai: " + soHieu);
        }
    }

    private void gan(VanBanDhkg v, VanBanDhkgRequest request) {
        LoaiVanBan loai = loaiVanBanRepository.findById(request.loaiVanBanId())
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay loai van ban: " + request.loaiVanBanId()));
        v.setSoHieu(request.soHieu());
        v.setTenVanBan(request.tenVanBan());
        v.setLoaiVanBan(entityManager.getReference(LoaiVanBan.class, loai.getId()));
        v.setNgayBanHanh(request.ngayBanHanh());
        v.setNgayHieuLuc(request.ngayHieuLuc());
        v.setTinhTrangHieuLuc(request.tinhTrangHieuLuc());
        v.setCoQuanBanHanh(request.coQuanBanHanh());
        v.setGhiChu(request.ghiChu());
        v.setNoiDungChinh(request.noiDungChinh());
    }

    private VanBanDhkg timHoacLoi(UUID id) {
        return vanBanDhkgRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay van ban: " + id));
    }

    private UUID currentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof SecurityUser user) {
            return user.getId();
        }
        return null;
    }
}
