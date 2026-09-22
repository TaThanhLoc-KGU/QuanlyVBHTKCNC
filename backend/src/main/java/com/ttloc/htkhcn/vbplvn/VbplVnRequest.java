package com.ttloc.htkhcn.vbplvn;

import java.time.LocalDate;
import java.util.UUID;

import com.ttloc.htkhcn.common.TinhTrangHieuLuc;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VbplVnRequest(
        @NotBlank(message = "So hieu khong duoc de trong") String soHieu,
        @NotBlank(message = "Ten van ban khong duoc de trong") String tenVanBan,
        @NotNull(message = "Loai van ban khong duoc de trong") UUID loaiVanBanId,
        @NotNull(message = "Ngay ban hanh khong duoc de trong") LocalDate ngayBanHanh,
        LocalDate ngayHieuLuc,
        @NotNull(message = "Tinh trang hieu luc khong duoc de trong") TinhTrangHieuLuc tinhTrangHieuLuc,
        @NotBlank(message = "Co quan ban hanh khong duoc de trong") String coQuanBanHanh,
        String ghiChu,
        String noiDungChinh) {
}
