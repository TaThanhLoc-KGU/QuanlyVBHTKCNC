package com.ttloc.htkhcn.congvan;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CongVanDenRepository extends JpaRepository<CongVanDen, UUID>,
        JpaSpecificationExecutor<CongVanDen> {

    Optional<CongVanDen> findByCongvanId(Long congvanId);
}
