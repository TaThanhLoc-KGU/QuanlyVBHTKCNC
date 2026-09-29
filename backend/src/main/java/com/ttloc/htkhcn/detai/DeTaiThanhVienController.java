package com.ttloc.htkhcn.detai;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ttloc.htkhcn.common.exception.ResourceNotFoundException;
import com.ttloc.htkhcn.danhmuc.TuDien;
import com.ttloc.htkhcn.danhmuc.TuDienRepository;
import com.ttloc.htkhcn.user.NguoiDung;
import com.ttloc.htkhcn.user.NguoiDungRepository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/de-tai/{deTaiId}/thanh-vien")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'EDITOR', 'VIEWER')")
public class DeTaiThanhVienController {

    private static final String MODULE = "DE_TAI_NCKH";

    private final DeTaiThanhVienRepository thanhVienRepository;
    private final DeTaiService deTaiService;
    private final NguoiDungRepository nguoiDungRepository;
    private final TuDienRepository tuDienRepository;
    private final EntityManager entityManager;

    @GetMapping
    public List<DeTaiThanhVienResponse> danhSach(@PathVariable UUID deTaiId) {
        return thanhVienRepository.findByDeTaiIdOrderByThuTuAsc(deTaiId).stream()
                .map(DeTaiThanhVienResponse::from)
                .toList();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or @moduleAccess.canEdit(authentication, '" + MODULE + "')")
    @Transactional
    public DeTaiThanhVienResponse them(@PathVariable UUID deTaiId, @RequestBody DeTaiThanhVienRequest request) {
        DeTai deTai = deTaiService.timHoacLoi(deTaiId);
        DeTaiThanhVien tv = new DeTaiThanhVien();
        tv.setDeTai(deTai);
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
        tv.setThuTu(request.thuTu() != null ? request.thuTu() : 0);
        DeTaiThanhVien saved = thanhVienRepository.save(tv);
        entityManager.flush();
        entityManager.refresh(saved);
        return DeTaiThanhVienResponse.from(saved);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN') or @moduleAccess.canEdit(authentication, '" + MODULE + "')")
    public void xoa(@PathVariable UUID deTaiId, @PathVariable UUID id) {
        DeTaiThanhVien tv = thanhVienRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay thanh vien: " + id));
        thanhVienRepository.delete(tv);
    }
}
