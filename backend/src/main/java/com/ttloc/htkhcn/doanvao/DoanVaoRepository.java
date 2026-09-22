package com.ttloc.htkhcn.doanvao;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DoanVaoRepository extends JpaRepository<DoanVao, UUID>, JpaSpecificationExecutor<DoanVao> {
    Optional<DoanVao> findByIdAndDeletedAtIsNull(UUID id);
}
