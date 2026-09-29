package com.ttloc.htkhcn.detai;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ttloc.htkhcn.common.PageResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/de-tai")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'EDITOR', 'VIEWER')")
public class DeTaiController {

    private static final String MODULE = "DE_TAI_NCKH";

    private final DeTaiService deTaiService;

    @GetMapping
    public PageResponse<DeTaiResponse> danhSach(
            @RequestParam(required = false) Integer namDeXuat,
            @RequestParam(required = false) TrangThaiDeTai trangThai,
            @RequestParam(required = false) UUID chuNhiemId,
            @RequestParam(required = false) UUID linhVucId,
            @RequestParam(required = false) String tuKhoa,
            Pageable pageable) {
        return PageResponse.of(deTaiService.danhSach(namDeXuat, trangThai, chuNhiemId, linhVucId, tuKhoa, pageable));
    }

    @GetMapping("/{id}")
    public DeTaiResponse chiTiet(@PathVariable UUID id) {
        return deTaiService.layTheoId(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or @moduleAccess.canEdit(authentication, '" + MODULE + "')")
    public DeTaiResponse tao(@Valid @RequestBody DeTaiRequest request) {
        return deTaiService.tao(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @moduleAccess.canEdit(authentication, '" + MODULE + "')")
    public DeTaiResponse sua(@PathVariable UUID id, @Valid @RequestBody DeTaiRequest request) {
        return deTaiService.sua(id, request);
    }

    @PatchMapping("/{id}/trang-thai")
    @PreAuthorize("hasRole('ADMIN') or @moduleAccess.canEdit(authentication, '" + MODULE + "')")
    public DeTaiResponse capNhatTrangThai(@PathVariable UUID id, @Valid @RequestBody CapNhatTrangThaiRequest request) {
        return deTaiService.capNhatTrangThai(id, request.trangThai(), request.lyDoHuy());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN') or @moduleAccess.canEdit(authentication, '" + MODULE + "')")
    public void xoa(@PathVariable UUID id) {
        deTaiService.xoa(id);
    }
}
