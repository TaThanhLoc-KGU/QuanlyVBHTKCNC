package com.ttloc.htkhcn.detai;

import java.time.OffsetDateTime;
import java.util.UUID;

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

/** 1 dong trong danh muc "Linh vuc nghien cuu" phan cap 3 tang (cap 1/2/3),
 * rieng voi tu_dien (danh sach phang) vi can quan he cha-con that su. */
@Entity
@Table(name = "linh_vuc_nghien_cuu")
@Getter
@Setter
@NoArgsConstructor
public class LinhVucNghienCuu {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "ma")
    private String ma;

    @Column(name = "ten", nullable = false)
    private String ten;

    @Column(name = "cap", nullable = false)
    private short cap;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cha_id")
    private LinhVucNghienCuu cha;

    @Column(name = "thu_tu", nullable = false)
    private int thuTu;

    @Column(name = "hoat_dong", nullable = false)
    private boolean hoatDong = true;

    @Column(name = "ngay_tao", nullable = false)
    private OffsetDateTime ngayTao;

    @Column(name = "ngay_sua")
    private OffsetDateTime ngaySua;
}
