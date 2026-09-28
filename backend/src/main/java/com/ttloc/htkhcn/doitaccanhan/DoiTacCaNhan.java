package com.ttloc.htkhcn.doitaccanhan;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.ttloc.htkhcn.doitac.DoiTac;

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

/** Ca nhan lien he (khac "Tu dien" - khong phai gia tri danh muc phang), co
 * the gan voi 1 doi tac to chuc (khong bat buoc). */
@Entity
@Table(name = "doi_tac_ca_nhan")
@Getter
@Setter
@NoArgsConstructor
public class DoiTacCaNhan {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "ho_ten", nullable = false)
    private String hoTen;

    @Column(name = "chuc_vu")
    private String chucVu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doi_tac_id")
    private DoiTac doiTac;

    @Column(name = "email")
    private String email;

    @Column(name = "dien_thoai")
    private String dienThoai;

    @Column(name = "ghi_chu")
    private String ghiChu;

    @Column(name = "hoat_dong", nullable = false)
    private boolean hoatDong = true;

    @Column(name = "ngay_tao", nullable = false)
    private OffsetDateTime ngayTao;

    @Column(name = "ngay_sua")
    private OffsetDateTime ngaySua;
}
