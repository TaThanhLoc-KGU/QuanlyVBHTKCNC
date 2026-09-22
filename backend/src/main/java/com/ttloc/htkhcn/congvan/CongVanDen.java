package com.ttloc.htkhcn.congvan;

import java.time.LocalDate;
import java.time.OffsetDateTime;
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

/** Cong van den (thu tu don vi khac gui den can xu ly) tai ve tu CongVan cua
 * truong (mot chieu, KHONG day nguoc) - xem API_VANBAN_DEN.md. Khac ban chat
 * voi van_ban_dhkg nen la bang/module rieng, dong bo la upsert truc tiep
 * (khong qua buoc "duyet ung vien" vi day da la du lieu chinh thuc, chi phan
 * anh lai trang thai xu ly ben CongVan). */
@Entity
@Table(name = "cong_van_den")
@Getter
@Setter
@NoArgsConstructor
public class CongVanDen {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "congvan_id", nullable = false, unique = true)
    private Long congvanId;

    @Column(name = "so_van_ban")
    private String soVanBan;

    @Column(name = "so_den")
    private String soDen;

    @Column(name = "ngay_den")
    private LocalDate ngayDen;

    @Column(name = "ngay_ban_hanh")
    private LocalDate ngayBanHanh;

    @Column(name = "trich_yeu", nullable = false)
    private String trichYeu;

    @Column(name = "co_quan_ban_hanh")
    private String coQuanBanHanh;

    @Column(name = "loai_van_ban")
    private String loaiVanBan;

    @Column(name = "nguoi_ky")
    private String nguoiKy;

    @Column(name = "don_vi_xu_ly_chinh")
    private String donViXuLyChinh;

    @Column(name = "han_xu_ly")
    private LocalDate hanXuLy;

    @Column(name = "ngay_hoan_thanh")
    private LocalDate ngayHoanThanh;

    @Column(name = "trang_thai")
    private String trangThai;

    @Column(name = "trang_thai_text")
    private String trangThaiText;

    @Column(name = "ghi_chu")
    private String ghiChu;

    @Column(name = "co_file", nullable = false)
    private boolean coFile;

    @Column(name = "ngay_dong_bo", nullable = false)
    private OffsetDateTime ngayDongBo;
}
