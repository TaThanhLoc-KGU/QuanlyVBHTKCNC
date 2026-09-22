package com.ttloc.htkhcn.doanvao;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record DoanVaoResponse(
        UUID id,
        String tenDoan,
        UUID doiTacId,
        String tenDoiTac,
        LocalDate thoiGianDen,
        LocalDate thoiGianDi,
        Integer soLuongNguoiNuocNgoai,
        Integer soLuongNguoiVietNam,
        String[] quocTich,
        String noiDungLamViec,
        Integer nam,
        Integer soNgay,
        UUID nguoiTaoId,
        Instant ngayTao,
        UUID nguoiSuaId,
        Instant ngaySua) {

    public static DoanVaoResponse from(DoanVao d) {
        return new DoanVaoResponse(
                d.getId(), d.getTenDoan(),
                d.getDoiTac() != null ? d.getDoiTac().getId() : null,
                d.getDoiTac() != null ? d.getDoiTac().getTenDoiTac() : null,
                d.getThoiGianDen(), d.getThoiGianDi(),
                d.getSoLuongNguoiNuocNgoai(), d.getSoLuongNguoiVietNam(),
                d.getQuocTich(), d.getNoiDungLamViec(), d.getNam(), d.getSoNgay(),
                d.getNguoiTaoId(), d.getNgayTao(), d.getNguoiSuaId(), d.getNgaySua());
    }
}
