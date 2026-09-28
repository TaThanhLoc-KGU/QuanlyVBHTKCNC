package com.ttloc.htkhcn.visa;

import java.time.LocalDate;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.ttloc.htkhcn.common.BaseAuditableEntity;
import com.ttloc.htkhcn.danhmuc.TuDien;
import com.ttloc.htkhcn.doanvao.DoanVao;

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
@Table(name = "visa")
@Getter
@Setter
@NoArgsConstructor
public class Visa extends BaseAuditableEntity {

    @Column(name = "ho_ten", nullable = false)
    private String hoTen;

    @Column(name = "quoc_tich")
    private String quocTich;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "loai_cap", nullable = false, columnDefinition = "loai_cap_visa_enum")
    private LoaiCapVisa loaiCap;

    @Column(name = "ngay_cap", nullable = false)
    private LocalDate ngayCap;

    @Column(name = "ngay_het_han")
    private LocalDate ngayHetHan;

    @Column(name = "co_quan_cap")
    private String coQuanCap;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "muc_dich_tu_dien_id")
    private TuDien mucDich;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doan_vao_id")
    private DoanVao doanVao;

    @Column(name = "ghi_chu")
    private String ghiChu;

    @Column(name = "nam", insertable = false, updatable = false)
    private Integer nam;
}
