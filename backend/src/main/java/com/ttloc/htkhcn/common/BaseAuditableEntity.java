package com.ttloc.htkhcn.common;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

/**
 * 4 truong an truy vet (SPEC muc 3, 4.2): nguoi tao, ngay tao, nguoi sua, ngay
 * sua. Duoc Spring Data JPA Auditing tu dong dien - service/controller khong
 * can set tay. Trigger fn_ghi_lich_su() (V11) doc lai dung 2 cot nguoiTaoId/
 * nguoiSuaId nay tu NEW row de biet "nguoi thuc hien" cho lich_su_thay_doi.
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @CreatedBy
    @Column(name = "nguoi_tao_id", updatable = false)
    private UUID nguoiTaoId;

    @CreatedDate
    @Column(name = "ngay_tao", nullable = false, updatable = false)
    private Instant ngayTao;

    @LastModifiedBy
    @Column(name = "nguoi_sua_id")
    private UUID nguoiSuaId;

    @LastModifiedDate
    @Column(name = "ngay_sua")
    private Instant ngaySua;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Column(name = "deleted_by")
    private UUID deletedBy;
}
