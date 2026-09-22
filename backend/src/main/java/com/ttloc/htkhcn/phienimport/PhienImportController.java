package com.ttloc.htkhcn.phienimport;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ttloc.htkhcn.common.PageResponse;
import com.ttloc.htkhcn.common.exception.BadRequestException;

import lombok.RequiredArgsConstructor;

/** Lich su cac phien import + rollback (SPEC muc 4.3). */
@RestController
@RequestMapping("/api/phien-import")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
public class PhienImportController {

    private final PhienImportRepository phienImportRepository;
    private final PhienImportBanGhiService phienImportBanGhiService;

    @GetMapping
    public PageResponse<PhienImport> danhSach(Pageable pageable) {
        return PageResponse.of(phienImportRepository.findAllByOrderByThoiDiemDesc(pageable));
    }

    @PostMapping("/{id}/rollback")
    @PreAuthorize("hasRole('ADMIN')")
    public RollbackResponse rollback(@PathVariable UUID id) {
        PhienImport phien = phienImportRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Khong tim thay phien import: " + id));
        if (!phien.isCoTheRollback() || phien.isDaRollback()) {
            throw new BadRequestException("Phien import nay khong the (hoac da) rollback");
        }
        int soLuong = phienImportBanGhiService.rollback(id);
        phien.setDaRollback(true);
        phienImportRepository.save(phien);
        return new RollbackResponse(soLuong);
    }

    public record RollbackResponse(int soBanGhiDaXoa) {
    }
}
