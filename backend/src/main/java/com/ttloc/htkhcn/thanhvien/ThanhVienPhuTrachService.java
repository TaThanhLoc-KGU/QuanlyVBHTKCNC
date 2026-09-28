package com.ttloc.htkhcn.thanhvien;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ttloc.htkhcn.common.exception.ResourceNotFoundException;
import com.ttloc.htkhcn.danhmuc.TuDien;
import com.ttloc.htkhcn.danhmuc.TuDienRepository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ThanhVienPhuTrachService {

    private final ThanhVienPhuTrachRepository thanhVienPhuTrachRepository;
    private final TuDienRepository tuDienRepository;
    private final EntityManager entityManager;

    @Transactional(readOnly = true)
    public List<ThanhVienPhuTrachResponse> danhSach() {
        return thanhVienPhuTrachRepository.findAllByOrderByHoTenAsc().stream()
                .map(ThanhVienPhuTrachResponse::from).toList();
    }

    @Transactional
    public ThanhVienPhuTrachResponse tao(ThanhVienPhuTrachRequest request) {
        ThanhVienPhuTrach tv = new ThanhVienPhuTrach();
        gan(tv, request);
        tv.setNgayTao(OffsetDateTime.now());
        return ThanhVienPhuTrachResponse.from(thanhVienPhuTrachRepository.save(tv));
    }

    @Transactional
    public ThanhVienPhuTrachResponse sua(UUID id, ThanhVienPhuTrachRequest request) {
        ThanhVienPhuTrach tv = timHoacLoi(id);
        gan(tv, request);
        tv.setNgaySua(OffsetDateTime.now());
        return ThanhVienPhuTrachResponse.from(thanhVienPhuTrachRepository.save(tv));
    }

    @Transactional
    public void xoa(UUID id) {
        thanhVienPhuTrachRepository.delete(timHoacLoi(id));
    }

    private void gan(ThanhVienPhuTrach tv, ThanhVienPhuTrachRequest request) {
        tv.setHoTen(request.hoTen());
        tv.setChucVu(request.chucVu());
        tv.setDonVi(request.donVi());
        tv.setEmail(request.email());
        tv.setDienThoai(request.dienThoai());
        if (request.vaiTroTuDienId() != null) {
            if (!tuDienRepository.existsById(request.vaiTroTuDienId())) {
                throw new ResourceNotFoundException("Khong tim thay vai tro (tu dien): " + request.vaiTroTuDienId());
            }
            tv.setVaiTro(entityManager.getReference(TuDien.class, request.vaiTroTuDienId()));
        } else {
            tv.setVaiTro(null);
        }
        tv.setGhiChu(request.ghiChu());
        tv.setHoatDong(request.hoatDong() == null || request.hoatDong());
    }

    private ThanhVienPhuTrach timHoacLoi(UUID id) {
        return thanhVienPhuTrachRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay thanh vien: " + id));
    }
}
