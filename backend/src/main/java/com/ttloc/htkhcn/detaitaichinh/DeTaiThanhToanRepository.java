package com.ttloc.htkhcn.detaitaichinh;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DeTaiThanhToanRepository extends JpaRepository<DeTaiThanhToan, UUID> {
    List<DeTaiThanhToan> findByDeTaiIdOrderByNamDesc(UUID deTaiId);
}
