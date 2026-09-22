package com.ttloc.htkhcn.danhmuc;

import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.ttloc.htkhcn.common.PhamViVanBan;

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

/** Danh muc "Loai van ban" mo rong duoc boi Admin (SPEC muc 3.2/3.3), dung chung
 * cho Van ban DHKG (pham_vi=DHKG) va VBPL VN (pham_vi=VBPL). */
@Entity
@Table(name = "danh_muc_loai_van_ban")
@Getter
@Setter
@NoArgsConstructor
public class LoaiVanBan {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "ma", nullable = false)
    private String ma;

    @Column(name = "ten", nullable = false)
    private String ten;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "pham_vi", nullable = false, columnDefinition = "pham_vi_van_ban_enum")
    private PhamViVanBan phamVi;

    @Column(name = "thu_tu", nullable = false)
    private int thuTu;
}
