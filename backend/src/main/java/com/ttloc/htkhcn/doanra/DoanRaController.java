package com.ttloc.htkhcn.doanra;

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
@RequestMapping("/api/doan-ra")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'EDITOR', 'VIEWER')")
public class DoanRaController {

    private static final String MODULE = "DOAN_RA";

    private final DoanRaService doanRaService;

    @GetMapping
    public PageResponse<DoanRaResponse> danhSach(
            @RequestParam(required = false) Integer nam,
            @RequestParam(required = false) UUID doiTacId,
            @RequestParam(required = false) String tuKhoa,
            Pageable pageable) {
        return PageResponse.of(doanRaService.danhSach(nam, doiTacId, tuKhoa, pageable));
    }

    @GetMapping("/{id}")
    public DoanRaResponse chiTiet(@PathVariable UUID id) {
        return doanRaService.layTheoId(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or @moduleAccess.canEdit(authentication, '" + MODULE + "')")
    public DoanRaResponse tao(@Valid @RequestBody DoanRaRequest request) {
        return doanRaService.tao(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @moduleAccess.canEdit(authentication, '" + MODULE + "')")
    public DoanRaResponse sua(@PathVariable UUID id, @Valid @RequestBody DoanRaRequest request) {
        return doanRaService.sua(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN') or @moduleAccess.canEdit(authentication, '" + MODULE + "')")
    public void xoa(@PathVariable UUID id) {
        doanRaService.xoa(id);
    }
}
