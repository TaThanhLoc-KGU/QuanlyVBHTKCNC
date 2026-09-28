package com.ttloc.htkhcn.doandiaphuong;

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

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/doan-dia-phuong")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'EDITOR', 'VIEWER')")
public class DoanDiaPhuongController {

    private static final String MODULE = "DOAN_DIA_PHUONG";

    private final DoanDiaPhuongService doanDiaPhuongService;

    @GetMapping
    public PageResponse<DoanDiaPhuongResponse> danhSach(
            @RequestParam(required = false) Integer nam,
            @RequestParam(required = false) UUID doiTacId,
            @RequestParam(required = false) String tuKhoa,
            Pageable pageable) {
        return PageResponse.of(doanDiaPhuongService.danhSach(nam, doiTacId, tuKhoa, pageable));
    }

    @GetMapping("/{id}")
    public DoanDiaPhuongResponse chiTiet(@PathVariable UUID id) {
        return doanDiaPhuongService.layTheoId(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or @moduleAccess.canEdit(authentication, '" + MODULE + "')")
    public DoanDiaPhuongResponse tao(@Valid @RequestBody DoanDiaPhuongRequest request) {
        return doanDiaPhuongService.tao(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @moduleAccess.canEdit(authentication, '" + MODULE + "')")
    public DoanDiaPhuongResponse sua(@PathVariable UUID id, @Valid @RequestBody DoanDiaPhuongRequest request) {
        return doanDiaPhuongService.sua(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN') or @moduleAccess.canEdit(authentication, '" + MODULE + "')")
    public void xoa(@PathVariable UUID id) {
        doanDiaPhuongService.xoa(id);
    }
}
