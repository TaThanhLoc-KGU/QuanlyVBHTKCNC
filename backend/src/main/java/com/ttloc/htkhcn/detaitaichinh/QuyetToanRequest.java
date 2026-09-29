package com.ttloc.htkhcn.detaitaichinh;

import java.math.BigDecimal;
import java.time.LocalDate;

public record QuyetToanRequest(
        BigDecimal tongKinhPhiDaCap,
        BigDecimal tongKinhPhiDaSuDung,
        LocalDate ngayQuyetToan,
        String ghiChu) {
}
