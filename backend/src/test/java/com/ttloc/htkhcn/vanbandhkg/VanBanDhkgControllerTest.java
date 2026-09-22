package com.ttloc.htkhcn.vanbandhkg;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.ttloc.htkhcn.BaseIntegrationTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Test module Van ban DHKG (SPEC muc 3.2). */
class VanBanDhkgControllerTest extends BaseIntegrationTest {

    @Test
    @DisplayName("Tao van ban DHKG hop le -> 200")
    void taoHopLe() throws Exception {
        String loaiId = layLoaiVanBanDhkg();
        mockMvc.perform(auth(post("/api/van-ban-dhkg"), tokenAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"soHieu\":\"" + soHieuNgauNhien() + "\",\"tenVanBan\":\"Quyet dinh test\","
                                + "\"loaiVanBanId\":\"" + loaiId + "\",\"ngayBanHanh\":\"2026-01-01\","
                                + "\"tinhTrangHieuLuc\":\"CON_HIEU_LUC\",\"coQuanBanHanh\":\"DHKG\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tenLoaiVanBan").isNotEmpty());
    }

    @Test
    @DisplayName("So hieu trung (UNIQUE) -> 409 Conflict")
    void soHieuTrung() throws Exception {
        String loaiId = layLoaiVanBanDhkg();
        String soHieu = soHieuNgauNhien();
        String body = "{\"soHieu\":\"" + soHieu + "\",\"tenVanBan\":\"A\",\"loaiVanBanId\":\"" + loaiId
                + "\",\"ngayBanHanh\":\"2026-01-01\",\"tinhTrangHieuLuc\":\"CON_HIEU_LUC\",\"coQuanBanHanh\":\"DHKG\"}";
        mockMvc.perform(auth(post("/api/van-ban-dhkg"), tokenAdmin())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk());
        mockMvc.perform(auth(post("/api/van-ban-dhkg"), tokenAdmin())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("loaiVanBanId thuoc pham vi VBPL (khong phai DHKG) -> loi khi luu")
    void loaiVanBanSaiPhamVi() throws Exception {
        String loaiVbplId = layLoaiVanBanVbpl();
        mockMvc.perform(auth(post("/api/van-ban-dhkg"), tokenAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"soHieu\":\"" + soHieuNgauNhien() + "\",\"tenVanBan\":\"A\","
                                + "\"loaiVanBanId\":\"" + loaiVbplId + "\",\"ngayBanHanh\":\"2026-01-01\","
                                + "\"tinhTrangHieuLuc\":\"CON_HIEU_LUC\",\"coQuanBanHanh\":\"DHKG\"}"))
                .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("Thieu So hieu -> 400")
    void thieuSoHieu() throws Exception {
        String loaiId = layLoaiVanBanDhkg();
        mockMvc.perform(auth(post("/api/van-ban-dhkg"), tokenAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tenVanBan\":\"A\",\"loaiVanBanId\":\"" + loaiId + "\",\"ngayBanHanh\":\"2026-01-01\","
                                + "\"tinhTrangHieuLuc\":\"CON_HIEU_LUC\",\"coQuanBanHanh\":\"DHKG\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Danh muc loai van ban DHKG tra ve danh sach khong rong (da seed San)")
    void danhMucLoaiVanBan() throws Exception {
        mockMvc.perform(auth(get("/api/danh-muc/loai-van-ban").param("phamVi", "DHKG"), tokenAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ma").isNotEmpty());
    }

    private String layLoaiVanBanDhkg() throws Exception {
        String resp = mockMvc.perform(auth(get("/api/danh-muc/loai-van-ban").param("phamVi", "DHKG"), tokenAdmin()))
                .andReturn().getResponse().getContentAsString();
        return json(resp).get(0).get("id").asText();
    }

    private String layLoaiVanBanVbpl() throws Exception {
        String resp = mockMvc.perform(auth(get("/api/danh-muc/loai-van-ban").param("phamVi", "VBPL"), tokenAdmin()))
                .andReturn().getResponse().getContentAsString();
        return json(resp).get(0).get("id").asText();
    }

    private String soHieuNgauNhien() {
        return "SH-" + System.nanoTime();
    }
}
