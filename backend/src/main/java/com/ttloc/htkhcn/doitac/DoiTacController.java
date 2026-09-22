package com.ttloc.htkhcn.doitac;

import java.util.List;
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
@RequestMapping("/api/doi-tac")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'EDITOR', 'VIEWER')")
public class DoiTacController {

    private static final String MODULE = "DOI_TAC";

    private final DoiTacService doiTacService;

    @GetMapping
    public PageResponse<DoiTacResponse> danhSach(
            @RequestParam(required = false) LoaiDoiTac loaiDoiTac,
            @RequestParam(required = false) String quocGia,
            @RequestParam(required = false) String tuKhoa,
            Pageable pageable) {
        return PageResponse.of(doiTacService.danhSach(loaiDoiTac, quocGia, tuKhoa, pageable));
    }

    @GetMapping("/{id}")
    public DoiTacResponse chiTiet(@PathVariable UUID id) {
        return doiTacService.layTheoId(id);
    }

    @GetMapping("/goi-y-trung-ten")
    public List<DoiTacResponse> goiYTrungTen(@RequestParam String ten) {
        return doiTacService.goiYTrungTen(ten);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or @moduleAccess.canEdit(authentication, '" + MODULE + "')")
    public DoiTacResponse tao(@Valid @RequestBody DoiTacRequest request) {
        return doiTacService.tao(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @moduleAccess.canEdit(authentication, '" + MODULE + "')")
    public DoiTacResponse sua(@PathVariable UUID id, @Valid @RequestBody DoiTacRequest request) {
        return doiTacService.sua(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN') or @moduleAccess.canEdit(authentication, '" + MODULE + "')")
    public void xoa(@PathVariable UUID id) {
        doiTacService.xoa(id);
    }

    @PostMapping("/{id}/khoi-phuc")
    @PreAuthorize("hasRole('ADMIN')")
    public void khoiPhuc(@PathVariable UUID id) {
        doiTacService.khoiPhuc(id);
    }
}
