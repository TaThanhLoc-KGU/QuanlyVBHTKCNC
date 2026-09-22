package com.ttloc.htkhcn.vbplvn;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import com.ttloc.htkhcn.common.TinhTrangHieuLuc;

public record VbplVnResponse(
        UUID id,
        String soHieu,
        String tenVanBan,
        UUID loaiVanBanId,
        String tenLoaiVanBan,
        LocalDate ngayBanHanh,
        LocalDate ngayHieuLuc,
        TinhTrangHieuLuc tinhTrangHieuLuc,
        String coQuanBanHanh,
        String ghiChu,
        String noiDungChinh,
        LocalDate ngayDoiChieuGanNhat,
        UUID nguoiTaoId,
        Instant ngayTao,
        UUID nguoiSuaId,
        Instant ngaySua) {

    public static VbplVnResponse from(VbplVn v) {
        return new VbplVnResponse(
                v.getId(), v.getSoHieu(), v.getTenVanBan(),
                v.getLoaiVanBan().getId(), v.getLoaiVanBan().getTen(),
                v.getNgayBanHanh(), v.getNgayHieuLuc(), v.getTinhTrangHieuLuc(),
                v.getCoQuanBanHanh(), v.getGhiChu(), v.getNoiDungChinh(), v.getNgayDoiChieuGanNhat(),
                v.getNguoiTaoId(), v.getNgayTao(), v.getNguoiSuaId(), v.getNgaySua());
    }
}
