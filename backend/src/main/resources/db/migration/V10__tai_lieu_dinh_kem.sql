-- V10: Dinh kem tai lieu dung chung cho Van ban DHKG / VBPL VN / MoU (SPEC muc 4.5).
-- Dung 1 bang chung (polymorphic qua cot "bang") thay vi 3 bang rieng vi cau truc
-- giong nhau hoan toan - giam trung lap, de mo rong cho module khac sau nay.

CREATE TABLE tai_lieu_dinh_kem (
  id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  bang              VARCHAR(50) NOT NULL,
  ban_ghi_id        UUID NOT NULL,
  ten_file          VARCHAR(500) NOT NULL,
  duong_dan         VARCHAR(1000) NOT NULL,
  kich_thuoc_byte   BIGINT,
  loai_mime         VARCHAR(255),
  nguoi_tao_id      UUID REFERENCES nguoi_dung(id),
  ngay_tao          TIMESTAMPTZ NOT NULL DEFAULT now(),

  CONSTRAINT chk_tai_lieu_bang CHECK (bang IN ('van_ban_dhkg', 'vbpl_vn', 'mou'))
);

CREATE INDEX idx_tai_lieu_dinh_kem_lookup ON tai_lieu_dinh_kem (bang, ban_ghi_id);
