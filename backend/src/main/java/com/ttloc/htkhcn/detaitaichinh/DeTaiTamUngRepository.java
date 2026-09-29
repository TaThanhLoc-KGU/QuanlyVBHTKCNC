package com.ttloc.htkhcn.detaitaichinh;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DeTaiTamUngRepository extends JpaRepository<DeTaiTamUng, UUID> {
    List<DeTaiTamUng> findByDeTaiIdOrderByNgayDeNghiDesc(UUID deTaiId);
}
