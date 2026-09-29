package com.ttloc.htkhcn.detai;

import java.util.UUID;

public record DeTaiThanhVienRequest(
        UUID nguoiDungId,
        String hoTenNgoai,
        UUID vaiTroTuDienId,
        Integer thuTu) {
}
