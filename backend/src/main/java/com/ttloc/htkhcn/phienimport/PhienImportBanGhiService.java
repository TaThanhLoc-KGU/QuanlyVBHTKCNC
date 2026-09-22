package com.ttloc.htkhcn.phienimport;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ttloc.htkhcn.common.ModuleKey;
import com.ttloc.htkhcn.common.exception.BadRequestException;
import com.ttloc.htkhcn.common.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

/**
 * Ghi lai (bang, ban_ghi_id) cua tung ban ghi duoc tao ra tu 1 phien import de
 * co the rollback (SPEC muc 4.3: "xem lai hoac rollback duoc"). Dung JdbcTemplate
 * truc tiep vi phien_import_ban_ghi chi la bang danh dau, khong can entity JPA
 * day du (PK ghep 3 cot, khong co cot id rieng).
 */
@Service
@RequiredArgsConstructor
public class PhienImportBanGhiService {

    private static final Map<ModuleKey, String> BANG_THEO_MODULE = Map.of(
            ModuleKey.DOI_TAC, "doi_tac",
            ModuleKey.VAN_BAN_DHKG, "van_ban_dhkg",
            ModuleKey.VBPL_VN, "vbpl_vn",
            ModuleKey.MOU, "mou",
            ModuleKey.DOAN_VAO, "doan_vao",
            ModuleKey.DOAN_RA, "doan_ra");

    private final JdbcTemplate jdbcTemplate;

    public void ghiNhan(UUID phienImportId, ModuleKey moDun, UUID banGhiId) {
        String bang = tenBang(moDun);
        jdbcTemplate.update(
                "INSERT INTO phien_import_ban_ghi (phien_import_id, bang, ban_ghi_id) VALUES (?, ?, ?)",
                phienImportId, bang, banGhiId);
    }

    @Transactional
    public int rollback(UUID phienImportId) {
        List<Map<String, Object>> banGhis = jdbcTemplate.queryForList(
                "SELECT bang, ban_ghi_id FROM phien_import_ban_ghi WHERE phien_import_id = ?", phienImportId);
        if (banGhis.isEmpty()) {
            throw new ResourceNotFoundException("Phien import khong co ban ghi nao de rollback: " + phienImportId);
        }
        int soLuong = 0;
        for (Map<String, Object> row : banGhis) {
            String bang = (String) row.get("bang");
            UUID banGhiId = (UUID) row.get("ban_ghi_id");
            if (BANG_THEO_MODULE.containsValue(bang)) {
                soLuong += jdbcTemplate.update(
                        "UPDATE " + bang + " SET deleted_at = ? WHERE id = ? AND deleted_at IS NULL",
                        Timestamp.from(Instant.now()), banGhiId);
            }
        }
        return soLuong;
    }

    private String tenBang(ModuleKey moDun) {
        String bang = BANG_THEO_MODULE.get(moDun);
        if (bang == null) {
            throw new BadRequestException("Module khong ho tro import: " + moDun);
        }
        return bang;
    }
}
