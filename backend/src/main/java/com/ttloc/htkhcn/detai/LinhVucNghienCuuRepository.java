package com.ttloc.htkhcn.detai;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LinhVucNghienCuuRepository extends JpaRepository<LinhVucNghienCuu, UUID> {
    List<LinhVucNghienCuu> findByHoatDongTrueOrderByCapAscThuTuAsc();

    List<LinhVucNghienCuu> findByChaIdOrderByThuTuAsc(UUID chaId);
}
