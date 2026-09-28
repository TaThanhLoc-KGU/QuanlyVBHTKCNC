package com.ttloc.htkhcn.thanhvien;

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
@RequestMapping("/api/thanh-vien-phu-trach")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'EDITOR', 'VIEWER')")
public class ThanhVienPhuTrachController {

    private static final String MODULE = "THANH_VIEN_PHU_TRACH";

    private final ThanhVienPhuTrachService thanhVienPhuTrachService;

    @GetMapping
    public List<ThanhVienPhuTrachResponse> danhSach() {
        return thanhVienPhuTrachService.danhSach();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or @moduleAccess.canEdit(authentication, '" + MODULE + "')")
    public ThanhVienPhuTrachResponse tao(@Valid @RequestBody ThanhVienPhuTrachRequest request) {
        return thanhVienPhuTrachService.tao(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @moduleAccess.canEdit(authentication, '" + MODULE + "')")
    public ThanhVienPhuTrachResponse sua(@PathVariable UUID id, @Valid @RequestBody ThanhVienPhuTrachRequest request) {
        return thanhVienPhuTrachService.sua(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN') or @moduleAccess.canEdit(authentication, '" + MODULE + "')")
    public void xoa(@PathVariable UUID id) {
        thanhVienPhuTrachService.xoa(id);
    }
}
