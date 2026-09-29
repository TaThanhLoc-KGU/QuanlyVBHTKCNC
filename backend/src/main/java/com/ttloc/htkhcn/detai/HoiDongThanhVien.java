package com.ttloc.htkhcn.detai;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.ttloc.htkhcn.danhmuc.TuDien;
import com.ttloc.htkhcn.user.NguoiDung;

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

@Entity
@Table(name = "hoi_dong_thanh_vien")
@Getter
@Setter
@NoArgsConstructor
public class HoiDongThanhVien {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hoi_dong_id", nullable = false)
    private HoiDong hoiDong;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_dung_id")
    private NguoiDung nguoiDung;

    @Column(name = "ho_ten_ngoai")
    private String hoTenNgoai;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vai_tro_tu_dien_id")
    private TuDien vaiTro;

    @Column(name = "y_kien")
    private String yKien;

    @Column(name = "diem")
    private BigDecimal diem;

    @Column(name = "dong_y")
    private Boolean dongY;

    @Column(name = "ngay_cho_y_kien")
    private Instant ngayChoYKien;
}
