-- V13: Phien import Excel (SPEC muc 4.3) + lich su xuat bao cao (SPEC muc 4.8).

CREATE TABLE phien_import (
  id                    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  mo_dun                mo_dun_enum NOT NULL,
  ten_file              VARCHAR(500) NOT NULL,
  nguoi_thuc_hien_id    UUID REFERENCES nguoi_dung(id),
  thoi_diem             TIMESTAMPTZ NOT NULL DEFAULT now(),
  tong_so_dong          INTEGER,
  so_dong_thanh_cong    INTEGER,
  so_dong_loi           INTEGER,
  chi_tiet_loi          JSONB,
  trang_thai            VARCHAR(30) NOT NULL DEFAULT 'DANG_XU_LY',
  co_the_rollback       BOOLEAN NOT NULL DEFAULT TRUE,
  da_rollback           BOOLEAN NOT NULL DEFAULT FALSE
);

-- Danh dau cac ban ghi duoc tao ra tu 1 phien import, de rollback duoc (xoa mem).
CREATE TABLE phien_import_ban_ghi (
  phien_import_id   UUID NOT NULL REFERENCES phien_import(id) ON DELETE CASCADE,
  bang              VARCHAR(50) NOT NULL,
  ban_ghi_id        UUID NOT NULL,
  PRIMARY KEY (phien_import_id, bang, ban_ghi_id)
);

CREATE TABLE lich_su_xuat_bao_cao (
  id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  loai_bao_cao      VARCHAR(100) NOT NULL,
  nguoi_xuat_id     UUID REFERENCES nguoi_dung(id),
  thoi_diem         TIMESTAMPTZ NOT NULL DEFAULT now(),
  tham_so           JSONB,
  dinh_dang         VARCHAR(20) NOT NULL
);
