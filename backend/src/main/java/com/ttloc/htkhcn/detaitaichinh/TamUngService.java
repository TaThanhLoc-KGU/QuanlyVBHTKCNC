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
public class TamUngService {

    private final DeTaiTamUngRepository repository;
    private final DeTaiService deTaiService;
    private final EntityManager entityManager;

    @Transactional(readOnly = true)
    public List<TamUngResponse> danhSach(UUID deTaiId) {
        return repository.findByDeTaiIdOrderByNgayDeNghiDesc(deTaiId).stream().map(TamUngResponse::from).toList();
    }

    @Transactional
    public TamUngResponse tao(UUID deTaiId, TamUngRequest request) {
        var deTai = deTaiService.timHoacLoi(deTaiId);
        DeTaiTamUng t = new DeTaiTamUng();
        t.setDeTai(deTai);
        gan(t, request);
        t.setNguoiTaoId(currentUserId());
        t.setNgayTao(Instant.now());
        DeTaiTamUng saved = repository.save(t);
        entityManager.flush();
        entityManager.refresh(saved);
        return TamUngResponse.from(saved);
    }

    @Transactional
    public TamUngResponse sua(UUID id, TamUngRequest request) {
        DeTaiTamUng t = timHoacLoi(id);
        gan(t, request);
        t.setNguoiSuaId(currentUserId());
        t.setNgaySua(Instant.now());
        DeTaiTamUng saved = repository.save(t);
        entityManager.flush();
        entityManager.refresh(saved);
        return TamUngResponse.from(saved);
    }

    @Transactional
    public TamUngResponse duyet(UUID id, DuyetRequest request) {
        DeTaiTamUng t = timHoacLoi(id);
        t.setTrangThai(request.trangThai());
        t.setGhiChu(request.ghiChu());
        t.setNguoiDuyetId(currentUserId());
        t.setNgayDuyet(Instant.now());
        DeTaiTamUng saved = repository.save(t);
        entityManager.flush();
        entityManager.refresh(saved);
        return TamUngResponse.from(saved);
    }

    @Transactional
    public void xoa(UUID id) {
        repository.delete(timHoacLoi(id));
    }

    private void gan(DeTaiTamUng t, TamUngRequest request) {
        t.setNam(request.nam());
        t.setSoTien(request.soTien());
        t.setLyDo(request.lyDo());
        t.setNgayDeNghi(request.ngayDeNghi());
        t.setGhiChu(request.ghiChu());
    }

    private DeTaiTamUng timHoacLoi(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay de nghi tam ung: " + id));
    }

    private UUID currentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof SecurityUser user) {
            return user.getId();
        }
        return null;
    }
}
