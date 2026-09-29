package com.ttloc.htkhcn.detaitaichinh;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import com.ttloc.htkhcn.detai.TrangThaiDuyet;

public record ThanhToanResponse(
        UUID id,
        UUID deTaiId,
        Integer nam,
        BigDecimal soTien,
        LocalDate ngayThanhToan,
        TrangThaiDuyet trangThai,
        UUID nguoiDuyetId,
        Instant ngayDuyet,
        String ghiChu) {

    public static ThanhToanResponse from(DeTaiThanhToan t) {
        return new ThanhToanResponse(
                t.getId(), t.getDeTai().getId(), t.getNam(), t.getSoTien(), t.getNgayThanhToan(),
                t.getTrangThai(), t.getNguoiDuyetId(), t.getNgayDuyet(), t.getGhiChu());
    }
}
