-- V26: Chi them cac gia tri ENUM can cho module "De tai du an nghien cuu
-- khoa hoc" (V27) - tach rieng migration nay (giong V23) vi Postgres cam
-- dung gia tri ENUM vua them trong CUNG 1 transaction voi luc them.

ALTER TYPE mo_dun_enum ADD VALUE 'DE_TAI_NCKH';

ALTER TYPE loai_tu_dien_enum ADD VALUE 'PHAN_LOAI_DE_TAI';
ALTER TYPE loai_tu_dien_enum ADD VALUE 'LOAI_HINH_NGHIEN_CUU';
ALTER TYPE loai_tu_dien_enum ADD VALUE 'VAI_TRO_HOI_DONG';
ALTER TYPE loai_tu_dien_enum ADD VALUE 'VAI_TRO_THANH_VIEN_DE_TAI';
ALTER TYPE loai_tu_dien_enum ADD VALUE 'MUC_XEP_LOAI_DE_TAI';
ALTER TYPE loai_tu_dien_enum ADD VALUE 'LOAI_SAN_PHAM_KHOA_HOC';
