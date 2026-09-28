package com.ttloc.htkhcn.danhmuc;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** 1 dong gia tri trong 1 "tu dien" (danh muc nho dung chung) - vi du 1 quoc
 * gia, 1 nguon kinh phi... Xem V21 migration ve ly do dung 1 bang chung. */
@Entity
@Table(name = "tu_dien")
@Getter
@Setter
@NoArgsConstructor
public class TuDien {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "loai", nullable = false, columnDefinition = "loai_tu_dien_enum")
    private LoaiTuDien loai;

    @Column(name = "ma")
    private String ma;

    @Column(name = "ten", nullable = false)
    private String ten;

    @Column(name = "mo_ta")
    private String moTa;

    @Column(name = "thu_tu", nullable = false)
    private int thuTu;

    @Column(name = "hoat_dong", nullable = false)
    private boolean hoatDong = true;

    @Column(name = "ngay_tao", nullable = false)
    private OffsetDateTime ngayTao;

    @Column(name = "ngay_sua")
    private OffsetDateTime ngaySua;
}
