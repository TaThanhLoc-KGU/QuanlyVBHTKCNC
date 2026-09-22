-- V4: Danh muc "Loai van ban" dung chung cho Van ban DHKG va VBPL VN.
-- Dung bang du lieu (khong dung ENUM cung) vi SPEC muc 3.2/3.3 noi ro danh muc nay
-- "mo rong duoc boi Admin" - ENUM Postgres kho mo rong an toan luc runtime,
-- nen chuyen sang danh muc co the CRUD qua API.

CREATE TYPE pham_vi_van_ban_enum AS ENUM ('DHKG', 'VBPL');
CREATE TYPE tinh_trang_hieu_luc_enum AS ENUM (
  'CON_HIEU_LUC', 'HET_HIEU_LUC_TOAN_BO', 'HET_HIEU_LUC_MOT_PHAN', 'BI_THAY_THE', 'DA_BI_BAI_BO'
);

CREATE TABLE danh_muc_loai_van_ban (
  id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  ma          VARCHAR(100) NOT NULL,
  ten         VARCHAR(255) NOT NULL,
  pham_vi     pham_vi_van_ban_enum NOT NULL,
  thu_tu      INTEGER NOT NULL DEFAULT 0,
  UNIQUE (ma, pham_vi)
);

INSERT INTO danh_muc_loai_van_ban (ma, ten, pham_vi, thu_tu) VALUES
  ('QUYET_DINH', 'Quyet dinh', 'DHKG', 1),
  ('NGHI_QUYET', 'Nghi quyet', 'DHKG', 2),
  ('QUY_DINH', 'Quy dinh', 'DHKG', 3),
  ('QUY_TRINH', 'Quy trinh', 'DHKG', 4),
  ('LUAT', 'Luat', 'VBPL', 1),
  ('PHAP_LENH', 'Phap lenh', 'VBPL', 2),
  ('NGHI_DINH', 'Nghi dinh', 'VBPL', 3),
  ('THONG_TU', 'Thong tu', 'VBPL', 4),
  ('THONG_TU_LIEN_TICH', 'Thong tu lien tich', 'VBPL', 5),
  ('CONG_VAN', 'Cong van', 'VBPL', 6);
