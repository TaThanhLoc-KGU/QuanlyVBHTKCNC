package com.ttloc.htkhcn.detaitaichinh;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/** Gop chung endpoint cho 5 loai ho so tai chinh/tien do cua de tai (du toan
 * nam, tam ung, thanh toan, quyet toan, bao cao tien do) - moi loai deu theo
 * mau: danh sach theo de tai, tao, sua, duyet (chuyen trang thai), xoa. Gop 1
 * controller thay vi 5 file rieng vi cac endpoint deu ngan va cung mau hinh. */
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'EDITOR', 'VIEWER')")
public class DeTaiTaiChinhController {

    private static final String MODULE = "DE_TAI_NCKH";

    private final DuToanNamService duToanNamService;
    private final TamUngService tamUngService;
    private final ThanhToanService thanhToanService;
    private final QuyetToanService quyetToanService;
    private final BaoCaoTienDoService baoCaoTienDoService;

    // ---------- Du toan nam ----------

    @GetMapping("/api/de-tai/{deTaiId}/du-toan-nam")
    public List<DuToanNamResponse> danhSachDuToanNam(@PathVariable UUID deTaiId) {
        return duToanNamService.danhSach(deTaiId);
    }

    @PostMapping("/api/de-tai/{deTaiId}/du-toan-nam")
    @PreAuthorize("hasRole('ADMIN') or @moduleAccess.canEdit(authentication, '" + MODULE + "')")
    public DuToanNamResponse taoDuToanNam(@PathVariable UUID deTaiId, @Valid @RequestBody DuToanNamRequest request) {
        return duToanNamService.tao(deTaiId, request);
    }

    @PutMapping("/api/du-toan-nam/{id}")
    @PreAuthorize("hasRole('ADMIN') or @moduleAccess.canEdit(authentication, '" + MODULE + "')")
    public DuToanNamResponse suaDuToanNam(@PathVariable UUID id, @Valid @RequestBody DuToanNamRequest request) {
        return duToanNamService.sua(id, request);
    }

    @PutMapping("/api/du-toan-nam/{id}/duyet")
    @PreAuthorize("hasRole('ADMIN') or @moduleAccess.canEdit(authentication, '" + MODULE + "')")
    public DuToanNamResponse duyetDuToanNam(@PathVariable UUID id, @Valid @RequestBody DuyetRequest request) {
        return duToanNamService.duyet(id, request);
    }

    @DeleteMapping("/api/du-toan-nam/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN') or @moduleAccess.canEdit(authentication, '" + MODULE + "')")
    public void xoaDuToanNam(@PathVariable UUID id) {
        duToanNamService.xoa(id);
    }

    // ---------- Tam ung ----------

    @GetMapping("/api/de-tai/{deTaiId}/tam-ung")
    public List<TamUngResponse> danhSachTamUng(@PathVariable UUID deTaiId) {
        return tamUngService.danhSach(deTaiId);
    }

    @PostMapping("/api/de-tai/{deTaiId}/tam-ung")
    @PreAuthorize("hasRole('ADMIN') or @moduleAccess.canEdit(authentication, '" + MODULE + "')")
    public TamUngResponse taoTamUng(@PathVariable UUID deTaiId, @Valid @RequestBody TamUngRequest request) {
        return tamUngService.tao(deTaiId, request);
    }

    @PutMapping("/api/tam-ung/{id}")
    @PreAuthorize("hasRole('ADMIN') or @moduleAccess.canEdit(authentication, '" + MODULE + "')")
    public TamUngResponse suaTamUng(@PathVariable UUID id, @Valid @RequestBody TamUngRequest request) {
        return tamUngService.sua(id, request);
    }

    @PutMapping("/api/tam-ung/{id}/duyet")
    @PreAuthorize("hasRole('ADMIN') or @moduleAccess.canEdit(authentication, '" + MODULE + "')")
    public TamUngResponse duyetTamUng(@PathVariable UUID id, @Valid @RequestBody DuyetRequest request) {
        return tamUngService.duyet(id, request);
    }

    @DeleteMapping("/api/tam-ung/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN') or @moduleAccess.canEdit(authentication, '" + MODULE + "')")
    public void xoaTamUng(@PathVariable UUID id) {
        tamUngService.xoa(id);
    }

    // ---------- Thanh toan ----------

    @GetMapping("/api/de-tai/{deTaiId}/thanh-toan")
    public List<ThanhToanResponse> danhSachThanhToan(@PathVariable UUID deTaiId) {
        return thanhToanService.danhSach(deTaiId);
    }

