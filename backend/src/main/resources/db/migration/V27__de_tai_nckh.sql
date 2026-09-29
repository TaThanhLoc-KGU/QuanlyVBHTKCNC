-- V27: Module "Quan ly De tai du an nghien cuu khoa hoc" (yeu cau bo sung
-- Muc 8). Pham vi giai doan 1 - day du: ho so de tai + vong doi trang thai,
-- hoi dong (de xuat/tham dinh/nghiem thu) voi thanh vien va y kien tung
-- nguoi (workflow theo vai tro qua uy quyen theo tung dong, khong can them
-- vai tro he thong rieng), tai chinh (du toan nam/tam ung/thanh toan/quyet
-- toan), bao cao tien do, dinh kem file (dung lai co che chung), va ho so
-- ly lich khoa hoc + hoat dong ngoai truong cua can bo.
--
-- Quyet dinh thiet ke quan trong: KHONG xay dung mot "workflow engine" rieng
-- voi bang phe duyet da hinh thai cho moi buoc (de xuat/tham dinh/du toan/
-- tam ung/thanh toan/quyet toan/bao cao tien do/nghiem thu) - moi loai duyet
-- co du lieu khac nhau (so tien, ket qua hoi dong, ky bao cao...) nen mo hinh
-- hoa rieng tung bang voi 1 cot trang_thai chung (trang_thai_duyet_enum) don
-- gian hon nhieu ma van du de UI hien dung luong cong viec, thay vi 1 bang
-- "phe_duyet" da hinh thai (polymorphic) rat kho truy van/bao cao ve sau.
--
-- Uy quyen "workflow theo vai tro" cho hoi dong: MOI thanh vien hoi dong (dong
-- hoi_dong_thanh_vien, gan qua nguoi_dung_id) chi duoc tu cap nhat Y KIEN CUA
-- CHINH MINH (kiem tra o tang service: nguoi_dung_id = nguoi dang dang nhap),
-- KHONG can them vai tro he thong rieng (vd "THANH_VIEN_HOI_DONG") - vi tu
-- cach nay gan voi TUNG hoi dong cu the (1 nguoi co the la thanh vien hoi
-- dong nay nhung khong phai hoi dong khac), khong phai 1 quyen toan he thong.
-- Nguoi tong hop ket qua cuoi (ket_qua/diem_trung_binh cua hoi_dong) va cap
-- nhat trang thai de tai la nguoi co quyen Editor module DE_TAI_NCKH (hoac
-- Admin) - giong cach cac module khac dang phan quyen Editor theo module.

-- ---------- Linh vuc nghien cuu (phan cap 3 tang: cap 1/2/3) ----------
-- Rieng bang nay (khong dung chung "tu_dien") vi tu_dien la danh sach PHANG,
-- con day can quan he cha-con that su (vd Khoa hoc tu nhien > Toan hoc >
-- Toan ung dung) de loc/bao cao theo tung cap.
CREATE TABLE linh_vuc_nghien_cuu (
  id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  ma          VARCHAR(50),
  ten         VARCHAR(500) NOT NULL,
  cap         SMALLINT NOT NULL CHECK (cap IN (1, 2, 3)),
  cha_id      UUID REFERENCES linh_vuc_nghien_cuu(id),
  thu_tu      INTEGER NOT NULL DEFAULT 0,
  hoat_dong   BOOLEAN NOT NULL DEFAULT TRUE,
  ngay_tao    TIMESTAMPTZ NOT NULL DEFAULT now(),
  ngay_sua    TIMESTAMPTZ,
  CONSTRAINT chk_linh_vuc_cap_cha CHECK (
    (cap = 1 AND cha_id IS NULL) OR (cap IN (2, 3) AND cha_id IS NOT NULL)
  )
);
CREATE INDEX idx_linh_vuc_nghien_cuu_cha ON linh_vuc_nghien_cuu (cha_id);

-- ---------- De tai (ho so trung tam) ----------
CREATE TYPE trang_thai_de_tai_enum AS ENUM (
  'DE_XUAT',
  'DANG_THAM_DINH_KHOA',
  'DANG_THAM_DINH_CHUYEN_MON',
  'DA_TRUNG_TUYEN',
  'KHONG_TRUNG_TUYEN',
  'DA_KY_HOP_DONG',
  'DANG_THUC_HIEN',
  'CHO_NGHIEM_THU_CO_SO',
  'CHO_NGHIEM_THU_CHINH_THUC',
  'DA_NGHIEM_THU',
  'DA_THANH_LY',
  'BI_HUY'
);

