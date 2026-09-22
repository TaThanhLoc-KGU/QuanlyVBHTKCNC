package com.ttloc.htkhcn.danhmuc;

import com.ttloc.htkhcn.common.PhamViVanBan;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoaiVanBanRequest(
        @NotBlank String ma,
        @NotBlank String ten,
        @NotNull PhamViVanBan phamVi,
        Integer thuTu) {
}
