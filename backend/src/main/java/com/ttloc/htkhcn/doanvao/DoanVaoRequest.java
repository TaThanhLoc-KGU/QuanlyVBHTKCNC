package com.ttloc.htkhcn.doanvao;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record DoanVaoRequest(
        @NotBlank(message = "Ten doan khong duoc de trong") String tenDoan,
        UUID doiTacId,
        @NotNull(message = "Thoi gian den khong duoc de trong") LocalDate thoiGianDen,
        @NotNull(message = "Thoi gian di khong duoc de trong") LocalDate thoiGianDi,
        @NotNull(message = "So luong nguoi nuoc ngoai khong duoc de trong") Integer soLuongNguoiNuocNgoai,
        Integer soLuongNguoiVietNam,
        @NotEmpty(message = "Quoc tich khong duoc de trong") String[] quocTich,
        String noiDungLamViec) {
}
