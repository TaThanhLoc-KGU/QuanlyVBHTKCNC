package com.ttloc.htkhcn.vbplvn;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.ttloc.htkhcn.BaseIntegrationTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Test module VBPL VN (SPEC muc 3.3) - trong tam la "Ngay doi chieu gan nhat". */
class VbplVnControllerTest extends BaseIntegrationTest {

    @Test
    @DisplayName("Tao VBPL VN hop le -> 200, ngayDoiChieuGanNhat ban dau la null")
    void taoHopLe() throws Exception {
        String loaiId = layLoaiVanBanVbpl();
        mockMvc.perform(auth(post("/api/vbpl-vn"), tokenAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"soHieu\":\"" + soHieuNgauNhien() + "\",\"tenVanBan\":\"Luat test\","
                                + "\"loaiVanBanId\":\"" + loaiId + "\",\"ngayBanHanh\":\"2024-06-01\","
                                + "\"tinhTrangHieuLuc\":\"CON_HIEU_LUC\",\"coQuanBanHanh\":\"Quoc hoi\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ngayDoiChieuGanNhat").isEmpty());
    }

    @Test
    @DisplayName("Xac nhan doi chieu -> ngayDoiChieuGanNhat duoc dien = hom nay")
    void xacNhanDoiChieu() throws Exception {
        String loaiId = layLoaiVanBanVbpl();
        String resp = mockMvc.perform(auth(post("/api/vbpl-vn"), tokenAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"soHieu\":\"" + soHieuNgauNhien() + "\",\"tenVanBan\":\"Luat test 2\","
                                + "\"loaiVanBanId\":\"" + loaiId + "\",\"ngayBanHanh\":\"2024-06-01\","
                                + "\"tinhTrangHieuLuc\":\"CON_HIEU_LUC\",\"coQuanBanHanh\":\"Quoc hoi\"}"))
                .andReturn().getResponse().getContentAsString();
        String id = json(resp).get("id").asText();

        mockMvc.perform(auth(post("/api/vbpl-vn/" + id + "/xac-nhan-doi-chieu"), tokenAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ngayDoiChieuGanNhat").value(java.time.LocalDate.now().toString()));
    }

    private String layLoaiVanBanVbpl() throws Exception {
        String resp = mockMvc.perform(auth(get("/api/danh-muc/loai-van-ban").param("phamVi", "VBPL"), tokenAdmin()))
                .andReturn().getResponse().getContentAsString();
        return json(resp).get(0).get("id").asText();
    }

    private String soHieuNgauNhien() {
        return "VBPL-" + System.nanoTime();
    }
}
