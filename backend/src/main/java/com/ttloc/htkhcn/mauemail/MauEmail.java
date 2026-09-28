package com.ttloc.htkhcn.mauemail;

import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Mau email co the sua qua giao dien - ThongBaoEmailScheduler doc theo "ma"
 * (vd THONG_BAO_MOU_GOP), thay {{placeholder}}, fallback ve noi dung code
 * cung neu khong tim thay mau hoat dong tuong ung. */
@Entity
@Table(name = "mau_email")
@Getter
@Setter
@NoArgsConstructor
public class MauEmail {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "ma", nullable = false, unique = true)
    private String ma;

    @Column(name = "ten_mau", nullable = false)
    private String tenMau;

    @Column(name = "tieu_de", nullable = false)
    private String tieuDe;

    @Column(name = "noi_dung", nullable = false)
    private String noiDung;

    @Column(name = "mo_ta")
    private String moTa;

    @Column(name = "hoat_dong", nullable = false)
    private boolean hoatDong = true;

    @Column(name = "ngay_tao", nullable = false)
    private OffsetDateTime ngayTao;

    @Column(name = "ngay_sua")
    private OffsetDateTime ngaySua;
}
