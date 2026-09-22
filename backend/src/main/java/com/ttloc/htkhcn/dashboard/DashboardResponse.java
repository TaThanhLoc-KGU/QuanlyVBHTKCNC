package com.ttloc.htkhcn.dashboard;

import java.time.Instant;
import java.util.List;

public record DashboardResponse(
        long tongVanBanDhkgHieuLuc,
        long tongVbplVnHieuLuc,
        long tongMouConHieuLuc,
        long tongMouSapHetHan,
        long tongMouDaHetHan,
        long tongDoanVaoNamHienTai,
        long tongKhachNuocNgoaiNamHienTai,
        long tongDoanRaNamHienTai,
        long tongLuotCanBoDiCongTacNamHienTai,
        Instant lamMoiLuc,
        List<MouTheoThangDto> mouDenHanTheoThang,
        List<MouWidgetDto> widgetMouSapHetHan) {

    public record MouTheoThangDto(String thang, long soLuong) {
    }
}
