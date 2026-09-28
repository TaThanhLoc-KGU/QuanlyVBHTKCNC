package com.ttloc.htkhcn.doitaccanhan;

import java.time.OffsetDateTime;
import java.util.UUID;

public record DoiTacCaNhanResponse(
        UUID id,
        String hoTen,
        String chucVu,
        UUID doiTacId,
        String tenDoiTac,
        String email,
        String dienThoai,
        String ghiChu,
        boolean hoatDong,
        OffsetDateTime ngayTao,
        OffsetDateTime ngaySua) {

    public static DoiTacCaNhanResponse from(DoiTacCaNhan d) {
        return new DoiTacCaNhanResponse(
                d.getId(), d.getHoTen(), d.getChucVu(),
                d.getDoiTac() != null ? d.getDoiTac().getId() : null,
                d.getDoiTac() != null ? d.getDoiTac().getTenDoiTac() : null,
                d.getEmail(), d.getDienThoai(), d.getGhiChu(), d.isHoatDong(), d.getNgayTao(), d.getNgaySua());
    }
}
