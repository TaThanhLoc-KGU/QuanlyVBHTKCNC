package com.ttloc.htkhcn.baocao;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Lich su xuat 3 bao cao chuyen de (SPEC muc 4.8: "ai, luc nao, thong so loc"). */
@Entity
@Table(name = "lich_su_xuat_bao_cao")
@Getter
@Setter
@NoArgsConstructor
public class LichSuXuatBaoCao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "loai_bao_cao", nullable = false)
    private String loaiBaoCao;

    @Column(name = "nguoi_xuat_id")
    private UUID nguoiXuatId;

    @Column(name = "thoi_diem", nullable = false)
    private Instant thoiDiem = Instant.now();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "tham_so")
    private String thamSo;

    @Column(name = "dinh_dang", nullable = false)
    private String dinhDang;
}
