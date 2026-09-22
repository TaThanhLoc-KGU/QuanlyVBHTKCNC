package com.ttloc.htkhcn.mou;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.ttloc.htkhcn.common.BaseAuditableEntity;
import com.ttloc.htkhcn.doitac.DoiTac;

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

import java.time.LocalDate;

@Entity
@Table(name = "mou")
@Getter
@Setter
@NoArgsConstructor
public class Mou extends BaseAuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "doi_tac_id", nullable = false)
    private DoiTac doiTac;

    @Column(name = "ten_tai_lieu")
    private String tenTaiLieu;

    @Column(name = "ngay_ban_hanh", nullable = false)
    private LocalDate ngayBanHanh;

    @Column(name = "ngay_het_han")
    private LocalDate ngayHetHan;

    @Column(name = "ca_nhan_dau_moi")
    private String caNhanDauMoi;

    @Column(name = "don_vi_thuc_hien")
    private String donViThucHien;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "pham_vi_hop_tac", columnDefinition = "pham_vi_hop_tac_enum")
    private PhamViHopTac phamViHopTac;

    @Column(name = "linh_vuc_hop_tac")
    private String linhVucHopTac;

    @Column(name = "dau_moi_ghi_trong_mou")
    private String dauMoiGhiTrongMou;

    @Column(name = "dai_dien_kgu_ky")
    private String daiDienKguKy;

    @Column(name = "thoi_han_hieu_luc")
    private String thoiHanHieuLuc;

    @Column(name = "so_cong_van")
    private String soCongVan;
}
