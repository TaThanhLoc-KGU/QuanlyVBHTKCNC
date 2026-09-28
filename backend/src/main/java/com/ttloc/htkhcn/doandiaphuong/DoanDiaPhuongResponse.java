package com.ttloc.htkhcn.doandiaphuong;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record DoanDiaPhuongResponse(
        UUID id,
        String tenDoan,
        UUID doiTacId,
        String tenDoiTac,
        LocalDate thoiGianDi,
        LocalDate thoiGianVe,
        String diaDiem,
        UUID mucTieuTuDienId,
        String mucTieuTen,
        UUID nguonKinhPhiTuDienId,
        String nguonKinhPhiTen,
        String thanhPhan,
        String noiDungLamViec,
        String ghiChu,
        Integer nam,
        UUID nguoiTaoId,
        Instant ngayTao,
        UUID nguoiSuaId,
        Instant ngaySua) {

    public static DoanDiaPhuongResponse from(DoanDiaPhuong d) {
        return new DoanDiaPhuongResponse(
                d.getId(), d.getTenDoan(),
                d.getDoiTac() != null ? d.getDoiTac().getId() : null,
                d.getDoiTac() != null ? d.getDoiTac().getTenDoiTac() : null,
                d.getThoiGianDi(), d.getThoiGianVe(), d.getDiaDiem(),
                d.getMucTieu() != null ? d.getMucTieu().getId() : null,
                d.getMucTieu() != null ? d.getMucTieu().getTen() : null,
                d.getNguonKinhPhi() != null ? d.getNguonKinhPhi().getId() : null,
                d.getNguonKinhPhi() != null ? d.getNguonKinhPhi().getTen() : null,
                d.getThanhPhan(), d.getNoiDungLamViec(), d.getGhiChu(), d.getNam(),
                d.getNguoiTaoId(), d.getNgayTao(), d.getNguoiSuaId(), d.getNgaySua());
    }
}
