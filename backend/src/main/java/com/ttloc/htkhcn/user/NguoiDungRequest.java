package com.ttloc.htkhcn.user;

import java.util.List;
import java.util.Set;

import com.ttloc.htkhcn.common.ModuleKey;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record NguoiDungRequest(
        @NotBlank(message = "Ten dang nhap khong duoc de trong") String tenDangNhap,
        @NotBlank @Email(message = "Email khong hop le") String email,
        @NotBlank(message = "Ho ten khong duoc de trong") String hoTen,
        @NotEmpty(message = "Phai gan it nhat 1 vai tro") List<String> vaiTroMa,
        Set<ModuleKey> bienTapMoDun) {
}
