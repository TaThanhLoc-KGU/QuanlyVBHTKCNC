package com.ttloc.htkhcn.notification;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Phuong an du phong cho pg_cron o local dev / khi chua cai duoc pg_cron tren
 * production (SPEC muc 10 - rui ro & khuyen nghi). Goi lai DUNG 1 ham PL/pgSQL
 * sp_quet_mou_sap_het_han() (V12 migration) - khong co logic quet trung lap
 * viet rieng o tang Java. Khi production da cai pg_cron, co the tat bean nay
 * (app.scheduling.mou-scan-enabled=false) va dung han cron.schedule() thay the.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MouCanhBaoScheduler {

    private final JdbcTemplate jdbcTemplate;

    @Scheduled(cron = "${app.scheduling.quet-mou-cron}")
    public void quetMouSapHetHan() {
        log.info("Bat dau quet MoU sap het han (fallback @Scheduled thay pg_cron)");
        jdbcTemplate.execute("CALL sp_quet_mou_sap_het_han()");
    }
}
