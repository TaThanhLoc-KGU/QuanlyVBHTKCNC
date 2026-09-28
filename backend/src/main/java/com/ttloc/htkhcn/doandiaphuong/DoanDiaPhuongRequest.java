package com.ttloc.htkhcn.doandiaphuong;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DoanDiaPhuongRequest(
        @NotBlank(message = "Ten doan khong duoc de trong") String tenDoan,
        UUID doiTacId,
        @NotNull(message = "Thoi gian di khong duoc de trong") LocalDate thoiGianDi,
        @NotNull(message = "Thoi gian ve khong duoc de trong") LocalDate thoiGianVe,
        String diaDiem,
        UUID mucTieuTuDienId,
        UUID nguonKinhPhiTuDienId,
        String thanhPhan,
        String noiDungLamViec,
        String ghiChu) {
}
