package com.ttloc.htkhcn.user;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VaiTroRepository extends JpaRepository<VaiTro, UUID> {
    Optional<VaiTro> findByMaVaiTro(String maVaiTro);

    List<VaiTro> findAllByIdIn(List<UUID> ids);
}
