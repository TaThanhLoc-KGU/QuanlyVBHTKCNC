package com.ttloc.htkhcn.baocao;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

/**
 * Xuat Excel (Apache POI) va PDF (OpenPDF) cho 3 bao cao chuyen de (SPEC muc
 * 4.8) - keo tieu de, ngay xuat, nguoi xuat vao dau file nhu SPEC yeu cau.
 */
@Service
public class BaoCaoExportService {

    private static final DateTimeFormatter NGAY = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ---------- Excel ----------

    public byte[] xuatExcelMouTrongNam(BaoCaoMouTrongNam.Response bc, String nguoiXuat) {
        try (XSSFWorkbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = wb.createSheet("MoU nam " + bc.nam());
            CellStyle tieuDe = kieuTieuDe(wb);
            CellStyle header = kieuHeader(wb);
            int r = 0;
            r = ghiTieuDeBaoCao(sheet, r, tieuDe, "BAO CAO MOU HOP TAC NAM " + bc.nam(), nguoiXuat);
            ghiDong(sheet, r++, "Tong so MoU moi ky:", String.valueOf(bc.tomTat().tongSoMouMoiKy()));
            ghiDong(sheet, r++, "Cung ky nam truoc:", String.valueOf(bc.tomTat().tongSoMouCungKyNamTruoc()));
            r++;
            String[] cols = {"Doi tac", "Loai doi tac", "Linh vuc hop tac", "Ngay ky", "Ngay het han", "Dau moi", "Trang thai"};
            ghiHeader(sheet, r++, header, cols);
            for (BaoCaoMouTrongNam.Dong d : bc.chiTiet()) {
                Row row = sheet.createRow(r++);
                dat(row, 0, d.tenDoiTac());
                dat(row, 1, String.valueOf(d.loaiDoiTac()));
                dat(row, 2, d.linhVucHopTac());
                dat(row, 3, dinhDangNgay(d.ngayKy()));
                dat(row, 4, dinhDangNgay(d.ngayHetHan()));
                dat(row, 5, d.donViDauMoi());
                dat(row, 6, String.valueOf(d.trangThai()));
            }
            tuDongDoRong(sheet, cols.length);
            wb.write(out);
            return out.toByteArray();
        } catch (IOException ex) {
            throw new IllegalStateException("Khong xuat duoc Excel", ex);
        }
    }

    public byte[] xuatExcelDoanRaVao(BaoCaoDoanRaVao.Response bc, String nguoiXuat) {
        try (XSSFWorkbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            CellStyle tieuDe = kieuTieuDe(wb);
            CellStyle header = kieuHeader(wb);

            Sheet sheetTongHop = wb.createSheet("Tong hop");
            int r = ghiTieuDeBaoCao(sheetTongHop, 0, tieuDe, "BAO CAO DOAN RA VA DOAN KHACH VAO TRUONG", nguoiXuat);
            ghiDong(sheetTongHop, r++, "Tong so doan vao:", String.valueOf(bc.tomTat().tongSoDoanVao()));
            ghiDong(sheetTongHop, r++, "Tong khach nuoc ngoai da den:", String.valueOf(bc.tomTat().tongKhachNuocNgoaiDaDen()));
            ghiDong(sheetTongHop, r++, "Tong so doan ra:", String.valueOf(bc.tomTat().tongSoDoanRa()));
            ghiDong(sheetTongHop, r++, "Tong luot can bo di cong tac:", String.valueOf(bc.tomTat().tongLuotCanBoDiCongTac()));
            ghiDong(sheetTongHop, r++, "Tong so ngay cong tac nuoc ngoai:", String.valueOf(bc.tomTat().tongSoNgayCongTacNuocNgoai()));

            Sheet sheetVao = wb.createSheet("Doan vao");
            int rv = ghiHeader(sheetVao, 0, header, new String[]{"Ten doan", "Doi tac", "Thoi gian den", "Thoi gian di",
                    "So nguoi nuoc ngoai", "So nguoi VN", "Quoc tich", "So ngay"});
            for (var d : bc.doanVao()) {
                Row row = sheetVao.createRow(rv++);
                dat(row, 0, d.tenDoan());
                dat(row, 1, d.tenDoiTac());
                dat(row, 2, dinhDangNgay(d.thoiGianDen()));
                dat(row, 3, dinhDangNgay(d.thoiGianDi()));
                dat(row, 4, String.valueOf(d.soLuongNguoiNuocNgoai()));
                dat(row, 5, d.soLuongNguoiVietNam() == null ? "" : String.valueOf(d.soLuongNguoiVietNam()));
                dat(row, 6, d.quocTich() == null ? "" : String.join(", ", d.quocTich()));
                dat(row, 7, String.valueOf(d.soNgay()));
            }
            tuDongDoRong(sheetVao, 8);

            Sheet sheetRa = wb.createSheet("Doan ra");
            int rr = ghiHeader(sheetRa, 0, header, new String[]{"Don vi lam viec", "Thoi gian di", "Thoi gian ve",
                    "Quoc gia lam viec", "So luong doan", "So ngay"});
            for (var d : bc.doanRa()) {
                Row row = sheetRa.createRow(rr++);
                dat(row, 0, d.tenDoiTac());
                dat(row, 1, dinhDangNgay(d.thoiGianDi()));
                dat(row, 2, dinhDangNgay(d.thoiGianVe()));
                dat(row, 3, d.quocGiaLamViec());
                dat(row, 4, String.valueOf(d.soLuongDoan()));
                dat(row, 5, String.valueOf(d.soNgay()));
            }
            tuDongDoRong(sheetRa, 6);

            wb.write(out);
            return out.toByteArray();
        } catch (IOException ex) {
            throw new IllegalStateException("Khong xuat duoc Excel", ex);
        }
    }

