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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "de_tai_quyet_toan")
@Getter
@Setter
@NoArgsConstructor
public class DeTaiQuyetToan {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "de_tai_id", nullable = false, unique = true)
    private DeTai deTai;

    @Column(name = "tong_kinh_phi_da_cap")
    private BigDecimal tongKinhPhiDaCap;

    @Column(name = "tong_kinh_phi_da_su_dung")
    private BigDecimal tongKinhPhiDaSuDung;

    @Column(name = "ngay_quyet_toan")
    private LocalDate ngayQuyetToan;

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
