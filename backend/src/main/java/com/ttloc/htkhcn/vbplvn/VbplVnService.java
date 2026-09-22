package com.ttloc.htkhcn.vbplvn;

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
public class VbplVnService {

    private final VbplVnRepository vbplVnRepository;
    private final LoaiVanBanRepository loaiVanBanRepository;
    private final EntityManager entityManager;

    @Transactional(readOnly = true)
    public Page<VbplVnResponse> danhSach(
            UUID loaiVanBanId, TinhTrangHieuLuc tinhTrang, LocalDate tu, LocalDate den, String tuKhoa,
            Pageable pageable) {
        Specification<VbplVn> spec = SpecUtils.and(
                VbplVnSpecifications.chuaBiXoa(),
                VbplVnSpecifications.loaiVanBanBang(loaiVanBanId),
                VbplVnSpecifications.tinhTrangBang(tinhTrang),
                VbplVnSpecifications.ngayBanHanhTu(tu),
                VbplVnSpecifications.ngayBanHanhDen(den),
                VbplVnSpecifications.tuKhoaKhongDauTrong(tuKhoa));
        return vbplVnRepository.findAll(spec, pageable).map(VbplVnResponse::from);
    }

    @Transactional(readOnly = true)
    public VbplVnResponse layTheoId(UUID id) {
        return VbplVnResponse.from(timHoacLoi(id));
    }

    @Transactional
    public VbplVnResponse tao(VbplVnRequest request) {
        kiemTraTrungSoHieu(request.soHieu(), null);
        VbplVn v = new VbplVn();
        gan(v, request);
        return VbplVnResponse.from(vbplVnRepository.saveAndFlush(v));
    }

    @Transactional
    public VbplVnResponse sua(UUID id, VbplVnRequest request) {
        kiemTraTrungSoHieu(request.soHieu(), id);
        VbplVn v = timHoacLoi(id);
        gan(v, request);
        return VbplVnResponse.from(vbplVnRepository.saveAndFlush(v));
    }

    /** SPEC 3.3: can bo bam "Da doi chieu" sau khi tu xac nhan tra cuu tren vbpl.vn. */
    @Transactional
    public VbplVnResponse xacNhanDoiChieu(UUID id) {
        VbplVn v = timHoacLoi(id);
        v.setNgayDoiChieuGanNhat(LocalDate.now());
        return VbplVnResponse.from(vbplVnRepository.saveAndFlush(v));
    }

    @Transactional
    public void xoa(UUID id) {
        VbplVn v = timHoacLoi(id);
        v.setDeletedAt(Instant.now());
        v.setDeletedBy(currentUserId());
        vbplVnRepository.saveAndFlush(v);
    }

    private void kiemTraTrungSoHieu(String soHieu, UUID excludeId) {
        boolean trung = excludeId == null
                ? vbplVnRepository.existsBySoHieu(soHieu)
                : vbplVnRepository.existsBySoHieuAndIdNot(soHieu, excludeId);
        if (trung) {
            throw new DuplicateResourceException("So hieu da ton tai: " + soHieu);
        }
    }

    private void gan(VbplVn v, VbplVnRequest request) {
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

    private VbplVn timHoacLoi(UUID id) {
        return vbplVnRepository.findByIdAndDeletedAtIsNull(id)
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
