package com.ttloc.htkhcn.doitaccanhan;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DoiTacCaNhanRepository extends JpaRepository<DoiTacCaNhan, UUID> {
    List<DoiTacCaNhan> findAllByOrderByHoTenAsc();
}
