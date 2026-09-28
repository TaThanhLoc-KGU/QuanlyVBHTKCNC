package com.ttloc.htkhcn.danhmuc;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TuDienRepository extends JpaRepository<TuDien, UUID> {

    List<TuDien> findByLoaiOrderByThuTuAscTenAsc(LoaiTuDien loai);

    boolean existsByLoaiAndTenIgnoreCase(LoaiTuDien loai, String ten);

    boolean existsByLoaiAndTenIgnoreCaseAndIdNot(LoaiTuDien loai, String ten, UUID id);
}
