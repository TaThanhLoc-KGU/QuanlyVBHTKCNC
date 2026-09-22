package com.ttloc.htkhcn;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.AbstractMockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Lop test tich hop chung: chay tren Postgres THAT (khong dung H2) vi schema
 * du an dung nhieu dac trung chi Postgres moi co (pg_trgm, unaccent, ENUM,
 * trigger, VIEW, materialized view, generated column).
 *
 * Dung 1 database rieng "htkhcn_test" tren chinh Postgres local cua may dev
 * (KHONG dung Testcontainers/Docker) - Docker Desktop trong may nay bi mat
 * ket noi container ngau nhien sau ~30-60s (thu 2 lan deu vay), trong khi
 * Postgres local da chay on dinh suot phien lam viec. Database nay duoc tao
 * mot lan (xem huong dan trong README test), Flyway migrate tu dau moi lan
 * chay (baseline-on-migrate=false) roi @Transactional rollback tung test.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public abstract class BaseIntegrationTest {

    @DynamicPropertySource
    static void cauHinhDongDataSource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> "jdbc:postgresql://localhost:5432/htkhcn_test");
        registry.add("spring.datasource.username", () -> "htkhcn");
        registry.add("spring.datasource.password", () -> "htkhcn");
        registry.add("spring.flyway.baseline-on-migrate", () -> "false");
        registry.add("spring.datasource.hikari.connection-timeout", () -> "8000");
    }

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    private static String adminTokenCache;

    protected static final String MAT_KHAU_ADMIN_MAC_DINH = "Admin@123";

    @BeforeAll
    static void resetTokenCacheGiuaCacLopTest() {
        adminTokenCache = null;
    }

    protected String tokenAdmin() throws Exception {
        if (adminTokenCache == null) {
            adminTokenCache = dangNhap("admin", MAT_KHAU_ADMIN_MAC_DINH);
        }
        return adminTokenCache;
    }

    protected String dangNhap(String tenDangNhap, String matKhau) throws Exception {
        String body = """
                {"tenDangNhap":"%s","matKhau":"%s"}
                """.formatted(tenDangNhap, matKhau);
        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        return objectMapper.readTree(response).get("accessToken").asText();
    }

    /**
     * Generic tren B (khong chi MockHttpServletRequestBuilder) vi
     * MockMultipartHttpServletRequestBuilder (tra ve tu multipart(...)) KHONG
     * ke thua MockHttpServletRequestBuilder - ca hai chi cung ke thua
     * AbstractMockHttpServletRequestBuilder<B> (self-type). Can generic de
     * .file(...) con goi tiep duoc sau auth(...) trong test upload file.
     */
    protected <B extends AbstractMockHttpServletRequestBuilder<B>> B auth(B builder, String token) {
        return builder.header("Authorization", "Bearer " + token);
    }

    protected JsonNode json(String content) throws Exception {
        return objectMapper.readTree(content);
    }

    /** Tao 1 nguoi dung moi (qua API Admin) voi 1 vai tro + (tuy chon) 1 module
     * duoc phep sua, tra ve mat khau tam de dang nhap trong test. */
    protected String taoNguoiDungVaiTro(String tenDangNhap, String vaiTro, String moduleDuocSua) throws Exception {
        String bienTap = moduleDuocSua == null ? "[]" : "[\"" + moduleDuocSua + "\"]";
        String body = """
                {"tenDangNhap":"%s","email":"%s@dhkg.edu.vn","hoTen":"Nguoi dung test","vaiTroMa":["%s"],"bienTapMoDun":%s}
                """.formatted(tenDangNhap, tenDangNhap, vaiTro, bienTap);
        String resp = mockMvc.perform(auth(post("/api/nguoi-dung"), tokenAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return json(resp).get("matKhauTam").asText();
    }
}
