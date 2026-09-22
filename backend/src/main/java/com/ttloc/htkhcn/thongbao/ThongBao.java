package com.ttloc.htkhcn.thongbao;

import java.time.Instant;
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

/** Thong bao web + email (SPEC muc 4.7) - hang duoc PL/pgSQL sp_quet_mou_sap_het_han() ghi (V12). */
@Entity
@Table(name = "thong_bao")
@Getter
@Setter
@NoArgsConstructor
public class ThongBao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "loai", nullable = false)
    private String loai;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "muc_do", nullable = false, columnDefinition = "muc_do_canh_bao_enum")
    private MucDoCanhBao mucDo;

    @Column(name = "tieu_de", nullable = false)
    private String tieuDe;

    @Column(name = "noi_dung")
    private String noiDung;

    @Column(name = "bang_lien_quan")
    private String bangLienQuan;

    @Column(name = "ban_ghi_lien_quan_id")
    private UUID banGhiLienQuanId;

    @Column(name = "nguoi_nhan_id", nullable = false)
    private UUID nguoiNhanId;

    @Column(name = "da_doc_web", nullable = false)
    private boolean daDocWeb = false;

    @Column(name = "da_gui_email", nullable = false)
    private boolean daGuiEmail = false;

    @Column(name = "ngay_tao", nullable = false)
    private Instant ngayTao = Instant.now();

    @Column(name = "ngay_doc")
    private Instant ngayDoc;
}
