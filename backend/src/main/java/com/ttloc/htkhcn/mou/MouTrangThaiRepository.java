package com.ttloc.htkhcn.mou;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MouTrangThaiRepository extends JpaRepository<MouTrangThai, UUID>,
        JpaSpecificationExecutor<MouTrangThai> {
}
