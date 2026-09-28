package com.ttloc.htkhcn.visa;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VisaRequest(
        @NotBlank(message = "Ho ten khong duoc de trong") String hoTen,
        String quocTich,
        @NotNull(message = "Loai cap khong duoc de trong") LoaiCapVisa loaiCap,
        @NotNull(message = "Ngay cap khong duoc de trong") LocalDate ngayCap,
        LocalDate ngayHetHan,
        String coQuanCap,
        UUID mucDichTuDienId,
        UUID doanVaoId,
        String ghiChu) {
}
