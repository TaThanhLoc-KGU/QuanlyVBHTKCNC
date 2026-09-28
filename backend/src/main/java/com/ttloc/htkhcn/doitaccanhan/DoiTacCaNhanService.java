package com.ttloc.htkhcn.doitaccanhan;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ttloc.htkhcn.common.exception.ResourceNotFoundException;
import com.ttloc.htkhcn.doitac.DoiTac;
import com.ttloc.htkhcn.doitac.DoiTacRepository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DoiTacCaNhanService {

    private final DoiTacCaNhanRepository doiTacCaNhanRepository;
    private final DoiTacRepository doiTacRepository;
    private final EntityManager entityManager;

    @Transactional(readOnly = true)
    public List<DoiTacCaNhanResponse> danhSach() {
        return doiTacCaNhanRepository.findAllByOrderByHoTenAsc().stream().map(DoiTacCaNhanResponse::from).toList();
    }

    @Transactional
    public DoiTacCaNhanResponse tao(DoiTacCaNhanRequest request) {
        DoiTacCaNhan d = new DoiTacCaNhan();
        gan(d, request);
        d.setNgayTao(OffsetDateTime.now());
        return DoiTacCaNhanResponse.from(doiTacCaNhanRepository.save(d));
    }

    @Transactional
    public DoiTacCaNhanResponse sua(UUID id, DoiTacCaNhanRequest request) {
        DoiTacCaNhan d = timHoacLoi(id);
        gan(d, request);
        d.setNgaySua(OffsetDateTime.now());
        return DoiTacCaNhanResponse.from(doiTacCaNhanRepository.save(d));
    }

    @Transactional
    public void xoa(UUID id) {
        doiTacCaNhanRepository.delete(timHoacLoi(id));
    }

    private void gan(DoiTacCaNhan d, DoiTacCaNhanRequest request) {
        d.setHoTen(request.hoTen());
        d.setChucVu(request.chucVu());
        if (request.doiTacId() != null) {
            if (!doiTacRepository.existsById(request.doiTacId())) {
                throw new ResourceNotFoundException("Khong tim thay doi tac: " + request.doiTacId());
            }
            d.setDoiTac(entityManager.getReference(DoiTac.class, request.doiTacId()));
        } else {
            d.setDoiTac(null);
        }
        d.setEmail(request.email());
        d.setDienThoai(request.dienThoai());
        d.setGhiChu(request.ghiChu());
        d.setHoatDong(request.hoatDong() == null || request.hoatDong());
    }

    private DoiTacCaNhan timHoacLoi(UUID id) {
        return doiTacCaNhanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay doi tac ca nhan: " + id));
    }
}
