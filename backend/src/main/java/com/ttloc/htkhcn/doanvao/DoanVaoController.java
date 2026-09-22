package com.ttloc.htkhcn.doanvao;

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
@RequestMapping("/api/doan-vao")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'EDITOR', 'VIEWER')")
public class DoanVaoController {

    private static final String MODULE = "DOAN_VAO";

    private final DoanVaoService doanVaoService;

    @GetMapping
    public PageResponse<DoanVaoResponse> danhSach(
            @RequestParam(required = false) Integer nam,
            @RequestParam(required = false) UUID doiTacId,
            @RequestParam(required = false) String tuKhoa,
            Pageable pageable) {
        return PageResponse.of(doanVaoService.danhSach(nam, doiTacId, tuKhoa, pageable));
    }

    @GetMapping("/{id}")
    public DoanVaoResponse chiTiet(@PathVariable UUID id) {
        return doanVaoService.layTheoId(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or @moduleAccess.canEdit(authentication, '" + MODULE + "')")
    public DoanVaoResponse tao(@Valid @RequestBody DoanVaoRequest request) {
        return doanVaoService.tao(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @moduleAccess.canEdit(authentication, '" + MODULE + "')")
    public DoanVaoResponse sua(@PathVariable UUID id, @Valid @RequestBody DoanVaoRequest request) {
        return doanVaoService.sua(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN') or @moduleAccess.canEdit(authentication, '" + MODULE + "')")
    public void xoa(@PathVariable UUID id) {
        doanVaoService.xoa(id);
    }
}
