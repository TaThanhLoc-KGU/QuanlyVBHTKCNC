package com.ttloc.htkhcn.detaitaichinh;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import com.ttloc.htkhcn.detai.TrangThaiDuyet;

public record BaoCaoTienDoResponse(
        UUID id,
        UUID deTaiId,
        String kyBaoCao,
        LocalDate hanNop,
        LocalDate ngayNop,
        String noiDung,
        TrangThaiDuyet trangThai,
        UUID nguoiDuyetId,
        Instant ngayDuyet,
        String ghiChu) {

    public static BaoCaoTienDoResponse from(DeTaiBaoCaoTienDo b) {
        return new BaoCaoTienDoResponse(
                b.getId(), b.getDeTai().getId(), b.getKyBaoCao(), b.getHanNop(), b.getNgayNop(), b.getNoiDung(),
                b.getTrangThai(), b.getNguoiDuyetId(), b.getNgayDuyet(), b.getGhiChu());
    }
}
