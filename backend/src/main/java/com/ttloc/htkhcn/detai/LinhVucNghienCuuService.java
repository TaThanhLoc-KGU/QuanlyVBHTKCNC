package com.ttloc.htkhcn.detai;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ttloc.htkhcn.common.exception.BadRequestException;
import com.ttloc.htkhcn.common.exception.ResourceNotFoundException;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LinhVucNghienCuuService {

    private final LinhVucNghienCuuRepository repository;
    private final EntityManager entityManager;

    @Transactional(readOnly = true)
    public List<LinhVucNghienCuuResponse> danhSach() {
        return repository.findByHoatDongTrueOrderByCapAscThuTuAsc().stream()
                .map(LinhVucNghienCuuResponse::from)
                .toList();
    }

    @Transactional
    public LinhVucNghienCuuResponse tao(LinhVucNghienCuuRequest request) {
        LinhVucNghienCuu l = new LinhVucNghienCuu();
        gan(l, request);
        l.setNgayTao(OffsetDateTime.now());
        LinhVucNghienCuu saved = repository.save(l);
        entityManager.flush();
        entityManager.refresh(saved);
        return LinhVucNghienCuuResponse.from(saved);
    }

    @Transactional
    public LinhVucNghienCuuResponse sua(UUID id, LinhVucNghienCuuRequest request) {
        LinhVucNghienCuu l = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay linh vuc nghien cuu: " + id));
        gan(l, request);
        l.setNgaySua(OffsetDateTime.now());
        LinhVucNghienCuu saved = repository.save(l);
        entityManager.flush();
        entityManager.refresh(saved);
        return LinhVucNghienCuuResponse.from(saved);
    }

    private void gan(LinhVucNghienCuu l, LinhVucNghienCuuRequest request) {
        if (request.cap() == 1 && request.chaId() != null) {
            throw new BadRequestException("Linh vuc cap 1 khong duoc co linh vuc cha");
        }
        if (request.cap() != 1 && request.chaId() == null) {
            throw new BadRequestException("Linh vuc cap 2/3 bat buoc phai chon linh vuc cha");
        }
        l.setMa(request.ma());
        l.setTen(request.ten());
        l.setCap(request.cap());
        if (request.chaId() != null) {
            LinhVucNghienCuu cha = repository.findById(request.chaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay linh vuc cha: " + request.chaId()));
            l.setCha(cha);
        } else {
            l.setCha(null);
        }
        l.setThuTu(request.thuTu() != null ? request.thuTu() : 0);
        l.setHoatDong(request.hoatDong() == null || request.hoatDong());
    }
}
