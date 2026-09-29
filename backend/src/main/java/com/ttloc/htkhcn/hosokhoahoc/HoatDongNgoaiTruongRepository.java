package com.ttloc.htkhcn.hosokhoahoc;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface HoatDongNgoaiTruongRepository extends JpaRepository<HoatDongNgoaiTruong, UUID> {
    List<HoatDongNgoaiTruong> findByNguoiDungIdOrderByThoiGianBatDauDesc(UUID nguoiDungId);
}
