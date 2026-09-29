package com.ttloc.htkhcn.hosokhoahoc;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LyLichKhoaHocRepository extends JpaRepository<LyLichKhoaHoc, UUID> {
    Optional<LyLichKhoaHoc> findByNguoiDungId(UUID nguoiDungId);
}