    public byte[] xuatExcelThoiHanMou(java.util.List<com.ttloc.htkhcn.mou.MouResponse> ds, String nguoiXuat) {
        try (XSSFWorkbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = wb.createSheet("Thoi han MoU");
            CellStyle tieuDe = kieuTieuDe(wb);
            CellStyle header = kieuHeader(wb);
            int r = ghiTieuDeBaoCao(sheet, 0, tieuDe, "BAO CAO THOI HAN MOU VOI CAC DON VI DOI TAC", nguoiXuat);
            r++;
            String[] cols = {"Doi tac", "Ngay ky", "Ngay het han", "So ngay con lai", "Trang thai"};
            r = ghiHeader(sheet, r, header, cols);
            for (var m : ds) {
                Row row = sheet.createRow(r++);
                dat(row, 0, m.tenDoiTac());
                dat(row, 1, dinhDangNgay(m.ngayBanHanh()));
                dat(row, 2, dinhDangNgay(m.ngayHetHan()));
                dat(row, 3, m.soNgayConLai() == null ? "" : String.valueOf(m.soNgayConLai()));
                dat(row, 4, String.valueOf(m.trangThai()));
            }
            tuDongDoRong(sheet, cols.length);
            wb.write(out);
            return out.toByteArray();
        } catch (IOException ex) {
            throw new IllegalStateException("Khong xuat duoc Excel", ex);
        }
    }

    // ---------- PDF ----------

    public byte[] xuatPdfMouTrongNam(BaoCaoMouTrongNam.Response bc, String nguoiXuat) {
        return taoPdf("BAO CAO MOU HOP TAC NAM " + bc.nam(), nguoiXuat, doc -> {
            themDong(doc, "Tong so MoU moi ky: " + bc.tomTat().tongSoMouMoiKy());
            themDong(doc, "Cung ky nam truoc: " + bc.tomTat().tongSoMouCungKyNamTruoc());
            PdfPTable table = taoBang(new String[]{"Doi tac", "Loai", "Ngay ky", "Ngay het han", "Trang thai"});
            for (BaoCaoMouTrongNam.Dong d : bc.chiTiet()) {
                themDongBang(table, d.tenDoiTac(), String.valueOf(d.loaiDoiTac()),
                        dinhDangNgay(d.ngayKy()), dinhDangNgay(d.ngayHetHan()), String.valueOf(d.trangThai()));
            }
            them(doc, table);
        });
    }

    public byte[] xuatPdfThoiHanMou(java.util.List<com.ttloc.htkhcn.mou.MouResponse> ds, String nguoiXuat) {
        return taoPdf("BAO CAO THOI HAN MOU VOI CAC DON VI DOI TAC", nguoiXuat, doc -> {
            PdfPTable table = taoBang(new String[]{"Doi tac", "Ngay ky", "Ngay het han", "So ngay con lai", "Trang thai"});
            for (var m : ds) {
                themDongBang(table, m.tenDoiTac(), dinhDangNgay(m.ngayBanHanh()), dinhDangNgay(m.ngayHetHan()),
                        m.soNgayConLai() == null ? "" : String.valueOf(m.soNgayConLai()), String.valueOf(m.trangThai()));
            }
            them(doc, table);
        });
    }

