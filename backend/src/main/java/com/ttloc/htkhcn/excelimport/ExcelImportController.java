package com.ttloc.htkhcn.excelimport;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ttloc.htkhcn.common.ModuleKey;
import com.ttloc.htkhcn.security.SecurityUser;

import lombok.RequiredArgsConstructor;

/** Import Excel cho 6 module (SPEC muc 4.3). */
@RestController
@RequestMapping("/api/import")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
public class ExcelImportController {

    private final ExcelImportService excelImportService;

    @PostMapping("/{moDun}/xem-truoc")
    public ImportPreviewResponse xemTruoc(
            @PathVariable ModuleKey moDun,
            @RequestParam MultipartFile file,
            @AuthenticationPrincipal SecurityUser currentUser) {
        kiemTraQuyen(moDun, currentUser);
        return excelImportService.xemTruoc(moDun, file);
    }

    @PostMapping("/{moDun}/xac-nhan")
    public ImportConfirmResponse xacNhan(
            @PathVariable ModuleKey moDun,
            @RequestParam MultipartFile file,
            @AuthenticationPrincipal SecurityUser currentUser) {
        kiemTraQuyen(moDun, currentUser);
        return excelImportService.xacNhan(moDun, file, currentUser.getId());
    }

    private void kiemTraQuyen(ModuleKey moDun, SecurityUser currentUser) {
        if (!currentUser.coTheSuaModule(moDun)) {
            throw new AccessDeniedException("Ban khong co quyen import du lieu cho module nay");
        }
    }
}
