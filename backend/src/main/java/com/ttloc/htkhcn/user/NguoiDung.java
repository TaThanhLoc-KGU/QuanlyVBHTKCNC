package com.ttloc.htkhcn.user;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.ttloc.htkhcn.common.ModuleKey;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "nguoi_dung")
@Getter
@Setter
@NoArgsConstructor
public class NguoiDung {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "ten_dang_nhap", nullable = false, unique = true)
    private String tenDangNhap;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "mat_khau_hash", nullable = false)
    private String matKhauHash;

    @Column(name = "ho_ten", nullable = false)
    private String hoTen;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "trang_thai", nullable = false, columnDefinition = "trang_thai_tai_khoan_enum")
    private TrangThaiTaiKhoan trangThai = TrangThaiTaiKhoan.HOAT_DONG;

    @Column(name = "so_lan_dang_nhap_sai", nullable = false)
    private int soLanDangNhapSai = 0;

    @Column(name = "khoa_den")
    private Instant khoaDen;

    @Column(name = "phai_doi_mat_khau", nullable = false)
    private boolean phaiDoiMatKhau = true;

    @Column(name = "ngay_tao", nullable = false)
    private Instant ngayTao = Instant.now();

    @Column(name = "ngay_sua")
    private Instant ngaySua;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "nguoi_dung_vai_tro",
            joinColumns = @JoinColumn(name = "nguoi_dung_id"),
            inverseJoinColumns = @JoinColumn(name = "vai_tro_id"))
    private Set<VaiTro> vaiTros = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "bien_tap_mo_dun", joinColumns = @JoinColumn(name = "nguoi_dung_id"))
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "mo_dun", columnDefinition = "mo_dun_enum")
    private Set<ModuleKey> bienTapMoDun = new HashSet<>();

    public boolean coVaiTro(String maVaiTro) {
        return vaiTros.stream().anyMatch(v -> v.getMaVaiTro().equals(maVaiTro));
    }
}
