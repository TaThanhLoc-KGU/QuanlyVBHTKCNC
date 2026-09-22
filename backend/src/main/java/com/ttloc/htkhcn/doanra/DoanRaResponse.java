package com.ttloc.htkhcn.doanra;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record DoanRaResponse(
        UUID id,
        UUID doiTacId,
        String tenDoiTac,
        LocalDate thoiGianDi,
        LocalDate thoiGianVe,
        String diaDiemDi,
        String diaDiemDen,
        Integer soLuongDoan,
        String thanhPhan,
        String quocGiaLamViec,
        String noiDungLamViec,
        Integer nam,
        Integer soNgay,
        UUID nguoiTaoId,
        Instant ngayTao,
        UUID nguoiSuaId,
        Instant ngaySua) {

    public static DoanRaResponse from(DoanRa d) {
        return new DoanRaResponse(
                d.getId(), d.getDoiTac().getId(), d.getDoiTac().getTenDoiTac(),
                d.getThoiGianDi(), d.getThoiGianVe(), d.getDiaDiemDi(), d.getDiaDiemDen(),
                d.getSoLuongDoan(), d.getThanhPhan(), d.getQuocGiaLamViec(), d.getNoiDungLamViec(),
                d.getNam(), d.getSoNgay(),
                d.getNguoiTaoId(), d.getNgayTao(), d.getNguoiSuaId(), d.getNgaySua());
    }
}
