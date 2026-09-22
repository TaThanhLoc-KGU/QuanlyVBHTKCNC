package com.ttloc.htkhcn.congvan;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ttloc.htkhcn.common.PageResponse;
import com.ttloc.htkhcn.crawlphapluat.TrangThaiUngVien;
import com.ttloc.htkhcn.vanbandhkg.VanBanDhkgRequest;
import com.ttloc.htkhcn.vanbandhkg.VanBanDhkgResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/congvan-dhkg")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'EDITOR', 'VIEWER')")
public class CongVanImportController {

    private static final String MODULE = "VAN_BAN_DHKG";

    private final CongVanImportService congVanImportService;

    @PostMapping("/dong-bo-ngay")
    @PreAuthorize("hasRole('ADMIN') or @moduleAccess.canEdit(authentication, '" + MODULE + "')")
    public CongVanImportService.DongBoKetQua dongBoNgay() {
        return congVanImportService.dongBoNgay();
    }

    @GetMapping("/ung-vien")
    @PreAuthorize("hasRole('ADMIN') or @moduleAccess.canEdit(authentication, '" + MODULE + "')")
    public PageResponse<CongVanImportService.UngVienDhkgResponse> danhSachUngVien(
            @RequestParam(required = false) TrangThaiUngVien trangThai, Pageable pageable) {
        return PageResponse.of(congVanImportService.danhSachUngVien(trangThai, pageable));
    }

    @PostMapping("/ung-vien/{id}/nhan")
    @PreAuthorize("hasRole('ADMIN') or @moduleAccess.canEdit(authentication, '" + MODULE + "')")
    public VanBanDhkgResponse nhan(@PathVariable UUID id, @Valid @RequestBody VanBanDhkgRequest request) {
        return congVanImportService.nhanUngVien(id, request);
    }

    @PostMapping("/ung-vien/{id}/bo-qua")
    @PreAuthorize("hasRole('ADMIN') or @moduleAccess.canEdit(authentication, '" + MODULE + "')")
    public void boQua(@PathVariable UUID id) {
        congVanImportService.boQuaUngVien(id);
    }
}
