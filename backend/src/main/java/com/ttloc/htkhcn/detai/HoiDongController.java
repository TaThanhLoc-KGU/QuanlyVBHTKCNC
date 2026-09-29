package com.ttloc.htkhcn.detai;

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
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'EDITOR', 'VIEWER')")
public class HoiDongController {

    private static final String MODULE = "DE_TAI_NCKH";

    private final HoiDongService hoiDongService;

    @GetMapping("/api/de-tai/{deTaiId}/hoi-dong")
    public List<HoiDongResponse> danhSach(@PathVariable UUID deTaiId) {
        return hoiDongService.danhSach(deTaiId);
    }

    @PostMapping("/api/de-tai/{deTaiId}/hoi-dong")
    @PreAuthorize("hasRole('ADMIN') or @moduleAccess.canEdit(authentication, '" + MODULE + "')")
    public HoiDongResponse tao(@PathVariable UUID deTaiId, @Valid @RequestBody HoiDongRequest request) {
        return hoiDongService.tao(deTaiId, request);
    }

    @PutMapping("/api/hoi-dong/{id}")
    @PreAuthorize("hasRole('ADMIN') or @moduleAccess.canEdit(authentication, '" + MODULE + "')")
    public HoiDongResponse sua(@PathVariable UUID id, @Valid @RequestBody HoiDongRequest request) {
        return hoiDongService.sua(id, request);
    }

    @DeleteMapping("/api/hoi-dong/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN') or @moduleAccess.canEdit(authentication, '" + MODULE + "')")
    public void xoa(@PathVariable UUID id) {
        hoiDongService.xoa(id);
    }

    @GetMapping("/api/hoi-dong/{hoiDongId}/thanh-vien")
    public List<HoiDongThanhVienResponse> danhSachThanhVien(@PathVariable UUID hoiDongId) {
        return hoiDongService.danhSachThanhVien(hoiDongId);
    }

    @PostMapping("/api/hoi-dong/{hoiDongId}/thanh-vien")
    @PreAuthorize("hasRole('ADMIN') or @moduleAccess.canEdit(authentication, '" + MODULE + "')")
    public HoiDongThanhVienResponse themThanhVien(@PathVariable UUID hoiDongId, @RequestBody HoiDongThanhVienRequest request) {
        return hoiDongService.themThanhVien(hoiDongId, request);
    }

    @DeleteMapping("/api/hoi-dong/thanh-vien/{thanhVienId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN') or @moduleAccess.canEdit(authentication, '" + MODULE + "')")
    public void xoaThanhVien(@PathVariable UUID thanhVienId) {
        hoiDongService.xoaThanhVien(thanhVienId);
    }

    // Ghi y kien: MOI nguoi dung dang nhap deu goi duoc endpoint nay (khong
    // gioi han Editor/Admin o day) - HoiDongService.ghiYKien tu kiem tra ben
    // trong CHI cho phep chinh chu (hoac Admin/Editor) ghi de.
    @PutMapping("/api/hoi-dong/thanh-vien/{thanhVienId}/y-kien")
    public HoiDongThanhVienResponse ghiYKien(@PathVariable UUID thanhVienId, @RequestBody YKienHoiDongRequest request) {
        return hoiDongService.ghiYKien(thanhVienId, request);
    }
}
