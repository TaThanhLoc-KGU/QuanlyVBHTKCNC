package com.ttloc.htkhcn.crawlphapluat;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** Crawl tu dong hang ngay (theo yeu cau nguoi dung, ngoai pham vi SPEC ban
 * dau) - cung co che @Scheduled du phong nhu MouCanhBaoScheduler. */
@Slf4j
@Component
@RequiredArgsConstructor
public class CrawlPhapLuatScheduler {

    private final CrawlPhapLuatService crawlPhapLuatService;

    @Scheduled(cron = "${app.scheduling.crawl-phap-luat-cron}")
    public void crawlHangNgay() {
        log.info("Bat dau crawl phap luat tu dong hang ngay");
        CrawlKetQuaResponse ketQua = crawlPhapLuatService.chayCrawlNgay();
        log.info("Crawl phap luat tu dong xong: {} ung vien moi tu {} tu khoa",
                ketQua.soUngVienMoi(), ketQua.soTuKhoaDaQuet());
    }
}
