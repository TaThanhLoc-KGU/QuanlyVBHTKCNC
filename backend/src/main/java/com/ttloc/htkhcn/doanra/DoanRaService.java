package com.ttloc.htkhcn.doanra;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ttloc.htkhcn.common.SpecUtils;
import com.ttloc.htkhcn.common.exception.BadRequestException;
import com.ttloc.htkhcn.common.exception.ResourceNotFoundException;
import com.ttloc.htkhcn.doitac.DoiTac;
import com.ttloc.htkhcn.doitac.DoiTacRepository;
import com.ttloc.htkhcn.security.SecurityUser;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DoanRaService {

    private final DoanRaRepository doanRaRepository;
    private final DoiTacRepository doiTacRepository;
    private final EntityManager entityManager;

    @Transactional(readOnly = true)
    public Page<DoanRaResponse> danhSach(Integer nam, UUID doiTacId, String tuKhoa, Pageable pageable) {
        Specification<DoanRa> spec = SpecUtils.and(
                DoanRaSpecifications.chuaBiXoa(),
                DoanRaSpecifications.namBang(nam),
                DoanRaSpecifications.doiTacBang(doiTacId),
                DoanRaSpecifications.tuKhoaKhongDauTrong(tuKhoa));
        return doanRaRepository.findAll(spec, pageable).map(DoanRaResponse::from);
    }

    @Transactional(readOnly = true)
    public DoanRaResponse layTheoId(UUID id) {
        return DoanRaResponse.from(timHoacLoi(id));
    }

    @Transactional
    public DoanRaResponse tao(DoanRaRequest request) {
        validate(request);
        DoanRa d = new DoanRa();
        gan(d, request);
        DoanRa saved = doanRaRepository.save(d);
        entityManager.flush();
        entityManager.refresh(saved);
        return DoanRaResponse.from(saved);
    }

    @Transactional
    public DoanRaResponse sua(UUID id, DoanRaRequest request) {
        validate(request);
        DoanRa d = timHoacLoi(id);
        gan(d, request);
        DoanRa saved = doanRaRepository.save(d);
        entityManager.flush();
        entityManager.refresh(saved);
        return DoanRaResponse.from(saved);
    }

    @Transactional
    public void xoa(UUID id) {
        DoanRa d = timHoacLoi(id);
        d.setDeletedAt(Instant.now());
        d.setDeletedBy(currentUserId());
        doanRaRepository.saveAndFlush(d);
    }

    private void validate(DoanRaRequest request) {
        if (request.thoiGianVe().isBefore(request.thoiGianDi())) {
            throw new BadRequestException("Thoi gian ve khong duoc truoc thoi gian di");
        }
    }

    private void gan(DoanRa d, DoanRaRequest request) {
        if (!doiTacRepository.existsById(request.doiTacId())) {
            throw new ResourceNotFoundException("Khong tim thay doi tac: " + request.doiTacId());
        }
        d.setDoiTac(entityManager.getReference(DoiTac.class, request.doiTacId()));
        d.setThoiGianDi(request.thoiGianDi());
        d.setThoiGianVe(request.thoiGianVe());
        d.setDiaDiemDi(request.diaDiemDi());
        d.setDiaDiemDen(request.diaDiemDen());
        d.setSoLuongDoan(request.soLuongDoan());
        d.setThanhPhan(request.thanhPhan());
        d.setQuocGiaLamViec(request.quocGiaLamViec());
        d.setNoiDungLamViec(request.noiDungLamViec());
    }

    private DoanRa timHoacLoi(UUID id) {
        return doanRaRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay doan ra: " + id));
    }

    private UUID currentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof SecurityUser user) {
            return user.getId();
        }
        return null;
    }
}
