package com.ttloc.htkhcn.detaitaichinh;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.ttloc.htkhcn.detai.TrangThaiDuyet;

public record DuToanNamResponse(
        UUID id,
        UUID deTaiId,
        Integer nam,
        BigDecimal kinhPhiDeXuat,
        BigDecimal kinhPhiDuyet,
        boolean daGuiBo,
        TrangThaiDuyet trangThai,
        UUID nguoiDuyetId,
        Instant ngayDuyet,
        String ghiChu) {

    public static DuToanNamResponse from(DeTaiDuToanNam d) {
        return new DuToanNamResponse(
                d.getId(), d.getDeTai().getId(), d.getNam(), d.getKinhPhiDeXuat(), d.getKinhPhiDuyet(),
                d.isDaGuiBo(), d.getTrangThai(), d.getNguoiDuyetId(), d.getNgayDuyet(), d.getGhiChu());
    }
}
