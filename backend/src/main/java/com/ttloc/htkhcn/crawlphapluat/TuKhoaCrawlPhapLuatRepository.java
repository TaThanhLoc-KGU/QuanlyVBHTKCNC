package com.ttloc.htkhcn.crawlphapluat;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TuKhoaCrawlPhapLuatRepository extends JpaRepository<TuKhoaCrawlPhapLuat, UUID> {
    List<TuKhoaCrawlPhapLuat> findByHoatDongTrue();
}
