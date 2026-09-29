package com.ttloc.htkhcn.hosokhoahoc;

import java.time.OffsetDateTime;
import java.util.UUID;

public record LyLichKhoaHocResponse(
        UUID id,
        UUID nguoiDungId,
        String hoTen,
        String hocHam,
        String hocVi,
        String chuyenNganh,
        String quaTrinhCongTac,
        String ghiChu,
        OffsetDateTime ngaySua) {

    public static LyLichKhoaHocResponse from(LyLichKhoaHoc l) {
        return new LyLichKhoaHocResponse(
                l.getId(), l.getNguoiDung().getId(), l.getNguoiDung().getHoTen(),
                l.getHocHam(), l.getHocVi(), l.getChuyenNganh(), l.getQuaTrinhCongTac(), l.getGhiChu(), l.getNgaySua());
    }
}
