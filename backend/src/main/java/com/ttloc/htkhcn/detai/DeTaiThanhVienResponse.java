package com.ttloc.htkhcn.detai;

import java.util.UUID;

public record DeTaiThanhVienResponse(
        UUID id,
        UUID nguoiDungId,
        String hoTen,
        String hoTenNgoai,
        UUID vaiTroTuDienId,
        String vaiTroTen,
        int thuTu) {

    public static DeTaiThanhVienResponse from(DeTaiThanhVien t) {
        return new DeTaiThanhVienResponse(
                t.getId(),
                t.getNguoiDung() != null ? t.getNguoiDung().getId() : null,
                t.getNguoiDung() != null ? t.getNguoiDung().getHoTen() : null,
                t.getHoTenNgoai(),
                t.getVaiTro() != null ? t.getVaiTro().getId() : null,
                t.getVaiTro() != null ? t.getVaiTro().getTen() : null,
                t.getThuTu());
    }
}
