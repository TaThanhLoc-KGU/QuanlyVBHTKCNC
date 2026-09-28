package com.ttloc.htkhcn.sukien;

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
@RequestMapping("/api/su-kien")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'EDITOR', 'VIEWER')")
public class SuKienController {

    private static final String MODULE = "SU_KIEN";

    private final SuKienService suKienService;

    @GetMapping
    public PageResponse<SuKienResponse> danhSach(
            @RequestParam(required = false) Integer nam,
            @RequestParam(required = false) UUID loaiSuKienTuDienId,
            @RequestParam(required = false) String tuKhoa,
            Pageable pageable) {
        return PageResponse.of(suKienService.danhSach(nam, loaiSuKienTuDienId, tuKhoa, pageable));
    }

    @GetMapping("/{id}")
    public SuKienResponse chiTiet(@PathVariable UUID id) {
        return suKienService.layTheoId(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or @moduleAccess.canEdit(authentication, '" + MODULE + "')")
    public SuKienResponse tao(@Valid @RequestBody SuKienRequest request) {
        return suKienService.tao(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @moduleAccess.canEdit(authentication, '" + MODULE + "')")
    public SuKienResponse sua(@PathVariable UUID id, @Valid @RequestBody SuKienRequest request) {
        return suKienService.sua(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN') or @moduleAccess.canEdit(authentication, '" + MODULE + "')")
    public void xoa(@PathVariable UUID id) {
        suKienService.xoa(id);
    }
}
