package com.ttloc.htkhcn.phienimport;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.ttloc.htkhcn.common.ModuleKey;

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

/** 1 phien import Excel (SPEC muc 4.3: "ai, luc nao, file goc, so dong thanh cong/loi"). */
@Entity
@Table(name = "phien_import")
@Getter
@Setter
@NoArgsConstructor
public class PhienImport {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "mo_dun", nullable = false, columnDefinition = "mo_dun_enum")
    private ModuleKey moDun;

    @Column(name = "ten_file", nullable = false)
    private String tenFile;

    @Column(name = "nguoi_thuc_hien_id")
    private UUID nguoiThucHienId;

    @Column(name = "thoi_diem", nullable = false)
    private Instant thoiDiem = Instant.now();

    @Column(name = "tong_so_dong")
    private Integer tongSoDong;

    @Column(name = "so_dong_thanh_cong")
    private Integer soDongThanhCong;

    @Column(name = "so_dong_loi")
    private Integer soDongLoi;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "chi_tiet_loi")
    private String chiTietLoi;

    @Column(name = "trang_thai", nullable = false)
    private String trangThai = "HOAN_THANH";

    @Column(name = "co_the_rollback", nullable = false)
    private boolean coTheRollback = true;

    @Column(name = "da_rollback", nullable = false)
    private boolean daRollback = false;
}
