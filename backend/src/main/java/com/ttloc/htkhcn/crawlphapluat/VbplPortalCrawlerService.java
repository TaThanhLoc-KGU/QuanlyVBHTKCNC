package com.ttloc.htkhcn.crawlphapluat;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitForSelectorState;

import lombok.extern.slf4j.Slf4j;

/**
 * Crawl vbpl.vn (Co so du lieu quoc gia ve phap luat) - robots.txt cho phep
 * (Allow: /, chi chan /api/ va /Pages/), nhung toan bo trang la SPA (Next.js,
 * goi server action qua POST noi bo, khong phai REST/JSON on dinh) nen bat
 * buoc phai dung trinh duyet that (Playwright) de render, KHONG duoc goi
 * thang cac endpoint noi bo (vi pham robots.txt /api/ va cung khong on dinh
 * giua cac lan trien khai).
 *
 * Trang nay tai kha nang (nhieu anh/logo bo nganh) nen mo TRANG 1 LAN duy
 * nhat cho ca phien crawl (dung chung 1 browser/page cho tat ca tu khoa)
 * thay vi mo lai tu dau moi tu khoa - vua nhanh hon vua tranh timeout do tai
 * trang lap lai.
 *
 * Vi hanh vi loc theo tu khoa cua giao dien tim kiem site nay kho xac dinh
 * chac chan tu ben ngoai, ham nay LOC LAI o phia minh (chi giu ket qua co
 * tieu de chua tu khoa, khong dau, khong phan biet hoa/thuong) truoc khi coi
 * la ung vien - tranh dua toan bo danh sach khong loc vao ung vien neu tim
 * kiem cua site khong hoat dong nhu mong doi.
 */
@Slf4j
@Service
public class VbplPortalCrawlerService {

    private static final String GOC = "https://vbpl.vn";
    private static final DateTimeFormatter DD_MM_YYYY = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Pattern NGAY_BAN_HANH_PATTERN = Pattern.compile("Ngày ban hành:\\s*(\\d{2}/\\d{2}/\\d{4})");

    public Map<String, List<KetQuaCrawl>> timTheoNhieuTuKhoa(List<String> danhSachTuKhoa) {
        Map<String, List<KetQuaCrawl>> ketQuaTheoTuKhoa = new LinkedHashMap<>();
        if (danhSachTuKhoa.isEmpty()) {
            return ketQuaTheoTuKhoa;
        }
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
            try {
                Page page = browser.newPage();
                // Trang co nhieu anh/logo, lan tai dau tien co the cham - cho
                // "domcontentloaded" (khong doi het anh/polling nen) va cho
                // rieng khung tim kiem xuat hien de chac chan JS da chay xong.
                page.navigate(GOC, new Page.NavigateOptions()
                        .setTimeout(60000)
                        .setWaitUntil(com.microsoft.playwright.options.WaitUntilState.DOMCONTENTLOADED));
                page.waitForSelector("input[placeholder='Nhập từ khóa tìm kiếm']",
                        new Page.WaitForSelectorOptions().setTimeout(30000));
                dongBannerCookieNeuCo(page);

                for (String tuKhoa : danhSachTuKhoa) {
                    ketQuaTheoTuKhoa.put(tuKhoa, timMotTuKhoa(page, tuKhoa));
                }
            } finally {
                browser.close();
            }
        } catch (Exception ex) {
            log.warn("Loi khi khoi tao trinh duyet crawl vbpl.vn: {}", ex.getMessage());
        }
        return ketQuaTheoTuKhoa;
    }

    private List<KetQuaCrawl> timMotTuKhoa(Page page, String tuKhoa) {
        List<KetQuaCrawl> ketQua = new ArrayList<>();
        try {
            Locator oTimKiem = page.locator("input[placeholder='Nhập từ khóa tìm kiếm']").first();
            oTimKiem.click();
            oTimKiem.fill("");
            oTimKiem.fill(tuKhoa);
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Tìm kiếm").setExact(true))
                    .first().click();
            // Cho ket qua that xuat hien (link chi tiet van ban) thay vi cho
            // "networkidle" - SPA nay co the khong bao gio thuc su idle.
            page.waitForSelector("a[href*='/van-ban/chi-tiet/']",
                    new Page.WaitForSelectorOptions().setTimeout(15000).setState(WaitForSelectorState.ATTACHED));
            page.waitForTimeout(800);

            List<ElementHandle> theKetQua = page.querySelectorAll("a[href*='/van-ban/chi-tiet/']");
            String tuKhoaKhongDau = boDauTiengViet(tuKhoa);
            for (ElementHandle the : theKetQua) {
                KetQuaCrawl kq = phanTich(the);
                if (kq == null) {
                    continue;
                }
                if (!boDauTiengViet(kq.tenVanBan()).contains(tuKhoaKhongDau)) {
                    continue;
                }
                ketQua.add(kq);
            }
        } catch (Exception ex) {
            log.warn("Loi khi crawl vbpl.vn voi tu khoa '{}': {}", tuKhoa, ex.getMessage());
        }
        return ketQua;
    }

    private void dongBannerCookieNeuCo(Page page) {
        try {
            Locator tuChoi = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Từ chối"));
            if (tuChoi.count() > 0) {
                tuChoi.first().click(new Locator.ClickOptions().setTimeout(3000));
            }
        } catch (Exception ignored) {
            // khong co banner cookie thi bo qua, khong chan luong crawl
        }
    }

    private KetQuaCrawl phanTich(ElementHandle the) {
        String href = the.getAttribute("href");
        String tieuDe = the.innerText();
        if (href == null || tieuDe == null || tieuDe.isBlank()) {
            return null;
        }
        tieuDe = tieuDe.trim().replaceAll("\\s+", " ");
        String urlNguon = href.startsWith("http") ? href : GOC + href;

        // Cha gan nhat chua ca card ket qua (tieu de + trang thai + ngay) -
        // dung XPath "ancestor" don gian qua evaluate vi Playwright Java
        // khong co ham "closest" tien loi nhu JS.
        String ngayBanHanhText = null;
        try {
            Object ketQua = the.evaluate(
                    "el => { let n = el; for (let i = 0; i < 6 && n; i++) { n = n.parentElement; if (n && n.innerText && n.innerText.includes('Ngày ban hành')) return n.innerText; } return null; }");
            if (ketQua != null) {
                ngayBanHanhText = ketQua.toString();
            }
        } catch (Exception ignored) {
            // khong lay duoc ngay ban hanh thi de trong, khong chan ket qua
        }

        LocalDate ngayBanHanh = null;
        if (ngayBanHanhText != null) {
            Matcher m = NGAY_BAN_HANH_PATTERN.matcher(ngayBanHanhText);
            if (m.find()) {
                try {
                    ngayBanHanh = LocalDate.parse(m.group(1), DD_MM_YYYY);
                } catch (Exception ignored) {
                    // giu null neu khong parse duoc
                }
            }
        }

        // vbpl.vn khong tach rieng "so hieu" trong tieu de - dung tam tieu
        // de lam so hieu tra cuu, nguoi duyet se sua lai khi nhan vao VBPL VN.
        return new KetQuaCrawl(tieuDe, tieuDe, null, ngayBanHanh, null, urlNguon);
    }

    private String boDauTiengViet(String s) {
        if (s == null) {
            return "";
        }
        String bin = java.text.Normalizer.normalize(s.trim().toLowerCase(Locale.ROOT), java.text.Normalizer.Form.NFD);
        return bin.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "")
                .replace('đ', 'd').replace('Đ', 'D')
                .replaceAll("\\s+", " ");
    }
}
