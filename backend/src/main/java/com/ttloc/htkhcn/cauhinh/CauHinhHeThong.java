package com.ttloc.htkhcn.cauhinh;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Cau hinh he thong dang khoa-gia tri (SPEC muc 10), doc truc tiep boi cac
 * ham/procedure PL/pgSQL (fn_cau_hinh_int, sp_quet_mou_sap_het_han...). */
@Entity
@Table(name = "cau_hinh_he_thong")
@Getter
@Setter
@NoArgsConstructor
public class CauHinhHeThong {

    @Id
    @Column(name = "ma")
    private String ma;

    @Column(name = "gia_tri", nullable = false)
    private String giaTri;

    @Column(name = "mo_ta")
    private String moTa;

    @Column(name = "ngay_sua", nullable = false)
    private OffsetDateTime ngaySua;
}
