package com.ttloc.htkhcn.dashboard;

import java.time.LocalDate;
import java.util.UUID;

import com.ttloc.htkhcn.mou.TrangThaiMou;

/** SPEC muc 4.6: widget "Thoi han hieu luc MoU" - sap xep theo so ngay con lai tang dan. */
public record MouWidgetDto(
        UUID id,
        String tenDoiTac,
        LocalDate ngayBanHanh,
        LocalDate ngayHetHan,
        Integer soNgayConLai,
        TrangThaiMou trangThai,
        Integer phanTramThoiGianDaQua) {
}
