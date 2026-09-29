package com.ttloc.htkhcn.hosokhoahoc;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.ttloc.htkhcn.user.NguoiDung;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "ly_lich_khoa_hoc")
@Getter
@Setter
@NoArgsConstructor
public class LyLichKhoaHoc {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_dung_id", nullable = false, unique = true)
    private NguoiDung nguoiDung;

    @Column(name = "hoc_ham")
    private String hocHam;

    @Column(name = "hoc_vi")
    private String hocVi;

    @Column(name = "chuyen_nganh")
    private String chuyenNganh;

    @Column(name = "qua_trinh_cong_tac")
    private String quaTrinhCongTac;

    @Column(name = "ghi_chu")
    private String ghiChu;

    @Column(name = "nguoi_sua_id")
    private UUID nguoiSuaId;

    @Column(name = "ngay_sua", nullable = false)
    private OffsetDateTime ngaySua;
}
