package com.ttloc.htkhcn.doanra;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DoanRaRequest(
        @NotNull(message = "Don vi lam viec (doi tac) khong duoc de trong") UUID doiTacId,
        @NotNull(message = "Thoi gian di khong duoc de trong") LocalDate thoiGianDi,
        @NotNull(message = "Thoi gian ve khong duoc de trong") LocalDate thoiGianVe,
        String diaDiemDi,
        String diaDiemDen,
        @NotNull(message = "So luong doan khong duoc de trong") Integer soLuongDoan,
        String thanhPhan,
        @NotBlank(message = "Quoc gia lam viec khong duoc de trong") String quocGiaLamViec,
        String noiDungLamViec) {
}
