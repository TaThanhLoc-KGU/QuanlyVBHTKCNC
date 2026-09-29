package com.ttloc.htkhcn.detaitaichinh;

import java.time.Instant;
import java.util.UUID;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ttloc.htkhcn.common.exception.ResourceNotFoundException;
import com.ttloc.htkhcn.detai.DeTaiService;
import com.ttloc.htkhcn.security.SecurityUser;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

/** Quyet toan la ho so DUY NHAT tren 1 de tai (UNIQUE de_tai_id) - tao/sua
 * dung chung 1 phuong thuc "luu" (upsert) thay vi tach rieng tao/sua nhu cac
 * loai ho so tai chinh khac (du toan nam/tam ung/thanh toan co the co NHIEU
 * dong tren 1 de tai). */
@Service
@RequiredArgsConstructor
public class QuyetToanService {

    private final DeTaiQuyetToanRepository repository;
    private final DeTaiService deTaiService;
    private final EntityManager entityManager;

    @Transactional(readOnly = true)
    public QuyetToanResponse layTheoDeTai(UUID deTaiId) {
        return repository.findByDeTaiId(deTaiId).map(QuyetToanResponse::from).orElse(null);
    }

    @Transactional
    public QuyetToanResponse luu(UUID deTaiId, QuyetToanRequest request) {
        DeTaiQuyetToan q = repository.findByDeTaiId(deTaiId).orElseGet(() -> {
            DeTaiQuyetToan moi = new DeTaiQuyetToan();
            moi.setDeTai(deTaiService.timHoacLoi(deTaiId));
            moi.setNguoiTaoId(currentUserId());
            moi.setNgayTao(Instant.now());
            return moi;
        });
        q.setTongKinhPhiDaCap(request.tongKinhPhiDaCap());
        q.setTongKinhPhiDaSuDung(request.tongKinhPhiDaSuDung());
        q.setNgayQuyetToan(request.ngayQuyetToan());
        q.setGhiChu(request.ghiChu());
        q.setNguoiSuaId(currentUserId());
        q.setNgaySua(Instant.now());
        DeTaiQuyetToan saved = repository.save(q);
        entityManager.flush();
        entityManager.refresh(saved);
        return QuyetToanResponse.from(saved);
    }

    @Transactional
    public QuyetToanResponse duyet(UUID deTaiId, DuyetRequest request) {
        DeTaiQuyetToan q = repository.findByDeTaiId(deTaiId)
                .orElseThrow(() -> new ResourceNotFoundException("De tai chua co ho so quyet toan: " + deTaiId));
        q.setTrangThai(request.trangThai());
        q.setGhiChu(request.ghiChu());
        q.setNguoiDuyetId(currentUserId());
        q.setNgayDuyet(Instant.now());
        DeTaiQuyetToan saved = repository.save(q);
        entityManager.flush();
        entityManager.refresh(saved);
        return QuyetToanResponse.from(saved);
    }

    private UUID currentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof SecurityUser user) {
            return user.getId();
        }
        return null;
    }
}
