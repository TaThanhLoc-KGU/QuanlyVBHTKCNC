package com.ttloc.htkhcn.detai;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface HoiDongThanhVienRepository extends JpaRepository<HoiDongThanhVien, UUID> {
    List<HoiDongThanhVien> findByHoiDongIdOrderById(UUID hoiDongId);
}
