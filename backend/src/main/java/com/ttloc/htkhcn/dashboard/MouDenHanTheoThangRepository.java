package com.ttloc.htkhcn.dashboard;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MouDenHanTheoThangRepository extends JpaRepository<MouDenHanTheoThang, LocalDate> {
    List<MouDenHanTheoThang> findAllByOrderByThangAsc();
}
