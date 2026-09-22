package com.ttloc.htkhcn.audit;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LichSuThayDoiRepository extends JpaRepository<LichSuThayDoi, Long> {

    List<LichSuThayDoi> findByBangAndBanGhiIdOrderByThoiDiemDesc(String bang, UUID banGhiId);

    @Query("select h from LichSuThayDoi h where h.bang = :bang and h.banGhiId = :banGhiId "
            + "and h.hanhDong = com.ttloc.htkhcn.audit.HanhDong.INSERT")
    Optional<LichSuThayDoi> findInsertRecord(@Param("bang") String bang, @Param("banGhiId") UUID banGhiId);
}
