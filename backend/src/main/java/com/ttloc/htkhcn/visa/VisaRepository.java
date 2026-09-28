package com.ttloc.htkhcn.visa;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface VisaRepository extends JpaRepository<Visa, UUID>, JpaSpecificationExecutor<Visa> {
    Optional<Visa> findByIdAndDeletedAtIsNull(UUID id);
}
