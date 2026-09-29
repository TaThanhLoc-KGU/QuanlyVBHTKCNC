package com.ttloc.htkhcn.detaitaichinh;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DeTaiBaoCaoTienDoRepository extends JpaRepository<DeTaiBaoCaoTienDo, UUID> {
    List<DeTaiBaoCaoTienDo> findByDeTaiIdOrderByHanNopDesc(UUID deTaiId);
}
