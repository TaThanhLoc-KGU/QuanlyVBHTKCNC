package com.ttloc.htkhcn.crawlphapluat;

import java.io.IOException;
import java.net.CookieManager;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * Crawl congbao.chinhphu.vn (Cong bao dien tu - noi cong bo chinh thuc van
 * ban phap luat). robots.txt cua trang nay cho phep hoan toan (Allow: / cho
 * moi user-agent, khong chan bot AI nao) va trang chi tiet + endpoint tim
 * kiem deu render san HTML phia server - khong can trinh duyet ao.
 *
 * Co che tim kiem: trang "/tim-kiem-van-ban.htm?keyword=..." luu tu khoa vao
 * session (cookie), sau do "/api-searchvanban/.../trang-N.htm" tra ve ket qua
 * theo tu khoa da luu trong cung session - can 2 buoc goi lien tiep dung
 * chung 1 CookieManager.
 */
@Slf4j
@Service
public class CongBaoCrawlerService {

    private static final String GOC = "https://congbao.chinhphu.vn";
    private static final String USER_AGENT = "PHTKHCN-DHKG-CrawlBot/1.0 (+mailto:admin@dhkg.edu.vn; tra cuu van ban phap luat lien quan hop tac KHCN & QHQT)";
    private static final int SO_TRANG_TOI_DA = 3;
    private static final Pattern BAN_HANH_PATTERN = Pattern.compile("Ban hành:\\s*(\\d{2}/\\d{2}/\\d{4})");
    private static final DateTimeFormatter DD_MM_YYYY = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public List<KetQuaCrawl> timTheoTuKhoa(String tuKhoa) {
        List<KetQuaCrawl> ketQua = new ArrayList<>();
        try {
            HttpClient client = HttpClient.newBuilder()
                    .cookieHandler(new CookieManager())
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .connectTimeout(Duration.ofSeconds(15))
                    .build();

            // Buoc 1: mo trang tim kiem de server luu tu khoa vao session.
            String urlTimKiem = GOC + "/tim-kiem-van-ban.htm?keyword=" + URLEncoder.encode(tuKhoa, StandardCharsets.UTF_8);
            client.send(taoRequest(urlTimKiem), HttpResponse.BodyHandlers.discarding());

            // Buoc 2: doc ket qua (c0=tat ca co quan, l0=tat ca loai, d0/h0=khong loc theo dia phuong,
            // 0-0=khong loc ngay, s10=10 ket qua/trang) - lay toi da SO_TRANG_TOI_DA trang.
            for (int trang = 1; trang <= SO_TRANG_TOI_DA; trang++) {
                String urlKetQua = GOC + "/api-searchvanban/c0/l0/d0/h0/0-0/s10/trang-" + trang + ".htm";
                HttpResponse<String> resp = client.send(taoRequest(urlKetQua), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
                if (resp.statusCode() != 200) {
                    log.warn("Cong bao tra ve HTTP {} cho tu khoa '{}' trang {}", resp.statusCode(), tuKhoa, trang);
                    break;
                }
                Document doc = Jsoup.parse(resp.body(), GOC);
                Elements items = doc.select(".item-newspaper");
                if (items.isEmpty()) {
                    break;
                }
                for (Element item : items) {
                    KetQuaCrawl kq = phanTich(item);
                    if (kq != null) {
                        ketQua.add(kq);
                    }
                }
            }
        } catch (IOException | InterruptedException ex) {
            if (ex instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            log.warn("Loi khi crawl congbao.chinhphu.vn voi tu khoa '{}': {}", tuKhoa, ex.getMessage());
        }
        return ketQua;
    }

    private HttpRequest taoRequest(String url) {
        return HttpRequest.newBuilder(URI.create(url))
                .header("User-Agent", USER_AGENT)
                .timeout(Duration.ofSeconds(20))
                .GET()
                .build();
    }

    private KetQuaCrawl phanTich(Element item) {
        Element kyHieuEl = item.selectFirst(".txt-l");
        Element tieuDeEl = item.selectFirst("a.sapo");
        Element ngayEl = item.selectFirst("p.bh");
        Element loaiEl = item.selectFirst("p.tt");
        if (kyHieuEl == null || tieuDeEl == null) {
            return null;
        }
        String soHieu = kyHieuEl.text().replaceFirst("(?i)^Ký hiệu:\\s*", "").trim();
        String tenVanBan = tieuDeEl.text().trim();
        if (soHieu.isBlank() || tenVanBan.isBlank()) {
            return null;
        }
        String urlNguon = tieuDeEl.absUrl("href");
        String loaiVanBan = loaiEl != null ? loaiEl.text().trim() : null;
        LocalDate ngayBanHanh = null;
        if (ngayEl != null) {
            Matcher m = BAN_HANH_PATTERN.matcher(ngayEl.text());
            if (m.find()) {
                try {
                    ngayBanHanh = LocalDate.parse(m.group(1), DD_MM_YYYY);
                } catch (Exception ignored) {
                    // giu null neu khong parse duoc, khong chan ca ket qua
                }
            }
        }
        return new KetQuaCrawl(soHieu, tenVanBan, loaiVanBan, ngayBanHanh, null, urlNguon);
    }
}
