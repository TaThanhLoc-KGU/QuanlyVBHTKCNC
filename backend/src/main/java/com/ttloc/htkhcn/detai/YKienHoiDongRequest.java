package com.ttloc.htkhcn.detai;

import java.math.BigDecimal;

public record YKienHoiDongRequest(
        String yKien,
        BigDecimal diem,
        Boolean dongY) {
}
