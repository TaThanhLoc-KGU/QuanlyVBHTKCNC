package com.ttloc.htkhcn.user;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NguoiDungRepository extends JpaRepository<NguoiDung, UUID> {

    Optional<NguoiDung> findByTenDangNhapAndDeletedAtIsNull(String tenDangNhap);

    Optional<NguoiDung> findByEmailAndDeletedAtIsNull(String email);

    boolean existsByTenDangNhap(String tenDangNhap);

    boolean existsByEmail(String email);

    Page<NguoiDung> findByDeletedAtIsNull(Pageable pageable);

    @org.springframework.data.jpa.repository.Query(
            "select u from NguoiDung u join u.vaiTros v where v.maVaiTro = :maVaiTro and u.deletedAt is null")
    List<NguoiDung> findByVaiTro(String maVaiTro);
}