CREATE TABLE de_tai (
  id                        UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  ma_de_tai                 VARCHAR(50),
  ten_de_tai                VARCHAR(1000) NOT NULL,
  chu_nhiem_id              UUID REFERENCES nguoi_dung(id),
  chu_nhiem_ngoai           VARCHAR(255),
  don_vi_thuc_hien          VARCHAR(500),
  don_vi_chu_quan           VARCHAR(500),
  phan_loai_tu_dien_id      UUID REFERENCES tu_dien(id),
  loai_hinh_tu_dien_id      UUID REFERENCES tu_dien(id),
  nguon_kinh_phi_tu_dien_id UUID REFERENCES tu_dien(id),
  linh_vuc_id               UUID REFERENCES linh_vuc_nghien_cuu(id),
  nam_de_xuat               INTEGER,
  thoi_gian_bat_dau         DATE,
  thoi_gian_ket_thuc        DATE,
  kinh_phi_de_xuat          NUMERIC(18, 2),
  kinh_phi_duyet            NUMERIC(18, 2),
  muc_tieu                  TEXT,
  noi_dung                  TEXT,
  san_pham_du_kien          TEXT,
  trang_thai                trang_thai_de_tai_enum NOT NULL DEFAULT 'DE_XUAT',
  muc_xep_loai_tu_dien_id   UUID REFERENCES tu_dien(id),
  ly_do_huy                 TEXT,
  ghi_chu                   TEXT,

  nguoi_tao_id  UUID REFERENCES nguoi_dung(id),
  ngay_tao      TIMESTAMPTZ NOT NULL DEFAULT now(),
  nguoi_sua_id  UUID REFERENCES nguoi_dung(id),
  ngay_sua      TIMESTAMPTZ,
  deleted_at    TIMESTAMPTZ,
  deleted_by    UUID REFERENCES nguoi_dung(id),

  CONSTRAINT chk_de_tai_ngay CHECK (thoi_gian_ket_thuc IS NULL OR thoi_gian_bat_dau IS NULL
    OR thoi_gian_ket_thuc >= thoi_gian_bat_dau)
);
CREATE INDEX idx_de_tai_deleted_at ON de_tai (deleted_at);
CREATE INDEX idx_de_tai_trang_thai ON de_tai (trang_thai);
CREATE INDEX idx_de_tai_chu_nhiem ON de_tai (chu_nhiem_id);
CREATE INDEX idx_de_tai_nam_de_xuat ON de_tai (nam_de_xuat);

-- ---------- Thanh vien tham gia de tai (khac chu nhiem) ----------
CREATE TABLE de_tai_thanh_vien (
  id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  de_tai_id           UUID NOT NULL REFERENCES de_tai(id) ON DELETE CASCADE,
  nguoi_dung_id       UUID REFERENCES nguoi_dung(id),
  ho_ten_ngoai        VARCHAR(255),
  vai_tro_tu_dien_id  UUID REFERENCES tu_dien(id),
  thu_tu              INTEGER NOT NULL DEFAULT 0,
  CONSTRAINT chk_de_tai_thanh_vien_nguoi CHECK (nguoi_dung_id IS NOT NULL OR ho_ten_ngoai IS NOT NULL)
);
CREATE INDEX idx_de_tai_thanh_vien_de_tai ON de_tai_thanh_vien (de_tai_id);

-- ---------- Hoi dong (de xuat/tham dinh chuyen mon/nghiem thu co so/chinh thuc) ----------
CREATE TYPE loai_hoi_dong_enum AS ENUM (
  'HOI_DONG_KHOA_VIEN',
  'TIEU_BAN_CHUYEN_MON',
  'NGHIEM_THU_CO_SO',
  'NGHIEM_THU_CHINH_THUC'
);
CREATE TYPE ket_qua_hoi_dong_enum AS ENUM ('CHUA_CO_KET_QUA', 'DAT', 'DAT_CO_SUA_CHUA', 'KHONG_DAT');

CREATE TABLE hoi_dong (
  id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  de_tai_id         UUID NOT NULL REFERENCES de_tai(id) ON DELETE CASCADE,
  loai              loai_hoi_dong_enum NOT NULL,
  ngay_hop          DATE,
  dia_diem          VARCHAR(500),
  ket_qua           ket_qua_hoi_dong_enum NOT NULL DEFAULT 'CHUA_CO_KET_QUA',
  diem_trung_binh   NUMERIC(4, 2),
  ghi_chu           TEXT,

  nguoi_tao_id  UUID REFERENCES nguoi_dung(id),
  ngay_tao      TIMESTAMPTZ NOT NULL DEFAULT now(),
  nguoi_sua_id  UUID REFERENCES nguoi_dung(id),
  ngay_sua      TIMESTAMPTZ
);
CREATE INDEX idx_hoi_dong_de_tai ON hoi_dong (de_tai_id);

