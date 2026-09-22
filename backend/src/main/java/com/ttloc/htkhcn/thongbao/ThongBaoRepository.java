package com.ttloc.htkhcn.thongbao;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ThongBaoRepository extends JpaRepository<ThongBao, UUID> {

    Page<ThongBao> findByNguoiNhanIdOrderByNgayTaoDesc(UUID nguoiNhanId, Pageable pageable);

    Page<ThongBao> findByNguoiNhanIdAndDaDocWebOrderByNgayTaoDesc(UUID nguoiNhanId, boolean daDocWeb, Pageable pageable);

    long countByNguoiNhanIdAndDaDocWebFalse(UUID nguoiNhanId);

    List<ThongBao> findByDaGuiEmailFalse();

    @Modifying
    @Query("update ThongBao t set t.daDocWeb = true, t.ngayDoc = CURRENT_TIMESTAMP "
            + "where t.nguoiNhanId = :nguoiNhanId and t.daDocWeb = false")
    int danhDauTatCaDaDoc(@Param("nguoiNhanId") UUID nguoiNhanId);
}
