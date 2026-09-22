package com.ttloc.htkhcn.baocao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.ttloc.htkhcn.BaseIntegrationTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Test 3 bao cao chuyen de (SPEC muc 4.8). */
class BaoCaoControllerTest extends BaseIntegrationTest {

    @Test
    @DisplayName("Bao cao MoU trong nam (JSON) dem dung so MoU ky trong nam duoc chon")
    void mouTrongNamJson() throws Exception {
        String doiTacId = taoDoiTac("Doi tac Bao Cao 1");
        taoMou(doiTacId, "2026-03-01", "2030-01-01");
        taoMou(doiTacId, "2020-01-01", "2030-01-01"); // nam khac, khong duoc tinh

        mockMvc.perform(auth(get("/api/bao-cao/mou-trong-nam").param("nam", "2026"), tokenAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nam").value(2026))
                .andExpect(jsonPath("$.tomTat.tongSoMouMoiKy").value(1))
                .andExpect(jsonPath("$.chiTiet.length()").value(1));
    }

    @Test
    @DisplayName("Bao cao MoU trong nam xuat Excel -> tra ve file .xlsx hop le, ghi lich su xuat")
    void mouTrongNamExcel() throws Exception {
        mockMvc.perform(auth(get("/api/bao-cao/mou-trong-nam")
                        .param("nam", "2026").param("dinhDang", "EXCEL"), tokenAdmin()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));

        mockMvc.perform(auth(get("/api/bao-cao/lich-su-xuat"), tokenAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].loaiBaoCao").value("MOU_TRONG_NAM"))
                .andExpect(jsonPath("$.content[0].dinhDang").value("EXCEL"));
    }

    @Test
    @DisplayName("Bao cao MoU trong nam xuat PDF -> tra ve file PDF hop le (bat dau bang %PDF)")
    void mouTrongNamPdf() throws Exception {
        byte[] noiDung = mockMvc.perform(auth(get("/api/bao-cao/mou-trong-nam")
                        .param("nam", "2026").param("dinhDang", "PDF"), tokenAdmin()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andReturn().getResponse().getContentAsByteArray();

        String daumo = new String(noiDung, 0, 4);
        org.junit.jupiter.api.Assertions.assertEquals("%PDF", daumo);
    }

    @Test
    @DisplayName("Bao cao doan ra/vao (JSON) tong hop dung so lieu")
    void doanRaVaoJson() throws Exception {
        String doiTacId = taoDoiTac("Doi tac Bao Cao 2");
        mockMvc.perform(auth(post("/api/doan-vao"), tokenAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"tenDoan":"Doan BC","thoiGianDen":"2026-06-01","thoiGianDi":"2026-06-03",
                                 "soLuongNguoiNuocNgoai":4,"quocTich":["Nhat Ban"]}
                                """))
                .andExpect(status().isOk());
        mockMvc.perform(auth(post("/api/doan-ra"), tokenAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"doiTacId\":\"" + doiTacId + "\",\"thoiGianDi\":\"2026-06-01\","
                                + "\"thoiGianVe\":\"2026-06-05\",\"soLuongDoan\":2,\"quocGiaLamViec\":\"Nhat Ban\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(auth(get("/api/bao-cao/doan-ra-vao")
                        .param("tu", "2026-06-01").param("den", "2026-06-30"), tokenAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tomTat.tongSoDoanVao").value(1))
                .andExpect(jsonPath("$.tomTat.tongKhachNuocNgoaiDaDen").value(4))
                .andExpect(jsonPath("$.tomTat.tongSoDoanRa").value(1));
    }

    @Test
    @DisplayName("Bao cao thoi han MoU: loc theo thangToi chi tra ve MoU het han trong khoang do")
    void thoiHanMouLocThangToi() throws Exception {
        String doiTacId = taoDoiTac("Doi tac Bao Cao 3");
        taoMou(doiTacId, "2024-01-01", java.time.LocalDate.now().plusMonths(1).toString());
        taoMou(doiTacId, "2024-01-01", java.time.LocalDate.now().plusYears(3).toString());

        mockMvc.perform(auth(get("/api/bao-cao/thoi-han-mou").param("thangToi", "2"), tokenAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("Viewer duoc xem bao cao (JSON) nhung khong xem duoc lich su xuat (chi Admin)")
    void viewerQuyenBaoCao() throws Exception {
        String ten = "viewer_bc_" + System.nanoTime();
        String matKhau = taoNguoiDungVaiTro(ten, "VIEWER", null);
        String token = dangNhap(ten, matKhau);

        mockMvc.perform(auth(get("/api/bao-cao/thoi-han-mou"), token)).andExpect(status().isOk());
        mockMvc.perform(auth(get("/api/bao-cao/lich-su-xuat"), token)).andExpect(status().isForbidden());
    }

    private String taoDoiTac(String ten) throws Exception {
        String resp = mockMvc.perform(auth(post("/api/doi-tac"), tokenAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tenDoiTac\":\"" + ten + "\",\"loaiDoiTac\":\"TRONG_NUOC\"}"))
                .andReturn().getResponse().getContentAsString();
        return json(resp).get("id").asText();
    }

    private void taoMou(String doiTacId, String ngayBanHanh, String ngayHetHan) throws Exception {
        mockMvc.perform(auth(post("/api/mou"), tokenAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"doiTacId\":\"" + doiTacId + "\",\"ngayBanHanh\":\"" + ngayBanHanh
                                + "\",\"ngayHetHan\":\"" + ngayHetHan + "\"}"))
                .andExpect(status().isOk());
    }
}
