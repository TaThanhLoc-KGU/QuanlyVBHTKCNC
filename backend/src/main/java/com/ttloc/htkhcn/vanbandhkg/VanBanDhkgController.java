package com.ttloc.htkhcn.vanbandhkg;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ttloc.htkhcn.common.PageResponse;
import com.ttloc.htkhcn.common.TinhTrangHieuLuc;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/van-ban-dhkg")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'EDITOR', 'VIEWER')")
public class VanBanDhkgController {

    private static final String MODULE = "VAN_BAN_DHKG";

    private final VanBanDhkgService vanBanDhkgService;

    @GetMapping
    public PageResponse<VanBanDhkgResponse> danhSach(
            @RequestParam(required = false) UUID loaiVanBanId,
            @RequestParam(required = false) TinhTrangHieuLuc tinhTrang,
            @RequestParam(required = false) LocalDate tu,
            @RequestParam(required = false) LocalDate den,
            @RequestParam(required = false) String tuKhoa,
            Pageable pageable) {
        return PageResponse.of(vanBanDhkgService.danhSach(loaiVanBanId, tinhTrang, tu, den, tuKhoa, pageable));
    }

    @GetMapping("/{id}")
    public VanBanDhkgResponse chiTiet(@PathVariable UUID id) {
        return vanBanDhkgService.layTheoId(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or @moduleAccess.canEdit(authentication, '" + MODULE + "')")
    public VanBanDhkgResponse tao(@Valid @RequestBody VanBanDhkgRequest request) {
        return vanBanDhkgService.tao(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @moduleAccess.canEdit(authentication, '" + MODULE + "')")
    public VanBanDhkgResponse sua(@PathVariable UUID id, @Valid @RequestBody VanBanDhkgRequest request) {
        return vanBanDhkgService.sua(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN') or @moduleAccess.canEdit(authentication, '" + MODULE + "')")
    public void xoa(@PathVariable UUID id) {
        vanBanDhkgService.xoa(id);
    }
}
