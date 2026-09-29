package com.ttloc.htkhcn.detaitaichinh;

import com.ttloc.htkhcn.detai.TrangThaiDuyet;

import jakarta.validation.constraints.NotNull;

/** Request dung chung cho hanh dong duyet/tu choi tren ca 5 loai ho so tai
 * chinh cua de tai (du toan nam/tam ung/thanh toan/quyet toan/bao cao tien do). */
public record DuyetRequest(
        @NotNull(message = "Trang thai khong duoc de trong") TrangThaiDuyet trangThai,
        String ghiChu) {
}
