-- V8: Module "Doan vao" (SPEC muc 3.5).

CREATE TABLE doan_vao (
  id                          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  ten_doan                    TEXT NOT NULL,
  doi_tac_id                  UUID REFERENCES doi_tac(id),
  thoi_gian_den                DATE NOT NULL,
  thoi_gian_di                 DATE NOT NULL,
  so_luong_nguoi_nuoc_ngoai     INTEGER NOT NULL,
  so_luong_nguoi_viet_nam       INTEGER,
  quoc_tich                    TEXT[] NOT NULL DEFAULT '{}',
  noi_dung_lam_viec             TEXT,

  nam INTEGER GENERATED ALWAYS AS (EXTRACT(YEAR FROM thoi_gian_den)::INTEGER) STORED,
  so_ngay INTEGER GENERATED ALWAYS AS (thoi_gian_di - thoi_gian_den + 1) STORED,

  nguoi_tao_id          UUID REFERENCES nguoi_dung(id),
  ngay_tao              TIMESTAMPTZ NOT NULL DEFAULT now(),
  nguoi_sua_id          UUID REFERENCES nguoi_dung(id),
  ngay_sua              TIMESTAMPTZ,
  deleted_at            TIMESTAMPTZ,
  deleted_by            UUID REFERENCES nguoi_dung(id),

  CONSTRAINT chk_doan_vao_ngay CHECK (thoi_gian_di >= thoi_gian_den)
);

ALTER TABLE doan_vao ADD COLUMN tsv tsvector GENERATED ALWAYS AS (
  to_tsvector('simple', fn_unaccent_lower(
    coalesce(ten_doan, '') || ' ' || coalesce(noi_dung_lam_viec, '') || ' ' || fn_array_text_join(quoc_tich, ' ')
  ))
) STORED;
CREATE INDEX idx_doan_vao_tsv ON doan_vao USING GIN (tsv);
CREATE INDEX idx_doan_vao_deleted_at ON doan_vao (deleted_at);
CREATE INDEX idx_doan_vao_doi_tac_id ON doan_vao (doi_tac_id);
CREATE INDEX idx_doan_vao_nam ON doan_vao (nam);
