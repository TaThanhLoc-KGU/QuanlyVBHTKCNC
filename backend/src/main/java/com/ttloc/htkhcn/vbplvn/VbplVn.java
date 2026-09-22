package com.ttloc.htkhcn.vbplvn;

import java.time.LocalDate;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.ttloc.htkhcn.common.BaseAuditableEntity;
import com.ttloc.htkhcn.common.TinhTrangHieuLuc;
import com.ttloc.htkhcn.danhmuc.LoaiVanBan;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "vbpl_vn")
@Getter
@Setter
@NoArgsConstructor
public class VbplVn extends BaseAuditableEntity {

    @Column(name = "so_hieu", nullable = false, unique = true)
    private String soHieu;

    @Column(name = "ten_van_ban", nullable = false)
    private String tenVanBan;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "loai_van_ban_id", nullable = false)
    private LoaiVanBan loaiVanBan;

    @Column(name = "ngay_ban_hanh", nullable = false)
    private LocalDate ngayBanHanh;

    @Column(name = "ngay_hieu_luc")
    private LocalDate ngayHieuLuc;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "tinh_trang_hieu_luc", nullable = false, columnDefinition = "tinh_trang_hieu_luc_enum")
    private TinhTrangHieuLuc tinhTrangHieuLuc;

    @Column(name = "co_quan_ban_hanh", nullable = false)
    private String coQuanBanHanh;

    @Column(name = "ghi_chu")
    private String ghiChu;

    @Column(name = "noi_dung_chinh")
    private String noiDungChinh;

    @Column(name = "ngay_doi_chieu_gan_nhat")
    private LocalDate ngayDoiChieuGanNhat;
}
