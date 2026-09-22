package com.ttloc.htkhcn.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.ttloc.htkhcn.BaseIntegrationTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Test quan ly tai khoan - chi Admin duoc phep (SPEC muc 2). */
class NguoiDungControllerTest extends BaseIntegrationTest {

    @Test
    @DisplayName("Admin tao tai khoan moi -> 200, tra ve mat khau tam, phaiDoiMatKhau=true")
    void adminTaoTaiKhoan() throws Exception {
        String ten = "nd_test_" + System.nanoTime();
        mockMvc.perform(auth(post("/api/nguoi-dung"), tokenAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tenDangNhap\":\"" + ten + "\",\"email\":\"" + ten + "@dhkg.edu.vn\","
                                + "\"hoTen\":\"Nguyen Van Test\",\"vaiTroMa\":[\"EDITOR\"],\"bienTapMoDun\":[\"MOU\"]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.matKhauTam").isNotEmpty())
                .andExpect(jsonPath("$.nguoiDung.phaiDoiMatKhau").value(true))
                .andExpect(jsonPath("$.nguoiDung.vaiTro[0]").value("EDITOR"));
    }

    @Test
    @DisplayName("Tao tai khoan trung ten dang nhap -> 409")
    void taoTrungTenDangNhap() throws Exception {
        String ten = "nd_trung_" + System.nanoTime();
        String body = "{\"tenDangNhap\":\"" + ten + "\",\"email\":\"" + ten + "@dhkg.edu.vn\","
                + "\"hoTen\":\"A\",\"vaiTroMa\":[\"VIEWER\"]}";
        mockMvc.perform(auth(post("/api/nguoi-dung"), tokenAdmin())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk());
        mockMvc.perform(auth(post("/api/nguoi-dung"), tokenAdmin())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Khoa tai khoan -> dang nhap bi tu choi; Mo khoa -> dang nhap lai duoc")
    void khoaVaMoKhoaTaiKhoan() throws Exception {
        String ten = "nd_khoa_" + System.nanoTime();
        String resp = mockMvc.perform(auth(post("/api/nguoi-dung"), tokenAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tenDangNhap\":\"" + ten + "\",\"email\":\"" + ten + "@dhkg.edu.vn\","
                                + "\"hoTen\":\"A\",\"vaiTroMa\":[\"VIEWER\"]}"))
                .andReturn().getResponse().getContentAsString();
        String id = json(resp).get("nguoiDung").get("id").asText();
        String matKhauTam = json(resp).get("matKhauTam").asText();

        mockMvc.perform(auth(post("/api/nguoi-dung/" + id + "/khoa"), tokenAdmin())).andExpect(status().isOk());
        // 423 Locked (khong phai 401): dung ma trang thai rieng cho tai khoan bi
        // Admin khoa, giong nhu khoa tam thoi do dang nhap sai (AccountLockedException).
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tenDangNhap\":\"" + ten + "\",\"matKhau\":\"" + matKhauTam + "\"}"))
                .andExpect(status().isLocked());

        mockMvc.perform(auth(post("/api/nguoi-dung/" + id + "/mo-khoa"), tokenAdmin())).andExpect(status().isOk());
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tenDangNhap\":\"" + ten + "\",\"matKhau\":\"" + matKhauTam + "\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Dat lai mat khau -> mat khau cu khong dang nhap duoc nua, mat khau moi dang nhap duoc")
    void datLaiMatKhau() throws Exception {
        String ten = "nd_reset_" + System.nanoTime();
        String resp = mockMvc.perform(auth(post("/api/nguoi-dung"), tokenAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tenDangNhap\":\"" + ten + "\",\"email\":\"" + ten + "@dhkg.edu.vn\","
                                + "\"hoTen\":\"A\",\"vaiTroMa\":[\"VIEWER\"]}"))
                .andReturn().getResponse().getContentAsString();
        String id = json(resp).get("nguoiDung").get("id").asText();
        String matKhauCu = json(resp).get("matKhauTam").asText();

        String respReset = mockMvc.perform(auth(post("/api/nguoi-dung/" + id + "/dat-lai-mat-khau"), tokenAdmin()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        String matKhauMoi = json(respReset).get("matKhauTam").asText();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tenDangNhap\":\"" + ten + "\",\"matKhau\":\"" + matKhauCu + "\"}"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tenDangNhap\":\"" + ten + "\",\"matKhau\":\"" + matKhauMoi + "\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Editor/Viewer khong duoc quan ly tai khoan -> 403")
    void khongPhaiAdminBiCam() throws Exception {
        String ten = "nd_vw_" + System.nanoTime();
        String matKhauTam = taoNguoiDungVaiTro(ten, "VIEWER", null);
        String tokenViewer = dangNhap(ten, matKhauTam);

        mockMvc.perform(auth(get("/api/nguoi-dung"), tokenViewer)).andExpect(status().isForbidden());
    }
}
