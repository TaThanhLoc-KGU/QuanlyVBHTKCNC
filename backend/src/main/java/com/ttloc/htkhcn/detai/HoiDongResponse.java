package com.ttloc.htkhcn.detai;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record HoiDongResponse(
        UUID id,
        UUID deTaiId,
        LoaiHoiDong loai,
        LocalDate ngayHop,
        String diaDiem,
        KetQuaHoiDong ketQua,
        BigDecimal diemTrungBinh,
        String ghiChu,
        UUID nguoiTaoId,
        Instant ngayTao) {

    public static HoiDongResponse from(HoiDong h) {
        return new HoiDongResponse(
                h.getId(), h.getDeTai().getId(), h.getLoai(), h.getNgayHop(), h.getDiaDiem(),
                h.getKetQua(), h.getDiemTrungBinh(), h.getGhiChu(), h.getNguoiTaoId(), h.getNgayTao());
    }
}
