package com.ttloc.htkhcn.detai;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;

public record DeTaiRequest(
        String maDeTai,
        @NotBlank(message = "Ten de tai khong duoc de trong") String tenDeTai,
        UUID chuNhiemId,
        String chuNhiemNgoai,
        String donViThucHien,
        String donViChuQuan,
        UUID phanLoaiTuDienId,
        UUID loaiHinhTuDienId,
        UUID nguonKinhPhiTuDienId,
        UUID linhVucId,
        Integer namDeXuat,
        LocalDate thoiGianBatDau,
        LocalDate thoiGianKetThuc,
        BigDecimal kinhPhiDeXuat,
        BigDecimal kinhPhiDuyet,
        String mucTieu,
        String noiDung,
        String sanPhamDuKien,
        UUID mucXepLoaiTuDienId,
        String ghiChu) {
}
