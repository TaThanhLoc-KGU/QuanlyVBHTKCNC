package com.ttloc.htkhcn.sukien;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SuKienRequest(
        @NotBlank(message = "Ten su kien khong duoc de trong") String tenSuKien,
        UUID loaiSuKienTuDienId,
        UUID linhVucTuDienId,
        @NotNull(message = "Thoi gian bat dau khong duoc de trong") LocalDate thoiGianBatDau,
        LocalDate thoiGianKetThuc,
        String diaDiem,
        String donViToChuc,
        Integer soLuongThamGia,
        String noiDung,
        String ghiChu) {
}
