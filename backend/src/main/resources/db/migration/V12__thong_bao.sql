-- V12: He thong canh bao MoU sap het han (SPEC muc 4.7).
--
-- Ham PL/pgSQL sp_quet_mou_sap_het_han() duoc thiet ke de goi duoc tu 2 noi:
--   - production: pg_cron  (SELECT cron.schedule('quet-mou-sap-het-han', '0 6 * * *', ...))
--   - local dev / khi pg_cron chua cai duoc: Spring @Scheduled goi lai dung ham nay
--     qua 1 native query (xem vn.edu... MouCanhBaoScheduler) - phuong an du phong
--     dung nhu SPEC muc 10 da neu, khong can 2 bo logic khac nhau.

CREATE TYPE muc_do_canh_bao_enum AS ENUM ('INFO', 'WARNING', 'CRITICAL');

CREATE TABLE thong_bao (
  id                    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  loai                  VARCHAR(50) NOT NULL DEFAULT 'MOU_SAP_HET_HAN',
  muc_do                muc_do_canh_bao_enum NOT NULL,
  tieu_de               VARCHAR(500) NOT NULL,
  noi_dung              TEXT,
  bang_lien_quan        VARCHAR(50),
  ban_ghi_lien_quan_id  UUID,
  nguoi_nhan_id         UUID NOT NULL REFERENCES nguoi_dung(id),
  da_doc_web            BOOLEAN NOT NULL DEFAULT FALSE,
  da_gui_email          BOOLEAN NOT NULL DEFAULT FALSE,
  ngay_tao              TIMESTAMPTZ NOT NULL DEFAULT now(),
  ngay_doc              TIMESTAMPTZ
);

CREATE INDEX idx_thong_bao_nguoi_nhan ON thong_bao (nguoi_nhan_id, da_doc_web);

-- Danh dau da canh bao 1 moc ngay cho 1 MoU, tranh tao lap lai thong bao (SPEC 4.7.1).
CREATE TABLE mou_canh_bao_da_gui (
  id            BIGSERIAL PRIMARY KEY,
  mou_id        UUID NOT NULL REFERENCES mou(id) ON DELETE CASCADE,
  nguong_ngay   INTEGER NOT NULL,
  ngay_gui      DATE NOT NULL DEFAULT CURRENT_DATE,
  UNIQUE (mou_id, nguong_ngay)
);

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

      -- Nguoi nhan: nguoi ghi o "Ca nhan/don vi dau moi" (neu tra duoc ve 1 user cu
      -- the qua email/ten dang nhap trung) + toan bo Admin. Vi cot ca_nhan_dau_moi
      -- hien la text tu do (SPEC 3.4), o day chi chac chan gui duoc cho Admin; lien
      -- ket toi dung 1 user dau moi cu the de backend lam khi co du lieu chuan hoa hon.
      INSERT INTO thong_bao (loai, muc_do, tieu_de, noi_dung, bang_lien_quan, ban_ghi_lien_quan_id, nguoi_nhan_id)
      SELECT
        'MOU_SAP_HET_HAN',
        v_muc_do,
        format('MoU voi %s con %s ngay den han', v_row.ten_doi_tac, v_moc),
        format('MoU (dau moi: %s) se het han vao %s.', COALESCE(v_row.ca_nhan_dau_moi, 'chua ro'), v_row.ngay_het_han),
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

-- Ghi chu trien khai production (SPEC muc 6.4, 10): can cai pg_cron (goi PGDG) +
-- shared_preload_libraries = 'pg_cron' trong postgresql.conf, sau do:
--   SELECT cron.schedule('quet-mou-sap-het-han', '0 6 * * *', $$CALL sp_quet_mou_sap_het_han();$$);
-- O local dev, Spring @Scheduled trong backend goi "CALL sp_quet_mou_sap_het_han()"
-- hang ngay (xem MouCanhBaoScheduler) - khong can pg_cron.
