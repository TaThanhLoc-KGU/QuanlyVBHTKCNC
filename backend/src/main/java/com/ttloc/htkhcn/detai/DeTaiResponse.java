package com.ttloc.htkhcn.detai;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record DeTaiResponse(
        UUID id,
        String maDeTai,
        String tenDeTai,
        UUID chuNhiemId,
        String chuNhiemTen,
        String chuNhiemNgoai,
        String donViThucHien,
        String donViChuQuan,
        UUID phanLoaiTuDienId,
        String phanLoaiTen,
        UUID loaiHinhTuDienId,
        String loaiHinhTen,
        UUID nguonKinhPhiTuDienId,
        String nguonKinhPhiTen,
        UUID linhVucId,
        String linhVucTen,
        Integer namDeXuat,
        LocalDate thoiGianBatDau,
        LocalDate thoiGianKetThuc,
        BigDecimal kinhPhiDeXuat,
        BigDecimal kinhPhiDuyet,
        String mucTieu,
        String noiDung,
        String sanPhamDuKien,
        TrangThaiDeTai trangThai,
        UUID mucXepLoaiTuDienId,
        String mucXepLoaiTen,
        String lyDoHuy,
        String ghiChu,
        UUID nguoiTaoId,
        Instant ngayTao,
        UUID nguoiSuaId,
        Instant ngaySua) {

    public static DeTaiResponse from(DeTai d) {
        return new DeTaiResponse(
                d.getId(), d.getMaDeTai(), d.getTenDeTai(),
                d.getChuNhiem() != null ? d.getChuNhiem().getId() : null,
                d.getChuNhiem() != null ? d.getChuNhiem().getHoTen() : null,
                d.getChuNhiemNgoai(), d.getDonViThucHien(), d.getDonViChuQuan(),
                d.getPhanLoai() != null ? d.getPhanLoai().getId() : null,
                d.getPhanLoai() != null ? d.getPhanLoai().getTen() : null,
                d.getLoaiHinh() != null ? d.getLoaiHinh().getId() : null,
                d.getLoaiHinh() != null ? d.getLoaiHinh().getTen() : null,
                d.getNguonKinhPhi() != null ? d.getNguonKinhPhi().getId() : null,
                d.getNguonKinhPhi() != null ? d.getNguonKinhPhi().getTen() : null,
                d.getLinhVuc() != null ? d.getLinhVuc().getId() : null,
                d.getLinhVuc() != null ? d.getLinhVuc().getTen() : null,
                d.getNamDeXuat(), d.getThoiGianBatDau(), d.getThoiGianKetThuc(),
                d.getKinhPhiDeXuat(), d.getKinhPhiDuyet(),
                d.getMucTieu(), d.getNoiDung(), d.getSanPhamDuKien(),
                d.getTrangThai(),
                d.getMucXepLoai() != null ? d.getMucXepLoai().getId() : null,
                d.getMucXepLoai() != null ? d.getMucXepLoai().getTen() : null,
                d.getLyDoHuy(), d.getGhiChu(),
                d.getNguoiTaoId(), d.getNgayTao(), d.getNguoiSuaId(), d.getNgaySua());
    }
}
