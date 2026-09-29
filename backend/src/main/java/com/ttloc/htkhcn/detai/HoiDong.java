package com.ttloc.htkhcn.detai;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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
@Table(name = "hoi_dong")
@Getter
@Setter
@NoArgsConstructor
public class HoiDong {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "de_tai_id", nullable = false)
    private DeTai deTai;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "loai", nullable = false, columnDefinition = "loai_hoi_dong_enum")
    private LoaiHoiDong loai;

    @Column(name = "ngay_hop")
    private LocalDate ngayHop;

    @Column(name = "dia_diem")
    private String diaDiem;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "ket_qua", nullable = false, columnDefinition = "ket_qua_hoi_dong_enum")
    private KetQuaHoiDong ketQua = KetQuaHoiDong.CHUA_CO_KET_QUA;

    @Column(name = "diem_trung_binh")
    private BigDecimal diemTrungBinh;

    @Column(name = "ghi_chu")
    private String ghiChu;

    @Column(name = "nguoi_tao_id")
    private UUID nguoiTaoId;

    @Column(name = "ngay_tao", nullable = false)
    private Instant ngayTao;

    @Column(name = "nguoi_sua_id")
    private UUID nguoiSuaId;

    @Column(name = "ngay_sua")
    private Instant ngaySua;
}
