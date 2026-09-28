-- V22: Mo rong dinh kem tai lieu (V10/V20) cho "Doan vao" va "Doan ra" - theo
-- yeu cau bo sung nguoi dung: 1 doan vao/doan ra co the dinh kem 1 hoac nhieu
-- file (giay moi, chuong trinh lam viec, quyet dinh cu doan...), giong cach
-- van_ban_dhkg/vbpl_vn/mou/cong_van_den da lam.

ALTER TABLE tai_lieu_dinh_kem DROP CONSTRAINT chk_tai_lieu_bang;
ALTER TABLE tai_lieu_dinh_kem ADD CONSTRAINT chk_tai_lieu_bang
  CHECK (bang IN ('van_ban_dhkg', 'vbpl_vn', 'mou', 'cong_van_den', 'doan_vao', 'doan_ra'));
