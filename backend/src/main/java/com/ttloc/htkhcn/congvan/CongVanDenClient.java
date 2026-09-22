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

/**
 * Client goi API "Cong van den theo don vi" cua he thong CongVan truong DHKG
 * (qlvb.vnkgu.edu.vn - xem API_VANBAN_DEN.md). API nay CHI CO GET (doc), khop
 * voi yeu cau "chi keo ve, khong day nguoc" cua nguoi dung. Dung chung
 * CONGVAN_API_KEY voi CongVanClient (API "van ban noi bo") vi ca 2 la API
 * khac nhau cua CUNG 1 he thong, cap cho cung 1 don vi (P.HTKHCN).
 */
@Component
public class CongVanDenClient {

    private static final int PAGE_SIZE = 100; // toi da theo API_VANBAN_DEN.md

    @Value("${app.congvan.den-base-url}")
    private String baseUrl;

    @Value("${app.congvan.api-key:}")
    private String apiKey;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(15))
            .build();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public boolean daCauHinh() {
        return apiKey != null && !apiKey.isBlank();
    }

    /** Lay TOAN BO cong van den (lap qua tat ca trang) - dung cho dong bo. */
    public List<JsonNode> layTatCa() {
        List<JsonNode> ketQua = new java.util.ArrayList<>();
        int page = 1;
        while (true) {
            TrangKetQua trang = layTrang(page);
            ketQua.addAll(trang.items());
            if ((long) page * PAGE_SIZE >= trang.total() || trang.items().isEmpty()) {
                break;
            }
            page++;
        }
        return ketQua;
    }

    public TrangKetQua layTrang(int page) {
        JsonNode ket = goi("?page=" + page + "&pageSize=" + PAGE_SIZE);
        List<JsonNode> items = new java.util.ArrayList<>();
        ket.path("items").forEach(items::add);
        return new TrangKetQua(ket.path("total").asInt(0), page, items);
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

    public record TrangKetQua(int total, int page, List<JsonNode> items) {
    }
}
