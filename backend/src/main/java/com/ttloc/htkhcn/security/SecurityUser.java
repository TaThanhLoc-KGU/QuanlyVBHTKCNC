package com.ttloc.htkhcn.security;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.ttloc.htkhcn.common.ModuleKey;
import com.ttloc.htkhcn.user.NguoiDung;
import com.ttloc.htkhcn.user.TrangThaiTaiKhoan;

import lombok.Getter;

/**
 * Boc NguoiDung thanh UserDetails. Vai tro duoc expose duoi dang GrantedAuthority
 * dang "ROLE_ADMIN"/"ROLE_EDITOR"/"ROLE_VIEWER" (+ vai tro tuy chinh Admin tao
 * them) de dung duoc voi @PreAuthorize("hasRole(...)").
 */
@Getter
public class SecurityUser implements UserDetails {

    private final UUID id;
    private final String tenDangNhap;
    private final String matKhauHash;
    private final String hoTen;
    private final boolean hoatDong;
    private final Set<String> vaiTroCodes;
    private final Set<ModuleKey> bienTapMoDun;

    public SecurityUser(NguoiDung nd) {
        this.id = nd.getId();
        this.tenDangNhap = nd.getTenDangNhap();
        this.matKhauHash = nd.getMatKhauHash();
        this.hoTen = nd.getHoTen();
        this.hoatDong = nd.getTrangThai() == TrangThaiTaiKhoan.HOAT_DONG;
        this.vaiTroCodes = nd.getVaiTros().stream().map(v -> v.getMaVaiTro()).collect(Collectors.toSet());
        this.bienTapMoDun = Set.copyOf(nd.getBienTapMoDun());
    }

    public boolean isAdmin() {
        return vaiTroCodes.contains("ADMIN");
    }

    public boolean coTheSuaModule(ModuleKey moduleKey) {
        return isAdmin() || (vaiTroCodes.contains("EDITOR") && bienTapMoDun.contains(moduleKey));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return vaiTroCodes.stream().map(c -> new SimpleGrantedAuthority("ROLE_" + c)).collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return matKhauHash;
    }

    @Override
    public String getUsername() {
        return tenDangNhap;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return hoatDong;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return hoatDong;
    }

    public List<String> vaiTroCodesList() {
        return List.copyOf(vaiTroCodes);
    }
}