CREATE TABLE hoi_dong_thanh_vien (
  id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  hoi_dong_id         UUID NOT NULL REFERENCES hoi_dong(id) ON DELETE CASCADE,
  nguoi_dung_id       UUID REFERENCES nguoi_dung(id),
  ho_ten_ngoai        VARCHAR(255),
  vai_tro_tu_dien_id  UUID REFERENCES tu_dien(id),
  y_kien              TEXT,
  diem                NUMERIC(4, 2),
  dong_y              BOOLEAN,
  ngay_cho_y_kien     TIMESTAMPTZ,
  CONSTRAINT chk_hoi_dong_thanh_vien_nguoi CHECK (nguoi_dung_id IS NOT NULL OR ho_ten_ngoai IS NOT NULL),
  CONSTRAINT uq_hoi_dong_thanh_vien_nguoi UNIQUE (hoi_dong_id, nguoi_dung_id)
);
CREATE INDEX idx_hoi_dong_thanh_vien_hoi_dong ON hoi_dong_thanh_vien (hoi_dong_id);
CREATE INDEX idx_hoi_dong_thanh_vien_nguoi_dung ON hoi_dong_thanh_vien (nguoi_dung_id);

-- ---------- Tai chinh: du toan nam / tam ung / thanh toan / quyet toan ----------
CREATE TYPE trang_thai_duyet_enum AS ENUM ('CHO_DUYET', 'DA_DUYET', 'TU_CHOI');

CREATE TABLE de_tai_du_toan_nam (
  id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  de_tai_id         UUID NOT NULL REFERENCES de_tai(id) ON DELETE CASCADE,
  nam               INTEGER NOT NULL,
  kinh_phi_de_xuat  NUMERIC(18, 2),
  kinh_phi_duyet    NUMERIC(18, 2),
  da_gui_bo         BOOLEAN NOT NULL DEFAULT FALSE,
  trang_thai        trang_thai_duyet_enum NOT NULL DEFAULT 'CHO_DUYET',
  ghi_chu           TEXT,
  nguoi_duyet_id    UUID REFERENCES nguoi_dung(id),
  ngay_duyet        TIMESTAMPTZ,

  nguoi_tao_id  UUID REFERENCES nguoi_dung(id),
  ngay_tao      TIMESTAMPTZ NOT NULL DEFAULT now(),
  nguoi_sua_id  UUID REFERENCES nguoi_dung(id),
  ngay_sua      TIMESTAMPTZ,
  CONSTRAINT uq_de_tai_du_toan_nam UNIQUE (de_tai_id, nam)
);
CREATE INDEX idx_de_tai_du_toan_nam_de_tai ON de_tai_du_toan_nam (de_tai_id);

CREATE TABLE de_tai_tam_ung (
  id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  de_tai_id       UUID NOT NULL REFERENCES de_tai(id) ON DELETE CASCADE,
  nam             INTEGER,
  so_tien         NUMERIC(18, 2) NOT NULL,
  ly_do           TEXT,
  ngay_de_nghi    DATE NOT NULL DEFAULT CURRENT_DATE,
  trang_thai      trang_thai_duyet_enum NOT NULL DEFAULT 'CHO_DUYET',
  nguoi_duyet_id  UUID REFERENCES nguoi_dung(id),
  ngay_duyet      TIMESTAMPTZ,
  ghi_chu         TEXT,

  nguoi_tao_id  UUID REFERENCES nguoi_dung(id),
  ngay_tao      TIMESTAMPTZ NOT NULL DEFAULT now(),
  nguoi_sua_id  UUID REFERENCES nguoi_dung(id),
  ngay_sua      TIMESTAMPTZ
);
CREATE INDEX idx_de_tai_tam_ung_de_tai ON de_tai_tam_ung (de_tai_id);

CREATE TABLE de_tai_thanh_toan (
  id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  de_tai_id         UUID NOT NULL REFERENCES de_tai(id) ON DELETE CASCADE,
  nam               INTEGER NOT NULL,
  so_tien           NUMERIC(18, 2) NOT NULL,
  ngay_thanh_toan   DATE,
  trang_thai        trang_thai_duyet_enum NOT NULL DEFAULT 'CHO_DUYET',
  nguoi_duyet_id    UUID REFERENCES nguoi_dung(id),
  ngay_duyet        TIMESTAMPTZ,
  ghi_chu           TEXT,

  nguoi_tao_id  UUID REFERENCES nguoi_dung(id),
  ngay_tao      TIMESTAMPTZ NOT NULL DEFAULT now(),
  nguoi_sua_id  UUID REFERENCES nguoi_dung(id),
  ngay_sua      TIMESTAMPTZ
);
CREATE INDEX idx_de_tai_thanh_toan_de_tai ON de_tai_thanh_toan (de_tai_id);

