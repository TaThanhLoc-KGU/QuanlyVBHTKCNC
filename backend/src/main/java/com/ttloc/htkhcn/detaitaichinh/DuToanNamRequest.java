package com.ttloc.htkhcn.detaitaichinh;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;

public record DuToanNamRequest(
        @NotNull(message = "Nam khong duoc de trong") Integer nam,
        BigDecimal kinhPhiDeXuat,
        BigDecimal kinhPhiDuyet,
        Boolean daGuiBo,
        String ghiChu) {
}
