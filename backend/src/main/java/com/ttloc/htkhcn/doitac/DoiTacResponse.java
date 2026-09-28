package com.ttloc.htkhcn.doitac;

import java.time.Instant;
import java.util.UUID;

public record DoiTacResponse(
        UUID id,
        String tenDoiTac,
        LoaiDoiTac loaiDoiTac,
        String quocGia,
        String diaChi,
        String thongTinLienHe,
        String ghiChu,
        UUID loaiTuDienId,
        String loaiTuDienTen,
        UUID nguoiTaoId,
        Instant ngayTao,
        UUID nguoiSuaId,
        Instant ngaySua) {

    public static DoiTacResponse from(DoiTac d) {
        return new DoiTacResponse(
                d.getId(),
                d.getTenDoiTac(),
                d.getLoaiDoiTac(),
                d.getQuocGia(),
                d.getDiaChi(),
                d.getThongTinLienHe(),
                d.getGhiChu(),
                d.getLoaiTuDien() != null ? d.getLoaiTuDien().getId() : null,
                d.getLoaiTuDien() != null ? d.getLoaiTuDien().getTen() : null,
                d.getNguoiTaoId(),
                d.getNgayTao(),
                d.getNguoiSuaId(),
                d.getNgaySua());
    }
}
