package com.ttloc.htkhcn.mou;

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

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/mou")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'EDITOR', 'VIEWER')")
public class MouController {

    private static final String MODULE = "MOU";

    private final MouService mouService;

    @GetMapping
    public PageResponse<MouResponse> danhSach(
            @RequestParam(required = false) UUID doiTacId,
            @RequestParam(required = false) TrangThaiMou trangThai,
            @RequestParam(required = false) LocalDate tu,
            @RequestParam(required = false) LocalDate den,
            @RequestParam(required = false) String tuKhoa,
            Pageable pageable) {
        return PageResponse.of(mouService.danhSach(doiTacId, trangThai, tu, den, tuKhoa, pageable));
    }

    @GetMapping("/{id}")
    public MouResponse chiTiet(@PathVariable UUID id) {
        return mouService.layTheoId(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or @moduleAccess.canEdit(authentication, '" + MODULE + "')")
    public MouResponse tao(@Valid @RequestBody MouRequest request) {
        return mouService.tao(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @moduleAccess.canEdit(authentication, '" + MODULE + "')")
    public MouResponse sua(@PathVariable UUID id, @Valid @RequestBody MouRequest request) {
        return mouService.sua(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN') or @moduleAccess.canEdit(authentication, '" + MODULE + "')")
    public void xoa(@PathVariable UUID id) {
        mouService.xoa(id);
    }
}
