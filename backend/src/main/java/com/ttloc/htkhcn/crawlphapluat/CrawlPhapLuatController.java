package com.ttloc.htkhcn.crawlphapluat;

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
import com.ttloc.htkhcn.vbplvn.VbplVnRequest;
import com.ttloc.htkhcn.vbplvn.VbplVnResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/crawl-phap-luat")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'EDITOR', 'VIEWER')")
public class CrawlPhapLuatController {

    private static final String MODULE = "VBPL_VN";

    private final CrawlPhapLuatService crawlPhapLuatService;

    @PostMapping("/chay-ngay")
    @PreAuthorize("hasRole('ADMIN')")
    public CrawlKetQuaResponse chayNgay() {
        return crawlPhapLuatService.chayCrawlNgay();
    }

    @GetMapping("/ung-vien")
    @PreAuthorize("hasRole('ADMIN') or @moduleAccess.canEdit(authentication, '" + MODULE + "')")
    public PageResponse<UngVienResponse> danhSachUngVien(
            @RequestParam(required = false) TrangThaiUngVien trangThai, Pageable pageable) {
        return PageResponse.of(crawlPhapLuatService.danhSachUngVien(trangThai, pageable));
    }

    @PostMapping("/ung-vien/{id}/nhan")
    @PreAuthorize("hasRole('ADMIN') or @moduleAccess.canEdit(authentication, '" + MODULE + "')")
    public VbplVnResponse nhan(@PathVariable UUID id, @Valid @RequestBody VbplVnRequest request) {
        return crawlPhapLuatService.nhanUngVien(id, request);
    }

    @PostMapping("/ung-vien/{id}/bo-qua")
    @PreAuthorize("hasRole('ADMIN') or @moduleAccess.canEdit(authentication, '" + MODULE + "')")
    public void boQua(@PathVariable UUID id) {
        crawlPhapLuatService.boQuaUngVien(id);
    }

    @GetMapping("/tu-khoa")
    @PreAuthorize("hasRole('ADMIN')")
    public List<TuKhoaResponse> danhSachTuKhoa() {
        return crawlPhapLuatService.danhSachTuKhoa();
    }

    @PostMapping("/tu-khoa")
    @PreAuthorize("hasRole('ADMIN')")
    public TuKhoaResponse themTuKhoa(@Valid @RequestBody TuKhoaRequest request) {
        return crawlPhapLuatService.themTuKhoa(request);
    }

    @PutMapping("/tu-khoa/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public TuKhoaResponse doiTrangThaiTuKhoa(@PathVariable UUID id, @RequestParam boolean hoatDong) {
        return crawlPhapLuatService.doiTrangThaiTuKhoa(id, hoatDong);
    }

    @DeleteMapping("/tu-khoa/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void xoaTuKhoa(@PathVariable UUID id) {
        crawlPhapLuatService.xoaTuKhoa(id);
    }
}
