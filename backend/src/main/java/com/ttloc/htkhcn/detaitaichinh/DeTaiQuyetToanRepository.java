package com.ttloc.htkhcn.detaitaichinh;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DeTaiQuyetToanRepository extends JpaRepository<DeTaiQuyetToan, UUID> {
    Optional<DeTaiQuyetToan> findByDeTaiId(UUID deTaiId);
}
