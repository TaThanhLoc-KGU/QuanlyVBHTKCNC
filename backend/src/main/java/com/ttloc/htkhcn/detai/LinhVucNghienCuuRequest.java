package com.ttloc.htkhcn.detai;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LinhVucNghienCuuRequest(
        String ma,
        @NotBlank(message = "Ten linh vuc khong duoc de trong") String ten,
        @NotNull(message = "Cap khong duoc de trong") Short cap,
        UUID chaId,
        Integer thuTu,
        Boolean hoatDong) {
}
