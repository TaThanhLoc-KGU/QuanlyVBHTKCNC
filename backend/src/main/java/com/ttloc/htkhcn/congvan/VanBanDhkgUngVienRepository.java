package com.ttloc.htkhcn.congvan;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ttloc.htkhcn.crawlphapluat.TrangThaiUngVien;

public interface VanBanDhkgUngVienRepository extends JpaRepository<VanBanDhkgUngVien, UUID> {
    boolean existsByCongvanId(Long congvanId);

    Page<VanBanDhkgUngVien> findByTrangThaiOrderByNgayDongBoDesc(TrangThaiUngVien trangThai, Pageable pageable);

    Page<VanBanDhkgUngVien> findAllByOrderByNgayDongBoDesc(Pageable pageable);

    Optional<VanBanDhkgUngVien> findByIdAndTrangThai(UUID id, TrangThaiUngVien trangThai);
}
