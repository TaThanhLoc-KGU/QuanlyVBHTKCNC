package com.ttloc.htkhcn.congvan;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.ttloc.htkhcn.cauhinh.CauHinhHeThongRepository;
import com.ttloc.htkhcn.common.exception.ResourceNotFoundException;
import com.ttloc.htkhcn.crawlphapluat.TrangThaiUngVien;
import com.ttloc.htkhcn.security.SecurityUser;
import com.ttloc.htkhcn.tailieu.TaiLieuDinhKemService;
import com.ttloc.htkhcn.vanbandhkg.VanBanDhkgRequest;
import com.ttloc.htkhcn.vanbandhkg.VanBanDhkgResponse;
import com.ttloc.htkhcn.vanbandhkg.VanBanDhkgRepository;
import com.ttloc.htkhcn.vanbandhkg.VanBanDhkgService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Dong bo MOT CHIEU (chi keo ve) "Van ban noi bo don vi" tu CongVan cua
 * truong vao module Van ban DHKG - khong ghi nguoc len CongVan (theo yeu cau
 * nguoi dung, du API_VANBANNOIBO.md ho tro ca CRUD). Ket qua vao bang ung
 * vien (van_ban_dhkg_ung_vien), nguoi dung duyet tung dong (chon Loai van
 * ban + Tinh trang hieu luc - 2 truong CongVan khong co) truoc khi nhan vao
 * van_ban_dhkg chinh thuc, giong het luong crawl-phap-luat.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CongVanImportService {

    private static final String MA_CAU_HINH_LAN_CUOI = "congvan_dong_bo_lan_cuoi";
    // 0 = Du thao (chua chinh thuc, khong dong bo), 1 = Da ban hanh, 2 = Thu hoi.
    private static final int TRANG_THAI_DU_THAO = 0;

    private final CongVanClient congVanClient;
    private final VanBanDhkgUngVienRepository ungVienRepository;
    private final VanBanDhkgRepository vanBanDhkgRepository;
    private final VanBanDhkgService vanBanDhkgService;
    private final TaiLieuDinhKemService taiLieuDinhKemService;
    private final CauHinhHeThongRepository cauHinhHeThongRepository;

    public DongBoKetQua dongBoNgay() {
        List<JsonNode> danhSach = congVanClient.danhSach(null);
        int soMoi = 0;
        for (JsonNode vb : danhSach) {
            if (taoUngVienNeuMoi(vb)) {
                soMoi++;
            }
        }
        capNhatLanDongBoCuoi();
        log.info("Dong bo CongVan xong: {} van ban tu CongVan, {} ung vien moi", danhSach.size(), soMoi);
        return new DongBoKetQua(danhSach.size(), soMoi);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean taoUngVienNeuMoi(JsonNode vb) {
        long congvanId = vb.path("id").asLong();
        int trangThaiCongvan = vb.path("trangThai").asInt(-1);
        if (trangThaiCongvan == TRANG_THAI_DU_THAO) {
            return false; // van con la du thao, chua chinh thuc - khong dong bo
        }
        if (ungVienRepository.existsByCongvanId(congvanId) || vanBanDhkgRepository.existsBySoHieu(vb.path("soHieu").asText())) {
            return false;
        }
        VanBanDhkgUngVien uv = new VanBanDhkgUngVien();
        uv.setCongvanId(congvanId);
        uv.setSoHieu(vb.path("soHieu").asText());
        uv.setTieuDe(vb.path("tieuDe").asText());
        uv.setNoiDung(vb.hasNonNull("noiDung") ? vb.path("noiDung").asText() : null);
        uv.setNgayBanHanh(chuoiThanhNgay(vb.path("ngayBanHanh").asText(null)));
        uv.setNguoiKy(vb.hasNonNull("nguoiKy") ? vb.path("nguoiKy").asText() : null);
        uv.setTrangThaiCongvan(trangThaiCongvan);
        uv.setSoFile(vb.path("soFile").asInt(0));
        uv.setNgayDongBo(OffsetDateTime.now());
        ungVienRepository.save(uv);
        return true;
    }

    private void capNhatLanDongBoCuoi() {
        cauHinhHeThongRepository.findById(MA_CAU_HINH_LAN_CUOI).ifPresent(ch -> {
            ch.setGiaTri(java.time.Instant.now().toString());
            ch.setNgaySua(OffsetDateTime.now());
            cauHinhHeThongRepository.save(ch);
        });
    }

    private LocalDate chuoiThanhNgay(String s) {
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
    public Page<UngVienDhkgResponse> danhSachUngVien(TrangThaiUngVien trangThai, Pageable pageable) {
        Page<VanBanDhkgUngVien> trang = trangThai != null
                ? ungVienRepository.findByTrangThaiOrderByNgayDongBoDesc(trangThai, pageable)
                : ungVienRepository.findAllByOrderByNgayDongBoDesc(pageable);
        return trang.map(UngVienDhkgResponse::from);
    }

    @Transactional
    public VanBanDhkgResponse nhanUngVien(UUID id, @Valid VanBanDhkgRequest request) {
        VanBanDhkgUngVien uv = ungVienRepository.findByIdAndTrangThai(id, TrangThaiUngVien.CHUA_XU_LY)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay ung vien chua xu ly: " + id));

        VanBanDhkgResponse ketQua = vanBanDhkgService.tao(request);
        taiVaGanFile(uv.getCongvanId(), ketQua.id());

        uv.setTrangThai(TrangThaiUngVien.DA_NHAN);
        uv.setVanBanDhkgId(ketQua.id());
        uv.setNguoiXuLyId(currentUserId());
        uv.setNgayXuLy(OffsetDateTime.now());
        ungVienRepository.save(uv);
        return ketQua;
    }

    private void taiVaGanFile(long congvanId, UUID vanBanDhkgId) {
        if (!congVanClient.daCauHinh()) {
            return;
        }
        try {
            JsonNode chiTiet = congVanClient.chiTiet(congvanId);
            JsonNode files = chiTiet.path("files");
            for (JsonNode f : files) {
                long fileId = f.path("id").asLong();
                String tenFile = f.path("tenFile").asText("tep-dinh-kem");
                byte[] noiDung = congVanClient.taiFile(congvanId, fileId);
                taiLieuDinhKemService.taiLenTuBytes(
                        "van_ban_dhkg", vanBanDhkgId, tenFile, noiDung, doanLoaiMime(tenFile), currentUserId());
            }
        } catch (Exception ex) {
            // Khong chan viec nhan van ban chi vi loi tai file dinh kem (mang,
            // CongVan tam thoi loi...) - nguoi dung van co ban ghi van_ban_dhkg,
            // co the tai file thu cong sau qua AttachmentPanel neu can.
            log.warn("Khong tai duoc file dinh kem tu CongVan cho van ban {}: {}", congvanId, ex.getMessage());
        }
    }

    private String doanLoaiMime(String tenFile) {
        String duoi = tenFile.toLowerCase();
        if (duoi.endsWith(".pdf")) return "application/pdf";
        if (duoi.endsWith(".doc")) return "application/msword";
        if (duoi.endsWith(".docx")) return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        return "application/octet-stream";
    }

    @Transactional
    public void boQuaUngVien(UUID id) {
        VanBanDhkgUngVien uv = ungVienRepository.findByIdAndTrangThai(id, TrangThaiUngVien.CHUA_XU_LY)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay ung vien chua xu ly: " + id));
        uv.setTrangThai(TrangThaiUngVien.DA_BO_QUA);
        uv.setNguoiXuLyId(currentUserId());
        uv.setNgayXuLy(OffsetDateTime.now());
        ungVienRepository.save(uv);
    }

    private UUID currentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof SecurityUser user) {
            return user.getId();
        }
        return null;
    }

    public record DongBoKetQua(int tongSoTuCongVan, int soUngVienMoi) {
    }

    public record UngVienDhkgResponse(
            UUID id, String soHieu, String tieuDe, String noiDung, LocalDate ngayBanHanh, String nguoiKy,
            int soFile, TrangThaiUngVien trangThai, OffsetDateTime ngayDongBo) {
        static UngVienDhkgResponse from(VanBanDhkgUngVien uv) {
            return new UngVienDhkgResponse(
                    uv.getId(), uv.getSoHieu(), uv.getTieuDe(), uv.getNoiDung(), uv.getNgayBanHanh(), uv.getNguoiKy(),
                    uv.getSoFile(), uv.getTrangThai(), uv.getNgayDongBo());
        }
    }
}