    public byte[] xuatPdfDoanRaVao(BaoCaoDoanRaVao.Response bc, String nguoiXuat) {
        return taoPdf("BAO CAO DOAN RA VA DOAN KHACH VAO TRUONG", nguoiXuat, doc -> {
            themDong(doc, "Tong so doan vao: " + bc.tomTat().tongSoDoanVao()
                    + " | Tong khach nuoc ngoai: " + bc.tomTat().tongKhachNuocNgoaiDaDen());
            themDong(doc, "Tong so doan ra: " + bc.tomTat().tongSoDoanRa()
                    + " | Tong luot can bo: " + bc.tomTat().tongLuotCanBoDiCongTac());

            themDong(doc, "");
            themDong(doc, "Doan vao:");
            PdfPTable bangVao = taoBang(new String[]{"Ten doan", "Doi tac", "Thoi gian den", "Thoi gian di"});
            for (var d : bc.doanVao()) {
                themDongBang(bangVao, d.tenDoan(), d.tenDoiTac(), dinhDangNgay(d.thoiGianDen()), dinhDangNgay(d.thoiGianDi()));
            }
            them(doc, bangVao);

            themDong(doc, "");
            themDong(doc, "Doan ra:");
            PdfPTable bangRa = taoBang(new String[]{"Don vi lam viec", "Thoi gian di", "Thoi gian ve", "Quoc gia"});
            for (var d : bc.doanRa()) {
                themDongBang(bangRa, d.tenDoiTac(), dinhDangNgay(d.thoiGianDi()), dinhDangNgay(d.thoiGianVe()), d.quocGiaLamViec());
            }
            them(doc, bangRa);
        });
    }

    // ---------- Tien ich Excel ----------

    private CellStyle kieuTieuDe(XSSFWorkbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
        style.setFont(font);
        return style;
    }

    private CellStyle kieuHeader(XSSFWorkbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    private int ghiTieuDeBaoCao(Sheet sheet, int r, CellStyle tieuDe, String ten, String nguoiXuat) {
        Row r0 = sheet.createRow(r++);
        Cell c0 = r0.createCell(0);
        c0.setCellValue(ten);
        c0.setCellStyle(tieuDe);
        ghiDong(sheet, r++, "Ngay xuat:", dinhDangNgay(LocalDate.now()));
        ghiDong(sheet, r++, "Nguoi xuat:", nguoiXuat);
        r++;
        return r;
    }

    private void ghiDong(Sheet sheet, int r, String nhan, String giaTri) {
        Row row = sheet.createRow(r);
        dat(row, 0, nhan);
        dat(row, 1, giaTri);
    }

    private int ghiHeader(Sheet sheet, int r, CellStyle style, String[] cols) {
        Row row = sheet.createRow(r);
        for (int i = 0; i < cols.length; i++) {
            Cell c = row.createCell(i);
            c.setCellValue(cols[i]);
            c.setCellStyle(style);
        }
        return r + 1;
    }

    private void dat(Row row, int col, String giaTri) {
        row.createCell(col).setCellValue(giaTri == null ? "" : giaTri);
    }

    private void tuDongDoRong(Sheet sheet, int soCot) {
        for (int i = 0; i < soCot; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    // ---------- Tien ich PDF ----------

    private byte[] taoPdf(String tieuDe, String nguoiXuat, java.util.function.Consumer<Document> noiDung) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4);
            PdfWriter.getInstance(doc, out);
            doc.open();
            com.lowagie.text.Font fontTieuDe = new com.lowagie.text.Font(
                    com.lowagie.text.Font.HELVETICA, 16, com.lowagie.text.Font.BOLD);
            Paragraph p = new Paragraph(tieuDe, fontTieuDe);
            p.setAlignment(Element.ALIGN_CENTER);
            doc.add(p);
            doc.add(new Paragraph("Ngay xuat: " + dinhDangNgay(LocalDate.now()) + " | Nguoi xuat: " + nguoiXuat));
            doc.add(new Paragraph(" "));
            noiDung.accept(doc);
            doc.close();
            return out.toByteArray();
        } catch (IOException | DocumentException ex) {
            throw new IllegalStateException("Khong xuat duoc PDF", ex);
        }
    }

    private void themDong(Document doc, String text) {
        try {
            doc.add(new Paragraph(text));
        } catch (DocumentException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private void them(Document doc, Element el) {
        try {
            doc.add(el);
        } catch (DocumentException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private PdfPTable taoBang(String[] cols) {
        PdfPTable table = new PdfPTable(cols.length);
        table.setWidthPercentage(100);
        for (String c : cols) {
            PdfPCell cell = new PdfPCell(new Paragraph(c));
            table.addCell(cell);
        }
        return table;
    }

    private void themDongBang(PdfPTable table, String... giaTri) {
        for (String v : giaTri) {
            table.addCell(v == null ? "" : v);
        }
    }

    private String dinhDangNgay(LocalDate ngay) {
        return ngay == null ? "" : ngay.format(NGAY);
    }
}
