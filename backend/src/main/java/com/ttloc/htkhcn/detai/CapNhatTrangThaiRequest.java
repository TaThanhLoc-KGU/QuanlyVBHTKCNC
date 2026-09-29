package com.ttloc.htkhcn.detai;

import jakarta.validation.constraints.NotNull;

public record CapNhatTrangThaiRequest(
        @NotNull(message = "Trang thai khong duoc de trong") TrangThaiDeTai trangThai,
        String lyDoHuy) {
}
