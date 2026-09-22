package com.ttloc.htkhcn.excelimport;

import java.io.ByteArrayOutputStream;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import com.ttloc.htkhcn.BaseIntegrationTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Test import Excel (SPEC muc 4.3) - dung Apache POI de tu tao file .xlsx ngay trong test. */
class ExcelImportControllerTest extends BaseIntegrationTest {

    @Test
    @DisplayName("Xem truoc import Doi tac: 1 dong hop le + 1 dong loi (thieu ten)")
    void xemTruocDoiTac() throws Exception {
        MockMultipartFile file = taoFileExcel("DoiTac",
                new String[]{"Ten doi tac", "Loai doi tac", "Quoc gia"},
                new String[][]{
                        {"Dai hoc Import Test", "Trong nuoc", ""},
                        {"", "Trong nuoc", ""}
                });

        mockMvc.perform(auth(multipart("/api/import/DOI_TAC/xem-truoc"), tokenAdmin()).file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tongSoDong").value(2))
                .andExpect(jsonPath("$.soDongHopLe").value(1))
                .andExpect(jsonPath("$.soDongLoi").value(1));
    }

    @Test
    @DisplayName("Xac nhan import Doi tac -> tao ban ghi thanh cong, tim thay qua API danh sach")
    void xacNhanDoiTac() throws Exception {
        String tenDocDao = "Dai hoc Import Xac Nhan " + System.nanoTime();
        MockMultipartFile file = taoFileExcel("DoiTac",
                new String[]{"Ten doi tac", "Loai doi tac"},
                new String[][]{{tenDocDao, "Trong nuoc"}});

        String resp = mockMvc.perform(auth(multipart("/api/import/DOI_TAC/xac-nhan"), tokenAdmin()).file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.soDongThanhCong").value(1))
                .andExpect(jsonPath("$.soDongLoi").value(0))
                .andReturn().getResponse().getContentAsString();
        String phienId = json(resp).get("phienImportId").asText();

        mockMvc.perform(auth(get("/api/doi-tac").param("tuKhoa", tenDocDao), tokenAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1));

        rollbackXoaThanhCong(phienId);
    }

    @Test
    @DisplayName("Import MoU voi ten doi tac chua co san -> tu dong tao moi doi tac")
    void importMouTuTaoDoiTacMoi() throws Exception {
        String tenDoiTacMoi = "Dai hoc Import MoU Moi " + System.nanoTime();
        MockMultipartFile file = taoFileExcel("MoU",
                new String[]{"Ten doi tac", "Loai doi tac", "Ten tai lieu", "Ngay ban hanh", "Ngay het han"},
                new String[][]{{tenDoiTacMoi, "Trong nuoc", "MoU import test", "01/01/2024", "01/01/2030"}});

        String resp = mockMvc.perform(auth(multipart("/api/import/MOU/xac-nhan"), tokenAdmin()).file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.soDongThanhCong").value(1))
                .andReturn().getResponse().getContentAsString();
        String phienId = json(resp).get("phienImportId").asText();

        mockMvc.perform(auth(get("/api/doi-tac").param("tuKhoa", tenDoiTacMoi), tokenAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1));

        rollbackXoaThanhCong(phienId);
    }

    @Test
    @DisplayName("Rollback 1 phien import -> ban ghi bi xoa mem, khong con trong danh sach")
    void rollbackPhienImport() throws Exception {
        String ten = "Dai hoc Rollback Test " + System.nanoTime();
        MockMultipartFile file = taoFileExcel("DoiTac",
                new String[]{"Ten doi tac", "Loai doi tac"},
                new String[][]{{ten, "Trong nuoc"}});

        String resp = mockMvc.perform(auth(multipart("/api/import/DOI_TAC/xac-nhan"), tokenAdmin()).file(file))
                .andReturn().getResponse().getContentAsString();
        String phienId = json(resp).get("phienImportId").asText();

        mockMvc.perform(auth(post("/api/phien-import/" + phienId + "/rollback"), tokenAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.soBanGhiDaXoa").value(1));

        mockMvc.perform(auth(get("/api/doi-tac").param("tuKhoa", ten), tokenAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    @DisplayName("Editor khong duoc gan module -> 403 khi import")
    void editorKhongDuocGanModule() throws Exception {
        String tenNguoiDung = "editor_import_" + System.nanoTime();
        String matKhau = taoNguoiDungVaiTro(tenNguoiDung, "EDITOR", "DOAN_VAO");
        String token = dangNhap(tenNguoiDung, matKhau);

        MockMultipartFile file = taoFileExcel("DoiTac",
                new String[]{"Ten doi tac", "Loai doi tac"},
                new String[][]{{"X", "Trong nuoc"}});

        mockMvc.perform(auth(multipart("/api/import/DOI_TAC/xem-truoc"), token).file(file))
                .andExpect(status().isForbidden());
    }

    private void rollbackXoaThanhCong(String phienId) throws Exception {
        mockMvc.perform(auth(post("/api/phien-import/" + phienId + "/rollback"), tokenAdmin()))
                .andExpect(status().isOk());
    }

    private MockMultipartFile taoFileExcel(String tenSheet, String[] header, String[][] dong) throws Exception {
        try (XSSFWorkbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = wb.createSheet(tenSheet);
            Row hRow = sheet.createRow(0);
            for (int i = 0; i < header.length; i++) {
                hRow.createCell(i).setCellValue(header[i]);
            }
            for (int r = 0; r < dong.length; r++) {
                Row row = sheet.createRow(r + 1);
                for (int c = 0; c < dong[r].length; c++) {
                    row.createCell(c).setCellValue(dong[r][c]);
                }
            }
            wb.write(out);
            return new MockMultipartFile("file", tenSheet + ".xlsx",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", out.toByteArray());
        }
    }
}
