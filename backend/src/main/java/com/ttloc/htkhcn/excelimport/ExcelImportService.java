package com.ttloc.htkhcn.excelimport;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ttloc.htkhcn.common.ModuleKey;
import com.ttloc.htkhcn.common.TinhTrangHieuLuc;
import com.ttloc.htkhcn.common.exception.BadRequestException;
import com.ttloc.htkhcn.danhmuc.LoaiVanBan;
import com.ttloc.htkhcn.danhmuc.LoaiVanBanRepository;
import com.ttloc.htkhcn.doanra.DoanRaRequest;
import com.ttloc.htkhcn.doanvao.DoanVaoRequest;
import com.ttloc.htkhcn.doitac.DoiTac;
import com.ttloc.htkhcn.doitac.DoiTacRepository;
import com.ttloc.htkhcn.doitac.DoiTacRequest;
import com.ttloc.htkhcn.doitac.LoaiDoiTac;
import com.ttloc.htkhcn.mou.MouRequest;
import com.ttloc.htkhcn.mou.PhamViHopTac;
import com.ttloc.htkhcn.phienimport.PhienImport;
import com.ttloc.htkhcn.vanbandhkg.VanBanDhkgRequest;
import com.ttloc.htkhcn.vbplvn.VbplVnRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Import Excel cho 6 module (SPEC muc 4.3). Moi module co 1 ham parse rieng
 * doc file bang Apache POI, tra ve danh sach ImportRowResult (hop le hoac co
 * loi). Buoc "xem truoc" chi goi parse; buoc "xac nhan" parse LAI (cung file)
 * roi goi truc tiep *Service.tao() da co san cua tung module - tai su dung
 * toan bo validate/audit/trigger da xay cho CRUD, khong viet lai logic ghi DB.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExcelImportService {

    private static final double NGUONG_GAN_GIONG = 0.5;

    private final DoiTacRepository doiTacRepository;
    private final LoaiVanBanRepository loaiVanBanRepository;
    private final ImportRowWriter importRowWriter;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ImportPreviewResponse xemTruoc(ModuleKey moDun, MultipartFile file) {
        List<ImportRowResult> dong = parseFile(moDun, file);
        long soLoi = dong.stream().filter(d -> !d.hopLe()).count();
        return new ImportPreviewResponse(dong.size(), dong.size() - (int) soLoi, (int) soLoi, dong);
    }

    // KHONG @Transactional o day: moi dong duoc ghi trong transaction RIENG
    // (ImportRowWriter.ghiMotDong, REQUIRES_NEW) de 1 dong loi khong lam rollback
    // ca phien. Vi vay ban ghi "phien_import" (tao dau, cap nhat ket qua cuoi)
    // cung phai la REQUIRES_NEW (xem ImportRowWriter) de LUON commit ngay, ke
    // ca khi ham nay dang chay trong 1 transaction bao ngoai (test @Transactional) -
    // neu khong, cac transaction REQUIRES_NEW cua tung dong (chay tren connection
    // khac) se khong thay duoc du lieu chua commit, gay loi vi pham FK.
    public ImportConfirmResponse xacNhan(ModuleKey moDun, MultipartFile file, UUID nguoiThucHienId) {
        List<ImportRowResult> dong = parseFile(moDun, file);

        PhienImport phien = new PhienImport();
        phien.setMoDun(moDun);
        phien.setTenFile(file.getOriginalFilename());
        phien.setNguoiThucHienId(nguoiThucHienId);
        phien.setTongSoDong(dong.size());
        phien = importRowWriter.taoPhien(phien);

        int thanhCong = 0;
        List<ImportConfirmResponse.RowError> loi = new ArrayList<>();
        for (ImportRowResult row : dong) {
            if (!row.hopLe()) {
                loi.add(new ImportConfirmResponse.RowError(row.soDong(), String.join("; ", row.loi())));
                continue;
            }
            try {
                importRowWriter.ghiMotDong(phien.getId(), moDun, row.doiTuong());
                thanhCong++;
            } catch (Exception ex) {
                log.warn("Loi khi luu dong {} cua phien import {}: {}", row.soDong(), phien.getId(), ex.getMessage());
                loi.add(new ImportConfirmResponse.RowError(row.soDong(), "Loi khi luu: " + ex.getMessage()));
            }
        }

        importRowWriter.capNhatKetQua(phien.getId(), thanhCong, loi.size(), toJson(loi));

        return new ImportConfirmResponse(phien.getId(), dong.size(), thanhCong, loi.size(), loi);
    }

    // Ten sheet ung vien cho tung module - file mau P.HTKHCN dung 1 workbook
    // GOM CA 5 sheet (moi module 1 sheet, dat ten giong nhau giua cac nam), nen
    // phai chon dung sheet theo module dang import thay vi luon doc sheet dau
    // tien - neu khong, chon module "Doan vao"/"Doan ra" tren 1 file nhu vay se
    // vo tinh doc nham sheet "VB DHKG" (sheet dau) va bao loi "thieu truong" hang
    // loat du du lieu that su nam o sheet khac trong cung file.
    private static final Map<ModuleKey, List<String>> TEN_SHEET_UNG_VIEN = Map.of(
            ModuleKey.DOI_TAC, List.of("Doi tac"),
            ModuleKey.VAN_BAN_DHKG, List.of("VB DHKG", "Van ban DHKG"),
            ModuleKey.VBPL_VN, List.of("VBPL VN"),
            ModuleKey.MOU, List.of("MoU"),
            ModuleKey.DOAN_VAO, List.of("Doan vao"),
            ModuleKey.DOAN_RA, List.of("Doan ra"));

    private Sheet timSheet(Workbook wb, ModuleKey moDun) {
        List<String> tenUngVien = TEN_SHEET_UNG_VIEN.getOrDefault(moDun, List.of());
        for (int i = 0; i < wb.getNumberOfSheets(); i++) {
            Sheet sheet = wb.getSheetAt(i);
            String tenChuan = ExcelUtils.chuanHoa(sheet.getSheetName());
            for (String ung : tenUngVien) {
                if (tenChuan.equals(ExcelUtils.chuanHoa(ung))) {
                    return sheet;
                }
            }
        }
        // Khong khop ten sheet nao (file don gian chi co 1 sheet, ten tuy y) -
        // giu hanh vi cu: doc sheet dau tien.
        return wb.getSheetAt(0);
    }

    private List<ImportRowResult> parseFile(ModuleKey moDun, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File Excel khong duoc de trong");
        }
        try (InputStream is = file.getInputStream(); Workbook wb = WorkbookFactory.create(is)) {
            Sheet sheet = timSheet(wb, moDun);
            Map<String, Integer> header = ExcelUtils.docHeader(sheet);
            List<ImportRowResult> ketQua = new ArrayList<>();
            int soDongCuoi = sheet.getLastRowNum();
            for (int i = 1; i <= soDongCuoi; i++) {
                Row row = sheet.getRow(i);
                if (row == null || laDongTrong(row)) {
                    continue;
                }
                ketQua.add(parseDong(moDun, row, header, i + 1));
            }
            return ketQua;
        } catch (IOException ex) {
            throw new BadRequestException("Khong doc duoc file Excel: " + ex.getMessage());
        }
    }

    private boolean laDongTrong(Row row) {
        for (Cell c : row) {
            if (ExcelUtils.getCellString(c) != null && !ExcelUtils.getCellString(c).isBlank()) {
                return false;
            }
        }
        return true;
    }

    private ImportRowResult parseDong(ModuleKey moDun, Row row, Map<String, Integer> header, int soDong) {
        return switch (moDun) {
            case DOI_TAC -> parseDoiTac(row, header, soDong);
            case VAN_BAN_DHKG -> parseVanBan(row, header, soDong, com.ttloc.htkhcn.common.PhamViVanBan.DHKG);
            case VBPL_VN -> parseVanBan(row, header, soDong, com.ttloc.htkhcn.common.PhamViVanBan.VBPL);
            case MOU -> parseMou(row, header, soDong);
            case DOAN_VAO -> parseDoanVao(row, header, soDong);
            case DOAN_RA -> parseDoanRa(row, header, soDong);
            case CONG_VAN_DEN -> throw new BadRequestException(
                    "Cong van den khong ho tro nhap tu Excel - du lieu duoc dong bo tu he thong CongVan cua truong");
        };
    }

    // ---------- Doi tac ----------

    private ImportRowResult parseDoiTac(Row row, Map<String, Integer> h, int soDong) {
        List<String> loi = new ArrayList<>();
        String ten = ExcelUtils.getCellString(row, ExcelUtils.timCot(h, "Ten doi tac", "Ten"));
        LoaiDoiTac loai = chuanHoaLoaiDoiTac(ExcelUtils.getCellString(row, ExcelUtils.timCot(h, "Loai doi tac")));
        String quocGia = ExcelUtils.getCellString(row, ExcelUtils.timCot(h, "Quoc gia"));
        String diaChi = ExcelUtils.getCellString(row, ExcelUtils.timCot(h, "Dia chi"));
        String lienHe = ExcelUtils.getCellString(row, ExcelUtils.timCot(h, "Thong tin lien he"));
        String ghiChu = ExcelUtils.getCellString(row, ExcelUtils.timCot(h, "Ghi chu"));

        if (ten == null || ten.isBlank()) {
            loi.add("Thieu Ten doi tac");
        }
        if (loai == null) {
            loi.add("Loai doi tac phai la 'Trong nuoc' hoac 'Ngoai nuoc'");
        } else if (loai == LoaiDoiTac.NGOAI_NUOC && (quocGia == null || quocGia.isBlank())) {
            loi.add("Thieu Quoc gia (bat buoc khi Loai doi tac = Ngoai nuoc)");
        }

        Map<String, Object> duLieu = mapDuLieu("tenDoiTac", ten, "loaiDoiTac", loai, "quocGia", quocGia);
        if (!loi.isEmpty()) {
            return new ImportRowResult(soDong, duLieu, loi, null, null);
        }
        return new ImportRowResult(soDong, duLieu, loi,
                new DoiTacRequest(ten, loai, quocGia, diaChi, lienHe, ghiChu), null);
    }

    // ---------- Van ban DHKG / VBPL VN ----------

    private ImportRowResult parseVanBan(Row row, Map<String, Integer> h, int soDong, com.ttloc.htkhcn.common.PhamViVanBan phamVi) {
        List<String> loi = new ArrayList<>();
        String soHieu = ExcelUtils.getCellString(row, ExcelUtils.timCot(h, "So hieu"));
        String tenVanBan = ExcelUtils.getCellString(row, ExcelUtils.timCot(h, "Ten van ban"));
        String loaiText = ExcelUtils.getCellString(row, ExcelUtils.timCot(h, "Loai van ban"));
        LocalDate ngayBanHanh = ExcelUtils.getCellDate(row, ExcelUtils.timCot(h, "Ngay ban hanh"));
        LocalDate ngayHieuLuc = ExcelUtils.getCellDate(row, ExcelUtils.timCot(h, "Ngay hieu luc", "Ngay co hieu luc"));
        String tinhTrangText = ExcelUtils.getCellString(row, ExcelUtils.timCot(h, "Tinh trang hieu luc"));
        String coQuan = ExcelUtils.getCellString(row, ExcelUtils.timCot(h, "Co quan ban hanh"));
        String ghiChu = ExcelUtils.getCellString(row, ExcelUtils.timCot(h, "Ghi chu"));
        String noiDung = ExcelUtils.getCellString(row, ExcelUtils.timCot(h, "Noi dung chinh"));

        if (soHieu == null || soHieu.isBlank()) {
            loi.add("Thieu So hieu");
        }
        if (tenVanBan == null || tenVanBan.isBlank()) {
            loi.add("Thieu Ten van ban");
        }
        if (ngayBanHanh == null) {
            loi.add("Thieu hoac sai dinh dang Ngay ban hanh (dd/MM/yyyy)");
        }
        LoaiVanBan loaiVanBan = timLoaiVanBan(loaiText, phamVi);
        if (loaiVanBan == null) {
            loi.add("Loai van ban khong hop le: " + loaiText);
        }
        TinhTrangHieuLuc tinhTrang = chuanHoaTinhTrang(tinhTrangText);
        if (tinhTrang == null) {
            loi.add("Tinh trang hieu luc khong hop le: " + tinhTrangText);
        }
        if (coQuan == null || coQuan.isBlank()) {
            loi.add("Thieu Co quan ban hanh");
        }

        Map<String, Object> duLieu = mapDuLieu("soHieu", soHieu, "tenVanBan", tenVanBan, "loaiVanBan", loaiText,
                "ngayBanHanh", ngayBanHanh, "tinhTrangHieuLuc", tinhTrangText);
        if (!loi.isEmpty()) {
            return new ImportRowResult(soDong, duLieu, loi, null, null);
        }
        Object req = phamVi == com.ttloc.htkhcn.common.PhamViVanBan.DHKG
                ? new VanBanDhkgRequest(soHieu, tenVanBan, loaiVanBan.getId(), ngayBanHanh, ngayHieuLuc, tinhTrang, coQuan, ghiChu, noiDung)
                : new VbplVnRequest(soHieu, tenVanBan, loaiVanBan.getId(), ngayBanHanh, ngayHieuLuc, tinhTrang, coQuan, ghiChu, noiDung);
        return new ImportRowResult(soDong, duLieu, loi, req, null);
    }

    // ---------- MoU ----------

    private ImportRowResult parseMou(Row row, Map<String, Integer> h, int soDong) {
        List<String> loi = new ArrayList<>();
        String tenDoiTac = ExcelUtils.getCellString(row, ExcelUtils.timCot(h, "Ten doi tac", "Doi tac"));
        LoaiDoiTac loaiDoiTacGoiY = chuanHoaLoaiDoiTac(ExcelUtils.getCellString(row, ExcelUtils.timCot(h, "Loai doi tac")));
        String quocGiaDoiTac = ExcelUtils.getCellString(row, ExcelUtils.timCot(h, "Quoc gia"));
        String tenTaiLieu = ExcelUtils.getCellString(row, ExcelUtils.timCot(h, "Ten tai lieu"));
        LocalDate ngayBanHanh = ExcelUtils.getCellDate(row, ExcelUtils.timCot(h, "Ngay ban hanh"));
        LocalDate ngayHetHan = ExcelUtils.getCellDate(row, ExcelUtils.timCot(h, "Ngay het han"));
        String caNhanDauMoi = ExcelUtils.getCellString(row, ExcelUtils.timCot(h, "Ca nhan/don vi dau moi", "Ca nhan dau moi"));
        String donViThucHien = ExcelUtils.getCellString(row, ExcelUtils.timCot(h, "Don vi thuc hien"));
        String phamViText = ExcelUtils.getCellString(row, ExcelUtils.timCot(h, "Pham vi hop tac"));
        String linhVuc = ExcelUtils.getCellString(row, ExcelUtils.timCot(h, "Linh vuc hop tac"));
        String dauMoiMou = ExcelUtils.getCellString(row, ExcelUtils.timCot(h, "Dau moi ghi trong mou"));
        String daiDienKgu = ExcelUtils.getCellString(row, ExcelUtils.timCot(h, "Dai dien kgu ky"));
        String thoiHan = ExcelUtils.getCellString(row, ExcelUtils.timCot(h, "Thoi han hieu luc"));
        String soCongVan = ExcelUtils.getCellString(row, ExcelUtils.timCot(h, "So cong van"));

        if (tenDoiTac == null || tenDoiTac.isBlank()) {
            loi.add("Thieu Ten doi tac");
        }
        if (ngayBanHanh == null) {
            loi.add("Thieu hoac sai dinh dang Ngay ban hanh (dd/MM/yyyy)");
        }

        Map<String, Object> duLieu = mapDuLieu("tenDoiTac", tenDoiTac, "tenTaiLieu", tenTaiLieu,
                "ngayBanHanh", ngayBanHanh, "ngayHetHan", ngayHetHan);
        if (!loi.isEmpty()) {
            return new ImportRowResult(soDong, duLieu, loi, null, null);
        }

        var kq = timHoacDanhDauTaoMoiDoiTac(tenDoiTac, loaiDoiTacGoiY, quocGiaDoiTac);
        PhamViHopTac phamVi = chuanHoaPhamViHopTac(phamViText);
        MouRequest req = new MouRequest(kq.id, tenTaiLieu, ngayBanHanh, ngayHetHan, caNhanDauMoi, donViThucHien,
                phamVi, linhVuc, dauMoiMou, daiDienKgu, thoiHan, soCongVan);
        return new ImportRowResult(soDong, duLieu, loi, req, kq.ghiChu);
    }

    // ---------- Doan vao ----------

    private ImportRowResult parseDoanVao(Row row, Map<String, Integer> h, int soDong) {
        List<String> loi = new ArrayList<>();
        String tenDoan = ExcelUtils.getCellString(row, ExcelUtils.timCot(h, "Ten doan"));
        String tenDoiTac = ExcelUtils.getCellString(row, ExcelUtils.timCot(h, "Doi tac lien quan", "Doi tac"));
        LocalDate thoiGianDen = ExcelUtils.getCellDate(row, ExcelUtils.timCot(h, "Thoi gian den"));
        LocalDate thoiGianDi = ExcelUtils.getCellDate(row, ExcelUtils.timCot(h, "Thoi gian di"));
        Integer soNuocNgoai = ExcelUtils.getCellInt(row, ExcelUtils.timCot(h, "So luong nguoi nuoc ngoai", "So luong NNN"));
        Integer soVietNam = ExcelUtils.getCellInt(row, ExcelUtils.timCot(h, "So luong nguoi viet nam", "So luong nguoi VN"));
        String quocTichText = ExcelUtils.getCellString(row, ExcelUtils.timCot(h, "Quoc tich"));
        String noiDung = ExcelUtils.getCellString(row, ExcelUtils.timCot(h, "Noi dung lam viec"));

        if (tenDoan == null || tenDoan.isBlank()) {
            loi.add("Thieu Ten doan");
        }
        if (thoiGianDen == null) {
            loi.add("Thieu hoac sai dinh dang Thoi gian den");
        }
        if (thoiGianDi == null) {
            loi.add("Thieu hoac sai dinh dang Thoi gian di");
        } else if (thoiGianDen != null && thoiGianDi.isBefore(thoiGianDen)) {
            loi.add("Thoi gian di khong duoc truoc Thoi gian den");
        }
        if (soNuocNgoai == null) {
            loi.add("Thieu So luong nguoi nuoc ngoai");
        }
        String[] quocTich = quocTichText == null || quocTichText.isBlank()
                ? new String[0]
                : quocTichText.split("[,;]");
        for (int i = 0; i < quocTich.length; i++) {
            quocTich[i] = quocTich[i].trim();
        }
        if (quocTich.length == 0) {
            loi.add("Thieu Quoc tich (nhieu gia tri ngan cach boi , hoac ;)");
        }

        Map<String, Object> duLieu = mapDuLieu("tenDoan", tenDoan, "thoiGianDen", thoiGianDen, "thoiGianDi", thoiGianDi);
        if (!loi.isEmpty()) {
            return new ImportRowResult(soDong, duLieu, loi, null, null);
        }

        UUID doiTacId = null;
        String ghiChuGoiY = null;
        if (tenDoiTac != null && !tenDoiTac.isBlank()) {
            var kq = timHoacDanhDauTaoMoiDoiTac(tenDoiTac, null, null);
            doiTacId = kq.id;
            ghiChuGoiY = kq.ghiChu;
        }

        DoanVaoRequest req = new DoanVaoRequest(tenDoan, doiTacId, thoiGianDen, thoiGianDi, soNuocNgoai, soVietNam,
                quocTich, noiDung);
        return new ImportRowResult(soDong, duLieu, loi, req, ghiChuGoiY);
    }

    // ---------- Doan ra ----------

    private ImportRowResult parseDoanRa(Row row, Map<String, Integer> h, int soDong) {
        List<String> loi = new ArrayList<>();
        String tenDoiTac = ExcelUtils.getCellString(
                row, ExcelUtils.timCot(h, "Don vi lam viec", "Doi tac", "Ten don vi lam viec nn"));
        LocalDate thoiGianDi = ExcelUtils.getCellDate(row, ExcelUtils.timCot(h, "Thoi gian di"));
        LocalDate thoiGianVe = ExcelUtils.getCellDate(row, ExcelUtils.timCot(h, "Thoi gian ve"));
        String diaDiemDi = ExcelUtils.getCellString(row, ExcelUtils.timCot(h, "Dia diem di"));
        String diaDiemDen = ExcelUtils.getCellString(row, ExcelUtils.timCot(h, "Dia diem den"));
        Integer soLuongDoan = ExcelUtils.getCellInt(row, ExcelUtils.timCot(h, "So luong doan"));
        String thanhPhan = ExcelUtils.getCellString(row, ExcelUtils.timCot(h, "Thanh phan"));
        String quocGiaLamViec = ExcelUtils.getCellString(row, ExcelUtils.timCot(h, "Quoc gia lam viec"));
        String noiDung = ExcelUtils.getCellString(row, ExcelUtils.timCot(h, "Noi dung lam viec"));

        if (tenDoiTac == null || tenDoiTac.isBlank()) {
            loi.add("Thieu Don vi lam viec (doi tac)");
        }
        if (thoiGianDi == null) {
            loi.add("Thieu hoac sai dinh dang Thoi gian di");
        }
        if (thoiGianVe == null) {
            loi.add("Thieu hoac sai dinh dang Thoi gian ve");
        } else if (thoiGianDi != null && thoiGianVe.isBefore(thoiGianDi)) {
            loi.add("Thoi gian ve khong duoc truoc Thoi gian di");
        }
        if (soLuongDoan == null) {
            loi.add("Thieu So luong doan");
        }
        if (quocGiaLamViec == null || quocGiaLamViec.isBlank()) {
            loi.add("Thieu Quoc gia lam viec");
        }

        Map<String, Object> duLieu = mapDuLieu("tenDoiTac", tenDoiTac, "thoiGianDi", thoiGianDi, "thoiGianVe", thoiGianVe);
        if (!loi.isEmpty()) {
            return new ImportRowResult(soDong, duLieu, loi, null, null);
        }

        // Doan ra = can bo di cong tac NUOC NGOAI (theo dinh nghia module), nen
        // doi tac tu tao moi luon la Ngoai nuoc, khac voi Doan vao/MoU khong the
        // suy ra chieu nay tu du lieu sheet.
        var kq = timHoacDanhDauTaoMoiDoiTac(tenDoiTac, LoaiDoiTac.NGOAI_NUOC, quocGiaLamViec);
        DoanRaRequest req = new DoanRaRequest(kq.id, thoiGianDi, thoiGianVe, diaDiemDi, diaDiemDen, soLuongDoan,
                thanhPhan, quocGiaLamViec, noiDung);
        return new ImportRowResult(soDong, duLieu, loi, req, kq.ghiChu);
    }

    // ---------- Tien ich chung ----------

    /**
     * SPEC muc 3.1/4.3: khi ten doi tac trong file gan giong 1 doi tac da co
     * (pg_trgm), goi y gop vao do thay vi tao ban ghi moi. O day tu dong chon
     * ket qua gan giong nhat neu co, neu khong co gi gan giong thi tao moi -
     * don gian hoa vi chua co man hinh cho nguoi dung tu chon giua cac goi y
     * (de danh cho giai doan frontend).
     */
    private DoiTacKetQua timHoacDanhDauTaoMoiDoiTac(String ten, LoaiDoiTac loaiGoiY, String quocGia) {
        List<DoiTac> ganGiong = doiTacRepository.timDoiTacGanGiong(ten);
        if (!ganGiong.isEmpty()) {
            DoiTac gan = ganGiong.get(0);
            if (gan.getTenDoiTac().equalsIgnoreCase(ten)) {
                return new DoiTacKetQua(gan.getId(), null);
            }
            Double diem = doiTacRepository.diemGiongVoi(ten, gan.getId());
            // Nguong "%" cua pg_trgm (~0.3) chi du de GOI Y, khong du de TU DONG
            // gop - 2 ten chi chung tien to nhu "Dai hoc ..." van vuot 0.3 nhung
            // ro rang la 2 doi tac khac nhau. Chi tu dong gop khi diem >= 0.5.
            if (diem != null && diem >= NGUONG_GAN_GIONG) {
                return new DoiTacKetQua(gan.getId(),
                        "Da gop vao doi tac gan giong da co: \"" + gan.getTenDoiTac() + "\" (diem giong " + diem + ")");
            }
        }
        LoaiDoiTac loai = loaiGoiY != null ? loaiGoiY : LoaiDoiTac.TRONG_NUOC;
        DoiTac moi = importRowWriter.taoDoiTacMoiNgay(taoDoiTacMoi(ten, loai, quocGia));
        return new DoiTacKetQua(moi.getId(), "Da tu tao moi doi tac: \"" + ten + "\"");
    }

    private DoiTac taoDoiTacMoi(String ten, LoaiDoiTac loai, String quocGia) {
        DoiTac d = new DoiTac();
        d.setTenDoiTac(ten);
        d.setLoaiDoiTac(loai);
        // chk_doi_tac_quoc_gia (V3 migration) bat quoc gia khi Ngoai nuoc - sheet
        // MoU/Doan vao/Doan ra khong luon co cot Quoc gia rieng, dung placeholder
        // de khong chan ca dong import, sua lai sau trong module Doi tac.
        if (loai == LoaiDoiTac.NGOAI_NUOC) {
            d.setQuocGia(quocGia != null && !quocGia.isBlank() ? quocGia : "Chua xac dinh");
        }
        return d;
    }

    private record DoiTacKetQua(UUID id, String ghiChu) {
    }

    // Danh muc "Loai van ban" luu ten day du theo Dieu 4 Luat Ban hanh VBQPPL
    // (vi du "Nghi dinh cua Chinh phu"), nhung du lieu thuc te (Excel co san)
    // thuong chi ghi ten ngan gon ("Nghi dinh"). Map nay quy doi ten ngan gon
    // ve ten day du tuong ung de import khong bi bao "khong hop le" mot cach
    // gia tao - chi ap dung khi khop TUYET DOI (khong phai substring) de tranh
    // "Thong tu" khop nham vao "Thong tu lien tich".
    private static final Map<String, String> LOAI_VAN_BAN_TEN_RUT_GON = Map.of(
            "luat", "Luật, Bộ luật",
            "nghi dinh", "Nghị định của Chính phủ",
            "thong tu", "Thông tư của Bộ trưởng, Thủ trưởng cơ quan ngang bộ",
            "phap lenh", "Pháp lệnh của Ủy ban Thường vụ Quốc hội",
            "quyet dinh", "Quyết định của Thủ tướng Chính phủ",
            "nghi quyet", "Nghị quyết của Quốc hội");

    private LoaiVanBan timLoaiVanBan(String text, com.ttloc.htkhcn.common.PhamViVanBan phamVi) {
        if (text == null || text.isBlank()) {
            return null;
        }
        String chuan = ExcelUtils.chuanHoa(text);
        List<LoaiVanBan> danhMuc = loaiVanBanRepository.findByPhamViOrderByThuTu(phamVi);
        LoaiVanBan khopTrucTiep = danhMuc.stream()
                .filter(l -> ExcelUtils.chuanHoa(l.getTen()).equals(chuan))
                .findFirst()
                .orElse(null);
        if (khopTrucTiep != null) {
            return khopTrucTiep;
        }
        String tenDayDu = LOAI_VAN_BAN_TEN_RUT_GON.get(chuan);
        if (tenDayDu == null) {
            return null;
        }
        return danhMuc.stream()
                .filter(l -> l.getTen().equals(tenDayDu))
                .findFirst()
                .orElse(null);
    }

    private LoaiDoiTac chuanHoaLoaiDoiTac(String text) {
        if (text == null) {
            return null;
        }
        String c = ExcelUtils.chuanHoa(text);
        if (c.equals("trong nuoc") || c.equals("x") || c.isBlank()) {
            return c.isBlank() ? null : LoaiDoiTac.TRONG_NUOC;
        }
        if (c.equals("ngoai nuoc")) {
            return LoaiDoiTac.NGOAI_NUOC;
        }
        return null;
    }

    private TinhTrangHieuLuc chuanHoaTinhTrang(String text) {
        if (text == null) {
            return null;
        }
        String c = ExcelUtils.chuanHoa(text);
        if (c.contains("con hieu luc")) return TinhTrangHieuLuc.CON_HIEU_LUC;
        if (c.contains("het hieu luc toan bo")) return TinhTrangHieuLuc.HET_HIEU_LUC_TOAN_BO;
        if (c.contains("het hieu luc mot phan")) return TinhTrangHieuLuc.HET_HIEU_LUC_MOT_PHAN;
        if (c.contains("thay the")) return TinhTrangHieuLuc.BI_THAY_THE;
        if (c.contains("bai bo")) return TinhTrangHieuLuc.DA_BI_BAI_BO;
        return null;
    }

    private PhamViHopTac chuanHoaPhamViHopTac(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        String c = ExcelUtils.chuanHoa(text);
        if (c.equals("x") || c.contains("toan dien") || c.contains("xa giao")) {
            return PhamViHopTac.TOAN_DIEN;
        }
        if (c.contains("linh vuc")) {
            return PhamViHopTac.THEO_LINH_VUC;
        }
        return null;
    }

    private Map<String, Object> mapDuLieu(Object... keyValues) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 0; i < keyValues.length; i += 2) {
            map.put((String) keyValues[i], keyValues[i + 1]);
        }
        return map;
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception ex) {
            return "[]";
        }
    }
}
