package com.ttloc.htkhcn.detai;

import java.util.List;
import java.util.UUID;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/linh-vuc-nghien-cuu")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'EDITOR', 'VIEWER')")
public class LinhVucNghienCuuController {

    private static final String MODULE = "DE_TAI_NCKH";

    private final LinhVucNghienCuuService service;

    @GetMapping
    public List<LinhVucNghienCuuResponse> danhSach() {
        return service.danhSach();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or @moduleAccess.canEdit(authentication, '" + MODULE + "')")
    public LinhVucNghienCuuResponse tao(@Valid @RequestBody LinhVucNghienCuuRequest request) {
        return service.tao(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @moduleAccess.canEdit(authentication, '" + MODULE + "')")
    public LinhVucNghienCuuResponse sua(@PathVariable UUID id, @Valid @RequestBody LinhVucNghienCuuRequest request) {
        return service.sua(id, request);
    }
}
