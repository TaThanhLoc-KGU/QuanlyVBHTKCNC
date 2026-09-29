package com.ttloc.htkhcn.detaitaichinh;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DeTaiDuToanNamRepository extends JpaRepository<DeTaiDuToanNam, UUID> {
    List<DeTaiDuToanNam> findByDeTaiIdOrderByNamAsc(UUID deTaiId);
}
