-- V19: Dong bo (chi keo ve, mot chieu) van ban noi bo tu he thong CongVan cua
-- truong (qlvb.vnkgu.edu.vn - API_VANBANNOIBO.md) vao module "Van ban DHKG".
-- Theo yeu cau nguoi dung: KHONG day nguoc du lieu tu app nay len CongVan.

-- Bo sung danh muc "Loai van ban" pham vi DHKG - CongVan la so van ban noi bo
-- chung cua don vi nen co nhieu loai hon 4 loai da seed truoc do (V4).
INSERT INTO danh_muc_loai_van_ban (ma, ten, pham_vi, thu_tu) VALUES
  ('THONG_BAO_DHKG', 'Thông báo', 'DHKG', 5),
  ('CONG_VAN_DHKG', 'Công văn', 'DHKG', 6),
  ('KE_HOACH_DHKG', 'Kế hoạch', 'DHKG', 7),
  ('CONG_DIEN_DHKG', 'Công điện', 'DHKG', 8),
  ('CHI_THI_DHKG', 'Chỉ thị', 'DHKG', 9)
ON CONFLICT (ma, pham_vi) DO NOTHING;

-- Ung vien tu CongVan - tai xuong 1 chieu, nguoi dung duyet truoc khi nhan
-- vao van_ban_dhkg (giong luong vbpl_ung_vien o V18, tai su dung enum trang_thai).
CREATE TABLE van_ban_dhkg_ung_vien (
  id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  congvan_id        BIGINT NOT NULL UNIQUE,
  so_hieu           VARCHAR(150) NOT NULL,
  tieu_de           TEXT NOT NULL,
  noi_dung          TEXT,
  ngay_ban_hanh     DATE,
  nguoi_ky          VARCHAR(255),
  trang_thai_congvan INTEGER,
  so_file           INTEGER NOT NULL DEFAULT 0,
  trang_thai        trang_thai_ung_vien_enum NOT NULL DEFAULT 'CHUA_XU_LY',
  van_ban_dhkg_id   UUID REFERENCES van_ban_dhkg(id),
  ngay_dong_bo      TIMESTAMPTZ NOT NULL DEFAULT now(),
  nguoi_xu_ly_id    UUID REFERENCES nguoi_dung(id),
  ngay_xu_ly        TIMESTAMPTZ
);

CREATE INDEX idx_van_ban_dhkg_ung_vien_trang_thai ON van_ban_dhkg_ung_vien (trang_thai);

INSERT INTO cau_hinh_he_thong (ma, gia_tri, mo_ta) VALUES
  ('congvan_dong_bo_lan_cuoi', '', 'Thoi diem dong bo CongVan gan nhat (ISO datetime, rong = chua chay lan nao)');
