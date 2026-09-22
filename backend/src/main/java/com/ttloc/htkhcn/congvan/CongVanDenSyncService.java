package com.ttloc.htkhcn.congvan;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.ttloc.htkhcn.cauhinh.CauHinhHeThongRepository;
import com.ttloc.htkhcn.common.SpecUtils;
import com.ttloc.htkhcn.common.exception.ResourceNotFoundException;
import com.ttloc.htkhcn.security.SecurityUser;
import com.ttloc.htkhcn.tailieu.TaiLieuDinhKemService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Dong bo MOT CHIEU (chi keo ve) "Cong van den theo don vi" tu CongVan cua
 * truong. Khong day nguoc len CongVan. Khac voi luong Van ban DHKG/VBPL VN,
 * o day KHONG can buoc "duyet ung vien" - du lieu nay da la chinh thuc (thu
 * tu don vi khac gui den), chi phan anh lai trang thai xu ly ben CongVan nen
 * dong bo la UPSERT truc tiep theo congvanId.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CongVanDenSyncService {

    private static final String MA_CAU_HINH_LAN_CUOI = "congvan_den_dong_bo_lan_cuoi";

    private final CongVanDenClient congVanDenClient;
    private final CongVanDenRepository congVanDenRepository;
    private final TaiLieuDinhKemService taiLieuDinhKemService;
    private final CauHinhHeThongRepository cauHinhHeThongRepository;

    public DongBoKetQua dongBoNgay() {
        List<JsonNode> danhSach = congVanDenClient.layTatCa();
        int soMoi = 0;
        int soCapNhat = 0;
        for (JsonNode vb : danhSach) {
            UpsertKetQua kq = upsert(vb);
            if (kq.laMoi()) {
                soMoi++;
                // Tai file NGOAI transaction cua upsert() - day la goi mang (co
                // the toi vai MB/file) toi CongVan, khong nen giu ket noi DB mo
                // trong luc cho mang (dung bai hoc tu ExcelImportService truoc do).
                if (kq.coFile()) {
                    taiVaGanFile(kq.congvanId(), kq.id());
                }
            } else {
                soCapNhat++;
            }
        }
        capNhatLanDongBoCuoi();
        log.info("Dong bo Cong van den xong: {} tu CongVan, {} moi, {} cap nhat", danhSach.size(), soMoi, soCapNhat);
        return new DongBoKetQua(danhSach.size(), soMoi, soCapNhat);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public UpsertKetQua upsert(JsonNode vb) {
        long congvanId = vb.path("id").asLong();
        var hienCo = congVanDenRepository.findByCongvanId(congvanId);
        CongVanDen cv = hienCo.orElseGet(CongVanDen::new);
        boolean laMoi = hienCo.isEmpty();

        cv.setCongvanId(congvanId);
        cv.setSoVanBan(chuoi(vb, "soVanBan"));
        cv.setSoDen(chuoi(vb, "soDen"));
        cv.setNgayDen(ngay(vb, "ngayDen"));
        cv.setNgayBanHanh(ngay(vb, "ngayBanHanh"));
        cv.setTrichYeu(vb.path("trichYeu").asText(""));
        cv.setCoQuanBanHanh(chuoi(vb, "coQuanBanHanh"));
        cv.setLoaiVanBan(chuoi(vb, "loaiVanBan"));
        cv.setNguoiKy(chuoi(vb, "nguoiKy"));
        cv.setDonViXuLyChinh(chuoi(vb, "donViXuLyChinh"));
        cv.setHanXuLy(ngay(vb, "hanXuLy"));
        cv.setNgayHoanThanh(ngay(vb, "ngayHoanThanh"));
        cv.setTrangThai(chuoi(vb, "trangThai"));
        cv.setTrangThaiText(chuoi(vb, "trangThaiText"));
        cv.setCoFile(vb.path("coFile").asBoolean(false));
        cv.setNgayDongBo(OffsetDateTime.now());
        CongVanDen daLuu = congVanDenRepository.save(cv);

        return new UpsertKetQua(daLuu.getId(), congvanId, laMoi, daLuu.isCoFile());
    }

    private void taiVaGanFile(long congvanId, UUID congVanDenId) {
        try {
            JsonNode chiTiet = congVanDenClient.chiTiet(congvanId);
            for (JsonNode f : chiTiet.path("files")) {
                long fileId = f.path("id").asLong();
                String tenFile = f.path("tenFile").asText("tep-dinh-kem");
                byte[] noiDung = congVanDenClient.taiFile(congvanId, fileId);
                taiLieuDinhKemService.taiLenTuBytes(
                        "cong_van_den", congVanDenId, tenFile, noiDung, doanLoaiMime(tenFile), currentUserId());
            }
        } catch (Exception ex) {
            // Khong chan dong bo chi vi 1 cong van loi tai file (mang, CongVan
            // tam thoi loi...) - ban ghi van duoc tao/cap nhat binh thuong.
            log.warn("Khong tai duoc file dinh kem tu CongVan cho cong van den {}: {}", congvanId, ex.getMessage());
        }
    }

    private String doanLoaiMime(String tenFile) {
        String duoi = tenFile.toLowerCase();
        if (duoi.endsWith(".pdf")) return "application/pdf";
        if (duoi.endsWith(".doc")) return "application/msword";
        if (duoi.endsWith(".docx")) return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        return "application/octet-stream";
    }

    private void capNhatLanDongBoCuoi() {
        cauHinhHeThongRepository.findById(MA_CAU_HINH_LAN_CUOI).ifPresent(ch -> {
            ch.setGiaTri(java.time.Instant.now().toString());
            ch.setNgaySua(OffsetDateTime.now());
            cauHinhHeThongRepository.save(ch);
        });
    }

    private String chuoi(JsonNode node, String truong) {
        return node.hasNonNull(truong) ? node.path(truong).asText() : null;
    }

    private LocalDate ngay(JsonNode node, String truong) {
        String s = chuoi(node, truong);
        if (s == null || s.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(s.length() > 10 ? s.substring(0, 10) : s);
        } catch (Exception ex) {
            return null;
        }
    }

    @Transactional(readOnly = true)
    public Page<CongVanDenResponse> danhSach(String trangThai, Integer nam, String tuKhoa, Pageable pageable) {
        Specification<CongVanDen> spec = SpecUtils.and(
                CongVanDenSpecifications.trangThaiBang(trangThai),
                CongVanDenSpecifications.namBang(nam),
                CongVanDenSpecifications.tuKhoaKhongDauTrong(tuKhoa));
        return congVanDenRepository.findAll(spec, pageable).map(CongVanDenResponse::from);
    }

    @Transactional(readOnly = true)
    public CongVanDenResponse chiTiet(UUID id) {
        return CongVanDenResponse.from(timHoacLoi(id));
    }

    private CongVanDen timHoacLoi(UUID id) {
        return congVanDenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay cong van den: " + id));
    }

    private UUID currentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof SecurityUser user) {
            return user.getId();
        }
        return null;
    }

    public record DongBoKetQua(int tongSoTuCongVan, int soMoi, int soCapNhat) {
    }

    private record UpsertKetQua(UUID id, long congvanId, boolean laMoi, boolean coFile) {
    }

    public record CongVanDenResponse(
            UUID id, Long congvanId, String soVanBan, String soDen, LocalDate ngayDen, LocalDate ngayBanHanh,
            String trichYeu, String coQuanBanHanh, String loaiVanBan, String nguoiKy, String donViXuLyChinh,
            LocalDate hanXuLy, LocalDate ngayHoanThanh, String trangThai, String trangThaiText, String ghiChu,
            boolean coFile, OffsetDateTime ngayDongBo) {
        static CongVanDenResponse from(CongVanDen cv) {
            return new CongVanDenResponse(
                    cv.getId(), cv.getCongvanId(), cv.getSoVanBan(), cv.getSoDen(), cv.getNgayDen(),
                    cv.getNgayBanHanh(), cv.getTrichYeu(), cv.getCoQuanBanHanh(), cv.getLoaiVanBan(), cv.getNguoiKy(),
                    cv.getDonViXuLyChinh(), cv.getHanXuLy(), cv.getNgayHoanThanh(), cv.getTrangThai(),
                    cv.getTrangThaiText(), cv.getGhiChu(), cv.isCoFile(), cv.getNgayDongBo());
        }
    }
}
