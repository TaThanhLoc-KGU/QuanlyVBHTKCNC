package com.ttloc.htkhcn.mou;

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
import com.ttloc.htkhcn.common.exception.ResourceNotFoundException;
import com.ttloc.htkhcn.doitac.DoiTac;
import com.ttloc.htkhcn.doitac.DoiTacRepository;
import com.ttloc.htkhcn.security.SecurityUser;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MouService {

    private final MouRepository mouRepository;
    private final MouTrangThaiRepository mouTrangThaiRepository;
    private final DoiTacRepository doiTacRepository;
    private final EntityManager entityManager;

    @Transactional(readOnly = true)
    public Page<MouResponse> danhSach(
            UUID doiTacId, TrangThaiMou trangThai, LocalDate tu, LocalDate den, String tuKhoa, Pageable pageable) {
        Specification<MouTrangThai> spec = SpecUtils.and(
                MouSpecifications.doiTacBang(doiTacId),
                MouSpecifications.trangThaiBang(trangThai),
                MouSpecifications.ngayBanHanhTu(tu),
                MouSpecifications.ngayBanHanhDen(den),
                MouSpecifications.tuKhoaKhongDauTrong(tuKhoa));
        return mouTrangThaiRepository.findAll(spec, pageable).map(MouResponse::from);
    }

    @Transactional(readOnly = true)
    public MouResponse layTheoId(UUID id) {
        return MouResponse.from(mouTrangThaiRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay MoU: " + id)));
    }

    @Transactional
    public MouResponse tao(MouRequest request) {
        Mou m = new Mou();
        gan(m, request);
        Mou saved = mouRepository.save(m);
        // Flush + clear truoc khi doc lai qua v_mou_trang_thai: view la 1 entity
        // rieng voi Hibernate nen (a) auto-flush-before-query khong biet no phu
        // thuoc bang "mou" - phai flush tay; va (b) MouTrangThai da tung duoc
        // nap vao persistence context (VD: tu 1 lan goi layTheoId truoc do trong
        // cung transaction) se bi tra ve TU CACHE cap 1 (khong doc lai DB) neu
        // khong clear - gay doc du lieu cu sau xoa/sua.
        entityManager.flush();
        entityManager.clear();
        return layTheoId(saved.getId());
    }

    @Transactional
    public MouResponse sua(UUID id, MouRequest request) {
        Mou m = mouRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay MoU: " + id));
        gan(m, request);
        Mou saved = mouRepository.save(m);
        entityManager.flush();
        entityManager.clear();
        return layTheoId(saved.getId());
    }

    @Transactional
    public void xoa(UUID id) {
        Mou m = mouRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay MoU: " + id));
        m.setDeletedAt(Instant.now());
        m.setDeletedBy(currentUserId());
        mouRepository.saveAndFlush(m);
        entityManager.clear();
    }

    private void gan(Mou m, MouRequest request) {
        if (!doiTacRepository.existsById(request.doiTacId())) {
            throw new ResourceNotFoundException("Khong tim thay doi tac: " + request.doiTacId());
        }
        DoiTac doiTacRef = entityManager.getReference(DoiTac.class, request.doiTacId());
        m.setDoiTac(doiTacRef);
        m.setTenTaiLieu(request.tenTaiLieu());
        m.setNgayBanHanh(request.ngayBanHanh());
        m.setNgayHetHan(request.ngayHetHan());
        m.setCaNhanDauMoi(request.caNhanDauMoi());
        m.setDonViThucHien(request.donViThucHien());
        m.setPhamViHopTac(request.phamViHopTac());
        m.setLinhVucHopTac(request.linhVucHopTac());
        m.setDauMoiGhiTrongMou(request.dauMoiGhiTrongMou());
        m.setDaiDienKguKy(request.daiDienKguKy());
        m.setThoiHanHieuLuc(request.thoiHanHieuLuc());
        m.setSoCongVan(request.soCongVan());
    }

    private UUID currentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof SecurityUser user) {
            return user.getId();
        }
        return null;
    }
}
