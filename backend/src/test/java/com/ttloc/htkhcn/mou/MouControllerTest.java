package com.ttloc.htkhcn.mou;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.ttloc.htkhcn.BaseIntegrationTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Test module MoU - trong tam la trang thai tinh tu dong tu VIEW v_mou_trang_thai (SPEC muc 3.4). */
class MouControllerTest extends BaseIntegrationTest {

    @Test
    @DisplayName("MoU con hieu luc lau (het han sau 2 nam) -> trangThai CON_HIEU_LUC")
    void trangThaiConHieuLuc() throws Exception {
        String doiTacId = taoDoiTac("Doi tac MoU 1");
        String id = taoMou(doiTacId, LocalDate.now().minusYears(1), LocalDate.now().plusYears(2));

        mockMvc.perform(auth(get("/api/mou/" + id), tokenAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trangThai").value("CON_HIEU_LUC"));
    }

    @Test
    @DisplayName("MoU het han trong 30 ngay -> trangThai SAP_HET_HAN va soNgayConLai dung")
    void trangThaiSapHetHan() throws Exception {
        String doiTacId = taoDoiTac("Doi tac MoU 2");
        String id = taoMou(doiTacId, LocalDate.now().minusYears(1), LocalDate.now().plusDays(30));

        mockMvc.perform(auth(get("/api/mou/" + id), tokenAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trangThai").value("SAP_HET_HAN"))
                .andExpect(jsonPath("$.soNgayConLai").value(30));
    }

    @Test
    @DisplayName("MoU da het han (qua khu) -> trangThai DA_HET_HAN")
    void trangThaiDaHetHan() throws Exception {
        String doiTacId = taoDoiTac("Doi tac MoU 3");
        String id = taoMou(doiTacId, LocalDate.now().minusYears(5), LocalDate.now().minusDays(10));

        mockMvc.perform(auth(get("/api/mou/" + id), tokenAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trangThai").value("DA_HET_HAN"));
    }

    @Test
    @DisplayName("MoU khong co Ngay het han -> luon CON_HIEU_LUC")
    void moUKhongCoNgayHetHan() throws Exception {
        String doiTacId = taoDoiTac("Doi tac MoU 4");
        String resp = mockMvc.perform(auth(post("/api/mou"), tokenAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"doiTacId\":\"" + doiTacId + "\",\"ngayBanHanh\":\"2024-01-01\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trangThai").value("CON_HIEU_LUC"))
                .andReturn().getResponse().getContentAsString();
        json(resp);
    }

    @Test
    @DisplayName("Tao MoU voi doiTacId khong ton tai -> 404")
    void taoMouDoiTacKhongTonTai() throws Exception {
        mockMvc.perform(auth(post("/api/mou"), tokenAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"doiTacId\":\"00000000-0000-0000-0000-000000000000\",\"ngayBanHanh\":\"2024-01-01\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Xoa mem MoU -> khong con trong danh sach")
    void xoaMemMou() throws Exception {
        String doiTacId = taoDoiTac("Doi tac MoU 5");
        String id = taoMou(doiTacId, LocalDate.now(), LocalDate.now().plusYears(1));

        mockMvc.perform(auth(delete("/api/mou/" + id), tokenAdmin())).andExpect(status().isNoContent());
        mockMvc.perform(auth(get("/api/mou/" + id), tokenAdmin())).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Loc MoU theo trangThai=SAP_HET_HAN chi tra ve dung nhung MoU sap het han")
    void locTheoTrangThai() throws Exception {
        String doiTacId = taoDoiTac("Doi tac MoU Loc");
        taoMou(doiTacId, LocalDate.now().minusYears(1), LocalDate.now().plusDays(10));
        taoMou(doiTacId, LocalDate.now().minusYears(1), LocalDate.now().plusYears(3));

        mockMvc.perform(auth(get("/api/mou")
                        .param("doiTacId", doiTacId)
                        .param("trangThai", "SAP_HET_HAN"), tokenAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("Editor duoc gan module MOU tao MoU thanh cong; Editor gan module khac -> 403")
    void editorTheoModule() throws Exception {
        String doiTacId = taoDoiTac("Doi tac MoU Editor");

        String tenKhongDuoc = "editor_mou_khac_" + System.nanoTime();
        String mkKhong = taoNguoiDungVaiTro(tenKhongDuoc, "EDITOR", "DOI_TAC");
        String tokenKhong = dangNhap(tenKhongDuoc, mkKhong);
        mockMvc.perform(auth(post("/api/mou"), tokenKhong)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"doiTacId\":\"" + doiTacId + "\",\"ngayBanHanh\":\"2024-01-01\"}"))
                .andExpect(status().isForbidden());

        String tenDuoc = "editor_mou_" + System.nanoTime();
        String mkDuoc = taoNguoiDungVaiTro(tenDuoc, "EDITOR", "MOU");
        String tokenDuoc = dangNhap(tenDuoc, mkDuoc);
        mockMvc.perform(auth(post("/api/mou"), tokenDuoc)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"doiTacId\":\"" + doiTacId + "\",\"ngayBanHanh\":\"2024-01-01\"}"))
                .andExpect(status().isOk());
    }

    private String taoDoiTac(String ten) throws Exception {
        String resp = mockMvc.perform(auth(post("/api/doi-tac"), tokenAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tenDoiTac\":\"" + ten + "\",\"loaiDoiTac\":\"TRONG_NUOC\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return json(resp).get("id").asText();
    }

    private String taoMou(String doiTacId, LocalDate ngayBanHanh, LocalDate ngayHetHan) throws Exception {
        String resp = mockMvc.perform(auth(post("/api/mou"), tokenAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"doiTacId\":\"" + doiTacId + "\",\"ngayBanHanh\":\"" + ngayBanHanh
                                + "\",\"ngayHetHan\":\"" + ngayHetHan + "\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return json(resp).get("id").asText();
    }
}
