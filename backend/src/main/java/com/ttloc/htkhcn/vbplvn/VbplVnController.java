package com.ttloc.htkhcn.vbplvn;

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
@RequestMapping("/api/vbpl-vn")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'EDITOR', 'VIEWER')")
public class VbplVnController {

    private static final String MODULE = "VBPL_VN";

    private final VbplVnService vbplVnService;

    @GetMapping
    public PageResponse<VbplVnResponse> danhSach(
            @RequestParam(required = false) UUID loaiVanBanId,
            @RequestParam(required = false) TinhTrangHieuLuc tinhTrang,
            @RequestParam(required = false) LocalDate tu,
            @RequestParam(required = false) LocalDate den,
            @RequestParam(required = false) String tuKhoa,
            Pageable pageable) {
        return PageResponse.of(vbplVnService.danhSach(loaiVanBanId, tinhTrang, tu, den, tuKhoa, pageable));
    }

    @GetMapping("/{id}")
    public VbplVnResponse chiTiet(@PathVariable UUID id) {
        return vbplVnService.layTheoId(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or @moduleAccess.canEdit(authentication, '" + MODULE + "')")
    public VbplVnResponse tao(@Valid @RequestBody VbplVnRequest request) {
        return vbplVnService.tao(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @moduleAccess.canEdit(authentication, '" + MODULE + "')")
    public VbplVnResponse sua(@PathVariable UUID id, @Valid @RequestBody VbplVnRequest request) {
        return vbplVnService.sua(id, request);
    }

    @PostMapping("/{id}/xac-nhan-doi-chieu")
    @PreAuthorize("hasRole('ADMIN') or @moduleAccess.canEdit(authentication, '" + MODULE + "')")
    public VbplVnResponse xacNhanDoiChieu(@PathVariable UUID id) {
        return vbplVnService.xacNhanDoiChieu(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN') or @moduleAccess.canEdit(authentication, '" + MODULE + "')")
    public void xoa(@PathVariable UUID id) {
        vbplVnService.xoa(id);
    }
}
