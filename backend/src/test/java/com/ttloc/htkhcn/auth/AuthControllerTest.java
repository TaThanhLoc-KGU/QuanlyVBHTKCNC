package com.ttloc.htkhcn.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.ttloc.htkhcn.BaseIntegrationTest;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Test dang nhap/phan quyen (SPEC muc 4.1). */
class AuthControllerTest extends BaseIntegrationTest {

    @Test
    @DisplayName("Dang nhap dung tai khoan/mat khau -> 200, tra ve access+refresh token va vai tro ADMIN")
    void dangNhapThanhCong() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tenDangNhap\":\"admin\",\"matKhau\":\"" + MAT_KHAU_ADMIN_MAC_DINH + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andExpect(jsonPath("$.tenDangNhap").value("admin"))
                .andExpect(jsonPath("$.vaiTro", hasItem("ADMIN")));
    }

    @Test
    @DisplayName("Dang nhap sai mat khau -> 401")
    void dangNhapSaiMatKhau() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tenDangNhap\":\"admin\",\"matKhau\":\"sai_mat_khau\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Dang nhap tai khoan khong ton tai -> 401")
    void dangNhapTaiKhoanKhongTonTai() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tenDangNhap\":\"khong_ton_tai_123\",\"matKhau\":\"abc\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Dang nhap thieu truong bat buoc -> 400")
    void dangNhapThieuTruong() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tenDangNhap\":\"\",\"matKhau\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Sai mat khau 5 lan lien tiep -> tai khoan bi khoa tam (423), du dung mat khau lan thu 6")
    void khoaTaiKhoanSauNhieuLanDangNhapSai() throws Exception {
        String tenDangNhap = "khoa_test_" + System.nanoTime();
        String matKhauTam = taoNguoiDungVaiTro(tenDangNhap, "VIEWER", null);

        for (int i = 0; i < 5; i++) {
            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"tenDangNhap\":\"" + tenDangNhap + "\",\"matKhau\":\"sai\"}"))
                    .andExpect(status().isUnauthorized());
        }

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tenDangNhap\":\"" + tenDangNhap + "\",\"matKhau\":\"" + matKhauTam + "\"}"))
                .andExpect(status().isLocked());
    }

    @Test
    @DisplayName("GET /api/auth/me voi token hop le -> tra ve dung thong tin nguoi dang nhap")
    void layThongTinCaNhan() throws Exception {
        String token = tokenAdmin();
        mockMvc.perform(auth(get("/api/auth/me"), token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tenDangNhap").value("admin"))
                .andExpect(jsonPath("$.vaiTro", hasItem("ADMIN")));
    }

    @Test
    @DisplayName("Goi API can xac thuc ma khong co token -> 403")
    void truyCapKhongCoToken() throws Exception {
        mockMvc.perform(get("/api/auth/me")).andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Goi API can xac thuc voi token gia/hong -> 403")
    void truyCapTokenKhongHopLe() throws Exception {
        mockMvc.perform(auth(get("/api/auth/me"), "token.gia.hong")).andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Refresh token hop le -> cap access token moi")
    void refreshTokenThanhCong() throws Exception {
        String loginResp = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tenDangNhap\":\"admin\",\"matKhau\":\"" + MAT_KHAU_ADMIN_MAC_DINH + "\"}"))
                .andReturn().getResponse().getContentAsString();
        String refreshToken = json(loginResp).get("refreshToken").asText();

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + refreshToken + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }

    @Test
    @DisplayName("Refresh bang access token (khong phai refresh token) -> 400")
    void refreshBangAccessToken() throws Exception {
        String accessToken = tokenAdmin();
        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + accessToken + "\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Doi mat khau: sai mat khau hien tai -> 400; dung -> 200 va dang nhap lai duoc voi mat khau moi")
    void doiMatKhau() throws Exception {
        String tenDangNhap = "doimk_test_" + System.nanoTime();
        String matKhauTam = taoNguoiDungVaiTro(tenDangNhap, "VIEWER", null);
        String token = dangNhap(tenDangNhap, matKhauTam);

        mockMvc.perform(auth(post("/api/auth/doi-mat-khau"), token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"matKhauCu\":\"sai_roi\",\"matKhauMoi\":\"MatKhauMoi123\"}"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(auth(post("/api/auth/doi-mat-khau"), token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"matKhauCu\":\"" + matKhauTam + "\",\"matKhauMoi\":\"MatKhauMoi123\"}"))
                .andExpect(status().isOk());

        dangNhap(tenDangNhap, "MatKhauMoi123");
    }
}
