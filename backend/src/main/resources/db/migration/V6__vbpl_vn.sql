-- V6: Module "VBPL VN" (SPEC muc 3.3) - cau truc giong Van ban DHKG, them truong
-- an "Ngay doi chieu gan nhat" (cap nhat khi can bo xac nhan da ra soat tren vbpl.vn).

CREATE TABLE vbpl_vn (
  id                    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  so_hieu                VARCHAR(150) NOT NULL UNIQUE,
  ten_van_ban             TEXT NOT NULL,
  loai_van_ban_id         UUID NOT NULL REFERENCES danh_muc_loai_van_ban(id),
  ngay_ban_hanh           DATE NOT NULL,
  ngay_hieu_luc           DATE,
  tinh_trang_hieu_luc     tinh_trang_hieu_luc_enum NOT NULL,
  co_quan_ban_hanh        VARCHAR(255) NOT NULL,
  ghi_chu                 TEXT,
  noi_dung_chinh          TEXT,
  ngay_doi_chieu_gan_nhat DATE,

  nguoi_tao_id          UUID REFERENCES nguoi_dung(id),
  ngay_tao              TIMESTAMPTZ NOT NULL DEFAULT now(),
  nguoi_sua_id          UUID REFERENCES nguoi_dung(id),
  ngay_sua              TIMESTAMPTZ,
  deleted_at            TIMESTAMPTZ,
  deleted_by            UUID REFERENCES nguoi_dung(id)
);

CREATE OR REPLACE FUNCTION fn_kiem_tra_pham_vi_vbpl() RETURNS trigger AS $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM danh_muc_loai_van_ban d WHERE d.id = NEW.loai_van_ban_id AND d.pham_vi = 'VBPL'
  ) THEN
    RAISE EXCEPTION 'loai_van_ban_id % khong thuoc pham vi VBPL', NEW.loai_van_ban_id;
  END IF;
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_vbpl_vn_kiem_tra_pham_vi
  BEFORE INSERT OR UPDATE OF loai_van_ban_id ON vbpl_vn
  FOR EACH ROW EXECUTE FUNCTION fn_kiem_tra_pham_vi_vbpl();

ALTER TABLE vbpl_vn ADD COLUMN tsv tsvector GENERATED ALWAYS AS (
  to_tsvector('simple', fn_unaccent_lower(
    coalesce(so_hieu, '') || ' ' || coalesce(ten_van_ban, '') || ' ' || coalesce(noi_dung_chinh, '')
  ))
) STORED;
CREATE INDEX idx_vbpl_vn_tsv ON vbpl_vn USING GIN (tsv);
CREATE INDEX idx_vbpl_vn_deleted_at ON vbpl_vn (deleted_at);
