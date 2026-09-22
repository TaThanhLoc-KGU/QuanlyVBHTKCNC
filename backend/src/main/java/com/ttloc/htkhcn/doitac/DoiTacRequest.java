package com.ttloc.htkhcn.doitac;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DoiTacRequest(
        @NotBlank(message = "Ten doi tac khong duoc de trong") String tenDoiTac,
        @NotNull(message = "Loai doi tac khong duoc de trong") LoaiDoiTac loaiDoiTac,
        String quocGia,
        String diaChi,
        String thongTinLienHe,
        String ghiChu) {
}
