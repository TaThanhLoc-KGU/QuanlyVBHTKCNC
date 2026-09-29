package com.ttloc.htkhcn.detaitaichinh;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import com.ttloc.htkhcn.detai.TrangThaiDuyet;

public record TamUngResponse(
        UUID id,
        UUID deTaiId,
        Integer nam,
        BigDecimal soTien,
        String lyDo,
        LocalDate ngayDeNghi,
        TrangThaiDuyet trangThai,
        UUID nguoiDuyetId,
        Instant ngayDuyet,
        String ghiChu) {

    public static TamUngResponse from(DeTaiTamUng t) {
        return new TamUngResponse(
                t.getId(), t.getDeTai().getId(), t.getNam(), t.getSoTien(), t.getLyDo(), t.getNgayDeNghi(),
                t.getTrangThai(), t.getNguoiDuyetId(), t.getNgayDuyet(), t.getGhiChu());
    }
}
