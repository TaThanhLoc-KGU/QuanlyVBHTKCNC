-- V2: Tai khoan, vai tro, va phan quyen theo module (SPEC muc 2, 4.1).
-- Vai tro dung bang du lieu (khong dung ENUM) vi Admin duoc tao vai tro tuy chinh.

CREATE TYPE trang_thai_tai_khoan_enum AS ENUM ('HOAT_DONG', 'TAM_KHOA');

CREATE TYPE mo_dun_enum AS ENUM (
  'DOI_TAC', 'VAN_BAN_DHKG', 'VBPL_VN', 'MOU', 'DOAN_VAO', 'DOAN_RA'
);

CREATE TABLE nguoi_dung (
  id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  ten_dang_nhap           VARCHAR(100) NOT NULL UNIQUE,
  email                   VARCHAR(255) NOT NULL UNIQUE,
  mat_khau_hash           VARCHAR(255) NOT NULL,
  ho_ten                  VARCHAR(255) NOT NULL,
  trang_thai              trang_thai_tai_khoan_enum NOT NULL DEFAULT 'HOAT_DONG',
  so_lan_dang_nhap_sai    INTEGER NOT NULL DEFAULT 0,
  khoa_den                TIMESTAMPTZ,
  phai_doi_mat_khau       BOOLEAN NOT NULL DEFAULT TRUE,
  ngay_tao                TIMESTAMPTZ NOT NULL DEFAULT now(),
  ngay_sua                TIMESTAMPTZ,
  deleted_at              TIMESTAMPTZ
);

CREATE TABLE vai_tro (
  id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  ma_vai_tro    VARCHAR(50) NOT NULL UNIQUE,
  ten_vai_tro   VARCHAR(255) NOT NULL,
  he_thong      BOOLEAN NOT NULL DEFAULT FALSE, -- true = ADMIN/EDITOR/VIEWER goc, khong cho xoa
  ngay_tao      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE nguoi_dung_vai_tro (
  nguoi_dung_id UUID NOT NULL REFERENCES nguoi_dung(id) ON DELETE CASCADE,
  vai_tro_id    UUID NOT NULL REFERENCES vai_tro(id) ON DELETE CASCADE,
  PRIMARY KEY (nguoi_dung_id, vai_tro_id)
);

-- Cho Editor: gioi han duoc sua du lieu o module nao (Admin/Viewer khong can dong nay).
CREATE TABLE bien_tap_mo_dun (
  nguoi_dung_id UUID NOT NULL REFERENCES nguoi_dung(id) ON DELETE CASCADE,
  mo_dun        mo_dun_enum NOT NULL,
  PRIMARY KEY (nguoi_dung_id, mo_dun)
);

INSERT INTO vai_tro (ma_vai_tro, ten_vai_tro, he_thong) VALUES
  ('ADMIN', 'Quan tri vien', TRUE),
  ('EDITOR', 'Bien tap vien', TRUE),
  ('VIEWER', 'Nguoi xem', TRUE);

CREATE TABLE cau_hinh_he_thong (
  ma          VARCHAR(100) PRIMARY KEY,
  gia_tri     TEXT NOT NULL,
  mo_ta       TEXT,
  ngay_sua    TIMESTAMPTZ NOT NULL DEFAULT now()
);

INSERT INTO cau_hinh_he_thong (ma, gia_tri, mo_ta) VALUES
  ('mou_sap_het_han_nguong_ngay', '90', 'So ngay con lai de MoU duoc tinh la "Sap het han" tren Dashboard/view trang thai'),
  ('mou_cac_muc_canh_bao_ngay', '90,60,30,14,7,1,0', 'Cac moc ngay con lai se tao thong bao MoU sap het han (Muc 4.7.1)');
