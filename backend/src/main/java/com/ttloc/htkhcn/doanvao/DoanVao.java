package com.ttloc.htkhcn.doanvao;

import java.time.LocalDate;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.ttloc.htkhcn.common.BaseAuditableEntity;
import com.ttloc.htkhcn.doitac.DoiTac;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "doan_vao")
@Getter
@Setter
@NoArgsConstructor
public class DoanVao extends BaseAuditableEntity {

    @Column(name = "ten_doan", nullable = false)
    private String tenDoan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doi_tac_id")
    private DoiTac doiTac;

    @Column(name = "thoi_gian_den", nullable = false)
    private LocalDate thoiGianDen;

    @Column(name = "thoi_gian_di", nullable = false)
    private LocalDate thoiGianDi;

    @Column(name = "so_luong_nguoi_nuoc_ngoai", nullable = false)
    private Integer soLuongNguoiNuocNgoai;

    @Column(name = "so_luong_nguoi_viet_nam")
    private Integer soLuongNguoiVietNam;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "quoc_tich", columnDefinition = "text[]", nullable = false)
    private String[] quocTich;

    @Column(name = "noi_dung_lam_viec")
    private String noiDungLamViec;

    @Column(name = "nam", insertable = false, updatable = false)
    private Integer nam;

    @Column(name = "so_ngay", insertable = false, updatable = false)
    private Integer soNgay;
}
