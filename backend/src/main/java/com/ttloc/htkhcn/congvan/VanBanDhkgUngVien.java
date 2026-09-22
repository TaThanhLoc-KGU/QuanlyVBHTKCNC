package com.ttloc.htkhcn.congvan;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.ttloc.htkhcn.crawlphapluat.TrangThaiUngVien;

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

/** Van ban "van ban noi bo don vi" tai ve tu CongVan cua truong (mot chieu,
 * KHONG day nguoc), cho nguoi dung duyet truoc khi nhan vao van_ban_dhkg. */
@Entity
@Table(name = "van_ban_dhkg_ung_vien")
@Getter
@Setter
@NoArgsConstructor
public class VanBanDhkgUngVien {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "congvan_id", nullable = false, unique = true)
    private Long congvanId;

    @Column(name = "so_hieu", nullable = false)
    private String soHieu;

    @Column(name = "tieu_de", nullable = false)
    private String tieuDe;

    @Column(name = "noi_dung")
    private String noiDung;

    @Column(name = "ngay_ban_hanh")
    private LocalDate ngayBanHanh;

    @Column(name = "nguoi_ky")
    private String nguoiKy;

    @Column(name = "trang_thai_congvan")
    private Integer trangThaiCongvan;

    @Column(name = "so_file", nullable = false)
    private int soFile;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "trang_thai", nullable = false, columnDefinition = "trang_thai_ung_vien_enum")
    private TrangThaiUngVien trangThai = TrangThaiUngVien.CHUA_XU_LY;

    @Column(name = "van_ban_dhkg_id")
    private UUID vanBanDhkgId;

    @Column(name = "ngay_dong_bo", nullable = false)
    private OffsetDateTime ngayDongBo;

    @Column(name = "nguoi_xu_ly_id")
    private UUID nguoiXuLyId;

    @Column(name = "ngay_xu_ly")
    private OffsetDateTime ngayXuLy;
}
