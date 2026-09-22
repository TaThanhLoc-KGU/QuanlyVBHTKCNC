package com.ttloc.htkhcn.baocao;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ttloc.htkhcn.common.PageResponse;
import com.ttloc.htkhcn.security.SecurityUser;

import lombok.RequiredArgsConstructor;

/** 3 bao cao chuyen de (SPEC muc 4.8) - xem JSON hoac xuat Excel/PDF. */
@RestController
@RequestMapping("/api/bao-cao")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'EDITOR', 'VIEWER')")
public class BaoCaoController {

    private final BaoCaoService baoCaoService;
    private final BaoCaoExportService baoCaoExportService;
    private final LichSuXuatBaoCaoService lichSuXuatBaoCaoService;

    @GetMapping("/mou-trong-nam")
    public ResponseEntity<?> mouTrongNam(
            @RequestParam(required = false) Integer nam,
            @RequestParam(defaultValue = "JSON  ") DinhDangBaoCao dinhDang,
            @AuthenticationPrincipal SecurityUser currentUser) {
        int namThucTe = nam != null ? nam : LocalDate.now().getYear();
        var bc = baoCaoService.mouTrongNam(namThucTe);

        if (dinhDang == DinhDangBaoCao.JSON) {
            return ResponseEntity.ok(bc);
        }
        lichSuXuatBaoCaoService.ghiLai("MOU_TRONG_NAM", currentUser.getId(), dinhDang,
                java.util.Map.of("nam", String.valueOf(namThucTe)));
        byte[] file = dinhDang == DinhDangBaoCao.EXCEL
                ? baoCaoExportService.xuatExcelMouTrongNam(bc, currentUser.getHoTen())
                : baoCaoExportService.xuatPdfMouTrongNam(bc, currentUser.getHoTen());
        return tepDinhKem(file, "bao-cao-mou-nam-" + namThucTe, dinhDang);
    }

    @GetMapping("/doan-ra-vao")
    public ResponseEntity<?> doanRaVao(
            @RequestParam(required = false) LocalDate tu,
            @RequestParam(required = false) LocalDate den,
            @RequestParam(required = false) UUID doiTacId,
            @RequestParam(defaultValue = "JSON") DinhDangBaoCao dinhDang,
            @AuthenticationPrincipal SecurityUser currentUser) {
        var bc = baoCaoService.doanRaVao(tu, den, doiTacId);

        if (dinhDang == DinhDangBaoCao.JSON) {
            return ResponseEntity.ok(bc);
        }
        lichSuXuatBaoCaoService.ghiLai("DOAN_RA_VAO", currentUser.getId(), dinhDang,
                java.util.Map.of("tu", String.valueOf(tu), "den", String.valueOf(den)));
        byte[] file = dinhDang == DinhDangBaoCao.EXCEL
                ? baoCaoExportService.xuatExcelDoanRaVao(bc, currentUser.getHoTen())
                : baoCaoExportService.xuatPdfDoanRaVao(bc, currentUser.getHoTen());
        return tepDinhKem(file, "bao-cao-doan-ra-vao", dinhDang);
    }

    @GetMapping("/thoi-han-mou")
    public ResponseEntity<?> thoiHanMou(
            @RequestParam(required = false) Integer thangToi,
            @RequestParam(defaultValue = "JSON") DinhDangBaoCao dinhDang,
            @AuthenticationPrincipal SecurityUser currentUser) {
        var ds = baoCaoService.thoiHanMouTheoDoiTac(thangToi);

        if (dinhDang == DinhDangBaoCao.JSON) {
            return ResponseEntity.ok(ds);
        }
        lichSuXuatBaoCaoService.ghiLai("THOI_HAN_MOU", currentUser.getId(), dinhDang,
                java.util.Map.of("thangToi", String.valueOf(thangToi)));
        byte[] file = dinhDang == DinhDangBaoCao.EXCEL
                ? baoCaoExportService.xuatExcelThoiHanMou(ds, currentUser.getHoTen())
                : baoCaoExportService.xuatPdfThoiHanMou(ds, currentUser.getHoTen());
        return tepDinhKem(file, "bao-cao-thoi-han-mou", dinhDang);
    }

    @GetMapping("/lich-su-xuat")
    @PreAuthorize("hasRole('ADMIN')")
    public PageResponse<LichSuXuatBaoCao> lichSuXuat(Pageable pageable) {
        return PageResponse.of(lichSuXuatBaoCaoService.danhSach(pageable));
    }

    private ResponseEntity<byte[]> tepDinhKem(byte[] file, String tenGoc, DinhDangBaoCao dinhDang) {
        String duoi = dinhDang == DinhDangBaoCao.EXCEL ? ".xlsx" : ".pdf";
        MediaType loai = dinhDang == DinhDangBaoCao.EXCEL
                ? MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                : MediaType.APPLICATION_PDF;
        return ResponseEntity.ok()
                .contentType(loai)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(tenGoc + duoi).build().toString())
                .body(file);
    }
}
