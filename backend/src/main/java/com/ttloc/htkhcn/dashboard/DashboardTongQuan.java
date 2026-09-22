package com.ttloc.htkhcn.dashboard;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

/** Doc tu materialized view mv_dashboard_tong_quan (V14, refresh dinh ky). */
@Entity
@Table(name = "mv_dashboard_tong_quan")
@Immutable
@Getter
@Setter
@NoArgsConstructor
public class DashboardTongQuan {

    @Id
    @Column(name = "lam_moi_luc")
    private Instant lamMoiLuc;

    @Column(name = "tong_van_ban_dhkg_hieu_luc")
    private Long tongVanBanDhkgHieuLuc;

    @Column(name = "tong_vbpl_vn_hieu_luc")
    private Long tongVbplVnHieuLuc;

    @Column(name = "tong_mou_con_hieu_luc")
    private Long tongMouConHieuLuc;

    @Column(name = "tong_mou_sap_het_han")
    private Long tongMouSapHetHan;

    @Column(name = "tong_mou_da_het_han")
    private Long tongMouDaHetHan;

    @Column(name = "tong_doan_vao_nam_hien_tai")
    private Long tongDoanVaoNamHienTai;

    @Column(name = "tong_khach_nuoc_ngoai_nam_hien_tai")
    private Long tongKhachNuocNgoaiNamHienTai;

    @Column(name = "tong_doan_ra_nam_hien_tai")
    private Long tongDoanRaNamHienTai;

    @Column(name = "tong_luot_can_bo_di_cong_tac_nam_hien_tai")
    private Long tongLuotCanBoDiCongTacNamHienTai;
}
