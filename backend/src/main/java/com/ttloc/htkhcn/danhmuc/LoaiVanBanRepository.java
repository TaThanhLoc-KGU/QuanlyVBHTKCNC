package com.ttloc.htkhcn.danhmuc;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ttloc.htkhcn.common.PhamViVanBan;

public interface LoaiVanBanRepository extends JpaRepository<LoaiVanBan, UUID> {
    List<LoaiVanBan> findByPhamViOrderByThuTu(PhamViVanBan phamVi);
}
