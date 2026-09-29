package com.ttloc.htkhcn.detai;

import java.util.UUID;

public record HoiDongThanhVienRequest(
        UUID nguoiDungId,
        String hoTenNgoai,
        UUID vaiTroTuDienId) {
}
