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
public class BaoCaoTienDoService {

    private final DeTaiBaoCaoTienDoRepository repository;
    private final DeTaiService deTaiService;
    private final EntityManager entityManager;

    @Transactional(readOnly = true)
    public List<BaoCaoTienDoResponse> danhSach(UUID deTaiId) {
        return repository.findByDeTaiIdOrderByHanNopDesc(deTaiId).stream().map(BaoCaoTienDoResponse::from).toList();
    }

    @Transactional
    public BaoCaoTienDoResponse tao(UUID deTaiId, BaoCaoTienDoRequest request) {
        var deTai = deTaiService.timHoacLoi(deTaiId);
        DeTaiBaoCaoTienDo b = new DeTaiBaoCaoTienDo();
        b.setDeTai(deTai);
        gan(b, request);
        b.setNguoiTaoId(currentUserId());
        b.setNgayTao(Instant.now());
        DeTaiBaoCaoTienDo saved = repository.save(b);
        entityManager.flush();
        entityManager.refresh(saved);
        return BaoCaoTienDoResponse.from(saved);
    }

    @Transactional
    public BaoCaoTienDoResponse sua(UUID id, BaoCaoTienDoRequest request) {
        DeTaiBaoCaoTienDo b = timHoacLoi(id);
        gan(b, request);
        b.setNguoiSuaId(currentUserId());
        b.setNgaySua(Instant.now());
        DeTaiBaoCaoTienDo saved = repository.save(b);
        entityManager.flush();
        entityManager.refresh(saved);
        return BaoCaoTienDoResponse.from(saved);
    }

    @Transactional
    public BaoCaoTienDoResponse duyet(UUID id, DuyetRequest request) {
        DeTaiBaoCaoTienDo b = timHoacLoi(id);
        b.setTrangThai(request.trangThai());
        b.setGhiChu(request.ghiChu());
        b.setNguoiDuyetId(currentUserId());
        b.setNgayDuyet(Instant.now());
        DeTaiBaoCaoTienDo saved = repository.save(b);
        entityManager.flush();
        entityManager.refresh(saved);
        return BaoCaoTienDoResponse.from(saved);
    }

    @Transactional
    public void xoa(UUID id) {
        repository.delete(timHoacLoi(id));
    }

    private void gan(DeTaiBaoCaoTienDo b, BaoCaoTienDoRequest request) {
        b.setKyBaoCao(request.kyBaoCao());
        b.setHanNop(request.hanNop());
        b.setNgayNop(request.ngayNop());
        b.setNoiDung(request.noiDung());
        b.setGhiChu(request.ghiChu());
    }

    private DeTaiBaoCaoTienDo timHoacLoi(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay bao cao tien do: " + id));
    }

    private UUID currentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof SecurityUser user) {
            return user.getId();
        }
        return null;
    }
}
