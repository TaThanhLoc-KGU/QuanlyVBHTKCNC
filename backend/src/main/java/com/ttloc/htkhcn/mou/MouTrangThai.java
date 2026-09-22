package com.ttloc.htkhcn.mou;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.ttloc.htkhcn.doitac.LoaiDoiTac;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity chi doc, map vao VIEW v_mou_trang_thai (V7 migration) - trang thai va
 * so ngay con lai duoc Postgres tinh san (phu thuoc CURRENT_DATE nen phai la
 * VIEW, khong the la generated column - xem SPEC muc 3.4, 6.4).
 */
@Entity
@Table(name = "v_mou_trang_thai")
@Immutable
@Getter
@Setter
@NoArgsConstructor
public class MouTrangThai {

    @Id
    private UUID id;

    @Column(name = "doi_tac_id")
    private UUID doiTacId;

    @Column(name = "ten_tai_lieu")
    private String tenTaiLieu;

    @Column(name = "ngay_ban_hanh")
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

    @Column(name = "nguoi_tao_id")
    private UUID nguoiTaoId;

    @Column(name = "ngay_tao")
    private Instant ngayTao;

    @Column(name = "nguoi_sua_id")
    private UUID nguoiSuaId;

    @Column(name = "ngay_sua")
    private Instant ngaySua;

    @Column(name = "ten_doi_tac")
    private String tenDoiTac;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "loai_doi_tac", columnDefinition = "loai_doi_tac_enum")
    private LoaiDoiTac loaiDoiTac;

    @Column(name = "doi_tac_quoc_gia")
    private String doiTacQuocGia;

    @Column(name = "doi_tac_dia_chi")
    private String doiTacDiaChi;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "trang_thai", columnDefinition = "trang_thai_mou_enum")
    private TrangThaiMou trangThai;

    @Column(name = "so_ngay_con_lai")
    private Integer soNgayConLai;
}
