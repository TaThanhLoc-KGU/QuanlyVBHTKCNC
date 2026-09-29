package com.ttloc.htkhcn.detai;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;

public record HoiDongRequest(
        @NotNull(message = "Loai hoi dong khong duoc de trong") LoaiHoiDong loai,
        LocalDate ngayHop,
        String diaDiem,
        KetQuaHoiDong ketQua,
        BigDecimal diemTrungBinh,
        String ghiChu) {
}
