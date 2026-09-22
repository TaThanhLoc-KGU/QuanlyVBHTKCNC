package com.ttloc.htkhcn.crawlphapluat;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ttloc.htkhcn.cauhinh.CauHinhHeThongRepository;
import com.ttloc.htkhcn.common.exception.DuplicateResourceException;
import com.ttloc.htkhcn.common.exception.ResourceNotFoundException;
import com.ttloc.htkhcn.security.SecurityUser;
import com.ttloc.htkhcn.vbplvn.VbplVnRepository;
import com.ttloc.htkhcn.vbplvn.VbplVnRequest;
import com.ttloc.htkhcn.vbplvn.VbplVnResponse;
import com.ttloc.htkhcn.vbplvn.VbplVnService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CrawlPhapLuatService {

    private static final String MA_CAU_HINH_LAN_CUOI = "crawl_phap_luat_lan_cuoi";

    private final TuKhoaCrawlPhapLuatRepository tuKhoaRepository;
    private final VbplUngVienRepository ungVienRepository;
    private final VbplVnRepository vbplVnRepository;
    private final VbplVnService vbplVnService;
    private final CauHinhHeThongRepository cauHinhHeThongRepository;
    private final CongBaoCrawlerService congBaoCrawlerService;
    private final VbplPortalCrawlerService vbplPortalCrawlerService;

    // Tat mac dinh: vbpl.vn chan bot o tang WAF (403 "you have been blocked"
    // cho moi request tu dong, xac nhan qua kiem tra thuc te), xem ghi chu
    // trong chayCrawlNgay(). Chi bat lai neu co duong tiep can hop le.
    @Value("${app.crawl.vbpl-portal-enabled:false}")
    private boolean vbplCrawlBatDauKichHoat;

    // KHONG @Transactional o day: crawl goi HTTP/trinh duyet ra ngoai co the
    // mat vai phut, khong nen giu 1 transaction/connection DB mo suot luc do.
    // Moi ghi DB (quetMotTuKhoa/capNhatLanCrawlCuoi) tu commit rieng.
    public CrawlKetQuaResponse chayCrawlNgay() {
        List<TuKhoaCrawlPhapLuat> tuKhoaDangHoatDong = tuKhoaRepository.findByHoatDongTrue();
        List<String> danhSachTuKhoa = tuKhoaDangHoatDong.stream().map(TuKhoaCrawlPhapLuat::getTuKhoa).toList();
        int soUngVienMoi = 0;

        for (String tuKhoa : danhSachTuKhoa) {
            soUngVienMoi += quetMotTuKhoa(tuKhoa, NguonCrawl.CONG_BAO_CHINH_PHU,
                    congBaoCrawlerService.timTheoTuKhoa(tuKhoa));
        }

        // vbpl.vn: DA TAT - trang nay chan bot o tang WAF (tra ve "403 -
        // Sorry, you have been blocked" cho moi request tu trinh duyet tu
        // dong, khong phai loi selector/timeout). Day la chan chu dong o ha
        // tang, manh hon robots.txt rat nhieu - vuot qua no can ky thuat gia
        // mao dau van tay trinh duyet / IP dan cu, vuot ra ngoai pham vi mot
        // crawler thong thuong nen KHONG lam. Giu nguyen VbplPortalCrawlerService
        // (khong xoa) phong khi co duong tiep can hop le hon (vi du: lien he
        // xin API/allowlist). Nut "Tra cuu tren vbpl.vn" (thu cong) van con.
        if (vbplCrawlBatDauKichHoat) {
            Map<String, List<KetQuaCrawl>> ketQuaVbpl = vbplPortalCrawlerService.timTheoNhieuTuKhoa(danhSachTuKhoa);
            for (var entry : ketQuaVbpl.entrySet()) {
                soUngVienMoi += quetMotTuKhoa(entry.getKey(), NguonCrawl.VBPL_VN_PORTAL, entry.getValue());
            }
        }

        capNhatLanCrawlCuoi();
        log.info("Crawl phap luat xong: {} tu khoa, {} ung vien moi", danhSachTuKhoa.size(), soUngVienMoi);
        return new CrawlKetQuaResponse(soUngVienMoi, danhSachTuKhoa.size());
    }

    private int quetMotTuKhoa(String tuKhoa, NguonCrawl nguon, List<KetQuaCrawl> ketQuaTho) {
        int soMoi = 0;
        for (KetQuaCrawl kq : ketQuaTho) {
            if (kq.soHieu() == null || kq.soHieu().isBlank()) {
                continue;
            }
            if (vbplVnRepository.existsBySoHieu(kq.soHieu())) {
                continue; // da co san trong VBPL VN, khong can lam ung vien
            }
            if (ungVienRepository.existsByNguonAndSoHieu(nguon, kq.soHieu())) {
                continue; // da la ung vien tu lan crawl truoc
            }
            VbplUngVien uv = new VbplUngVien();
            uv.setNguon(nguon);
            uv.setSoHieu(kq.soHieu());
            uv.setTenVanBan(kq.tenVanBan());
            uv.setLoaiVanBanText(kq.loaiVanBanText());
            uv.setNgayBanHanh(kq.ngayBanHanh());
            uv.setCoQuanBanHanh(kq.coQuanBanHanh());
            uv.setUrlNguon(kq.urlNguon());
            uv.setTuKhoaKhop(tuKhoa);
            uv.setNgayCrawl(OffsetDateTime.now());
            ungVienRepository.save(uv);
            soMoi++;
        }
        return soMoi;
    }

    private void capNhatLanCrawlCuoi() {
        cauHinhHeThongRepository.findById(MA_CAU_HINH_LAN_CUOI).ifPresent(ch -> {
            ch.setGiaTri(Instant.now().toString());
            ch.setNgaySua(OffsetDateTime.now());
            cauHinhHeThongRepository.save(ch);
        });
    }

    @Transactional(readOnly = true)
    public Page<UngVienResponse> danhSachUngVien(TrangThaiUngVien trangThai, Pageable pageable) {
        Page<VbplUngVien> trang = trangThai != null
                ? ungVienRepository.findByTrangThaiOrderByNgayCrawlDesc(trangThai, pageable)
                : ungVienRepository.findAllByOrderByNgayCrawlDesc(pageable);
        return trang.map(UngVienResponse::from);
    }

    @Transactional
    public VbplVnResponse nhanUngVien(UUID id, @Valid VbplVnRequest request) {
        VbplUngVien uv = ungVienRepository.findByIdAndTrangThai(id, TrangThaiUngVien.CHUA_XU_LY)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay ung vien chua xu ly: " + id));
        VbplVnResponse ketQua = vbplVnService.tao(request);
        uv.setTrangThai(TrangThaiUngVien.DA_NHAN);
        uv.setVbplVnId(ketQua.id());
        uv.setNguoiXuLyId(currentUserId());
        uv.setNgayXuLy(OffsetDateTime.now());
        ungVienRepository.save(uv);
        return ketQua;
    }

    @Transactional
    public void boQuaUngVien(UUID id) {
        VbplUngVien uv = ungVienRepository.findByIdAndTrangThai(id, TrangThaiUngVien.CHUA_XU_LY)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay ung vien chua xu ly: " + id));
        uv.setTrangThai(TrangThaiUngVien.DA_BO_QUA);
        uv.setNguoiXuLyId(currentUserId());
        uv.setNgayXuLy(OffsetDateTime.now());
        ungVienRepository.save(uv);
    }

    @Transactional(readOnly = true)
    public List<TuKhoaResponse> danhSachTuKhoa() {
        return tuKhoaRepository.findAll().stream().map(TuKhoaResponse::from).toList();
    }

    @Transactional
    public TuKhoaResponse themTuKhoa(TuKhoaRequest request) {
        String tuKhoa = request.tuKhoa().trim();
        if (tuKhoaRepository.findAll().stream().anyMatch(t -> t.getTuKhoa().equalsIgnoreCase(tuKhoa))) {
            throw new DuplicateResourceException("Tu khoa da ton tai: " + tuKhoa);
        }
        TuKhoaCrawlPhapLuat t = new TuKhoaCrawlPhapLuat();
        t.setTuKhoa(tuKhoa);
        t.setNgayTao(OffsetDateTime.now());
        return TuKhoaResponse.from(tuKhoaRepository.save(t));
    }

    @Transactional
    public TuKhoaResponse doiTrangThaiTuKhoa(UUID id, boolean hoatDong) {
        TuKhoaCrawlPhapLuat t = tuKhoaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay tu khoa: " + id));
        t.setHoatDong(hoatDong);
        return TuKhoaResponse.from(tuKhoaRepository.save(t));
    }

    @Transactional
    public void xoaTuKhoa(UUID id) {
        if (!tuKhoaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Khong tim thay tu khoa: " + id);
        }
        tuKhoaRepository.deleteById(id);
    }

    private UUID currentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof SecurityUser user) {
            return user.getId();
        }
        return null;
    }
}
