package com.ttloc.htkhcn.mou;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import com.ttloc.htkhcn.doitac.LoaiDoiTac;

public record MouResponse(
        UUID id,
        UUID doiTacId,
        String tenDoiTac,
        LoaiDoiTac loaiDoiTac,
        String doiTacQuocGia,
        String doiTacDiaChi,
        String tenTaiLieu,
        LocalDate ngayBanHanh,
        LocalDate ngayHetHan,
        String caNhanDauMoi,
        String donViThucHien,
        PhamViHopTac phamViHopTac,
        String linhVucHopTac,
        String dauMoiGhiTrongMou,
        String daiDienKguKy,
        String thoiHanHieuLuc,
        String soCongVan,
        TrangThaiMou trangThai,
        Integer soNgayConLai,
        UUID nguoiTaoId,
        Instant ngayTao,
        UUID nguoiSuaId,
        Instant ngaySua) {

    public static MouResponse from(MouTrangThai m) {
        return new MouResponse(
                m.getId(), m.getDoiTacId(), m.getTenDoiTac(), m.getLoaiDoiTac(),
                m.getDoiTacQuocGia(), m.getDoiTacDiaChi(), m.getTenTaiLieu(),
                m.getNgayBanHanh(), m.getNgayHetHan(), m.getCaNhanDauMoi(), m.getDonViThucHien(),
                m.getPhamViHopTac(), m.getLinhVucHopTac(), m.getDauMoiGhiTrongMou(),
                m.getDaiDienKguKy(), m.getThoiHanHieuLuc(), m.getSoCongVan(),
                m.getTrangThai(), m.getSoNgayConLai(),
                m.getNguoiTaoId(), m.getNgayTao(), m.getNguoiSuaId(), m.getNgaySua());
    }
}
