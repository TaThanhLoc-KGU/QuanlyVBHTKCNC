-- V7: Module "MoU" (SPEC muc 3.4). Trang thai phu thuoc CURRENT_DATE nen dung VIEW
-- thuong, khong dung generated column (CURRENT_DATE khong immutable).

CREATE TYPE pham_vi_hop_tac_enum AS ENUM ('TOAN_DIEN', 'THEO_LINH_VUC');
CREATE TYPE trang_thai_mou_enum AS ENUM ('CON_HIEU_LUC', 'SAP_HET_HAN', 'DA_HET_HAN');

CREATE TABLE mou (
  id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  doi_tac_id              UUID NOT NULL REFERENCES doi_tac(id),
  ten_tai_lieu            TEXT,
  ngay_ban_hanh           DATE NOT NULL,
  ngay_het_han            DATE,
  ca_nhan_dau_moi         VARCHAR(255),
  don_vi_thuc_hien        VARCHAR(255),
  pham_vi_hop_tac         pham_vi_hop_tac_enum,
  linh_vuc_hop_tac        TEXT,
  dau_moi_ghi_trong_mou   VARCHAR(255),
  dai_dien_kgu_ky         VARCHAR(255),
  thoi_han_hieu_luc       VARCHAR(255),
  so_cong_van             VARCHAR(150),

  nguoi_tao_id          UUID REFERENCES nguoi_dung(id),
  ngay_tao              TIMESTAMPTZ NOT NULL DEFAULT now(),
  nguoi_sua_id          UUID REFERENCES nguoi_dung(id),
  ngay_sua              TIMESTAMPTZ,
  deleted_at            TIMESTAMPTZ,
  deleted_by            UUID REFERENCES nguoi_dung(id)
);

ALTER TABLE mou ADD COLUMN tsv tsvector GENERATED ALWAYS AS (
  to_tsvector('simple', fn_unaccent_lower(
    coalesce(ten_tai_lieu, '') || ' ' || coalesce(linh_vuc_hop_tac, '') || ' ' || coalesce(so_cong_van, '')
  ))
) STORED;
CREATE INDEX idx_mou_tsv ON mou USING GIN (tsv);
CREATE INDEX idx_mou_deleted_at ON mou (deleted_at);
CREATE INDEX idx_mou_doi_tac_id ON mou (doi_tac_id);
CREATE INDEX idx_mou_ngay_het_han ON mou (ngay_het_han);

-- Ham doc cau hinh nguong "sap het han" tu bang cau_hinh_he_thong (STABLE, khong
-- immutable vi doc du lieu bang - van dung tot trong VIEW).
-- Schema-qualify "public.cau_hinh_he_thong": ham SQL don gian nay co the bi
-- Postgres "inline" khi dung trong VIEW/MATERIALIZED VIEW, luc do search_path
-- dung de resolve ten bang KHONG tu dong bao gom 'public' (cung nguyen nhan
-- nhu fn_unaccent_lower o V1) - neu de ten tran se bao loi "relation ... does
-- not exist" mac du bang van ton tai binh thuong.
CREATE OR REPLACE FUNCTION fn_cau_hinh_int(p_ma VARCHAR, p_mac_dinh INTEGER)
RETURNS INTEGER
LANGUAGE sql
STABLE
AS $$
  SELECT COALESCE((SELECT gia_tri::INTEGER FROM public.cau_hinh_he_thong WHERE ma = p_ma), p_mac_dinh);
$$;

CREATE OR REPLACE VIEW v_mou_trang_thai AS
SELECT
  m.*,
  dt.ten_doi_tac,
  dt.loai_doi_tac,
  dt.quoc_gia AS doi_tac_quoc_gia,
  dt.dia_chi AS doi_tac_dia_chi,
  CASE
    WHEN m.ngay_het_han IS NULL THEN 'CON_HIEU_LUC'
    WHEN m.ngay_het_han < CURRENT_DATE THEN 'DA_HET_HAN'
    WHEN m.ngay_het_han <= CURRENT_DATE + fn_cau_hinh_int('mou_sap_het_han_nguong_ngay', 90) THEN 'SAP_HET_HAN'
    ELSE 'CON_HIEU_LUC'
  END::trang_thai_mou_enum AS trang_thai,
  (m.ngay_het_han - CURRENT_DATE) AS so_ngay_con_lai
FROM mou m
JOIN doi_tac dt ON dt.id = m.doi_tac_id
WHERE m.deleted_at IS NULL;
