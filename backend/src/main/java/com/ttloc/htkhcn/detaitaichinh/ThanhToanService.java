package com.ttloc.htkhcn.detaitaichinh;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ttloc.htkhcn.common.exception.ResourceNotFoundException;
import com.ttloc.htkhcn.detai.DeTaiService;
import com.ttloc.htkhcn.security.SecurityUser;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ThanhToanService {

    private final DeTaiThanhToanRepository repository;
    private final DeTaiService deTaiService;
    private final EntityManager entityManager;

    @Transactional(readOnly = true)
    public List<ThanhToanResponse> danhSach(UUID deTaiId) {
        return repository.findByDeTaiIdOrderByNamDesc(deTaiId).stream().map(ThanhToanResponse::from).toList();
    }

    @Transactional
    public ThanhToanResponse tao(UUID deTaiId, ThanhToanRequest request) {
        var deTai = deTaiService.timHoacLoi(deTaiId);
        DeTaiThanhToan t = new DeTaiThanhToan();
        t.setDeTai(deTai);
        gan(t, request);
        t.setNguoiTaoId(currentUserId());
        t.setNgayTao(Instant.now());
        DeTaiThanhToan saved = repository.save(t);
        entityManager.flush();
        entityManager.refresh(saved);
        return ThanhToanResponse.from(saved);
    }

    @Transactional
    public ThanhToanResponse sua(UUID id, ThanhToanRequest request) {
        DeTaiThanhToan t = timHoacLoi(id);
        gan(t, request);
        t.setNguoiSuaId(currentUserId());
        t.setNgaySua(Instant.now());
        DeTaiThanhToan saved = repository.save(t);
        entityManager.flush();
        entityManager.refresh(saved);
        return ThanhToanResponse.from(saved);
    }

    @Transactional
    public ThanhToanResponse duyet(UUID id, DuyetRequest request) {
        DeTaiThanhToan t = timHoacLoi(id);
        t.setTrangThai(request.trangThai());
        t.setGhiChu(request.ghiChu());
        t.setNguoiDuyetId(currentUserId());
        t.setNgayDuyet(Instant.now());
        DeTaiThanhToan saved = repository.save(t);
        entityManager.flush();
        entityManager.refresh(saved);
        return ThanhToanResponse.from(saved);
    }

    @Transactional
    public void xoa(UUID id) {
        repository.delete(timHoacLoi(id));
    }

    private void gan(DeTaiThanhToan t, ThanhToanRequest request) {
        t.setNam(request.nam());
        t.setSoTien(request.soTien());
        t.setNgayThanhToan(request.ngayThanhToan());
        t.setGhiChu(request.ghiChu());
    }

    private DeTaiThanhToan timHoacLoi(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay ho so thanh toan: " + id));
    }

    private UUID currentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof SecurityUser user) {
            return user.getId();
        }
        return null;
    }
}
