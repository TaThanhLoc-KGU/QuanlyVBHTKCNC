package com.ttloc.htkhcn.doanra;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.ttloc.htkhcn.BaseIntegrationTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Test module Doan ra (SPEC muc 3.6). */
class DoanRaControllerTest extends BaseIntegrationTest {

    @Test
    @DisplayName("Tao doan ra 10 ngay -> nam/soNgay generated column dung, doi tac bat buoc")
    void generatedColumnDungDan() throws Exception {
        String doiTacId = taoDoiTac("Doi tac Doan Ra");
        mockMvc.perform(auth(post("/api/doan-ra"), tokenAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"doiTacId\":\"" + doiTacId + "\",\"thoiGianDi\":\"2026-04-01\","
                                + "\"thoiGianVe\":\"2026-04-10\",\"soLuongDoan\":3,\"quocGiaLamViec\":\"Han Quoc\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nam").value(2026))
                .andExpect(jsonPath("$.soNgay").value(10));
    }

    @Test
    @DisplayName("Thieu doiTacId (bat buoc, khac Doan vao) -> 400")
    void thieuDoiTac() throws Exception {
        mockMvc.perform(auth(post("/api/doan-ra"), tokenAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"thoiGianDi\":\"2026-04-01\",\"thoiGianVe\":\"2026-04-10\","
                                + "\"soLuongDoan\":3,\"quocGiaLamViec\":\"Han Quoc\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Thoi gian ve truoc Thoi gian di -> 400")
    void thoiGianVeTruocThoiGianDi() throws Exception {
        String doiTacId = taoDoiTac("Doi tac Doan Ra 2");
        mockMvc.perform(auth(post("/api/doan-ra"), tokenAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"doiTacId\":\"" + doiTacId + "\",\"thoiGianDi\":\"2026-04-10\","
                                + "\"thoiGianVe\":\"2026-04-01\",\"soLuongDoan\":3,\"quocGiaLamViec\":\"Han Quoc\"}"))
                .andExpect(status().isBadRequest());
    }

    private String taoDoiTac(String ten) throws Exception {
        String resp = mockMvc.perform(auth(post("/api/doi-tac"), tokenAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tenDoiTac\":\"" + ten + "\",\"loaiDoiTac\":\"TRONG_NUOC\"}"))
                .andReturn().getResponse().getContentAsString();
        return json(resp).get("id").asText();
    }
}
