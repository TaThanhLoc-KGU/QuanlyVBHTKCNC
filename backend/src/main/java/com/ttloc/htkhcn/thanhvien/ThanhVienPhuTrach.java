package com.ttloc.htkhcn.thanhvien;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.ttloc.htkhcn.danhmuc.TuDien;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Danh sach nhan su phu trach hop tac quoc te - danh muc quan ly nhe (giong
 * tu_dien), khong bat buoc gan voi 1 tai khoan dang nhap he thong. */
@Entity
@Table(name = "thanh_vien_phu_trach")
@Getter
@Setter
@NoArgsConstructor
public class ThanhVienPhuTrach {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "ho_ten", nullable = false)
    private String hoTen;

    @Column(name = "chuc_vu")
    private String chucVu;

    @Column(name = "don_vi")
    private String donVi;

    @Column(name = "email")
    private String email;

    @Column(name = "dien_thoai")
    private String dienThoai;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vai_tro_tu_dien_id")
    private TuDien vaiTro;

    @Column(name = "ghi_chu")
    private String ghiChu;

    @Column(name = "hoat_dong", nullable = false)
    private boolean hoatDong = true;

    @Column(name = "ngay_tao", nullable = false)
    private OffsetDateTime ngayTao;

    @Column(name = "ngay_sua")
    private OffsetDateTime ngaySua;
}
