package com.ttloc.htkhcn.detai;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface HoiDongRepository extends JpaRepository<HoiDong, UUID> {
    List<HoiDong> findByDeTaiIdOrderByNgayTaoAsc(UUID deTaiId);
}
