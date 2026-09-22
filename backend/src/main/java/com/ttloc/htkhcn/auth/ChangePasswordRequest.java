package com.ttloc.htkhcn.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
        @NotBlank String matKhauCu,
        @NotBlank @Size(min = 8, message = "Mat khau moi phai co it nhat 8 ky tu") String matKhauMoi) {
}
