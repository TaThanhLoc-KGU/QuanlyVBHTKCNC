package com.ttloc.htkhcn.doanra;

import java.time.LocalDate;

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
@Table(name = "doan_ra")
@Getter
@Setter
@NoArgsConstructor
public class DoanRa extends BaseAuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "doi_tac_id", nullable = false)
    private DoiTac doiTac;

    @Column(name = "thoi_gian_di", nullable = false)
    private LocalDate thoiGianDi;

    @Column(name = "thoi_gian_ve", nullable = false)
    private LocalDate thoiGianVe;

    @Column(name = "dia_diem_di")
    private String diaDiemDi;

    @Column(name = "dia_diem_den")
    private String diaDiemDen;

    @Column(name = "so_luong_doan", nullable = false)
    private Integer soLuongDoan;

    @Column(name = "thanh_phan")
    private String thanhPhan;

    @Column(name = "quoc_gia_lam_viec", nullable = false)
    private String quocGiaLamViec;

    @Column(name = "noi_dung_lam_viec")
    private String noiDungLamViec;

    @Column(name = "nam", insertable = false, updatable = false)
    private Integer nam;

    @Column(name = "so_ngay", insertable = false, updatable = false)
    private Integer soNgay;
}
