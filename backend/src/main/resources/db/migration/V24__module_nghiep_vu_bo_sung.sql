-- V24: 6 module nghiep vu con thieu theo yeu cau bo sung (doi chieu voi bang
-- yeu cau Muc 5 - Hop tac quoc te / Muc 6 - Doi tac). "Su kien" gop chung
-- "Quan ly hoi nghi, hoi thao" (Muc 5) va "Quan ly thong tin su kien" (Muc 6)
-- lam MOT module duy nhat vi ban chat trung nhau hoan toan, phan biet qua
-- loai_su_kien_tu_dien_id (Hoi nghi/Hoi thao/Toa dam/Le ky ket - da seed san
-- trong tu_dien o V21) - tranh xay 2 module gan nhu giong het nhau.

-- ---------- Thanh vien phu trach hop tac quoc te ----------
-- Danh sach nhan su (co the KHONG co tai khoan dang nhap he thong) - danh
-- muc quan ly nhe, khong can audit trail/soft-delete nhu cac module nghiep
-- vu chinh (giong cach lam tu_dien).
CREATE TABLE thanh_vien_phu_trach (
  id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  ho_ten              VARCHAR(255) NOT NULL,
  chuc_vu             VARCHAR(255),
  don_vi              VARCHAR(255),
  email               VARCHAR(255),
  dien_thoai          VARCHAR(50),
  vai_tro_tu_dien_id  UUID REFERENCES tu_dien(id),
  ghi_chu             TEXT,
  hoat_dong           BOOLEAN NOT NULL DEFAULT TRUE,
  ngay_tao            TIMESTAMPTZ NOT NULL DEFAULT now(),
  ngay_sua            TIMESTAMPTZ
);

-- ---------- Mau email ----------
-- ThongBaoEmailScheduler doc mau theo "ma" (vd THONG_BAO_MOU_GOP), thay the
-- {{placeholder}} - neu khong co mau (hoac tat hoat_dong) thi fallback ve
-- noi dung code cung cu de KHONG lam hong tinh nang gui email dang chay.
CREATE TABLE mau_email (
  id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  ma          VARCHAR(100) NOT NULL UNIQUE,
  ten_mau     VARCHAR(255) NOT NULL,
  tieu_de     VARCHAR(500) NOT NULL,
  noi_dung    TEXT NOT NULL,
  mo_ta       TEXT,
  hoat_dong   BOOLEAN NOT NULL DEFAULT TRUE,
  ngay_tao    TIMESTAMPTZ NOT NULL DEFAULT now(),
  ngay_sua    TIMESTAMPTZ
);

INSERT INTO mau_email (ma, ten_mau, tieu_de, noi_dung, mo_ta) VALUES (
  'THONG_BAO_MOU_GOP',
  'Thông báo MoU gộp (mặc định)',
  '[P.HTKHCN] Bạn có {{soLuong}} thông báo MoU cần lưu ý',
  E'Xin chào {{hoTen}},\n\nHệ thống ghi nhận các thông báo sau:\n\n{{danhSach}}\n\nVui lòng đăng nhập hệ thống để xem chi tiết.',
  'Dung khi gui email gop cac thong bao MoU sap het han (ThongBaoEmailScheduler). Placeholder ho tro: {{hoTen}}, {{soLuong}}, {{danhSach}}.'
);

-- ---------- Doan di dia phuong ----------
-- Khac "Doan ra" (di CONG TAC NUOC NGOAI) - day la doan di lam viec TRONG
-- NUOC, nen KHONG bat buoc doi_tac_id (doi tac hien tai chi quan ly to chuc
-- nuoc ngoai/trong nuoc noi chung, di dia phuong co the khong gan doi tac).
CREATE TABLE doan_dia_phuong (
  id                          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  ten_doan                    VARCHAR(500) NOT NULL,
  doi_tac_id                  UUID REFERENCES doi_tac(id),
  thoi_gian_di                DATE NOT NULL,
  thoi_gian_ve                DATE NOT NULL,
  dia_diem                    VARCHAR(500),
  muc_tieu_tu_dien_id         UUID REFERENCES tu_dien(id),
  nguon_kinh_phi_tu_dien_id   UUID REFERENCES tu_dien(id),
  thanh_phan                  TEXT,
  noi_dung_lam_viec           TEXT,
  ghi_chu                     TEXT,

  nam INTEGER GENERATED ALWAYS AS (EXTRACT(YEAR FROM thoi_gian_di)::INTEGER) STORED,

  nguoi_tao_id  UUID REFERENCES nguoi_dung(id),
  ngay_tao      TIMESTAMPTZ NOT NULL DEFAULT now(),
  nguoi_sua_id  UUID REFERENCES nguoi_dung(id),
  ngay_sua      TIMESTAMPTZ,
  deleted_at    TIMESTAMPTZ,
  deleted_by    UUID REFERENCES nguoi_dung(id),

  CONSTRAINT chk_doan_dia_phuong_ngay CHECK (thoi_gian_ve >= thoi_gian_di)
);
CREATE INDEX idx_doan_dia_phuong_deleted_at ON doan_dia_phuong (deleted_at);
CREATE INDEX idx_doan_dia_phuong_nam ON doan_dia_phuong (nam);

-- ---------- Visa (cap moi / gia han) ----------
CREATE TYPE loai_cap_visa_enum AS ENUM ('MOI', 'GIA_HAN');

