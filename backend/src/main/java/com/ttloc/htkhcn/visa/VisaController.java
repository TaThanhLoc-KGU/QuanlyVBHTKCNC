package com.ttloc.htkhcn.visa;

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
@RequestMapping("/api/visa")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'EDITOR', 'VIEWER')")
public class VisaController {

    private static final String MODULE = "VISA";

    private final VisaService visaService;

    @GetMapping
    public PageResponse<VisaResponse> danhSach(
            @RequestParam(required = false) Integer nam,
            @RequestParam(required = false) LoaiCapVisa loaiCap,
            @RequestParam(required = false) String tuKhoa,
            Pageable pageable) {
        return PageResponse.of(visaService.danhSach(nam, loaiCap, tuKhoa, pageable));
    }

    @GetMapping("/{id}")
    public VisaResponse chiTiet(@PathVariable UUID id) {
        return visaService.layTheoId(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or @moduleAccess.canEdit(authentication, '" + MODULE + "')")
    public VisaResponse tao(@Valid @RequestBody VisaRequest request) {
        return visaService.tao(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @moduleAccess.canEdit(authentication, '" + MODULE + "')")
    public VisaResponse sua(@PathVariable UUID id, @Valid @RequestBody VisaRequest request) {
        return visaService.sua(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN') or @moduleAccess.canEdit(authentication, '" + MODULE + "')")
    public void xoa(@PathVariable UUID id) {
        visaService.xoa(id);
    }
}
