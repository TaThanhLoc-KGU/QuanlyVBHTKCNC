package com.ttloc.htkhcn.detaitaichinh;

import java.time.LocalDate;

public record BaoCaoTienDoRequest(
        String kyBaoCao,
        LocalDate hanNop,
        LocalDate ngayNop,
        String noiDung,
        String ghiChu) {
}
