package com.ttloc.htkhcn.thongbao;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ttloc.htkhcn.common.PageResponse;
import com.ttloc.htkhcn.security.SecurityUser;

import lombok.RequiredArgsConstructor;

/** Chuong thong bao web (SPEC muc 4.7.2) - moi nguoi chi thay thong bao cua minh. */
@RestController
@RequestMapping("/api/thong-bao")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'EDITOR', 'VIEWER')")
public class ThongBaoController {

    private final ThongBaoService thongBaoService;

    @GetMapping
    public PageResponse<ThongBao> danhSach(
            @RequestParam(required = false) Boolean chiChuaDoc,
            Pageable pageable,
            @AuthenticationPrincipal SecurityUser currentUser) {
        return PageResponse.of(thongBaoService.danhSach(currentUser.getId(), chiChuaDoc, pageable));
    }

    @GetMapping("/chua-doc-so-luong")
    public SoLuongResponse soLuongChuaDoc(@AuthenticationPrincipal SecurityUser currentUser) {
        return new SoLuongResponse(thongBaoService.soLuongChuaDoc(currentUser.getId()));
    }

    @PostMapping("/{id}/danh-dau-da-doc")
    public void danhDauDaDoc(@PathVariable UUID id, @AuthenticationPrincipal SecurityUser currentUser) {
        thongBaoService.danhDauDaDoc(id, currentUser.getId());
    }

    @PostMapping("/danh-dau-tat-ca-da-doc")
    public void danhDauTatCaDaDoc(@AuthenticationPrincipal SecurityUser currentUser) {
        thongBaoService.danhDauTatCaDaDoc(currentUser.getId());
    }

    public record SoLuongResponse(long soLuong) {
    }
}
