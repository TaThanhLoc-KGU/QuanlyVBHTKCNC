package com.ttloc.htkhcn.danhmuc;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TuDienRequest(
        @NotNull LoaiTuDien loai,
        String ma,
        @NotBlank String ten,
        String moTa,
        Integer thuTu,
        Boolean hoatDong) {
}
