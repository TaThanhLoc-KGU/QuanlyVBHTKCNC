package com.ttloc.htkhcn.mauemail;

import jakarta.validation.constraints.NotBlank;

public record MauEmailRequest(
        @NotBlank(message = "Ma khong duoc de trong") String ma,
        @NotBlank(message = "Ten mau khong duoc de trong") String tenMau,
        @NotBlank(message = "Tieu de khong duoc de trong") String tieuDe,
        @NotBlank(message = "Noi dung khong duoc de trong") String noiDung,
        String moTa,
        Boolean hoatDong) {
}