CREATE TABLE de_tai_quyet_toan (
  id                          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  de_tai_id                   UUID NOT NULL UNIQUE REFERENCES de_tai(id) ON DELETE CASCADE,
  tong_kinh_phi_da_cap        NUMERIC(18, 2),
  tong_kinh_phi_da_su_dung    NUMERIC(18, 2),
  ngay_quyet_toan             DATE,
  trang_thai                  trang_thai_duyet_enum NOT NULL DEFAULT 'CHO_DUYET',
  nguoi_duyet_id               UUID REFERENCES nguoi_dung(id),
  ngay_duyet                  TIMESTAMPTZ,
  ghi_chu                     TEXT,

  nguoi_tao_id  UUID REFERENCES nguoi_dung(id),
  ngay_tao      TIMESTAMPTZ NOT NULL DEFAULT now(),
  nguoi_sua_id  UUID REFERENCES nguoi_dung(id),
  ngay_sua      TIMESTAMPTZ
);

-- ---------- Bao cao tien do ----------
CREATE TABLE de_tai_bao_cao_tien_do (
  id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  de_tai_id       UUID NOT NULL REFERENCES de_tai(id) ON DELETE CASCADE,
  ky_bao_cao      VARCHAR(255),
  han_nop         DATE,
  ngay_nop        DATE,
  noi_dung        TEXT,
  trang_thai      trang_thai_duyet_enum NOT NULL DEFAULT 'CHO_DUYET',
  nguoi_duyet_id  UUID REFERENCES nguoi_dung(id),
  ngay_duyet      TIMESTAMPTZ,
  ghi_chu         TEXT,

  nguoi_tao_id  UUID REFERENCES nguoi_dung(id),
  ngay_tao      TIMESTAMPTZ NOT NULL DEFAULT now(),
  nguoi_sua_id  UUID REFERENCES nguoi_dung(id),
  ngay_sua      TIMESTAMPTZ
);
CREATE INDEX idx_de_tai_bao_cao_tien_do_de_tai ON de_tai_bao_cao_tien_do (de_tai_id);

