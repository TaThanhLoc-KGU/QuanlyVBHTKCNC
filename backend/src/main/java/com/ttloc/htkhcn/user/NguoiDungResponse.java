package com.ttloc.htkhcn.user;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.ttloc.htkhcn.common.ModuleKey;

public record NguoiDungResponse(
        UUID id,
        String tenDangNhap,
        String email,
        String hoTen,
        TrangThaiTaiKhoan trangThai,
        boolean phaiDoiMatKhau,
        List<String> vaiTro,
        Set<ModuleKey> bienTapMoDun,
        Instant ngayTao) {

    public static NguoiDungResponse from(NguoiDung u) {
        return new NguoiDungResponse(
                u.getId(),
                u.getTenDangNhap(),
                u.getEmail(),
                u.getHoTen(),
                u.getTrangThai(),
                u.isPhaiDoiMatKhau(),
                u.getVaiTros().stream().map(VaiTro::getMaVaiTro).toList(),
                u.getBienTapMoDun(),
                u.getNgayTao());
    }
}
