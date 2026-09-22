package com.ttloc.htkhcn.vanbandhkg;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface VanBanDhkgRepository extends JpaRepository<VanBanDhkg, UUID>,
        JpaSpecificationExecutor<VanBanDhkg> {

    Optional<VanBanDhkg> findByIdAndDeletedAtIsNull(UUID id);

    boolean existsBySoHieu(String soHieu);

    boolean existsBySoHieuAndIdNot(String soHieu, UUID id);
}
