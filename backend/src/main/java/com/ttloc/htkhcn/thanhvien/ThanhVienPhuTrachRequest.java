package com.ttloc.htkhcn.thanhvien;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;

public record ThanhVienPhuTrachRequest(
        @NotBlank(message = "Ho ten khong duoc de trong") String hoTen,
        String chucVu,
        String donVi,
        String email,
        String dienThoai,
        UUID vaiTroTuDienId,
        String ghiChu,
        Boolean hoatDong) {
}
