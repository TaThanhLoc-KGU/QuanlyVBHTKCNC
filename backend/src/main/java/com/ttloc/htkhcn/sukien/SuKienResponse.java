package com.ttloc.htkhcn.sukien;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record SuKienResponse(
        UUID id,
        String tenSuKien,
        UUID loaiSuKienTuDienId,
        String loaiSuKienTen,
        UUID linhVucTuDienId,
        String linhVucTen,
        LocalDate thoiGianBatDau,
        LocalDate thoiGianKetThuc,
        String diaDiem,
        String donViToChuc,
        Integer soLuongThamGia,
        String noiDung,
        String ghiChu,
        Integer nam,
        UUID nguoiTaoId,
        Instant ngayTao,
        UUID nguoiSuaId,
        Instant ngaySua) {

    public static SuKienResponse from(SuKien s) {
        return new SuKienResponse(
                s.getId(), s.getTenSuKien(),
                s.getLoaiSuKien() != null ? s.getLoaiSuKien().getId() : null,
                s.getLoaiSuKien() != null ? s.getLoaiSuKien().getTen() : null,
                s.getLinhVuc() != null ? s.getLinhVuc().getId() : null,
                s.getLinhVuc() != null ? s.getLinhVuc().getTen() : null,
                s.getThoiGianBatDau(), s.getThoiGianKetThuc(), s.getDiaDiem(), s.getDonViToChuc(),
                s.getSoLuongThamGia(), s.getNoiDung(), s.getGhiChu(), s.getNam(),
                s.getNguoiTaoId(), s.getNgayTao(), s.getNguoiSuaId(), s.getNgaySua());
    }
}
