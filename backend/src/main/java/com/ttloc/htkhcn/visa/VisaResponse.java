package com.ttloc.htkhcn.visa;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record VisaResponse(
        UUID id,
        String hoTen,
        String quocTich,
        LoaiCapVisa loaiCap,
        LocalDate ngayCap,
        LocalDate ngayHetHan,
        String coQuanCap,
        UUID mucDichTuDienId,
        String mucDichTen,
        UUID doanVaoId,
        String doanVaoTen,
        String ghiChu,
        Integer nam,
        UUID nguoiTaoId,
        Instant ngayTao,
        UUID nguoiSuaId,
        Instant ngaySua) {

    public static VisaResponse from(Visa v) {
        return new VisaResponse(
                v.getId(), v.getHoTen(), v.getQuocTich(), v.getLoaiCap(), v.getNgayCap(), v.getNgayHetHan(),
                v.getCoQuanCap(),
                v.getMucDich() != null ? v.getMucDich().getId() : null,
                v.getMucDich() != null ? v.getMucDich().getTen() : null,
                v.getDoanVao() != null ? v.getDoanVao().getId() : null,
                v.getDoanVao() != null ? v.getDoanVao().getTenDoan() : null,
                v.getGhiChu(), v.getNam(),
                v.getNguoiTaoId(), v.getNgayTao(), v.getNguoiSuaId(), v.getNgaySua());
    }
}
