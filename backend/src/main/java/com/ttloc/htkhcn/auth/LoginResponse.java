package com.ttloc.htkhcn.auth;

import java.util.List;
import java.util.UUID;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        UUID id,
        String tenDangNhap,
        String hoTen,
        List<String> vaiTro,
        boolean phaiDoiMatKhau) {
}
