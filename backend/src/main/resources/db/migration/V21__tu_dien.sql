-- V21: He thong "Tu dien" (danh muc gia tri nho) dung CHUNG cho nhieu loai
-- du lieu tham chieu (muc tieu doan ra, mau cong van, nguon kinh phi, quoc
-- gia, tien te, v.v. - theo yeu cau bo sung nguoi dung ngoai SPEC ban dau).
-- Tong quat hoa cach lam cua danh_muc_loai_van_ban (V4): 1 bang chung, cot
-- "loai" (ENUM co dinh - so luong loai tu dien khong doi thuong xuyen) phan
-- biet nhom, con GIA TRI trong tung nhom la du lieu CRUD tu do qua API,
-- khong phai ENUM cung.

ALTER TYPE mo_dun_enum ADD VALUE 'TU_DIEN';

CREATE TYPE loai_tu_dien_enum AS ENUM (
  'MUC_TIEU_DOAN_RA',
  'MAU_CONG_VAN_QUYET_DINH',
  'MUC_DICH_DEN',
  'NGUON_KINH_PHI',
  'NOI_GUI_CONG_VAN_DEN',
  'NOI_GUI_CONG_VAN_DEN_BO_SUNG',
  'QUOC_GIA',
  'TIEN_TE',
  'HOAT_DONG_KY_KET',
  'NGON_NGU_KY_KET',
  'VAI_TRO_THANH_VIEN',
  'LOAI_DOI_TAC',
  'LOAI_SU_KIEN',
  'LINH_VUC_HOAT_DONG'
);

CREATE TABLE tu_dien (
  id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  loai        loai_tu_dien_enum NOT NULL,
  ma          VARCHAR(100),
  ten         VARCHAR(500) NOT NULL,
  mo_ta       TEXT,
  thu_tu      INTEGER NOT NULL DEFAULT 0,
  hoat_dong   BOOLEAN NOT NULL DEFAULT TRUE,
  ngay_tao    TIMESTAMPTZ NOT NULL DEFAULT now(),
  ngay_sua    TIMESTAMPTZ,
  UNIQUE (loai, ten)
);

CREATE INDEX idx_tu_dien_loai ON tu_dien (loai, thu_tu);

-- Seed du lieu mau ban dau de tinh nang khong trong rong - Admin bo sung/sua
-- them qua giao dien Tu dien.
INSERT INTO tu_dien (loai, ten, thu_tu) VALUES
  ('MUC_TIEU_DOAN_RA', 'Hội thảo, hội nghị khoa học', 1),
  ('MUC_TIEU_DOAN_RA', 'Trao đổi hợp tác, ký kết', 2),
  ('MUC_TIEU_DOAN_RA', 'Bồi dưỡng, tập huấn', 3),
  ('MUC_TIEU_DOAN_RA', 'Khảo sát, học tập kinh nghiệm', 4),

  ('MAU_CONG_VAN_QUYET_DINH', 'Công văn cử cán bộ đi công tác', 1),
  ('MAU_CONG_VAN_QUYET_DINH', 'Quyết định cử cán bộ đi công tác', 2),
  ('MAU_CONG_VAN_QUYET_DINH', 'Công văn mời đoàn khách', 3),

  ('MUC_DICH_DEN', 'Hợp tác nghiên cứu khoa học', 1),
  ('MUC_DICH_DEN', 'Trao đổi giảng viên, sinh viên', 2),
  ('MUC_DICH_DEN', 'Tham dự hội nghị, hội thảo', 3),
  ('MUC_DICH_DEN', 'Ký kết thỏa thuận hợp tác', 4),

  ('NGUON_KINH_PHI', 'Ngân sách nhà trường', 1),
  ('NGUON_KINH_PHI', 'Đối tác tài trợ', 2),
  ('NGUON_KINH_PHI', 'Cá nhân tự túc', 3),
  ('NGUON_KINH_PHI', 'Ngân sách nhà nước (đề án, dự án)', 4),

  ('NOI_GUI_CONG_VAN_DEN', 'Bộ Giáo dục và Đào tạo', 1),
  ('NOI_GUI_CONG_VAN_DEN', 'Sở Khoa học và Công nghệ', 2),
  ('NOI_GUI_CONG_VAN_DEN', 'Đại sứ quán, Lãnh sự quán', 3),

  ('NOI_GUI_CONG_VAN_DEN_BO_SUNG', 'Tổ chức phi chính phủ (NGO)', 1),
  ('NOI_GUI_CONG_VAN_DEN_BO_SUNG', 'Trường đối tác nước ngoài', 2),

  ('QUOC_GIA', 'Việt Nam', 1),
  ('QUOC_GIA', 'Hoa Kỳ', 2),
  ('QUOC_GIA', 'Nhật Bản', 3),
  ('QUOC_GIA', 'Hàn Quốc', 4),
  ('QUOC_GIA', 'Trung Quốc', 5),
  ('QUOC_GIA', 'Thái Lan', 6),
  ('QUOC_GIA', 'Indonesia', 7),
  ('QUOC_GIA', 'Singapore', 8),
  ('QUOC_GIA', 'Malaysia', 9),
  ('QUOC_GIA', 'Úc', 10),

  ('TIEN_TE', 'VND', 1),
  ('TIEN_TE', 'USD', 2),
  ('TIEN_TE', 'EUR', 3),

  ('HOAT_DONG_KY_KET', 'Ký kết MoU/MoA', 1),
  ('HOAT_DONG_KY_KET', 'Gia hạn thỏa thuận', 2),
  ('HOAT_DONG_KY_KET', 'Chấm dứt thỏa thuận', 3),

  ('NGON_NGU_KY_KET', 'Tiếng Việt', 1),
  ('NGON_NGU_KY_KET', 'Tiếng Anh', 2),
  ('NGON_NGU_KY_KET', 'Song ngữ Việt - Anh', 3),

  ('VAI_TRO_THANH_VIEN', 'Trưởng đoàn', 1),
  ('VAI_TRO_THANH_VIEN', 'Thành viên', 2),
  ('VAI_TRO_THANH_VIEN', 'Phiên dịch', 3),

  ('LOAI_DOI_TAC', 'Trường đại học', 1),
  ('LOAI_DOI_TAC', 'Viện nghiên cứu', 2),
  ('LOAI_DOI_TAC', 'Doanh nghiệp', 3),
  ('LOAI_DOI_TAC', 'Tổ chức chính phủ', 4),
  ('LOAI_DOI_TAC', 'Tổ chức phi chính phủ', 5),

  ('LOAI_SU_KIEN', 'Hội nghị', 1),
  ('LOAI_SU_KIEN', 'Hội thảo', 2),
  ('LOAI_SU_KIEN', 'Tọa đàm', 3),
  ('LOAI_SU_KIEN', 'Lễ ký kết', 4),

  ('LINH_VUC_HOAT_DONG', 'Khoa học công nghệ', 1),
  ('LINH_VUC_HOAT_DONG', 'Giáo dục đào tạo', 2),
  ('LINH_VUC_HOAT_DONG', 'Văn hóa - xã hội', 3),
  ('LINH_VUC_HOAT_DONG', 'Nông nghiệp', 4);
