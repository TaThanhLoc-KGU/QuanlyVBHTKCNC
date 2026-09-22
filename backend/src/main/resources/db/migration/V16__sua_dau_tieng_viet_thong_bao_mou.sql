-- V16: Bo sung dau tieng Viet cho noi dung thong bao MoU sap het han (sp_quet_mou_sap_het_han),
-- vi noi dung nay duoc sinh trong PL/pgSQL nen khong nam trong pham vi sua o frontend.

CREATE OR REPLACE PROCEDURE sp_quet_mou_sap_het_han() AS $$
DECLARE
  v_moc INTEGER;
  v_moc_list INTEGER[];
  v_row RECORD;
  v_muc_do muc_do_canh_bao_enum;
  v_so_thong_bao_moi INTEGER := 0;
BEGIN
  SELECT string_to_array(gia_tri, ',')::INTEGER[] INTO v_moc_list
  FROM cau_hinh_he_thong WHERE ma = 'mou_cac_muc_canh_bao_ngay';

  IF v_moc_list IS NULL THEN
    v_moc_list := ARRAY[90, 60, 30, 14, 7, 1, 0];
  END IF;

  FOREACH v_moc IN ARRAY v_moc_list LOOP
    FOR v_row IN
      SELECT m.id AS mou_id, m.ca_nhan_dau_moi, m.ngay_het_han, dt.ten_doi_tac
      FROM mou m
      JOIN doi_tac dt ON dt.id = m.doi_tac_id
      WHERE m.deleted_at IS NULL
        AND m.ngay_het_han IS NOT NULL
        AND (m.ngay_het_han - CURRENT_DATE) = v_moc
        AND NOT EXISTS (
          SELECT 1 FROM mou_canh_bao_da_gui c
          WHERE c.mou_id = m.id AND c.nguong_ngay = v_moc
        )
    LOOP
      v_muc_do := CASE
        WHEN v_moc <= 7 THEN 'CRITICAL'
        WHEN v_moc <= 30 THEN 'WARNING'
        ELSE 'INFO'
      END;

      INSERT INTO thong_bao (loai, muc_do, tieu_de, noi_dung, bang_lien_quan, ban_ghi_lien_quan_id, nguoi_nhan_id)
      SELECT
        'MOU_SAP_HET_HAN',
        v_muc_do,
        format('MoU với %s còn %s ngày đến hạn', v_row.ten_doi_tac, v_moc),
        format('MoU (đầu mối: %s) sẽ hết hạn vào %s.', COALESCE(v_row.ca_nhan_dau_moi, 'chưa rõ'), v_row.ngay_het_han),
        'mou',
        v_row.mou_id,
        u.id
      FROM nguoi_dung u
      JOIN nguoi_dung_vai_tro nv ON nv.nguoi_dung_id = u.id
      JOIN vai_tro vt ON vt.id = nv.vai_tro_id AND vt.ma_vai_tro = 'ADMIN'
      WHERE u.deleted_at IS NULL;

      INSERT INTO mou_canh_bao_da_gui (mou_id, nguong_ngay) VALUES (v_row.mou_id, v_moc);
      v_so_thong_bao_moi := v_so_thong_bao_moi + 1;
    END LOOP;
  END LOOP;

  IF v_so_thong_bao_moi > 0 THEN
    PERFORM pg_notify('thong_bao_moi', v_so_thong_bao_moi::text);
  END IF;
END;
$$ LANGUAGE plpgsql;

-- Sua lai cac thong bao da sinh truoc do (khong dau) sang co dau, chi cho dung loai
-- MOU_SAP_HET_HAN sinh boi thu tuc tren.
UPDATE thong_bao
SET
  tieu_de = regexp_replace(
    regexp_replace(tieu_de, '^MoU voi (.*) con (\d+) ngay den han$', 'MoU với \1 còn \2 ngày đến hạn'),
    '^MoU voi (.*) con 0 ngay den han$', 'MoU với \1 còn 0 ngày đến hạn'
  ),
  noi_dung = regexp_replace(noi_dung, '^MoU \(dau moi: (.*)\) se het han vao (.*)\.$', 'MoU (đầu mối: \1) sẽ hết hạn vào \2.')
WHERE loai = 'MOU_SAP_HET_HAN'
  AND tieu_de LIKE 'MoU voi %con%ngay den han';
