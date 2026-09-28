package com.ttloc.htkhcn.sukien;

import java.time.LocalDate;

import com.ttloc.htkhcn.common.BaseAuditableEntity;
import com.ttloc.htkhcn.danhmuc.TuDien;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Gop "Quan ly hoi nghi, hoi thao" va "Quan ly thong tin su kien" thanh 1
 * module - phan biet qua loaiSuKien (Hoi nghi/Hoi thao/Toa dam/Le ky ket). */
@Entity
@Table(name = "su_kien")
@Getter
@Setter
@NoArgsConstructor
public class SuKien extends BaseAuditableEntity {

    @Column(name = "ten_su_kien", nullable = false)
    private String tenSuKien;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loai_su_kien_tu_dien_id")
    private TuDien loaiSuKien;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "linh_vuc_tu_dien_id")
    private TuDien linhVuc;

    @Column(name = "thoi_gian_bat_dau", nullable = false)
    private LocalDate thoiGianBatDau;

    @Column(name = "thoi_gian_ket_thuc")
    private LocalDate thoiGianKetThuc;

    @Column(name = "dia_diem")
    private String diaDiem;

    @Column(name = "don_vi_to_chuc")
    private String donViToChuc;

    @Column(name = "so_luong_tham_gia")
    private Integer soLuongThamGia;

    @Column(name = "noi_dung")
    private String noiDung;

    @Column(name = "ghi_chu")
    private String ghiChu;

    @Column(name = "nam", insertable = false, updatable = false)
    private Integer nam;
}
