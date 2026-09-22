package com.ttloc.htkhcn.tailieu;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import com.ttloc.htkhcn.BaseIntegrationTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Test dinh kem tai lieu (SPEC muc 4.5). */
class TaiLieuDinhKemControllerTest extends BaseIntegrationTest {

    @Test
    @DisplayName("Upload -> liet ke -> tai xuong dung noi dung -> xoa -> khong con trong danh sach")
    void uploadListDownloadDelete() throws Exception {
        String mouId = taoMou();
        byte[] noiDungGoc = "noi dung file test".getBytes();
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", noiDungGoc);

        String resp = mockMvc.perform(auth(multipart("/api/tai-lieu-dinh-kem")
                                .param("bang", "mou").param("banGhiId", mouId), tokenAdmin())
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tenFile").value("test.txt"))
                .andReturn().getResponse().getContentAsString();
        String id = json(resp).get("id").asText();

        mockMvc.perform(auth(get("/api/tai-lieu-dinh-kem")
                        .param("bang", "mou").param("banGhiId", mouId), tokenAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        byte[] taiVe = mockMvc.perform(auth(get("/api/tai-lieu-dinh-kem/" + id + "/tai-xuong"), tokenAdmin()))
                .andExpect(status().isOk())
                .andExpect(header().exists("Content-Disposition"))
                .andReturn().getResponse().getContentAsByteArray();
        org.junit.jupiter.api.Assertions.assertArrayEquals(noiDungGoc, taiVe);

        mockMvc.perform(auth(delete("/api/tai-lieu-dinh-kem/" + id), tokenAdmin())).andExpect(status().isNoContent());
        mockMvc.perform(auth(get("/api/tai-lieu-dinh-kem")
                        .param("bang", "mou").param("banGhiId", mouId), tokenAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("Bang dinh kem khong hop le (khong thuoc van_ban_dhkg/vbpl_vn/mou) -> 400")
    void bangKhongHopLe() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "a.txt", "text/plain", "x".getBytes());
        mockMvc.perform(auth(multipart("/api/tai-lieu-dinh-kem")
                                .param("bang", "doi_tac").param("banGhiId", java.util.UUID.randomUUID().toString()),
                        tokenAdmin())
                        .file(file))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Editor khong duoc gan module MOU -> 403 khi dinh kem vao MoU")
    void editorKhongDuocGanModule() throws Exception {
        String mouId = taoMou();
        String ten = "editor_tl_" + System.nanoTime();
        String matKhau = taoNguoiDungVaiTro(ten, "EDITOR", "DOAN_VAO");
        String token = dangNhap(ten, matKhau);

        MockMultipartFile file = new MockMultipartFile("file", "a.txt", "text/plain", "x".getBytes());
        mockMvc.perform(auth(multipart("/api/tai-lieu-dinh-kem")
                                .param("bang", "mou").param("banGhiId", mouId), token)
                        .file(file))
                .andExpect(status().isForbidden());
    }

    private String taoMou() throws Exception {
        String respDoiTac = mockMvc.perform(auth(post("/api/doi-tac"), tokenAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tenDoiTac\":\"Doi tac Dinh Kem " + System.nanoTime() + "\",\"loaiDoiTac\":\"TRONG_NUOC\"}"))
                .andReturn().getResponse().getContentAsString();
        String doiTacId = json(respDoiTac).get("id").asText();

        String respMou = mockMvc.perform(auth(post("/api/mou"), tokenAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"doiTacId\":\"" + doiTacId + "\",\"ngayBanHanh\":\"2024-01-01\"}"))
                .andReturn().getResponse().getContentAsString();
        return json(respMou).get("id").asText();
    }
}
