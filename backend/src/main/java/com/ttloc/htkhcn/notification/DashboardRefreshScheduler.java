package com.ttloc.htkhcn.notification;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Refresh dinh ky 2 materialized view Dashboard (SPEC muc 4.6, 6.4). O production
 * co the chuyen sang pg_cron; local dev dung @Scheduled cho don gian.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DashboardRefreshScheduler {

    private final JdbcTemplate jdbcTemplate;

    @Scheduled(fixedRateString = "${app.scheduling.dashboard-refresh-ms:900000}")
    public void refresh() {
        log.debug("Refresh materialized view Dashboard");
        jdbcTemplate.execute("REFRESH MATERIALIZED VIEW CONCURRENTLY mv_dashboard_tong_quan");
        jdbcTemplate.execute("REFRESH MATERIALIZED VIEW CONCURRENTLY mv_mou_den_han_theo_thang");
    }
}
