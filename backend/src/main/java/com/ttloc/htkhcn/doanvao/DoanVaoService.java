package com.ttloc.htkhcn.doanvao;

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
public class DoanVaoService {

    private final DoanVaoRepository doanVaoRepository;
    private final DoiTacRepository doiTacRepository;
    private final EntityManager entityManager;

    @Transactional(readOnly = true)
    public Page<DoanVaoResponse> danhSach(Integer nam, UUID doiTacId, String tuKhoa, Pageable pageable) {
        Specification<DoanVao> spec = SpecUtils.and(
                DoanVaoSpecifications.chuaBiXoa(),
                DoanVaoSpecifications.namBang(nam),
                DoanVaoSpecifications.doiTacBang(doiTacId),
                DoanVaoSpecifications.tuKhoaKhongDauTrong(tuKhoa));
        return doanVaoRepository.findAll(spec, pageable).map(DoanVaoResponse::from);
    }

    @Transactional(readOnly = true)
    public DoanVaoResponse layTheoId(UUID id) {
        return DoanVaoResponse.from(timHoacLoi(id));
    }

    @Transactional
    public DoanVaoResponse tao(DoanVaoRequest request) {
        validate(request);
        DoanVao d = new DoanVao();
        gan(d, request);
        DoanVao saved = doanVaoRepository.save(d);
        entityManager.flush();
        entityManager.refresh(saved);
        return DoanVaoResponse.from(saved);
    }

    @Transactional
    public DoanVaoResponse sua(UUID id, DoanVaoRequest request) {
        validate(request);
        DoanVao d = timHoacLoi(id);
        gan(d, request);
        DoanVao saved = doanVaoRepository.save(d);
        entityManager.flush();
        entityManager.refresh(saved);
        return DoanVaoResponse.from(saved);
    }

    @Transactional
    public void xoa(UUID id) {
        DoanVao d = timHoacLoi(id);
        d.setDeletedAt(Instant.now());
        d.setDeletedBy(currentUserId());
        doanVaoRepository.saveAndFlush(d);
    }

    private void validate(DoanVaoRequest request) {
        if (request.thoiGianDi().isBefore(request.thoiGianDen())) {
            throw new BadRequestException("Thoi gian di khong duoc truoc thoi gian den");
        }
    }

    private void gan(DoanVao d, DoanVaoRequest request) {
        d.setTenDoan(request.tenDoan());
        if (request.doiTacId() != null) {
            if (!doiTacRepository.existsById(request.doiTacId())) {
                throw new ResourceNotFoundException("Khong tim thay doi tac: " + request.doiTacId());
            }
            d.setDoiTac(entityManager.getReference(DoiTac.class, request.doiTacId()));
        } else {
            d.setDoiTac(null);
        }
        d.setThoiGianDen(request.thoiGianDen());
        d.setThoiGianDi(request.thoiGianDi());
        d.setSoLuongNguoiNuocNgoai(request.soLuongNguoiNuocNgoai());
        d.setSoLuongNguoiVietNam(request.soLuongNguoiVietNam());
        d.setQuocTich(request.quocTich());
        d.setNoiDungLamViec(request.noiDungLamViec());
    }

    private DoanVao timHoacLoi(UUID id) {
        return doanVaoRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay doan vao: " + id));
    }

    private UUID currentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof SecurityUser user) {
            return user.getId();
        }
        return null;
    }
}
