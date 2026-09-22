-- V15: (1) Bo sung dau tieng Viet cho cac ban ghi danh muc da tao o V4
-- (luc dau nhap khong dau). (2) Bo sung day du danh muc "Loai van ban" pham vi
-- VBPL theo Dieu 4 Luat Ban hanh van ban quy pham phap luat 2015 (sua doi 2020),
-- cong them mot so loai van ban hanh chinh thuong gap trong cong tac doi ngoai/HTQT.

UPDATE danh_muc_loai_van_ban SET ten = 'Quyết định', thu_tu = 1 WHERE ma = 'QUYET_DINH' AND pham_vi = 'DHKG';
UPDATE danh_muc_loai_van_ban SET ten = 'Nghị quyết', thu_tu = 2 WHERE ma = 'NGHI_QUYET' AND pham_vi = 'DHKG';
UPDATE danh_muc_loai_van_ban SET ten = 'Quy định', thu_tu = 3 WHERE ma = 'QUY_DINH' AND pham_vi = 'DHKG';
UPDATE danh_muc_loai_van_ban SET ten = 'Quy trình', thu_tu = 4 WHERE ma = 'QUY_TRINH' AND pham_vi = 'DHKG';

UPDATE danh_muc_loai_van_ban SET ten = 'Luật, Bộ luật', thu_tu = 2 WHERE ma = 'LUAT' AND pham_vi = 'VBPL';
UPDATE danh_muc_loai_van_ban SET ten = 'Pháp lệnh của Ủy ban Thường vụ Quốc hội', thu_tu = 4 WHERE ma = 'PHAP_LENH' AND pham_vi = 'VBPL';
UPDATE danh_muc_loai_van_ban SET ten = 'Nghị định của Chính phủ', thu_tu = 8 WHERE ma = 'NGHI_DINH' AND pham_vi = 'VBPL';
UPDATE danh_muc_loai_van_ban SET ten = 'Thông tư của Bộ trưởng, Thủ trưởng cơ quan ngang bộ', thu_tu = 13 WHERE ma = 'THONG_TU' AND pham_vi = 'VBPL';
UPDATE danh_muc_loai_van_ban SET ten = 'Thông tư liên tịch', thu_tu = 14 WHERE ma = 'THONG_TU_LIEN_TICH' AND pham_vi = 'VBPL';
UPDATE danh_muc_loai_van_ban SET ten = 'Công văn', thu_tu = 27 WHERE ma = 'CONG_VAN' AND pham_vi = 'VBPL';

INSERT INTO danh_muc_loai_van_ban (ma, ten, pham_vi, thu_tu) VALUES
  ('HIEN_PHAP', 'Hiến pháp', 'VBPL', 1),
  ('NGHI_QUYET_QH', 'Nghị quyết của Quốc hội', 'VBPL', 3),
  ('NGHI_QUYET_UBTVQH', 'Nghị quyết của Ủy ban Thường vụ Quốc hội', 'VBPL', 5),
  ('LENH_CTN', 'Lệnh của Chủ tịch nước', 'VBPL', 6),
  ('QUYET_DINH_CTN', 'Quyết định của Chủ tịch nước', 'VBPL', 7),
  ('QUYET_DINH_TTG', 'Quyết định của Thủ tướng Chính phủ', 'VBPL', 9),
  ('NGHI_QUYET_HDTP', 'Nghị quyết của Hội đồng Thẩm phán Tòa án nhân dân tối cao', 'VBPL', 10),
  ('THONG_TU_TANDTC', 'Thông tư của Chánh án Tòa án nhân dân tối cao', 'VBPL', 11),
  ('THONG_TU_VKSNDTC', 'Thông tư của Viện trưởng Viện kiểm sát nhân dân tối cao', 'VBPL', 12),
  ('QUYET_DINH_KTNN', 'Quyết định của Tổng Kiểm toán nhà nước', 'VBPL', 15),
  ('NGHI_QUYET_HDND_TINH', 'Nghị quyết của Hội đồng nhân dân cấp tỉnh', 'VBPL', 16),
  ('QUYET_DINH_UBND_TINH', 'Quyết định của Ủy ban nhân dân cấp tỉnh', 'VBPL', 17),
  ('VBQPPL_DVHC_KTDB', 'Văn bản QPPL của chính quyền địa phương ở đơn vị hành chính - kinh tế đặc biệt', 'VBPL', 18),
  ('NGHI_QUYET_HDND_HUYEN', 'Nghị quyết của Hội đồng nhân dân cấp huyện', 'VBPL', 19),
  ('QUYET_DINH_UBND_HUYEN', 'Quyết định của Ủy ban nhân dân cấp huyện', 'VBPL', 20),
  ('NGHI_QUYET_HDND_XA', 'Nghị quyết của Hội đồng nhân dân cấp xã', 'VBPL', 21),
  ('QUYET_DINH_UBND_XA', 'Quyết định của Ủy ban nhân dân cấp xã', 'VBPL', 22),
  ('CHI_THI', 'Chỉ thị', 'VBPL', 23),
  ('CONG_DIEN', 'Công điện', 'VBPL', 24),
  ('THONG_BAO', 'Thông báo', 'VBPL', 25),
  ('KE_HOACH', 'Kế hoạch', 'VBPL', 26)
ON CONFLICT (ma, pham_vi) DO NOTHING;
