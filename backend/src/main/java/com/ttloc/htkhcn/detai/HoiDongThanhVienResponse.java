package com.ttloc.htkhcn.detai;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record HoiDongThanhVienResponse(
        UUID id,
        UUID hoiDongId,
        UUID nguoiDungId,
        String hoTen,
        String hoTenNgoai,
        UUID vaiTroTuDienId,
        String vaiTroTen,
        String yKien,
        BigDecimal diem,
        Boolean dongY,
        Instant ngayChoYKien) {

    public static HoiDongThanhVienResponse from(HoiDongThanhVien t) {
        return new HoiDongThanhVienResponse(
                t.getId(), t.getHoiDong().getId(),
                t.getNguoiDung() != null ? t.getNguoiDung().getId() : null,
                t.getNguoiDung() != null ? t.getNguoiDung().getHoTen() : null,
                t.getHoTenNgoai(),
                t.getVaiTro() != null ? t.getVaiTro().getId() : null,
                t.getVaiTro() != null ? t.getVaiTro().getTen() : null,
                t.getYKien(), t.getDiem(), t.getDongY(), t.getNgayChoYKien());
    }
}
