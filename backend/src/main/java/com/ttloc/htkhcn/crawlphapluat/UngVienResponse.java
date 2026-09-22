package com.ttloc.htkhcn.crawlphapluat;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record UngVienResponse(
        UUID id,
        NguonCrawl nguon,
        String soHieu,
        String tenVanBan,
        String loaiVanBanText,
        LocalDate ngayBanHanh,
        String coQuanBanHanh,
        String urlNguon,
        String tuKhoaKhop,
        TrangThaiUngVien trangThai,
        OffsetDateTime ngayCrawl) {

    public static UngVienResponse from(VbplUngVien uv) {
        return new UngVienResponse(
                uv.getId(), uv.getNguon(), uv.getSoHieu(), uv.getTenVanBan(), uv.getLoaiVanBanText(),
                uv.getNgayBanHanh(), uv.getCoQuanBanHanh(), uv.getUrlNguon(), uv.getTuKhoaKhop(),
                uv.getTrangThai(), uv.getNgayCrawl());
    }
}
