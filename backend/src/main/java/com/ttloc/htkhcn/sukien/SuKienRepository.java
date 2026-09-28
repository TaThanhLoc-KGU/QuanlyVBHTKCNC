package com.ttloc.htkhcn.sukien;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SuKienRepository extends JpaRepository<SuKien, UUID>, JpaSpecificationExecutor<SuKien> {
    Optional<SuKien> findByIdAndDeletedAtIsNull(UUID id);
}