    @PostMapping("/api/de-tai/{deTaiId}/thanh-toan")
    @PreAuthorize("hasRole('ADMIN') or @moduleAccess.canEdit(authentication, '" + MODULE + "')")
    public ThanhToanResponse taoThanhToan(@PathVariable UUID deTaiId, @Valid @RequestBody ThanhToanRequest request) {
        return thanhToanService.tao(deTaiId, request);
    }

    @PutMapping("/api/thanh-toan/{id}")
    @PreAuthorize("hasRole('ADMIN') or @moduleAccess.canEdit(authentication, '" + MODULE + "')")
    public ThanhToanResponse suaThanhToan(@PathVariable UUID id, @Valid @RequestBody ThanhToanRequest request) {
        return thanhToanService.sua(id, request);
    }

    @PutMapping("/api/thanh-toan/{id}/duyet")
    @PreAuthorize("hasRole('ADMIN') or @moduleAccess.canEdit(authentication, '" + MODULE + "')")
    public ThanhToanResponse duyetThanhToan(@PathVariable UUID id, @Valid @RequestBody DuyetRequest request) {
        return thanhToanService.duyet(id, request);
    }

    @DeleteMapping("/api/thanh-toan/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN') or @moduleAccess.canEdit(authentication, '" + MODULE + "')")
    public void xoaThanhToan(@PathVariable UUID id) {
        thanhToanService.xoa(id);
    }

    // ---------- Quyet toan (1 ho so duy nhat / de tai) ----------

    @GetMapping("/api/de-tai/{deTaiId}/quyet-toan")
    public QuyetToanResponse layQuyetToan(@PathVariable UUID deTaiId) {
        return quyetToanService.layTheoDeTai(deTaiId);
    }

    @PutMapping("/api/de-tai/{deTaiId}/quyet-toan")
    @PreAuthorize("hasRole('ADMIN') or @moduleAccess.canEdit(authentication, '" + MODULE + "')")
    public QuyetToanResponse luuQuyetToan(@PathVariable UUID deTaiId, @Valid @RequestBody QuyetToanRequest request) {
        return quyetToanService.luu(deTaiId, request);
    }

    @PutMapping("/api/de-tai/{deTaiId}/quyet-toan/duyet")
    @PreAuthorize("hasRole('ADMIN') or @moduleAccess.canEdit(authentication, '" + MODULE + "')")
    public QuyetToanResponse duyetQuyetToan(@PathVariable UUID deTaiId, @Valid @RequestBody DuyetRequest request) {
        return quyetToanService.duyet(deTaiId, request);
    }

    // ---------- Bao cao tien do ----------

    @GetMapping("/api/de-tai/{deTaiId}/bao-cao-tien-do")
    public List<BaoCaoTienDoResponse> danhSachBaoCaoTienDo(@PathVariable UUID deTaiId) {
        return baoCaoTienDoService.danhSach(deTaiId);
    }

    @PostMapping("/api/de-tai/{deTaiId}/bao-cao-tien-do")
    @PreAuthorize("hasRole('ADMIN') or @moduleAccess.canEdit(authentication, '" + MODULE + "')")
    public BaoCaoTienDoResponse taoBaoCaoTienDo(@PathVariable UUID deTaiId, @Valid @RequestBody BaoCaoTienDoRequest request) {
        return baoCaoTienDoService.tao(deTaiId, request);
    }

    @PutMapping("/api/bao-cao-tien-do/{id}")
    @PreAuthorize("hasRole('ADMIN') or @moduleAccess.canEdit(authentication, '" + MODULE + "')")
    public BaoCaoTienDoResponse suaBaoCaoTienDo(@PathVariable UUID id, @Valid @RequestBody BaoCaoTienDoRequest request) {
        return baoCaoTienDoService.sua(id, request);
    }

    @PutMapping("/api/bao-cao-tien-do/{id}/duyet")
    @PreAuthorize("hasRole('ADMIN') or @moduleAccess.canEdit(authentication, '" + MODULE + "')")
    public BaoCaoTienDoResponse duyetBaoCaoTienDo(@PathVariable UUID id, @Valid @RequestBody DuyetRequest request) {
        return baoCaoTienDoService.duyet(id, request);
    }

    @DeleteMapping("/api/bao-cao-tien-do/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN') or @moduleAccess.canEdit(authentication, '" + MODULE + "')")
    public void xoaBaoCaoTienDo(@PathVariable UUID id) {
        baoCaoTienDoService.xoa(id);
    }
}
