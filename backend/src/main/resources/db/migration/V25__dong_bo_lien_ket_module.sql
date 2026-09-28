-- V25: Dong bo/lien ket cac module - theo phan hoi nguoi dung sau khi ra soat:
-- (1) Canh bao Visa sap het han giong het co che da co cua MoU (V12) - dung
--     LAI bang thong_bao chung, chi them bang "da gui" + procedure rieng cho
--     Visa, khong doi schema thong_bao.
-- (2) Gan tu dien vao Doan vao/Doan ra/MoU (module CU, truoc gio la o nhap
--     tu do) - cot moi de KHONG BAT BUOC (nullable), du lieu cu van hop le,
--     nguoi dung co the bo qua khong chon.
--
-- KHONG dong bo Cong van den voi tu dien "Noi gui cong van den": du lieu
-- Cong van den la dong bo TU DONG mot chieu tu he thong CongVan ben ngoai
-- (khong phai nguoi dung tu nhap), ep qua 1 tu dien quan ly thu cong se
-- khong khop voi ban chat "chi doc, mirror lai" cua module nay.

-- ---------- Canh bao Visa sap het han (giong MoU, V12) ----------
CREATE TABLE visa_canh_bao_da_gui (
  id            BIGSERIAL PRIMARY KEY,
  visa_id       UUID NOT NULL REFERENCES visa(id) ON DELETE CASCADE,
  nguong_ngay   INTEGER NOT NULL,
  ngay_gui      DATE NOT NULL DEFAULT CURRENT_DATE,
  UNIQUE (visa_id, nguong_ngay)
);

INSERT INTO cau_hinh_he_thong (ma, gia_tri, mo_ta) VALUES
  ('visa_cac_muc_canh_bao_ngay', '30,14,7,1,0', 'Cac moc ngay con lai (truoc Ngay het han) se tao thong bao Visa sap het han');

CREATE OR REPLACE PROCEDURE sp_quet_visa_sap_het_han() AS $$
DECLARE
  v_moc INTEGER;
  v_moc_list INTEGER[];
  v_row RECORD;
  v_muc_do muc_do_canh_bao_enum;
  v_so_thong_bao_moi INTEGER := 0;
BEGIN
  SELECT string_to_array(gia_tri, ',')::INTEGER[] INTO v_moc_list
  FROM cau_hinh_he_thong WHERE ma = 'visa_cac_muc_canh_bao_ngay';

  IF v_moc_list IS NULL THEN
    v_moc_list := ARRAY[30, 14, 7, 1, 0];
  END IF;

  FOREACH v_moc IN ARRAY v_moc_list LOOP
    FOR v_row IN
      SELECT v.id AS visa_id, v.ho_ten, v.ngay_het_han
      FROM visa v
      WHERE v.deleted_at IS NULL
        AND v.ngay_het_han IS NOT NULL
        AND (v.ngay_het_han - CURRENT_DATE) = v_moc
        AND NOT EXISTS (
          SELECT 1 FROM visa_canh_bao_da_gui c
          WHERE c.visa_id = v.id AND c.nguong_ngay = v_moc
        )
    LOOP
      v_muc_do := CASE
        WHEN v_moc <= 7 THEN 'CRITICAL'
        WHEN v_moc <= 14 THEN 'WARNING'
        ELSE 'INFO'
      END;

      INSERT INTO thong_bao (loai, muc_do, tieu_de, noi_dung, bang_lien_quan, ban_ghi_lien_quan_id, nguoi_nhan_id)
      SELECT
        'VISA_SAP_HET_HAN',
        v_muc_do,
        format('Visa cua %s con %s ngay den han', v_row.ho_ten, v_moc),
        format('Visa cua %s se het han vao %s.', v_row.ho_ten, v_row.ngay_het_han),
        'visa',
        v_row.visa_id,
        u.id
      FROM nguoi_dung u
      JOIN nguoi_dung_vai_tro nv ON nv.nguoi_dung_id = u.id
      JOIN vai_tro vt ON vt.id = nv.vai_tro_id AND vt.ma_vai_tro = 'ADMIN'
      WHERE u.deleted_at IS NULL;

      INSERT INTO visa_canh_bao_da_gui (visa_id, nguong_ngay) VALUES (v_row.visa_id, v_moc);
      v_so_thong_bao_moi := v_so_thong_bao_moi + 1;
    END LOOP;
  END LOOP;

  IF v_so_thong_bao_moi > 0 THEN
    PERFORM pg_notify('thong_bao_moi', v_so_thong_bao_moi::text);
  END IF;
