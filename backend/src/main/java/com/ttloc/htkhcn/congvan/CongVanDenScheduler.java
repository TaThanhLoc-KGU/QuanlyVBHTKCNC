package com.ttloc.htkhcn.congvan;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** Dong bo tu dong hang ngay Cong van den - chi chay khi da cau hinh CONGVAN_API_KEY. */
@Slf4j
@Component
@RequiredArgsConstructor
public class CongVanDenScheduler {

    private final CongVanDenSyncService congVanDenSyncService;
    private final CongVanDenClient congVanDenClient;

    @Scheduled(cron = "${app.scheduling.congvan-den-dong-bo-cron}")
    public void dongBoHangNgay() {
        if (!congVanDenClient.daCauHinh()) {
            return;
        }
        log.info("Bat dau dong bo Cong van den tu dong hang ngay");
        var ketQua = congVanDenSyncService.dongBoNgay();
        log.info("Dong bo Cong van den tu dong xong: {} tu CongVan, {} moi, {} cap nhat",
                ketQua.tongSoTuCongVan(), ketQua.soMoi(), ketQua.soCapNhat());
    }
}
