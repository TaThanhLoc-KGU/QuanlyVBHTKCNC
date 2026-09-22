package com.ttloc.htkhcn.audit;

import java.util.List;
import java.util.UUID;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.ttloc.htkhcn.security.SecurityUser;

import lombok.RequiredArgsConstructor;

/**
 * Admin xem lich su moi ban ghi; Editor chi xem lich su ban ghi do chinh minh
 * tao (SPEC muc 4.2).
 */
@Service
@RequiredArgsConstructor
public class AuditService {

    private final LichSuThayDoiRepository lichSuThayDoiRepository;

    public List<LichSuThayDoi> getHistory(String bang, UUID banGhiId, SecurityUser currentUser) {
        if (!currentUser.isAdmin()) {
            UUID nguoiTao = lichSuThayDoiRepository.findInsertRecord(bang, banGhiId)
                    .map(LichSuThayDoi::getNguoiThucHienId)
                    .orElse(null);
            if (nguoiTao == null || !nguoiTao.equals(currentUser.getId())) {
                throw new AccessDeniedException("Ban chi duoc xem lich su cua ban ghi do chinh minh tao");
            }
        }
        return lichSuThayDoiRepository.findByBangAndBanGhiIdOrderByThoiDiemDesc(bang, banGhiId);
    }
}
