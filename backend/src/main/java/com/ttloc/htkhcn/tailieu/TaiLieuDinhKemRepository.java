package com.ttloc.htkhcn.tailieu;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TaiLieuDinhKemRepository extends JpaRepository<TaiLieuDinhKem, UUID> {
    List<TaiLieuDinhKem> findByBangAndBanGhiIdOrderByNgayTaoDesc(String bang, UUID banGhiId);
}
