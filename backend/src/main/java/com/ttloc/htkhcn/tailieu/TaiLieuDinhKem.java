package com.ttloc.htkhcn.tailieu;

import java.time.Instant;
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

/** Dinh kem dung chung cho van_ban_dhkg/vbpl_vn/mou (SPEC muc 4.5, V10 migration). */
@Entity
@Table(name = "tai_lieu_dinh_kem")
@Getter
@Setter
@NoArgsConstructor
public class TaiLieuDinhKem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "bang", nullable = false)
    private String bang;

    @Column(name = "ban_ghi_id", nullable = false)
    private UUID banGhiId;

    @Column(name = "ten_file", nullable = false)
    private String tenFile;

    @Column(name = "duong_dan", nullable = false)
    private String duongDan;

    @Column(name = "kich_thuoc_byte")
    private Long kichThuocByte;

    @Column(name = "loai_mime")
    private String loaiMime;

    @Column(name = "nguoi_tao_id")
    private UUID nguoiTaoId;

    @Column(name = "ngay_tao", nullable = false)
    private Instant ngayTao = Instant.now();
}
