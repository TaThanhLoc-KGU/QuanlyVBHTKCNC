package com.ttloc.htkhcn.crawlphapluat;

import java.time.LocalDate;

/** Ket qua tho doc duoc tu 1 nguon crawl, truoc khi doi chieu voi DB. */
public record KetQuaCrawl(
        String soHieu,
        String tenVanBan,
        String loaiVanBanText,
        LocalDate ngayBanHanh,
        String coQuanBanHanh,
        String urlNguon) {
}
