package com.ttloc.htkhcn.hosokhoahoc;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record HoatDongNgoaiTruongRequest(
        @NotNull(message = "Loai hoat dong khong duoc de trong") LoaiHoatDongNgoaiTruong loai,
        @NotBlank(message = "Ten khong duoc de trong") String ten,
        String donViPhoiHop,
        String vaiTro,
        LocalDate thoiGianBatDau,
        LocalDate thoiGianKetThuc,
        String ghiChu) {
}
