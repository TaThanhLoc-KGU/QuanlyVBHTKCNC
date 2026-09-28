-- V23: Bo sung 6 ModuleKey moi cho cac module nghiep vu con thieu (SPEC bo
-- sung muc 5/6 - Hop tac quoc te & Doi tac). Tach rieng migration nay (chi
-- ALTER TYPE ADD VALUE, khong dung gia tri moi trong cung migration) vi
-- Postgres cam dung gia tri ENUM vua them trong CUNG 1 transaction.

ALTER TYPE mo_dun_enum ADD VALUE 'THANH_VIEN_PHU_TRACH';
ALTER TYPE mo_dun_enum ADD VALUE 'MAU_EMAIL';
ALTER TYPE mo_dun_enum ADD VALUE 'DOAN_DIA_PHUONG';
ALTER TYPE mo_dun_enum ADD VALUE 'VISA';
ALTER TYPE mo_dun_enum ADD VALUE 'SU_KIEN';
ALTER TYPE mo_dun_enum ADD VALUE 'DOI_TAC_CA_NHAN';
