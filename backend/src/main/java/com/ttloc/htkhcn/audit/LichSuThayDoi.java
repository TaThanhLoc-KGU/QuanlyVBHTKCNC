package com.ttloc.htkhcn.audit;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.Immutable;
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

/** Doc lai tu bang lich_su_thay_doi (V11) - chi doc, khong ghi qua JPA (trigger tu ghi). */
@Entity
@Table(name = "lich_su_thay_doi")
@Immutable
@Getter
@Setter
@NoArgsConstructor
public class LichSuThayDoi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bang", nullable = false)
    private String bang;

    @Column(name = "ban_ghi_id", nullable = false)
    private UUID banGhiId;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "hanh_dong", nullable = false, columnDefinition = "hanh_dong_enum")
    private HanhDong hanhDong;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "du_lieu_truoc")
    private String duLieuTruoc;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "du_lieu_sau")
    private String duLieuSau;

    @Column(name = "nguoi_thuc_hien_id")
    private UUID nguoiThucHienId;

    @Column(name = "thoi_diem", nullable = false)
    private Instant thoiDiem;
}
