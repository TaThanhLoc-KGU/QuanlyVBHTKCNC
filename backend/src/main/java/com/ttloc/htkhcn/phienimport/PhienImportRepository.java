package com.ttloc.htkhcn.phienimport;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhienImportRepository extends JpaRepository<PhienImport, UUID> {
    Page<PhienImport> findAllByOrderByThoiDiemDesc(Pageable pageable);
}
