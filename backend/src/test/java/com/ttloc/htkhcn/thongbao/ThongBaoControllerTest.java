package com.ttloc.htkhcn.thongbao;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;

import com.ttloc.htkhcn.BaseIntegrationTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test chuong thong bao web (SPEC muc 4.7.2). Goi truc tiep procedure
 * sp_quet_mou_sap_het_han() (thay vi cho lich @Scheduled hang ngay) de kiem
 * tra thong bao duoc tao dung khi 1 MoU roi vao dung 1 moc canh bao.
 */
class ThongBaoControllerTest extends BaseIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("MoU con dung 7 ngay -> sau khi quet, Admin nhan duoc 1 thong bao CRITICAL")
    void quetTaoThongBaoDungMoc() throws Exception {
        String doiTacId = taoDoiTac("Doi tac Thong Bao " + System.nanoTime());
        taoMou(doiTacId, LocalDate.now().plusDays(7));

        jdbcTemplate.execute("CALL sp_quet_mou_sap_het_han()");

        mockMvc.perform(auth(get("/api/thong-bao/chua-doc-so-luong"), tokenAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.soLuong").value(org.hamcrest.Matchers.greaterThanOrEqualTo(1)));

        mockMvc.perform(auth(get("/api/thong-bao").param("chiChuaDoc", "true"), tokenAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].mucDo").value("CRITICAL"));
    }

    @Test
    @DisplayName("Quet 2 lan cho cung 1 moc ngay -> khong tao thong bao trung (SPEC 4.7.1)")
    void khongTaoTrungChoCungMocNgay() throws Exception {
        String doiTacId = taoDoiTac("Doi tac Khong Trung " + System.nanoTime());
        taoMou(doiTacId, LocalDate.now().plusDays(14));

        jdbcTemplate.execute("CALL sp_quet_mou_sap_het_han()");
        Long soLanDau = jdbcTemplate.queryForObject("SELECT count(*) FROM thong_bao WHERE bang_lien_quan='mou'", Long.class);

        jdbcTemplate.execute("CALL sp_quet_mou_sap_het_han()");
        Long soLanHai = jdbcTemplate.queryForObject("SELECT count(*) FROM thong_bao WHERE bang_lien_quan='mou'", Long.class);

        org.junit.jupiter.api.Assertions.assertEquals(soLanDau, soLanHai);
    }

    @Test
    @DisplayName("Danh dau da doc -> so luong chua doc giam, thong bao cua nguoi khac khong bi doc nham")
    void danhDauDaDoc() throws Exception {
        String doiTacId = taoDoiTac("Doi tac Danh Dau " + System.nanoTime());
        taoMou(doiTacId, LocalDate.now().plusDays(1));
        jdbcTemplate.execute("CALL sp_quet_mou_sap_het_han()");

        String resp = mockMvc.perform(auth(get("/api/thong-bao"), tokenAdmin()))
                .andReturn().getResponse().getContentAsString();
        String id = json(resp).get("content").get(0).get("id").asText();

        mockMvc.perform(auth(post("/api/thong-bao/" + id + "/danh-dau-da-doc"), tokenAdmin()))
                .andExpect(status().isOk());

        mockMvc.perform(auth(get("/api/thong-bao").param("chiChuaDoc", "true"), tokenAdmin()))
                .andExpect(status().isOk());
    }

    private String taoDoiTac(String ten) throws Exception {
        String resp = mockMvc.perform(auth(post("/api/doi-tac"), tokenAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tenDoiTac\":\"" + ten + "\",\"loaiDoiTac\":\"TRONG_NUOC\"}"))
                .andReturn().getResponse().getContentAsString();
        return json(resp).get("id").asText();
    }

    private String taoMou(String doiTacId, LocalDate ngayHetHan) throws Exception {
        String resp = mockMvc.perform(auth(post("/api/mou"), tokenAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"doiTacId\":\"" + doiTacId + "\",\"ngayBanHanh\":\"2020-01-01\","
                                + "\"ngayHetHan\":\"" + ngayHetHan + "\",\"caNhanDauMoi\":\"Test\"}"))
                .andReturn().getResponse().getContentAsString();
        return json(resp).get("id").asText();
    }
}
