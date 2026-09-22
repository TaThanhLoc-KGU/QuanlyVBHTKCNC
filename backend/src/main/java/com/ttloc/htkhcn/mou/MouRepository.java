package com.ttloc.htkhcn.mou;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MouRepository extends JpaRepository<Mou, UUID> {
    Optional<Mou> findByIdAndDeletedAtIsNull(UUID id);
}
