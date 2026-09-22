package com.ttloc.htkhcn.congvan;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ttloc.htkhcn.common.PageResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cong-van-den")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'EDITOR', 'VIEWER')")
public class CongVanDenController {

    private static final String MODULE = "CONG_VAN_DEN";

    private final CongVanDenSyncService congVanDenSyncService;

    @PostMapping("/dong-bo-ngay")
    @PreAuthorize("hasRole('ADMIN') or @moduleAccess.canEdit(authentication, '" + MODULE + "')")
    public CongVanDenSyncService.DongBoKetQua dongBoNgay() {
        return congVanDenSyncService.dongBoNgay();
    }

    @GetMapping
    public PageResponse<CongVanDenSyncService.CongVanDenResponse> danhSach(
            @RequestParam(required = false) String trangThai,
            @RequestParam(required = false) Integer nam,
            @RequestParam(required = false) String tuKhoa,
            Pageable pageable) {
        return PageResponse.of(congVanDenSyncService.danhSach(trangThai, nam, tuKhoa, pageable));
    }

    @GetMapping("/{id}")
    public CongVanDenSyncService.CongVanDenResponse chiTiet(@PathVariable UUID id) {
        return congVanDenSyncService.chiTiet(id);
    }
}