-- ---------- Ho so ly lich khoa hoc + hoat dong ngoai truong cua can bo ----------
CREATE TABLE ly_lich_khoa_hoc (
  id                    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  nguoi_dung_id         UUID NOT NULL UNIQUE REFERENCES nguoi_dung(id) ON DELETE CASCADE,
  hoc_ham                VARCHAR(255),
  hoc_vi                VARCHAR(255),
  chuyen_nganh          VARCHAR(500),
  qua_trinh_cong_tac    TEXT,
  ghi_chu               TEXT,
  nguoi_sua_id  UUID REFERENCES nguoi_dung(id),
  ngay_sua      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TYPE loai_hoat_dong_ngoai_truong_enum AS ENUM ('DE_TAI_DU_AN', 'SACH_GIAO_TRINH');

CREATE TABLE hoat_dong_ngoai_truong (
  id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  nguoi_dung_id       UUID NOT NULL REFERENCES nguoi_dung(id) ON DELETE CASCADE,
  loai                loai_hoat_dong_ngoai_truong_enum NOT NULL,
  ten                 VARCHAR(1000) NOT NULL,
  don_vi_phoi_hop     VARCHAR(500),
  vai_tro             VARCHAR(255),
  thoi_gian_bat_dau   DATE,
  thoi_gian_ket_thuc  DATE,
  ghi_chu             TEXT,

  nguoi_tao_id  UUID REFERENCES nguoi_dung(id),
  ngay_tao      TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_hoat_dong_ngoai_truong_nguoi ON hoat_dong_ngoai_truong (nguoi_dung_id);

-- ---------- Dinh kem file (thuyet minh, hop dong, bao cao, ket qua...) ----------
ALTER TABLE tai_lieu_dinh_kem DROP CONSTRAINT chk_tai_lieu_bang;
ALTER TABLE tai_lieu_dinh_kem ADD CONSTRAINT chk_tai_lieu_bang
  CHECK (bang IN ('van_ban_dhkg', 'vbpl_vn', 'mou', 'cong_van_den', 'doan_vao', 'doan_ra',
                   'doan_dia_phuong', 'visa', 'su_kien', 'de_tai'));

-- ---------- Audit trail cho cac bang nghiep vu chinh ----------
CREATE TRIGGER trg_audit_de_tai AFTER INSERT OR UPDATE OR DELETE ON de_tai
  FOR EACH ROW EXECUTE FUNCTION fn_ghi_lich_su();
CREATE TRIGGER trg_audit_hoi_dong AFTER INSERT OR UPDATE OR DELETE ON hoi_dong
  FOR EACH ROW EXECUTE FUNCTION fn_ghi_lich_su();
CREATE TRIGGER trg_audit_de_tai_du_toan_nam AFTER INSERT OR UPDATE OR DELETE ON de_tai_du_toan_nam
  FOR EACH ROW EXECUTE FUNCTION fn_ghi_lich_su();
CREATE TRIGGER trg_audit_de_tai_tam_ung AFTER INSERT OR UPDATE OR DELETE ON de_tai_tam_ung
  FOR EACH ROW EXECUTE FUNCTION fn_ghi_lich_su();
CREATE TRIGGER trg_audit_de_tai_thanh_toan AFTER INSERT OR UPDATE OR DELETE ON de_tai_thanh_toan
  FOR EACH ROW EXECUTE FUNCTION fn_ghi_lich_su();
CREATE TRIGGER trg_audit_de_tai_quyet_toan AFTER INSERT OR UPDATE OR DELETE ON de_tai_quyet_toan
  FOR EACH ROW EXECUTE FUNCTION fn_ghi_lich_su();
CREATE TRIGGER trg_audit_de_tai_bao_cao_tien_do AFTER INSERT OR UPDATE OR DELETE ON de_tai_bao_cao_tien_do
  FOR EACH ROW EXECUTE FUNCTION fn_ghi_lich_su();

-- ---------- Seed du lieu tu dien co ban ----------
INSERT INTO tu_dien (loai, ten, thu_tu) VALUES
  ('PHAN_LOAI_DE_TAI', 'Cap Truong', 1),
  ('PHAN_LOAI_DE_TAI', 'Cap Tinh', 2),
  ('PHAN_LOAI_DE_TAI', 'Cap Bo', 3),
  ('PHAN_LOAI_DE_TAI', 'Cap Nha nuoc', 4),
  ('LOAI_HINH_NGHIEN_CUU', 'Nghien cuu co ban', 1),
  ('LOAI_HINH_NGHIEN_CUU', 'Nghien cuu ung dung', 2),
  ('LOAI_HINH_NGHIEN_CUU', 'Trien khai thuc nghiem', 3),
  ('VAI_TRO_HOI_DONG', 'Chu tich', 1),
  ('VAI_TRO_HOI_DONG', 'Pho chu tich', 2),
  ('VAI_TRO_HOI_DONG', 'Phan bien 1', 3),
  ('VAI_TRO_HOI_DONG', 'Phan bien 2', 4),
  ('VAI_TRO_HOI_DONG', 'Uy vien', 5),
  ('VAI_TRO_HOI_DONG', 'Uy vien thu ky', 6),
  ('VAI_TRO_THANH_VIEN_DE_TAI', 'Thu ky de tai', 1),
  ('VAI_TRO_THANH_VIEN_DE_TAI', 'Thanh vien chinh', 2),
  ('VAI_TRO_THANH_VIEN_DE_TAI', 'Thanh vien', 3),
  ('MUC_XEP_LOAI_DE_TAI', 'Xuat sac', 1),
  ('MUC_XEP_LOAI_DE_TAI', 'Tot', 2),
  ('MUC_XEP_LOAI_DE_TAI', 'Kha', 3),
  ('MUC_XEP_LOAI_DE_TAI', 'Dat', 4),
  ('MUC_XEP_LOAI_DE_TAI', 'Khong dat', 5),
  ('LOAI_SAN_PHAM_KHOA_HOC', 'Bai bao khoa hoc', 1),
  ('LOAI_SAN_PHAM_KHOA_HOC', 'Sach/Giao trinh', 2),
  ('LOAI_SAN_PHAM_KHOA_HOC', 'Bang sang che/giai phap huu ich', 3),
  ('LOAI_SAN_PHAM_KHOA_HOC', 'San pham ung dung', 4),
  ('LOAI_SAN_PHAM_KHOA_HOC', 'Bao cao khoa hoc/hoi thao', 5);

INSERT INTO linh_vuc_nghien_cuu (ten, cap) VALUES
  ('Khoa hoc tu nhien', 1),
  ('Khoa hoc ky thuat va cong nghe', 1),
  ('Khoa hoc y duoc', 1),
  ('Khoa hoc nong nghiep', 1),
  ('Khoa hoc xa hoi', 1),
  ('Khoa hoc nhan van', 1);
