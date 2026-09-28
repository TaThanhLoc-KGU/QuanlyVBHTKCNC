package com.ttloc.htkhcn.mauemail;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/mau-email")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'EDITOR', 'VIEWER')")
public class MauEmailController {

    private static final String MODULE = "MAU_EMAIL";

    private final MauEmailService mauEmailService;

    @GetMapping
    public List<MauEmail> danhSach() {
        return mauEmailService.danhSach();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or @moduleAccess.canEdit(authentication, '" + MODULE + "')")
    public MauEmail tao(@Valid @RequestBody MauEmailRequest request) {
        return mauEmailService.tao(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @moduleAccess.canEdit(authentication, '" + MODULE + "')")
    public MauEmail sua(@PathVariable UUID id, @Valid @RequestBody MauEmailRequest request) {
        return mauEmailService.sua(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN') or @moduleAccess.canEdit(authentication, '" + MODULE + "')")
    public void xoa(@PathVariable UUID id) {
        mauEmailService.xoa(id);
    }
}
