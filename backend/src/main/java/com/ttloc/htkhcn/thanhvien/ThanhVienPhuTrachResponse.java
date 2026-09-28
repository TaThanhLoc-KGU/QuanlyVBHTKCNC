package com.ttloc.htkhcn.thanhvien;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ThanhVienPhuTrachResponse(
        UUID id,
        String hoTen,
        String chucVu,
        String donVi,
        String email,
        String dienThoai,
        UUID vaiTroTuDienId,
        String vaiTroTen,
        String ghiChu,
        boolean hoatDong,
        OffsetDateTime ngayTao,
        OffsetDateTime ngaySua) {

    public static ThanhVienPhuTrachResponse from(ThanhVienPhuTrach tv) {
        return new ThanhVienPhuTrachResponse(
                tv.getId(), tv.getHoTen(), tv.getChucVu(), tv.getDonVi(), tv.getEmail(), tv.getDienThoai(),
                tv.getVaiTro() != null ? tv.getVaiTro().getId() : null,
                tv.getVaiTro() != null ? tv.getVaiTro().getTen() : null,
                tv.getGhiChu(), tv.isHoatDong(), tv.getNgayTao(), tv.getNgaySua());
    }
}
