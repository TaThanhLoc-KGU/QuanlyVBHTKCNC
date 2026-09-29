package com.ttloc.htkhcn.detaitaichinh;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ttloc.htkhcn.common.exception.DuplicateResourceException;
import com.ttloc.htkhcn.common.exception.ResourceNotFoundException;
import com.ttloc.htkhcn.detai.DeTaiService;
import com.ttloc.htkhcn.security.SecurityUser;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DuToanNamService {

    private final DeTaiDuToanNamRepository repository;
    private final DeTaiService deTaiService;
    private final EntityManager entityManager;

    @Transactional(readOnly = true)
    public List<DuToanNamResponse> danhSach(UUID deTaiId) {
        return repository.findByDeTaiIdOrderByNamAsc(deTaiId).stream().map(DuToanNamResponse::from).toList();
    }

    @Transactional
    public DuToanNamResponse tao(UUID deTaiId, DuToanNamRequest request) {
        var deTai = deTaiService.timHoacLoi(deTaiId);
        if (repository.findByDeTaiIdOrderByNamAsc(deTaiId).stream().anyMatch(d -> d.getNam().equals(request.nam()))) {
            throw new DuplicateResourceException("De tai da co du toan cho nam " + request.nam());
        }
        DeTaiDuToanNam d = new DeTaiDuToanNam();
        d.setDeTai(deTai);
        gan(d, request);
        d.setNguoiTaoId(currentUserId());
        d.setNgayTao(Instant.now());
        DeTaiDuToanNam saved = repository.save(d);
        entityManager.flush();
        entityManager.refresh(saved);
        return DuToanNamResponse.from(saved);
    }

    @Transactional
    public DuToanNamResponse sua(UUID id, DuToanNamRequest request) {
        DeTaiDuToanNam d = timHoacLoi(id);
        gan(d, request);
        d.setNguoiSuaId(currentUserId());
        d.setNgaySua(Instant.now());
        DeTaiDuToanNam saved = repository.save(d);
        entityManager.flush();
        entityManager.refresh(saved);
        return DuToanNamResponse.from(saved);
    }

    @Transactional
    public DuToanNamResponse duyet(UUID id, DuyetRequest request) {
        DeTaiDuToanNam d = timHoacLoi(id);
        d.setTrangThai(request.trangThai());
        d.setGhiChu(request.ghiChu());
        d.setNguoiDuyetId(currentUserId());
        d.setNgayDuyet(Instant.now());
        DeTaiDuToanNam saved = repository.save(d);
        entityManager.flush();
        entityManager.refresh(saved);
        return DuToanNamResponse.from(saved);
    }

    @Transactional
    public void xoa(UUID id) {
        repository.delete(timHoacLoi(id));
    }

    private void gan(DeTaiDuToanNam d, DuToanNamRequest request) {
        d.setNam(request.nam());
        d.setKinhPhiDeXuat(request.kinhPhiDeXuat());
        d.setKinhPhiDuyet(request.kinhPhiDuyet());
        d.setDaGuiBo(request.daGuiBo() != null && request.daGuiBo());
        d.setGhiChu(request.ghiChu());
    }

    private DeTaiDuToanNam timHoacLoi(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay du toan nam: " + id));
    }

    private UUID currentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof SecurityUser user) {
            return user.getId();
        }
        return null;
    }
}
