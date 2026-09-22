package com.ttloc.htkhcn.congvan;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ttloc.htkhcn.common.exception.BadRequestException;

import lombok.extern.slf4j.Slf4j;

/**
 * Client goi API "Van ban noi bo don vi" cua he thong CongVan truong DHKG
 * (qlvb.vnkgu.edu.vn - xem API_VANBANNOIBO.md). Chi dung cac endpoint GET
 * (doc) - KHONG goi POST/PUT/DELETE cua API nay, theo dung yeu cau "chi keo
 * ve, khong day nguoc" cua nguoi dung, du API ho tro ca ghi.
 */
@Slf4j
@Component
public class CongVanClient {

    @Value("${app.congvan.base-url}")
    private String baseUrl;

    @Value("${app.congvan.api-key:}")
    private String apiKey;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(15))
            .build();
    // Dung Jackson 2.x (com.fasterxml.jackson) rieng o day de doc JSON tra ve
    // tu CongVan - KHONG phai ObjectMapper Spring quan ly (Jackson 3.x/tools.jackson,
    // xem ghi chu trong AuthService/jjwt ve 2 the he Jackson cung ton tai trong app).
    private final ObjectMapper objectMapper = new ObjectMapper();

    public boolean daCauHinh() {
        return apiKey != null && !apiKey.isBlank();
    }

    public JsonNode whoami() {
        return goi("/whoami");
    }

    public List<JsonNode> danhSach(Integer nam) {
        String duongDan = nam != null ? "?nam=" + nam : "";
        JsonNode ket = goi(duongDan);
        return ket.isArray() ? java.util.stream.StreamSupport.stream(ket.spliterator(), false).toList() : List.of();
    }

    public JsonNode chiTiet(long id) {
        return goi("/" + id);
    }

    public byte[] taiFile(long vanBanId, long fileId) {
        kiemTraCauHinh();
        try {
            HttpRequest req = HttpRequest.newBuilder(URI.create(baseUrl + "/" + vanBanId + "/files/" + fileId))
                    .header("X-Api-Key", apiKey)
                    .timeout(Duration.ofSeconds(30))
                    .GET()
                    .build();
            HttpResponse<byte[]> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofByteArray());
            if (resp.statusCode() != 200) {
                throw new BadRequestException("CongVan tra ve loi khi tai file (HTTP " + resp.statusCode() + ")");
            }
            return resp.body();
        } catch (IOException | InterruptedException ex) {
            if (ex instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new BadRequestException("Khong tai duoc file tu CongVan: " + ex.getMessage());
        }
    }

    private JsonNode goi(String duongDanTuongDoi) {
        kiemTraCauHinh();
        try {
            HttpRequest req = HttpRequest.newBuilder(URI.create(baseUrl + duongDanTuongDoi))
                    .header("X-Api-Key", apiKey)
                    .header("Accept", "application/json")
                    .timeout(Duration.ofSeconds(20))
                    .GET()
                    .build();
            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (resp.statusCode() == 401) {
                throw new BadRequestException("CongVan tu choi API key (401) - kiem tra lai cau hinh CONGVAN_API_KEY");
            }
            if (resp.statusCode() != 200) {
                throw new BadRequestException("CongVan tra ve loi HTTP " + resp.statusCode());
            }
            return objectMapper.readTree(resp.body());
        } catch (IOException | InterruptedException ex) {
            if (ex instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new BadRequestException("Khong ket noi duoc toi CongVan: " + ex.getMessage());
        }
    }

    private void kiemTraCauHinh() {
        if (!daCauHinh()) {
            throw new BadRequestException("Chua cau hinh CONGVAN_API_KEY - lien he Admin CongVan de xin key cho don vi");
        }
    }
}
