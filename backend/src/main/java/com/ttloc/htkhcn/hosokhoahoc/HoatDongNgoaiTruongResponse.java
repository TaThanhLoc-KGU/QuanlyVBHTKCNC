package com.ttloc.htkhcn.hosokhoahoc;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record HoatDongNgoaiTruongResponse(
        UUID id,
        UUID nguoiDungId,
        LoaiHoatDongNgoaiTruong loai,
        String ten,
        String donViPhoiHop,
        String vaiTro,
        LocalDate thoiGianBatDau,
        LocalDate thoiGianKetThuc,
        String ghiChu,
        Instant ngayTao) {

    public static HoatDongNgoaiTruongResponse from(HoatDongNgoaiTruong h) {
        return new HoatDongNgoaiTruongResponse(
                h.getId(), h.getNguoiDung().getId(), h.getLoai(), h.getTen(), h.getDonViPhoiHop(), h.getVaiTro(),
                h.getThoiGianBatDau(), h.getThoiGianKetThuc(), h.getGhiChu(), h.getNgayTao());
    }
}