END;
$$ LANGUAGE plpgsql;

-- ---------- Gan tu dien vao Doan vao / Doan ra / MoU (module cu) ----------
ALTER TABLE doan_vao ADD COLUMN muc_dich_tu_dien_id UUID REFERENCES tu_dien(id);
ALTER TABLE doan_ra ADD COLUMN muc_tieu_tu_dien_id UUID REFERENCES tu_dien(id);
ALTER TABLE doan_ra ADD COLUMN nguon_kinh_phi_tu_dien_id UUID REFERENCES tu_dien(id);
ALTER TABLE mou ADD COLUMN hoat_dong_ky_ket_tu_dien_id UUID REFERENCES tu_dien(id);
ALTER TABLE mou ADD COLUMN ngon_ngu_ky_ket_tu_dien_id UUID REFERENCES tu_dien(id);

-- ---------- Mo rong Dashboard voi 3 module moi (Visa/Su kien/Doan dia phuong) ----------
-- v_mou_trang_thai (V7) dung "m.*": 2 cot moi vua them vao mou lam m.* danh
-- them 2 cot NGAY TRUOC vi tri cua ten_doi_tac trong view cu, nen CREATE OR
-- REPLACE VIEW bi Postgres tu choi (chi cho phep them cot vao CUOI, khong cho
-- doi ten/vi tri cot da co). Phai DROP roi CREATE lai. mv_dashboard_tong_quan
-- dang SELECT FROM view nay nen phai drop no truoc, roi tao lai view, roi moi
-- tao lai mv (du lieu dien lai ngay, WITH DATA la mac dinh, khong co khoang trong).
DROP MATERIALIZED VIEW mv_dashboard_tong_quan;
DROP VIEW v_mou_trang_thai;

CREATE VIEW v_mou_trang_thai AS
SELECT
  m.*,
  dt.ten_doi_tac,
  dt.loai_doi_tac,
  dt.quoc_gia AS doi_tac_quoc_gia,
  dt.dia_chi AS doi_tac_dia_chi,
  hdkk.ten AS hoat_dong_ky_ket_ten,
  nnkk.ten AS ngon_ngu_ky_ket_ten,
  CASE
    WHEN m.ngay_het_han IS NULL THEN 'CON_HIEU_LUC'
    WHEN m.ngay_het_han < CURRENT_DATE THEN 'DA_HET_HAN'
    WHEN m.ngay_het_han <= CURRENT_DATE + fn_cau_hinh_int('mou_sap_het_han_nguong_ngay', 90) THEN 'SAP_HET_HAN'
    ELSE 'CON_HIEU_LUC'
  END::trang_thai_mou_enum AS trang_thai,
  (m.ngay_het_han - CURRENT_DATE) AS so_ngay_con_lai
FROM mou m
JOIN doi_tac dt ON dt.id = m.doi_tac_id
LEFT JOIN tu_dien hdkk ON hdkk.id = m.hoat_dong_ky_ket_tu_dien_id
LEFT JOIN tu_dien nnkk ON nnkk.id = m.ngon_ngu_ky_ket_tu_dien_id
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
  (SELECT count(*) FROM visa WHERE deleted_at IS NULL AND ngay_het_han IS NOT NULL AND ngay_het_han >= CURRENT_DATE) AS tong_visa_con_hieu_luc,
  (SELECT count(*) FROM visa WHERE deleted_at IS NULL AND nam = EXTRACT(YEAR FROM CURRENT_DATE)) AS tong_visa_nam_hien_tai,
  (SELECT count(*) FROM su_kien WHERE deleted_at IS NULL AND nam = EXTRACT(YEAR FROM CURRENT_DATE)) AS tong_su_kien_nam_hien_tai,
  (SELECT count(*) FROM doan_dia_phuong WHERE deleted_at IS NULL AND nam = EXTRACT(YEAR FROM CURRENT_DATE)) AS tong_doan_dia_phuong_nam_hien_tai,
  now() AS lam_moi_luc;

CREATE UNIQUE INDEX idx_mv_dashboard_tong_quan_luc ON mv_dashboard_tong_quan (lam_moi_luc);
