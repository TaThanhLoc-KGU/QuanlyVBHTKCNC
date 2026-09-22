package com.ttloc.htkhcn.doitac;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.ttloc.htkhcn.BaseIntegrationTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Test module Doi tac (SPEC muc 3.1). */
class DoiTacControllerTest extends BaseIntegrationTest {

    @Test
    @DisplayName("Tao doi tac Trong nuoc hop le -> 200")
    void taoDoiTacTrongNuoc() throws Exception {
        mockMvc.perform(auth(post("/api/doi-tac"), tokenAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tenDoiTac\":\"DHDT Truong A\",\"loaiDoiTac\":\"TRONG_NUOC\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.tenDoiTac").value("DHDT Truong A"));
    }

    @Test
    @DisplayName("Tao doi tac Ngoai nuoc thieu Quoc gia -> 400")
    void taoDoiTacNgoaiNuocThieuQuocGia() throws Exception {
        mockMvc.perform(auth(post("/api/doi-tac"), tokenAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tenDoiTac\":\"Foreign Uni\",\"loaiDoiTac\":\"NGOAI_NUOC\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Tao doi tac thieu ten -> 400 (validation)")
    void taoDoiTacThieuTen() throws Exception {
        mockMvc.perform(auth(post("/api/doi-tac"), tokenAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"loaiDoiTac\":\"TRONG_NUOC\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Sua doi tac -> cap nhat dung, xoa mem roi khong con thay trong danh sach, khoi phuc lai thay")
    void suaXoaMemKhoiPhuc() throws Exception {
        String id = taoDoiTac("DHDT De Sua", "TRONG_NUOC");

        mockMvc.perform(auth(put("/api/doi-tac/" + id), tokenAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tenDoiTac\":\"DHDT Da Sua\",\"loaiDoiTac\":\"TRONG_NUOC\",\"ghiChu\":\"cap nhat\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tenDoiTac").value("DHDT Da Sua"))
                .andExpect(jsonPath("$.ghiChu").value("cap nhat"));

        mockMvc.perform(auth(delete("/api/doi-tac/" + id), tokenAdmin())).andExpect(status().isNoContent());
        mockMvc.perform(auth(get("/api/doi-tac/" + id), tokenAdmin())).andExpect(status().isNotFound());

        mockMvc.perform(auth(post("/api/doi-tac/" + id + "/khoi-phuc"), tokenAdmin())).andExpect(status().isOk());
        mockMvc.perform(auth(get("/api/doi-tac/" + id), tokenAdmin())).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Goi y doi tac gan giong ten (pg_trgm) tim thay ban ghi da co")
    void goiYTrungTen() throws Exception {
        taoDoiTac("Dai hoc Cong nghe Thong tin Test", "TRONG_NUOC");

        mockMvc.perform(auth(get("/api/doi-tac/goi-y-trung-ten").param("ten", "Dai hoc Cong nghe Thong tin"), tokenAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tenDoiTac").exists());
    }

    @Test
    @DisplayName("Tim kiem khong dau: 'dai hoc test xyz' van tim ra 'Đại Học Test XYZ'")
    void timKiemKhongPhanBietDau() throws Exception {
        mockMvc.perform(auth(post("/api/doi-tac"), tokenAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tenDoiTac\":\"\\u0110\\u1ea1i H\\u1ecdc Test XYZ\",\"loaiDoiTac\":\"TRONG_NUOC\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(auth(get("/api/doi-tac").param("tuKhoa", "dai hoc test xyz"), tokenAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("Viewer khong duoc tao doi tac -> 403")
    void viewerKhongDuocTao() throws Exception {
        String tenDangNhap = "viewer_dt_" + System.nanoTime();
        String matKhauTam = taoNguoiDungVaiTro(tenDangNhap, "VIEWER", null);
        String tokenViewer = dangNhap(tenDangNhap, matKhauTam);

        mockMvc.perform(auth(post("/api/doi-tac"), tokenViewer)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tenDoiTac\":\"X\",\"loaiDoiTac\":\"TRONG_NUOC\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Viewer van xem duoc danh sach doi tac -> 200")
    void viewerXemDuocDanhSach() throws Exception {
        String tenDangNhap = "viewer_dt2_" + System.nanoTime();
        String matKhauTam = taoNguoiDungVaiTro(tenDangNhap, "VIEWER", null);
        String tokenViewer = dangNhap(tenDangNhap, matKhauTam);

        mockMvc.perform(auth(get("/api/doi-tac"), tokenViewer)).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Editor khong duoc gan module DOI_TAC -> 403 khi tao; Editor duoc gan -> 200")
    void editorTheoModule() throws Exception {
        String khongDuocGan = "editor_khac_" + System.nanoTime();
        String mkKhong = taoNguoiDungVaiTro(khongDuocGan, "EDITOR", "MOU");
        String tokenKhongDuoc = dangNhap(khongDuocGan, mkKhong);
        mockMvc.perform(auth(post("/api/doi-tac"), tokenKhongDuoc)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tenDoiTac\":\"X\",\"loaiDoiTac\":\"TRONG_NUOC\"}"))
                .andExpect(status().isForbidden());

        String duocGan = "editor_dt_" + System.nanoTime();
        String mkDuoc = taoNguoiDungVaiTro(duocGan, "EDITOR", "DOI_TAC");
        String tokenDuoc = dangNhap(duocGan, mkDuoc);
        mockMvc.perform(auth(post("/api/doi-tac"), tokenDuoc)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tenDoiTac\":\"Y\",\"loaiDoiTac\":\"TRONG_NUOC\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Lich su chinh sua (audit trail) ghi lai dung hanh dong INSERT sau khi tao")
    void lichSuChinhSua() throws Exception {
        String id = taoDoiTac("DHDT Lich Su", "TRONG_NUOC");

        mockMvc.perform(auth(get("/api/lich-su/doi_tac/" + id), tokenAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].hanhDong").value("INSERT"))
                .andExpect(jsonPath("$[0].bang").value("doi_tac"));
    }

    private String taoDoiTac(String ten, String loai) throws Exception {
        String resp = mockMvc.perform(auth(post("/api/doi-tac"), tokenAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tenDoiTac\":\"" + ten + "\",\"loaiDoiTac\":\"" + loai + "\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return json(resp).get("id").asText();
    }
}
