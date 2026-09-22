-- V17: Du lieu MoU thuc te (SPEC 3.4) cho thay cac truong "dau moi", "don vi
-- thuc hien", "ca nhan dau moi", "dai dien ky", "thoi han hieu luc" thuong la
-- van ban tu do dai (ghi chu nhieu dong), khong phai ten rieng ngan gon -
-- varchar(255) qua chat, doi sang TEXT (khong gioi han) giong linh_vuc_hop_tac.

-- v_mou_trang_thai dung "m.*" nen phu thuoc kieu du lieu cua tat ca cot mou -
-- phai drop truoc khi ALTER TYPE, roi tao lai y nguyen (V7). mv_dashboard_tong_quan
-- (V14) lai SELECT tu v_mou_trang_thai nen cung phai drop/tao lai theo.
DROP MATERIALIZED VIEW mv_dashboard_tong_quan;
DROP VIEW v_mou_trang_thai;

ALTER TABLE mou
  ALTER COLUMN ca_nhan_dau_moi TYPE TEXT,
  ALTER COLUMN don_vi_thuc_hien TYPE TEXT,
  ALTER COLUMN dau_moi_ghi_trong_mou TYPE TEXT,
  ALTER COLUMN dai_dien_kgu_ky TYPE TEXT,
  ALTER COLUMN thoi_han_hieu_luc TYPE TEXT;

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

CREATE MATERIALIZED VIEW mv_dashboard_tong_quan AS
SELECT
  (SELECT count(*) FROM van_ban_dhkg WHERE deleted_at IS NULL AND tinh_trang_hieu_luc = 'CON_HIEU_LUC') AS tong_van_ban_dhkg_hieu_luc,
  (SELECT count(*) FROM vbpl_vn WHERE deleted_at IS NULL AND tinh_trang_hieu_luc = 'CON_HIEU_LUC') AS tong_vbpl_vn_hieu_luc,
  (SELECT count(*) FROM v_mou_trang_thai WHERE trang_thai = 'CON_HIEU_LUC') AS tong_mou_con_hieu_luc,
  (SELECT count(*) FROM v_mou_trang_thai WHERE trang_thai = 'SAP_HET_HAN') AS tong_mou_sap_het_han,
  (SELECT count(*) FROM v_mou_trang_thai WHERE trang_thai = 'DA_HET_HAN') AS tong_mou_da_het_han,
  (SELECT count(*) FROM doan_vao WHERE deleted_at IS NULL AND nam = EXTRACT(YEAR FROM CURRENT_DATE)) AS tong_doan_vao_nam_hien_tai,
  (SELECT coalesce(sum(so_luong_nguoi_nuoc_ngoai), 0) FROM doan_vao WHERE deleted_at IS NULL AND nam = EXTRACT(YEAR FROM CURRENT_DATE)) AS tong_khach_nuoc_ngoai_nam_hien_tai,
  (SELECT count(*) FROM doan_ra WHERE deleted_at IS NULL AND nam = EXTRACT(YEAR FROM CURRENT_DATE)) AS tong_doan_ra_nam_hien_tai,
  (SELECT coalesce(sum(so_luong_doan), 0) FROM doan_ra WHERE deleted_at IS NULL AND nam = EXTRACT(YEAR FROM CURRENT_DATE)) AS tong_luot_can_bo_di_cong_tac_nam_hien_tai,
  now() AS lam_moi_luc;

CREATE UNIQUE INDEX idx_mv_dashboard_tong_quan_luc ON mv_dashboard_tong_quan (lam_moi_luc);
