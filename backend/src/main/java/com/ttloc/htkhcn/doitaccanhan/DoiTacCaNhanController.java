package com.ttloc.htkhcn.doitaccanhan;

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
@RequestMapping("/api/doi-tac-ca-nhan")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'EDITOR', 'VIEWER')")
public class DoiTacCaNhanController {

    private static final String MODULE = "DOI_TAC_CA_NHAN";

    private final DoiTacCaNhanService doiTacCaNhanService;

    @GetMapping
    public List<DoiTacCaNhanResponse> danhSach() {
        return doiTacCaNhanService.danhSach();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or @moduleAccess.canEdit(authentication, '" + MODULE + "')")
    public DoiTacCaNhanResponse tao(@Valid @RequestBody DoiTacCaNhanRequest request) {
        return doiTacCaNhanService.tao(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @moduleAccess.canEdit(authentication, '" + MODULE + "')")
    public DoiTacCaNhanResponse sua(@PathVariable UUID id, @Valid @RequestBody DoiTacCaNhanRequest request) {
        return doiTacCaNhanService.sua(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN') or @moduleAccess.canEdit(authentication, '" + MODULE + "')")
    public void xoa(@PathVariable UUID id) {
        doiTacCaNhanService.xoa(id);
    }
}
