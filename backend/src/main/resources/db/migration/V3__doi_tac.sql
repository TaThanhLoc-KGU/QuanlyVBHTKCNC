-- V3: Module "Doi tac" - danh muc dung chung (SPEC muc 3.1).

CREATE TYPE loai_doi_tac_enum AS ENUM ('TRONG_NUOC', 'NGOAI_NUOC');

CREATE TABLE doi_tac (
  id                    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  ten_doi_tac           TEXT NOT NULL,
  loai_doi_tac          loai_doi_tac_enum NOT NULL,
  quoc_gia              VARCHAR(150),
  dia_chi               TEXT,
  thong_tin_lien_he     TEXT,
  ghi_chu               TEXT,

  nguoi_tao_id          UUID REFERENCES nguoi_dung(id),
  ngay_tao              TIMESTAMPTZ NOT NULL DEFAULT now(),
  nguoi_sua_id          UUID REFERENCES nguoi_dung(id),
  ngay_sua              TIMESTAMPTZ,
  deleted_at            TIMESTAMPTZ,
  deleted_by            UUID REFERENCES nguoi_dung(id),

  CONSTRAINT chk_doi_tac_quoc_gia
    CHECK (loai_doi_tac <> 'NGOAI_NUOC' OR quoc_gia IS NOT NULL)
);

-- Trigram index de goi y doi tac trung/gan giong ten (SPEC muc 3.1, 4.3, 6.4).
CREATE INDEX idx_doi_tac_ten_trgm ON doi_tac USING GIN (fn_unaccent_lower(ten_doi_tac) gin_trgm_ops);

-- Full-text search khong dau tren ten/dia chi/ghi chu.
ALTER TABLE doi_tac ADD COLUMN tsv tsvector GENERATED ALWAYS AS (
  to_tsvector('simple', fn_unaccent_lower(
    coalesce(ten_doi_tac, '') || ' ' || coalesce(dia_chi, '') || ' ' || coalesce(ghi_chu, '')
  ))
) STORED;
CREATE INDEX idx_doi_tac_tsv ON doi_tac USING GIN (tsv);

CREATE INDEX idx_doi_tac_deleted_at ON doi_tac (deleted_at);
