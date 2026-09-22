package com.ttloc.htkhcn.tailieu;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ttloc.htkhcn.security.SecurityUser;

import lombok.RequiredArgsConstructor;

/** Dinh kem tai lieu cho Van ban DHKG / VBPL VN / MoU (SPEC muc 4.5). */
@RestController
@RequestMapping("/api/tai-lieu-dinh-kem")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'EDITOR', 'VIEWER')")
public class TaiLieuDinhKemController {

    private final TaiLieuDinhKemService taiLieuDinhKemService;

    @GetMapping
    public List<TaiLieuDinhKem> danhSach(@RequestParam String bang, @RequestParam UUID banGhiId) {
        return taiLieuDinhKemService.danhSach(bang, banGhiId);
    }

    @PostMapping
    public TaiLieuDinhKem taiLen(
            @RequestParam String bang,
            @RequestParam UUID banGhiId,
            @RequestParam MultipartFile file,
            @AuthenticationPrincipal SecurityUser currentUser) {
        kiemTraQuyenSua(bang, currentUser);
        return taiLieuDinhKemService.taiLen(bang, banGhiId, file, currentUser.getId());
    }

    @GetMapping("/{id}/tai-xuong")
    public ResponseEntity<Resource> taiXuong(@PathVariable UUID id) {
        TaiLieuDinhKem tl = taiLieuDinhKemService.layTheoId(id);
        Resource resource = new FileSystemResource(Path.of(tl.getDuongDan()));
        if (!resource.exists()) {
            throw new com.ttloc.htkhcn.common.exception.ResourceNotFoundException(
                    "File khong con tren dia: " + tl.getTenFile());
        }
        String tenFileEncoded = java.net.URLEncoder.encode(tl.getTenFile(), StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .contentType(tl.getLoaiMime() != null ? MediaType.parseMediaType(tl.getLoaiMime()) : MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + tenFileEncoded)
                .body(resource);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void xoa(@PathVariable UUID id, @AuthenticationPrincipal SecurityUser currentUser) {
        TaiLieuDinhKem tl = taiLieuDinhKemService.layTheoId(id);
        kiemTraQuyenSua(tl.getBang(), currentUser);
        taiLieuDinhKemService.xoa(id);
    }

    private void kiemTraQuyenSua(String bang, SecurityUser currentUser) {
        var moduleKey = BangDinhKem.tuTen(bang).toModuleKey();
        if (!currentUser.coTheSuaModule(moduleKey)) {
            throw new AccessDeniedException("Ban khong co quyen dinh kem tai lieu cho module nay");
        }
    }
}
