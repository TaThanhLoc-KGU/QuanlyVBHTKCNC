package com.ttloc.htkhcn.crawlphapluat;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VbplUngVienRepository extends JpaRepository<VbplUngVien, UUID> {
    boolean existsByNguonAndSoHieu(NguonCrawl nguon, String soHieu);

    Page<VbplUngVien> findByTrangThaiOrderByNgayCrawlDesc(TrangThaiUngVien trangThai, Pageable pageable);

    Page<VbplUngVien> findAllByOrderByNgayCrawlDesc(Pageable pageable);

    Optional<VbplUngVien> findByIdAndTrangThai(UUID id, TrangThaiUngVien trangThai);
}
