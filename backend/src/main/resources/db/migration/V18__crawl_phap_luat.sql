-- V18: Crawl tu dong van ban phap luat moi (theo yeu cau bo sung cua nguoi dung
-- ngoai pham vi SPEC ban dau) tu 2 nguon: Cong bao Chinh phu (congbao.chinhphu.vn,
-- robots.txt cho phep hoan toan, HTML render san) va vbpl.vn (robots.txt cho phep
-- nhung la SPA, can Playwright de render). thuvienphapluat.vn KHONG duoc dung vi
-- robots.txt chan tuong minh AI bot (ClaudeBot, GPTBot...).
--
-- Thiet ke: ket qua crawl vao bang "ung vien" (staging), KHONG ghi thang vao
-- vbpl_vn - vi pham vi HTQT/KHCN kha rong (nguoi dung xac nhan), can nguoi
-- duyet tung dong truoc khi nhan vao du lieu chinh thuc, giong luong Import
-- Excel (xem truoc -> xac nhan) da co san.

CREATE TYPE nguon_crawl_enum AS ENUM ('CONG_BAO_CHINH_PHU', 'VBPL_VN_PORTAL');
CREATE TYPE trang_thai_ung_vien_enum AS ENUM ('CHUA_XU_LY', 'DA_NHAN', 'DA_BO_QUA');

CREATE TABLE tu_khoa_crawl_phap_luat (
  id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  tu_khoa     VARCHAR(255) NOT NULL UNIQUE,
  hoat_dong   BOOLEAN NOT NULL DEFAULT TRUE,
  ngay_tao    TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Tu khoa mac dinh dung pham vi P.HTKHCN dang quan ly (hop tac KHCN & QHQT).
INSERT INTO tu_khoa_crawl_phap_luat (tu_khoa) VALUES
  ('hợp tác quốc tế'),
  ('khoa học và công nghệ'),
  ('người nước ngoài'),
  ('xuất cảnh, nhập cảnh'),
  ('cư trú của người nước ngoài'),
  ('cơ sở giáo dục đại học'),
  ('hợp tác nghiên cứu khoa học'),
  ('học bổng'),
  ('visa'),
  ('chuyên gia nước ngoài');

CREATE TABLE vbpl_ung_vien (
  id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  nguon             nguon_crawl_enum NOT NULL,
  so_hieu           VARCHAR(150) NOT NULL,
  ten_van_ban       TEXT NOT NULL,
  loai_van_ban_text VARCHAR(255),
  ngay_ban_hanh     DATE,
  co_quan_ban_hanh  VARCHAR(255),
  url_nguon         TEXT NOT NULL,
  tu_khoa_khop      VARCHAR(255),
  trang_thai        trang_thai_ung_vien_enum NOT NULL DEFAULT 'CHUA_XU_LY',
  vbpl_vn_id        UUID REFERENCES vbpl_vn(id),
  ngay_crawl        TIMESTAMPTZ NOT NULL DEFAULT now(),
  nguoi_xu_ly_id    UUID REFERENCES nguoi_dung(id),
  ngay_xu_ly        TIMESTAMPTZ,

  -- 1 van ban (theo so_hieu) tu 1 nguon chi can xuat hien 1 lan lam ung vien,
  -- ke ca khi khop nhieu tu khoa khac nhau - tranh trung lap khi crawl lai
  -- nhieu ngay lien tiep hoac khop nhieu tu khoa cung luc.
  UNIQUE (nguon, so_hieu)
);

CREATE INDEX idx_vbpl_ung_vien_trang_thai ON vbpl_ung_vien (trang_thai);

INSERT INTO cau_hinh_he_thong (ma, gia_tri, mo_ta) VALUES
  ('crawl_phap_luat_lan_cuoi', '', 'Thoi diem chay crawl phap luat gan nhat (ISO datetime, rong = chua chay lan nao)');
