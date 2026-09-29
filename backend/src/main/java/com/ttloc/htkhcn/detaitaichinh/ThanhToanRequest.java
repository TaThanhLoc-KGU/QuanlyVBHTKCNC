package com.ttloc.htkhcn.detaitaichinh;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;

public record ThanhToanRequest(
        @NotNull(message = "Nam khong duoc de trong") Integer nam,
        @NotNull(message = "So tien khong duoc de trong") BigDecimal soTien,
        LocalDate ngayThanhToan,
        String ghiChu) {
}
