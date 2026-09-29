package com.ttloc.htkhcn.detai;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DeTaiThanhVienRepository extends JpaRepository<DeTaiThanhVien, UUID> {
    List<DeTaiThanhVien> findByDeTaiIdOrderByThuTuAsc(UUID deTaiId);
}
