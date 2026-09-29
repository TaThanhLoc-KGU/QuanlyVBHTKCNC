package com.ttloc.htkhcn.detaitaichinh;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;

public record TamUngRequest(
        Integer nam,
        @NotNull(message = "So tien khong duoc de trong") BigDecimal soTien,
        String lyDo,
        @NotNull(message = "Ngay de nghi khong duoc de trong") LocalDate ngayDeNghi,
        String ghiChu) {
}
