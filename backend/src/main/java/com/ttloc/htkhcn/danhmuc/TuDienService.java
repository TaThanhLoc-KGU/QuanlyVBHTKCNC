package com.ttloc.htkhcn.danhmuc;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ttloc.htkhcn.common.exception.DuplicateResourceException;
import com.ttloc.htkhcn.common.exception.ResourceNotFoundException;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TuDienService {

    private final TuDienRepository tuDienRepository;

    @Transactional(readOnly = true)
    public List<TuDien> danhSach(LoaiTuDien loai) {
        return tuDienRepository.findByLoaiOrderByThuTuAscTenAsc(loai);
    }

    @Transactional
    public TuDien tao(@Valid TuDienRequest request) {
        if (tuDienRepository.existsByLoaiAndTenIgnoreCase(request.loai(), request.ten())) {
            throw new DuplicateResourceException("Gia tri da ton tai trong tu dien nay: " + request.ten());
        }
        TuDien td = new TuDien();
        ganTuRequest(td, request);
        td.setNgayTao(OffsetDateTime.now());
        return tuDienRepository.save(td);
    }

    @Transactional
    public TuDien sua(UUID id, @Valid TuDienRequest request) {
        TuDien td = timHoacLoi(id);
        if (tuDienRepository.existsByLoaiAndTenIgnoreCaseAndIdNot(request.loai(), request.ten(), id)) {
            throw new DuplicateResourceException("Gia tri da ton tai trong tu dien nay: " + request.ten());
        }
        ganTuRequest(td, request);
        td.setNgaySua(OffsetDateTime.now());
        return tuDienRepository.save(td);
    }

    @Transactional
    public void xoa(UUID id) {
        tuDienRepository.delete(timHoacLoi(id));
    }

    private void ganTuRequest(TuDien td, TuDienRequest request) {
        td.setLoai(request.loai());
        td.setMa(request.ma());
        td.setTen(request.ten());
        td.setMoTa(request.moTa());
        td.setThuTu(request.thuTu() != null ? request.thuTu() : 0);
        td.setHoatDong(request.hoatDong() == null || request.hoatDong());
    }

    private TuDien timHoacLoi(UUID id) {
        return tuDienRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay gia tri tu dien: " + id));
    }
}
