package com.ttloc.htkhcn.detaitaichinh;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.ttloc.htkhcn.detai.DeTai;
import com.ttloc.htkhcn.detai.TrangThaiDuyet;

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
@Table(name = "de_tai_thanh_toan")
@Getter
@Setter
@NoArgsConstructor
public class DeTaiThanhToan {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "de_tai_id", nullable = false)
    private DeTai deTai;

    @Column(name = "nam", nullable = false)
    private Integer nam;

    @Column(name = "so_tien", nullable = false)
    private BigDecimal soTien;

    @Column(name = "ngay_thanh_toan")
    private LocalDate ngayThanhToan;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "trang_thai", nullable = false, columnDefinition = "trang_thai_duyet_enum")
    private TrangThaiDuyet trangThai = TrangThaiDuyet.CHO_DUYET;

    @Column(name = "nguoi_duyet_id")
    private UUID nguoiDuyetId;

    @Column(name = "ngay_duyet")
    private Instant ngayDuyet;

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
