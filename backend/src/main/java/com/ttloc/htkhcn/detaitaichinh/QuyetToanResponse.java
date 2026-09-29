package com.ttloc.htkhcn.detaitaichinh;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import com.ttloc.htkhcn.detai.TrangThaiDuyet;

public record QuyetToanResponse(
        UUID id,
        UUID deTaiId,
        BigDecimal tongKinhPhiDaCap,
        BigDecimal tongKinhPhiDaSuDung,
        LocalDate ngayQuyetToan,
        TrangThaiDuyet trangThai,
        UUID nguoiDuyetId,
        Instant ngayDuyet,
        String ghiChu) {

    public static QuyetToanResponse from(DeTaiQuyetToan q) {
        return new QuyetToanResponse(
                q.getId(), q.getDeTai().getId(), q.getTongKinhPhiDaCap(), q.getTongKinhPhiDaSuDung(),
                q.getNgayQuyetToan(), q.getTrangThai(), q.getNguoiDuyetId(), q.getNgayDuyet(), q.getGhiChu());
    }
}
