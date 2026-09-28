package com.ttloc.htkhcn.doitaccanhan;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;

public record DoiTacCaNhanRequest(
        @NotBlank(message = "Ho ten khong duoc de trong") String hoTen,
        String chucVu,
        UUID doiTacId,
        String email,
        String dienThoai,
        String ghiChu,
        Boolean hoatDong) {
}
