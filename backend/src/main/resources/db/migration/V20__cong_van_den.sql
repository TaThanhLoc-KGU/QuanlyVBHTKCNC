-- V20: Dong bo (chi keo ve, mot chieu) "Cong van den theo don vi" tu he thong
-- CongVan cua truong (qlvb.vnkgu.edu.vn/api/v1/vanban-den - API_VANBAN_DEN.md).
-- Tao MODULE RIENG (khong dung chung Van ban DHKG) vi ban chat khac nhau: day
-- la thu tu don vi khac gui den can xu ly (co han xu ly, trang thai xu ly cua
-- don vi, co quan gui), khong phai van ban do truong ban hanh nhu Van ban DHKG.
-- Vi day la du lieu phan anh dung trang thai xu ly ben CongVan (co the doi
-- theo thoi gian), dong bo la UPSERT truc tiep - khong can buoc "duyet ung
-- vien" nhu Van ban DHKG/VBPL VN (khong co "muc tieu" schema nao khac de nhan vao).

ALTER TYPE mo_dun_enum ADD VALUE 'CONG_VAN_DEN';

ALTER TABLE tai_lieu_dinh_kem DROP CONSTRAINT chk_tai_lieu_bang;
ALTER TABLE tai_lieu_dinh_kem ADD CONSTRAINT chk_tai_lieu_bang
  CHECK (bang IN ('van_ban_dhkg', 'vbpl_vn', 'mou', 'cong_van_den'));

CREATE TABLE cong_van_den (
  id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  congvan_id          BIGINT NOT NULL UNIQUE,
  so_van_ban          VARCHAR(150),
  so_den              VARCHAR(100),
  ngay_den            DATE,
  ngay_ban_hanh       DATE,
  trich_yeu           TEXT NOT NULL,
  co_quan_ban_hanh    VARCHAR(500),
  loai_van_ban        VARCHAR(255),
  nguoi_ky            VARCHAR(255),
  don_vi_xu_ly_chinh  VARCHAR(255),
  han_xu_ly           DATE,
  ngay_hoan_thanh     DATE,
  trang_thai          VARCHAR(50),
  trang_thai_text     VARCHAR(255),
  ghi_chu             TEXT,
  co_file             BOOLEAN NOT NULL DEFAULT FALSE,
  ngay_dong_bo        TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_cong_van_den_ngay_den ON cong_van_den (ngay_den DESC);
CREATE INDEX idx_cong_van_den_trang_thai ON cong_van_den (trang_thai);

INSERT INTO cau_hinh_he_thong (ma, gia_tri, mo_ta) VALUES
  ('congvan_den_dong_bo_lan_cuoi', '', 'Thoi diem dong bo Cong van den gan nhat (ISO datetime, rong = chua chay lan nao)');
