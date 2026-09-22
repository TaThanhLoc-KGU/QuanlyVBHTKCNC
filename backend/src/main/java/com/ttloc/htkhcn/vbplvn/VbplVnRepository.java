package com.ttloc.htkhcn.vbplvn;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface VbplVnRepository extends JpaRepository<VbplVn, UUID>, JpaSpecificationExecutor<VbplVn> {

    Optional<VbplVn> findByIdAndDeletedAtIsNull(UUID id);

    boolean existsBySoHieu(String soHieu);

    boolean existsBySoHieuAndIdNot(String soHieu, UUID id);
}
