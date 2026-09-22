-- V14: Materialized view cho Dashboard (SPEC muc 4.6) - tranh JOIN/aggregate nang
-- moi lan tai trang. Duoc REFRESH dinh ky (pg_cron o production, @Scheduled o
-- local dev - cung co che du phong nhu sp_quet_mou_sap_het_han o V12).

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

-- Bat buoc co UNIQUE index tren cot bat ky de dung duoc REFRESH ... CONCURRENTLY.
CREATE UNIQUE INDEX idx_mv_dashboard_tong_quan_luc ON mv_dashboard_tong_quan (lam_moi_luc);

-- Bieu do "So luong MoU den han theo thang trong 12 thang toi" (SPEC muc 4.6).
CREATE MATERIALIZED VIEW mv_mou_den_han_theo_thang AS
SELECT
  date_trunc('month', d.thang)::date AS thang,
  count(m.id) AS so_luong_mou_den_han
FROM generate_series(
  date_trunc('month', CURRENT_DATE),
  date_trunc('month', CURRENT_DATE) + INTERVAL '11 months',
  INTERVAL '1 month'
) AS d(thang)
LEFT JOIN mou m
  ON m.deleted_at IS NULL
  AND date_trunc('month', m.ngay_het_han) = date_trunc('month', d.thang)
GROUP BY date_trunc('month', d.thang)
ORDER BY 1;

CREATE UNIQUE INDEX idx_mv_mou_den_han_theo_thang ON mv_mou_den_han_theo_thang (thang);
