package com.ttloc.htkhcn.doanra;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DoanRaRepository extends JpaRepository<DoanRa, UUID>, JpaSpecificationExecutor<DoanRa> {
    Optional<DoanRa> findByIdAndDeletedAtIsNull(UUID id);
}
