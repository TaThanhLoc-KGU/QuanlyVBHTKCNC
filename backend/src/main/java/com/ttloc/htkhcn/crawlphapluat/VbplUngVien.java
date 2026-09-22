package com.ttloc.htkhcn.crawlphapluat;

import java.time.LocalDate;
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

/** Van ban phap luat crawl duoc, cho Admin/Editor VBPL_VN duyet truoc khi
 * nhan chinh thuc vao module VBPL VN (khong tu dong ghi thang). */
@Entity
@Table(name = "vbpl_ung_vien")
@Getter
@Setter
@NoArgsConstructor
public class VbplUngVien {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "nguon", nullable = false, columnDefinition = "nguon_crawl_enum")
    private NguonCrawl nguon;

    @Column(name = "so_hieu", nullable = false)
    private String soHieu;

    @Column(name = "ten_van_ban", nullable = false)
    private String tenVanBan;

    @Column(name = "loai_van_ban_text")
    private String loaiVanBanText;

    @Column(name = "ngay_ban_hanh")
    private LocalDate ngayBanHanh;

    @Column(name = "co_quan_ban_hanh")
    private String coQuanBanHanh;

    @Column(name = "url_nguon", nullable = false)
    private String urlNguon;

    @Column(name = "tu_khoa_khop")
    private String tuKhoaKhop;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "trang_thai", nullable = false, columnDefinition = "trang_thai_ung_vien_enum")
    private TrangThaiUngVien trangThai = TrangThaiUngVien.CHUA_XU_LY;

    @Column(name = "vbpl_vn_id")
    private UUID vbplVnId;

    @Column(name = "ngay_crawl", nullable = false)
    private OffsetDateTime ngayCrawl;

    @Column(name = "nguoi_xu_ly_id")
    private UUID nguoiXuLyId;

    @Column(name = "ngay_xu_ly")
    private OffsetDateTime ngayXuLy;
}