CREATE TABLE visa (
  id                    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  ho_ten                VARCHAR(255) NOT NULL,
  quoc_tich             VARCHAR(255),
  loai_cap              loai_cap_visa_enum NOT NULL,
  ngay_cap              DATE NOT NULL,
  ngay_het_han          DATE,
  co_quan_cap           VARCHAR(255),
  muc_dich_tu_dien_id   UUID REFERENCES tu_dien(id),
  doan_vao_id           UUID REFERENCES doan_vao(id),
  ghi_chu               TEXT,

  nam INTEGER GENERATED ALWAYS AS (EXTRACT(YEAR FROM ngay_cap)::INTEGER) STORED,

  nguoi_tao_id  UUID REFERENCES nguoi_dung(id),
  ngay_tao      TIMESTAMPTZ NOT NULL DEFAULT now(),
  nguoi_sua_id  UUID REFERENCES nguoi_dung(id),
  ngay_sua      TIMESTAMPTZ,
  deleted_at    TIMESTAMPTZ,
  deleted_by    UUID REFERENCES nguoi_dung(id)
);
CREATE INDEX idx_visa_deleted_at ON visa (deleted_at);
CREATE INDEX idx_visa_nam ON visa (nam);

-- ---------- Su kien (gop Hoi nghi/Hoi thao + Thong tin su kien) ----------
CREATE TABLE su_kien (
  id                        UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  ten_su_kien               VARCHAR(500) NOT NULL,
  loai_su_kien_tu_dien_id   UUID REFERENCES tu_dien(id),
  linh_vuc_tu_dien_id       UUID REFERENCES tu_dien(id),
  thoi_gian_bat_dau         DATE NOT NULL,
  thoi_gian_ket_thuc        DATE,
  dia_diem                  VARCHAR(500),
  don_vi_to_chuc            VARCHAR(500),
  so_luong_tham_gia         INTEGER,
  noi_dung                  TEXT,
  ghi_chu                   TEXT,

  nam INTEGER GENERATED ALWAYS AS (EXTRACT(YEAR FROM thoi_gian_bat_dau)::INTEGER) STORED,

  nguoi_tao_id  UUID REFERENCES nguoi_dung(id),
  ngay_tao      TIMESTAMPTZ NOT NULL DEFAULT now(),
  nguoi_sua_id  UUID REFERENCES nguoi_dung(id),
  ngay_sua      TIMESTAMPTZ,
  deleted_at    TIMESTAMPTZ,
  deleted_by    UUID REFERENCES nguoi_dung(id)
);
CREATE INDEX idx_su_kien_deleted_at ON su_kien (deleted_at);
CREATE INDEX idx_su_kien_nam ON su_kien (nam);

-- ---------- Doi tac ca nhan ----------
-- Khac "Tu dien" (khong phai gia tri danh muc phang) - la 1 ca nhan lien he
-- co the gan voi 1 doi tac to chuc (khong bat buoc).
CREATE TABLE doi_tac_ca_nhan (
  id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  ho_ten        VARCHAR(255) NOT NULL,
  chuc_vu       VARCHAR(255),
  doi_tac_id    UUID REFERENCES doi_tac(id),
  email         VARCHAR(255),
  dien_thoai    VARCHAR(50),
  ghi_chu       TEXT,
  hoat_dong     BOOLEAN NOT NULL DEFAULT TRUE,
  nguoi_tao_id  UUID REFERENCES nguoi_dung(id),
  ngay_tao      TIMESTAMPTZ NOT NULL DEFAULT now(),
  nguoi_sua_id  UUID REFERENCES nguoi_dung(id),
  ngay_sua      TIMESTAMPTZ
);
CREATE INDEX idx_doi_tac_ca_nhan_doi_tac_id ON doi_tac_ca_nhan (doi_tac_id);

-- ---------- Tu dien "Loai doi tac" gan vao Doi tac ----------
-- Cot rieng, KHONG thay the cot loai_doi_tac (Trong nuoc/Ngoai nuoc) hien co
-- dang dung cho logic chk_doi_tac_quoc_gia - day la phan loai BO SUNG (Truong
-- dai hoc/Vien nghien cuu/Doanh nghiep...) tu tu_dien, khong bat buoc.
ALTER TABLE doi_tac ADD COLUMN loai_tu_dien_id UUID REFERENCES tu_dien(id);

-- ---------- Dinh kem file cho 3 module nghiep vu moi ----------
ALTER TABLE tai_lieu_dinh_kem DROP CONSTRAINT chk_tai_lieu_bang;
ALTER TABLE tai_lieu_dinh_kem ADD CONSTRAINT chk_tai_lieu_bang
  CHECK (bang IN ('van_ban_dhkg', 'vbpl_vn', 'mou', 'cong_van_den', 'doan_vao', 'doan_ra',
                   'doan_dia_phuong', 'visa', 'su_kien'));

-- ---------- Audit trail cho cac module nghiep vu chinh (khong ap dung cho
-- thanh_vien_phu_trach/mau_email/doi_tac_ca_nhan - danh muc quan ly nhe,
-- giong tu_dien, khong can lich su chi tiet) ----------
CREATE TRIGGER trg_audit_doan_dia_phuong AFTER INSERT OR UPDATE OR DELETE ON doan_dia_phuong
  FOR EACH ROW EXECUTE FUNCTION fn_ghi_lich_su();
CREATE TRIGGER trg_audit_visa AFTER INSERT OR UPDATE OR DELETE ON visa
  FOR EACH ROW EXECUTE FUNCTION fn_ghi_lich_su();
CREATE TRIGGER trg_audit_su_kien AFTER INSERT OR UPDATE OR DELETE ON su_kien
  FOR EACH ROW EXECUTE FUNCTION fn_ghi_lich_su();
