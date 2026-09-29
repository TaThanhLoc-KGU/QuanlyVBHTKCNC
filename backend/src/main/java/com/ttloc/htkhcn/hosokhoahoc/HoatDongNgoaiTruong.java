package com.ttloc.htkhcn.hosokhoahoc;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.ttloc.htkhcn.user.NguoiDung;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "hoat_dong_ngoai_truong")
@Getter
@Setter
@NoArgsConstructor
public class HoatDongNgoaiTruong {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_dung_id", nullable = false)
    private NguoiDung nguoiDung;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "loai", nullable = false, columnDefinition = "loai_hoat_dong_ngoai_truong_enum")
    private LoaiHoatDongNgoaiTruong loai;

    @Column(name = "ten", nullable = false)
    private String ten;

    @Column(name = "don_vi_phoi_hop")
    private String donViPhoiHop;

    @Column(name = "vai_tro")
    private String vaiTro;

    @Column(name = "thoi_gian_bat_dau")
    private LocalDate thoiGianBatDau;

    @Column(name = "thoi_gian_ket_thuc")
    private LocalDate thoiGianKetThuc;

    @Column(name = "ghi_chu")
    private String ghiChu;

    @Column(name = "nguoi_tao_id")
    private UUID nguoiTaoId;

    @Column(name = "ngay_tao", nullable = false)
    private Instant ngayTao;
}
