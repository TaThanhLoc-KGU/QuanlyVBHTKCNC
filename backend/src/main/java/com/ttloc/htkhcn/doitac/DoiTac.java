package com.ttloc.htkhcn.doitac;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.ttloc.htkhcn.common.BaseAuditableEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "doi_tac")
@Getter
@Setter
@NoArgsConstructor
public class DoiTac extends BaseAuditableEntity {

    @Column(name = "ten_doi_tac", nullable = false)
    private String tenDoiTac;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "loai_doi_tac", nullable = false, columnDefinition = "loai_doi_tac_enum")
    private LoaiDoiTac loaiDoiTac;

    @Column(name = "quoc_gia")
    private String quocGia;

    @Column(name = "dia_chi")
    private String diaChi;

    @Column(name = "thong_tin_lien_he")
    private String thongTinLienHe;

    @Column(name = "ghi_chu")
    private String ghiChu;
}
