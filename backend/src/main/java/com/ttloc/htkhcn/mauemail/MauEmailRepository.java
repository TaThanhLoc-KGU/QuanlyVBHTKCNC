package com.ttloc.htkhcn.mauemail;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MauEmailRepository extends JpaRepository<MauEmail, UUID> {
    List<MauEmail> findAllByOrderByTenMauAsc();

    Optional<MauEmail> findByMaAndHoatDongTrue(String ma);

    boolean existsByMaIgnoreCase(String ma);

    boolean existsByMaIgnoreCaseAndIdNot(String ma, UUID id);
}
