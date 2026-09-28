package com.ttloc.htkhcn.thanhvien;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ThanhVienPhuTrachRepository extends JpaRepository<ThanhVienPhuTrach, UUID> {
    List<ThanhVienPhuTrach> findAllByOrderByHoTenAsc();
}
