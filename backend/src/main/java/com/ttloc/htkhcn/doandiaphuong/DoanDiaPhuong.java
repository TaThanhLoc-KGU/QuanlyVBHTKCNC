package com.ttloc.htkhcn.doandiaphuong;

import java.time.LocalDate;

import com.ttloc.htkhcn.common.BaseAuditableEntity;
import com.ttloc.htkhcn.danhmuc.TuDien;
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

/** Doan di lam viec TRONG NUOC (khac "Doan ra" - di cong tac NUOC NGOAI). */
@Entity
@Table(name = "doan_dia_phuong")
@Getter
@Setter
@NoArgsConstructor
public class DoanDiaPhuong extends BaseAuditableEntity {

    @Column(name = "ten_doan", nullable = false)
    private String tenDoan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doi_tac_id")
    private DoiTac doiTac;

    @Column(name = "thoi_gian_di", nullable = false)
    private LocalDate thoiGianDi;

    @Column(name = "thoi_gian_ve", nullable = false)
    private LocalDate thoiGianVe;

    @Column(name = "dia_diem")
    private String diaDiem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "muc_tieu_tu_dien_id")
    private TuDien mucTieu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguon_kinh_phi_tu_dien_id")
    private TuDien nguonKinhPhi;

    @Column(name = "thanh_phan")
    private String thanhPhan;

    @Column(name = "noi_dung_lam_viec")
    private String noiDungLamViec;

    @Column(name = "ghi_chu")
    private String ghiChu;

    @Column(name = "nam", insertable = false, updatable = false)
    private Integer nam;
}
