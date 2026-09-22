-- V9: Module "Doan ra" (SPEC muc 3.6).

CREATE TABLE doan_ra (
  id                    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  doi_tac_id            UUID NOT NULL REFERENCES doi_tac(id),
  thoi_gian_di          DATE NOT NULL,
  thoi_gian_ve          DATE NOT NULL,
  dia_diem_di           VARCHAR(255),
  dia_diem_den          VARCHAR(255),
  so_luong_doan         INTEGER NOT NULL,
  thanh_phan            TEXT,
  quoc_gia_lam_viec      VARCHAR(255) NOT NULL,
  noi_dung_lam_viec      TEXT,

  nam INTEGER GENERATED ALWAYS AS (EXTRACT(YEAR FROM thoi_gian_di)::INTEGER) STORED,
  so_ngay INTEGER GENERATED ALWAYS AS (thoi_gian_ve - thoi_gian_di + 1) STORED,

  nguoi_tao_id          UUID REFERENCES nguoi_dung(id),
  ngay_tao              TIMESTAMPTZ NOT NULL DEFAULT now(),
  nguoi_sua_id          UUID REFERENCES nguoi_dung(id),
  ngay_sua              TIMESTAMPTZ,
  deleted_at            TIMESTAMPTZ,
  deleted_by            UUID REFERENCES nguoi_dung(id),

  CONSTRAINT chk_doan_ra_ngay CHECK (thoi_gian_ve >= thoi_gian_di)
);

ALTER TABLE doan_ra ADD COLUMN tsv tsvector GENERATED ALWAYS AS (
  to_tsvector('simple', fn_unaccent_lower(
    coalesce(thanh_phan, '') || ' ' || coalesce(noi_dung_lam_viec, '') || ' ' || coalesce(quoc_gia_lam_viec, '')
  ))
) STORED;
CREATE INDEX idx_doan_ra_tsv ON doan_ra USING GIN (tsv);
CREATE INDEX idx_doan_ra_deleted_at ON doan_ra (deleted_at);
CREATE INDEX idx_doan_ra_doi_tac_id ON doan_ra (doi_tac_id);
CREATE INDEX idx_doan_ra_nam ON doan_ra (nam);
