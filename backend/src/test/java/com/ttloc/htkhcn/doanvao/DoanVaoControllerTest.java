package com.ttloc.htkhcn.doanvao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.ttloc.htkhcn.BaseIntegrationTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Test module Doan vao (SPEC muc 3.5) - trong tam la 2 generated column Nam/So ngay. */
class DoanVaoControllerTest extends BaseIntegrationTest {

    @Test
    @DisplayName("Tao doan vao 5 ngay (01/03 - 05/03/2026) -> nam=2026, soNgay=5")
    void generatedColumnDungDan() throws Exception {
        mockMvc.perform(auth(post("/api/doan-vao"), tokenAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"tenDoan":"Doan test","thoiGianDen":"2026-03-01","thoiGianDi":"2026-03-05",
                                 "soLuongNguoiNuocNgoai":5,"quocTich":["Nhat Ban","Han Quoc"]}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nam").value(2026))
                .andExpect(jsonPath("$.soNgay").value(5))
                .andExpect(jsonPath("$.quocTich.length()").value(2));
    }

    @Test
    @DisplayName("Thoi gian di truoc Thoi gian den -> 400")
    void thoiGianDiTruocThoiGianDen() throws Exception {
        mockMvc.perform(auth(post("/api/doan-vao"), tokenAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"tenDoan":"Doan loi","thoiGianDen":"2026-03-05","thoiGianDi":"2026-03-01",
                                 "soLuongNguoiNuocNgoai":5,"quocTich":["Lao"]}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Thieu Quoc tich -> 400 (validation @NotEmpty)")
    void thieuQuocTich() throws Exception {
        mockMvc.perform(auth(post("/api/doan-vao"), tokenAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"tenDoan":"Doan loi 2","thoiGianDen":"2026-03-01","thoiGianDi":"2026-03-05",
                                 "soLuongNguoiNuocNgoai":5,"quocTich":[]}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Doan vao khong bat buoc gan doi tac (doiTacId co the null)")
    void khongBatBuocDoiTac() throws Exception {
        mockMvc.perform(auth(post("/api/doan-vao"), tokenAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"tenDoan":"Doan khong doi tac","thoiGianDen":"2026-03-01","thoiGianDi":"2026-03-01",
                                 "soLuongNguoiNuocNgoai":1,"quocTich":["Thai Lan"]}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.doiTacId").isEmpty())
                .andExpect(jsonPath("$.soNgay").value(1));
    }
}
