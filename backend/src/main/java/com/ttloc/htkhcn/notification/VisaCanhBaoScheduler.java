package com.ttloc.htkhcn.notification;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** Canh bao Visa sap het han - giong het co che MoUCanhBaoScheduler (V12),
 * goi lai procedure sp_quet_visa_sap_het_han() (V25 migration). */
@Slf4j
@Component
@RequiredArgsConstructor
public class VisaCanhBaoScheduler {

    private final JdbcTemplate jdbcTemplate;

    @Scheduled(cron = "${app.scheduling.quet-visa-cron}")
    public void quetVisaSapHetHan() {
        log.info("Bat dau quet Visa sap het han (fallback @Scheduled thay pg_cron)");
        jdbcTemplate.execute("CALL sp_quet_visa_sap_het_han()");
    }
}
