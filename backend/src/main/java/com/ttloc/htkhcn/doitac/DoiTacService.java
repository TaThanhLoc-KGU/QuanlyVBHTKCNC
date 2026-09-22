package com.ttloc.htkhcn.doitac;

import java.time.Instant;
import java.util.List;
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
import com.ttloc.htkhcn.security.SecurityUser;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DoiTacService {

    private final DoiTacRepository doiTacRepository;

    @Transactional(readOnly = true)
    public Page<DoiTacResponse> danhSach(LoaiDoiTac loaiDoiTac, String quocGia, String tuKhoa, Pageable pageable) {
        Specification<DoiTac> spec = SpecUtils.and(
                DoiTacSpecifications.chuaBiXoa(),
                DoiTacSpecifications.loaiDoiTacBang(loaiDoiTac),
                DoiTacSpecifications.quocGiaBang(quocGia),
                DoiTacSpecifications.tuKhoaKhongDauTrong(tuKhoa));
        return doiTacRepository.findAll(spec, pageable).map(DoiTacResponse::from);
    }

    @Transactional(readOnly = true)
    public DoiTacResponse layTheoId(UUID id) {
        return DoiTacResponse.from(timHoacLoi(id));
    }

    /** SPEC 3.1/4.3: goi y doi tac gan giong ten truoc khi tao moi de tranh phan manh danh muc. */
    @Transactional(readOnly = true)
    public List<DoiTacResponse> goiYTrungTen(String ten) {
        return doiTacRepository.timDoiTacGanGiong(ten).stream().map(DoiTacResponse::from).toList();
    }

    @Transactional
    public DoiTacResponse tao(DoiTacRequest request) {
        validate(request);
        DoiTac d = new DoiTac();
        gan(d, request);
        // saveAndFlush (khong phai save thuong): gui INSERT xuong DB ngay trong
        // cung request - de trigger audit (lich_su_thay_doi) va cac rang buoc
        // DB thay ngay, khong cho den luc commit moi phat hien loi.
        return DoiTacResponse.from(doiTacRepository.saveAndFlush(d));
    }

    @Transactional
    public DoiTacResponse sua(UUID id, DoiTacRequest request) {
        validate(request);
        DoiTac d = timHoacLoi(id);
        gan(d, request);
        return DoiTacResponse.from(doiTacRepository.saveAndFlush(d));
    }

    @Transactional
    public void xoa(UUID id) {
        DoiTac d = timHoacLoi(id);
        d.setDeletedAt(Instant.now());
        d.setDeletedBy(currentUserId());
        doiTacRepository.saveAndFlush(d);
    }

    @Transactional
    public void khoiPhuc(UUID id) {
        DoiTac d = doiTacRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay doi tac: " + id));
        d.setDeletedAt(null);
        d.setDeletedBy(null);
        doiTacRepository.save(d);
    }

    private void validate(DoiTacRequest request) {
        if (request.loaiDoiTac() == LoaiDoiTac.NGOAI_NUOC
                && (request.quocGia() == null || request.quocGia().isBlank())) {
            throw new BadRequestException("Quoc gia la bat buoc khi Loai doi tac = Ngoai nuoc");
        }
    }

    private void gan(DoiTac d, DoiTacRequest request) {
        d.setTenDoiTac(request.tenDoiTac());
        d.setLoaiDoiTac(request.loaiDoiTac());
        d.setQuocGia(request.quocGia());
        d.setDiaChi(request.diaChi());
        d.setThongTinLienHe(request.thongTinLienHe());
        d.setGhiChu(request.ghiChu());
    }

    private DoiTac timHoacLoi(UUID id) {
        return doiTacRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay doi tac: " + id));
    }

    private UUID currentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof SecurityUser user) {
            return user.getId();
        }
        return null;
    }
}
