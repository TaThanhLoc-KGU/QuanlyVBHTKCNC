package com.ttloc.htkhcn.danhmuc;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ttloc.htkhcn.common.PhamViVanBan;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/danh-muc/loai-van-ban")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'EDITOR', 'VIEWER')")
public class LoaiVanBanController {

    private final LoaiVanBanRepository loaiVanBanRepository;

    @GetMapping
    public List<LoaiVanBan> danhSach(@RequestParam PhamViVanBan phamVi) {
        return loaiVanBanRepository.findByPhamViOrderByThuTu(phamVi);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public LoaiVanBan them(@Valid @RequestBody LoaiVanBanRequest request) {
        LoaiVanBan lvb = new LoaiVanBan();
        lvb.setMa(request.ma());
        lvb.setTen(request.ten());
        lvb.setPhamVi(request.phamVi());
        lvb.setThuTu(request.thuTu() != null ? request.thuTu() : 0);
        return loaiVanBanRepository.save(lvb);
    }
}
