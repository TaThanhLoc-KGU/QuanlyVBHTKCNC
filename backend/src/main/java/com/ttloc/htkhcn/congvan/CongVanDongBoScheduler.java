package com.ttloc.htkhcn.congvan;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** Dong bo tu dong hang ngay tu CongVan - chi chay khi da cau hinh CONGVAN_API_KEY. */
@Slf4j
@Component
@RequiredArgsConstructor
public class CongVanDongBoScheduler {

    private final CongVanImportService congVanImportService;
    private final CongVanClient congVanClient;

    @Scheduled(cron = "${app.scheduling.congvan-dong-bo-cron}")
    public void dongBoHangNgay() {
        if (!congVanClient.daCauHinh()) {
            return;
        }
        log.info("Bat dau dong bo CongVan tu dong hang ngay");
        var ketQua = congVanImportService.dongBoNgay();
        log.info("Dong bo CongVan tu dong xong: {} van ban, {} ung vien moi",
                ketQua.tongSoTuCongVan(), ketQua.soUngVienMoi());
    }
}
