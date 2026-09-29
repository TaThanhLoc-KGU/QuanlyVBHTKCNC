package com.ttloc.htkhcn.detai;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.ttloc.htkhcn.common.BaseAuditableEntity;
import com.ttloc.htkhcn.danhmuc.TuDien;
import com.ttloc.htkhcn.user.NguoiDung;

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
@Table(name = "de_tai")
@Getter
@Setter
@NoArgsConstructor
public class DeTai extends BaseAuditableEntity {

    @Column(name = "ma_de_tai")
    private String maDeTai;

    @Column(name = "ten_de_tai", nullable = false)
    private String tenDeTai;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chu_nhiem_id")
    private NguoiDung chuNhiem;

    @Column(name = "chu_nhiem_ngoai")
    private String chuNhiemNgoai;

    @Column(name = "don_vi_thuc_hien")
    private String donViThucHien;

    @Column(name = "don_vi_chu_quan")
    private String donViChuQuan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phan_loai_tu_dien_id")
    private TuDien phanLoai;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loai_hinh_tu_dien_id")
    private TuDien loaiHinh;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguon_kinh_phi_tu_dien_id")
    private TuDien nguonKinhPhi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "linh_vuc_id")
    private LinhVucNghienCuu linhVuc;

    @Column(name = "nam_de_xuat")
    private Integer namDeXuat;

    @Column(name = "thoi_gian_bat_dau")
    private LocalDate thoiGianBatDau;

    @Column(name = "thoi_gian_ket_thuc")
    private LocalDate thoiGianKetThuc;

    @Column(name = "kinh_phi_de_xuat")
    private BigDecimal kinhPhiDeXuat;

    @Column(name = "kinh_phi_duyet")
    private BigDecimal kinhPhiDuyet;

    @Column(name = "muc_tieu")
    private String mucTieu;

    @Column(name = "noi_dung")
    private String noiDung;

    @Column(name = "san_pham_du_kien")
    private String sanPhamDuKien;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "trang_thai", nullable = false, columnDefinition = "trang_thai_de_tai_enum")
    private TrangThaiDeTai trangThai = TrangThaiDeTai.DE_XUAT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "muc_xep_loai_tu_dien_id")
    private TuDien mucXepLoai;

    @Column(name = "ly_do_huy")
    private String lyDoHuy;

    @Column(name = "ghi_chu")
    private String ghiChu;
}
