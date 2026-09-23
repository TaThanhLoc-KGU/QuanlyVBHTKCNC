package com.ttloc.htkhcn.excelimport;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Locale;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

/** Cac ham tien ich doc file Excel (.xlsx) bang Apache POI (SPEC muc 4.3). */
public final class ExcelUtils {

    private static final DateTimeFormatter[] DATE_FORMATS = {
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("d/M/yyyy"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
    };

    private ExcelUtils() {
    }

    /** Doc dong dau tien lam header, tra ve map ten-cot-da-chuan-hoa -> chi so cot. */
    public static Map<String, Integer> docHeader(Sheet sheet) {
        Row header = sheet.getRow(0);
        Map<String, Integer> ketQua = new HashMap<>();
        if (header == null) {
            return ketQua;
        }
        for (Cell cell : header) {
            String ten = chuanHoa(getCellString(cell));
            if (!ten.isBlank()) {
                ketQua.put(ten, cell.getColumnIndex());
            }
        }
        return ketQua;
    }

    /**
     * Bo dau, ha thuong, trim - dung de doi chieu ten cot header/gia tri linh
     * hoat. "d" (U+0111) va "D" (U+0110) khong bi NFD tach ra thanh chu cai
     * goc + dau (khac voi a/e/i/o/u co dau), nen phai thay the rieng - neu
     * khong "Nghi dinh"/"Quyet dinh" se khong khop voi "Nghi dinh"/"Quyet dinh".
     */
    public static String chuanHoa(String s) {
        if (s == null) {
            return "";
        }
        String bin = java.text.Normalizer.normalize(s.trim().toLowerCase(Locale.ROOT), java.text.Normalizer.Form.NFD);
        String khongDau = bin.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "")
                .replace('đ', 'd')
                .replace('Đ', 'D');
        return khongDau.replaceAll("\\s+", " ");
    }

    public static Integer timCot(Map<String, Integer> header, String... tenCotUngVien) {
        for (String ten : tenCotUngVien) {
            Integer idx = header.get(chuanHoa(ten));
            if (idx != null) {
                return idx;
            }
        }
        return null;
    }

    public static String getCellString(Cell cell) {
        if (cell == null) {
            return null;
        }
        if (cell.getCellType() == CellType.FORMULA) {
            // Doc GIA TRI DA TINH (cache trong file), khong phai chuoi cong
            // thuc - file mau thuc te dung cong thuc STT tu dong keo xuong ca
            // tram dong template (vd IF(B3<>"", ..., "")), neu tra ve chuoi
            // cong thuc thi laDongTrong() se coi moi dong template la "co du
            // lieu" (vi chuoi cong thuc khong bao gio rong) du cot do TINH RA
            // rong, khien hang loat dong trong bi doc nham thanh dong loi.
            return switch (cell.getCachedFormulaResultType()) {
                case STRING -> cell.getStringCellValue().trim();
                case NUMERIC -> DateUtil.isCellDateFormatted(cell)
                        ? cell.getLocalDateTimeCellValue().toLocalDate().toString()
                        : stripTrailingZero(cell.getNumericCellValue());
                case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
                default -> null;
            };
        }
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> DateUtil.isCellDateFormatted(cell)
                    ? cell.getLocalDateTimeCellValue().toLocalDate().toString()
                    : stripTrailingZero(cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> null;
        };
    }

    public static String getCellString(Row row, Integer col) {
        if (row == null || col == null) {
            return null;
        }
        return getCellString(row.getCell(col));
    }

    public static LocalDate getCellDate(Row row, Integer col) {
        if (row == null || col == null) {
            return null;
        }
        Cell cell = row.getCell(col);
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            return null;
        }
        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return cell.getLocalDateTimeCellValue().toLocalDate();
        }
        String text = getCellString(cell);
        if (text == null || text.isBlank()) {
            return null;
        }
        for (DateTimeFormatter fmt : DATE_FORMATS) {
            try {
                return LocalDate.parse(text, fmt);
            } catch (Exception ignored) {
                // thu format tiep theo
            }
        }
        return null;
    }

    public static Integer getCellInt(Row row, Integer col) {
        String s = getCellString(row, col);
        if (s == null || s.isBlank()) {
            return null;
        }
        try {
            return (int) Double.parseDouble(s.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private static String stripTrailingZero(double d) {
        if (d == Math.floor(d) && !Double.isInfinite(d)) {
            return String.valueOf((long) d);
        }
        return String.valueOf(d);
    }
}
