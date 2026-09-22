-- V5: Module "Van ban DHKG" (SPEC muc 3.2).
-- STT khong luu (chi la so thu tu hien thi tren UI), khong can persist.

CREATE TABLE van_ban_dhkg (
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

  nguoi_tao_id          UUID REFERENCES nguoi_dung(id),
  ngay_tao              TIMESTAMPTZ NOT NULL DEFAULT now(),
  nguoi_sua_id          UUID REFERENCES nguoi_dung(id),
  ngay_sua              TIMESTAMPTZ,
  deleted_at            TIMESTAMPTZ,
  deleted_by            UUID REFERENCES nguoi_dung(id)
);

-- Postgres CHECK khong cho subquery, nen dung trigger de bao dam loai_van_ban_id
-- luon thuoc pham_vi = 'DHKG'.
CREATE OR REPLACE FUNCTION fn_kiem_tra_pham_vi_dhkg() RETURNS trigger AS $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM danh_muc_loai_van_ban d WHERE d.id = NEW.loai_van_ban_id AND d.pham_vi = 'DHKG'
  ) THEN
    RAISE EXCEPTION 'loai_van_ban_id % khong thuoc pham vi DHKG', NEW.loai_van_ban_id;
  END IF;
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_van_ban_dhkg_kiem_tra_pham_vi
  BEFORE INSERT OR UPDATE OF loai_van_ban_id ON van_ban_dhkg
  FOR EACH ROW EXECUTE FUNCTION fn_kiem_tra_pham_vi_dhkg();

ALTER TABLE van_ban_dhkg ADD COLUMN tsv tsvector GENERATED ALWAYS AS (
  to_tsvector('simple', fn_unaccent_lower(
    coalesce(so_hieu, '') || ' ' || coalesce(ten_van_ban, '') || ' ' || coalesce(noi_dung_chinh, '')
  ))
) STORED;
CREATE INDEX idx_van_ban_dhkg_tsv ON van_ban_dhkg USING GIN (tsv);
CREATE INDEX idx_van_ban_dhkg_deleted_at ON van_ban_dhkg (deleted_at);
