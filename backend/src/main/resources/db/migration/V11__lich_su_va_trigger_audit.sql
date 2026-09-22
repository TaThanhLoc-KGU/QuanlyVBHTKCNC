-- V11: Audit trail bang trigger AFTER INSERT/UPDATE/DELETE (SPEC muc 4.2, 6.4).
--
-- Thiet ke: dung 1 bang lich su chung "lich_su_thay_doi" (co cot "bang" de biet
-- thuoc module nao) thay vi tao rieng moi bang mot bang "<ten_bang>_history" -
-- cung dat duoc dung yeu cau cua SPEC (ghi nguoi/thoi diem/module/noi dung
-- truoc-sau) nhung de bao tri hon: 1 ham trigger duy nhat dung duoc cho ca 6+
-- bang nghiep vu, khong phai lap lai schema history cho tung bang.
--
-- Nguoi thuc hien duoc doc truc tiep tu cot nguoi_tao_id/nguoi_sua_id cua chinh
-- ban ghi (da duoc tang Hibernate Auditing ghi truoc khi cau lenh INSERT/UPDATE
-- toi CSDL) - khong can bien session Postgres (SET LOCAL) nen van chay dung ca
-- khi co ai sua truc tiep bang cong cu quan tri DB (luc do se la NULL, hop ly).

CREATE TYPE hanh_dong_enum AS ENUM ('INSERT', 'UPDATE', 'DELETE');

CREATE TABLE lich_su_thay_doi (
  id                  BIGSERIAL PRIMARY KEY,
  bang                VARCHAR(50) NOT NULL,
  ban_ghi_id          UUID NOT NULL,
  hanh_dong           hanh_dong_enum NOT NULL,
  du_lieu_truoc       JSONB,
  du_lieu_sau         JSONB,
  nguoi_thuc_hien_id  UUID REFERENCES nguoi_dung(id),
  thoi_diem           TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_lich_su_lookup ON lich_su_thay_doi (bang, ban_ghi_id, thoi_diem DESC);

CREATE OR REPLACE FUNCTION fn_ghi_lich_su() RETURNS TRIGGER AS $$
DECLARE
  v_nguoi_thuc_hien UUID;
BEGIN
  IF TG_OP = 'DELETE' THEN
    v_nguoi_thuc_hien := COALESCE(
      (to_jsonb(OLD)->>'nguoi_sua_id')::UUID,
      (to_jsonb(OLD)->>'nguoi_tao_id')::UUID
    );
    INSERT INTO lich_su_thay_doi (bang, ban_ghi_id, hanh_dong, du_lieu_truoc, nguoi_thuc_hien_id)
    VALUES (TG_TABLE_NAME, OLD.id, 'DELETE', to_jsonb(OLD), v_nguoi_thuc_hien);
    RETURN OLD;
  ELSIF TG_OP = 'UPDATE' THEN
    v_nguoi_thuc_hien := COALESCE(
      (to_jsonb(NEW)->>'nguoi_sua_id')::UUID,
      (to_jsonb(NEW)->>'nguoi_tao_id')::UUID
    );
    INSERT INTO lich_su_thay_doi (bang, ban_ghi_id, hanh_dong, du_lieu_truoc, du_lieu_sau, nguoi_thuc_hien_id)
    VALUES (TG_TABLE_NAME, NEW.id, 'UPDATE', to_jsonb(OLD), to_jsonb(NEW), v_nguoi_thuc_hien);
    RETURN NEW;
  ELSE
    v_nguoi_thuc_hien := (to_jsonb(NEW)->>'nguoi_tao_id')::UUID;
    INSERT INTO lich_su_thay_doi (bang, ban_ghi_id, hanh_dong, du_lieu_sau, nguoi_thuc_hien_id)
    VALUES (TG_TABLE_NAME, NEW.id, 'INSERT', to_jsonb(NEW), v_nguoi_thuc_hien);
    RETURN NEW;
  END IF;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_audit_doi_tac AFTER INSERT OR UPDATE OR DELETE ON doi_tac
  FOR EACH ROW EXECUTE FUNCTION fn_ghi_lich_su();
CREATE TRIGGER trg_audit_van_ban_dhkg AFTER INSERT OR UPDATE OR DELETE ON van_ban_dhkg
  FOR EACH ROW EXECUTE FUNCTION fn_ghi_lich_su();
CREATE TRIGGER trg_audit_vbpl_vn AFTER INSERT OR UPDATE OR DELETE ON vbpl_vn
  FOR EACH ROW EXECUTE FUNCTION fn_ghi_lich_su();
CREATE TRIGGER trg_audit_mou AFTER INSERT OR UPDATE OR DELETE ON mou
  FOR EACH ROW EXECUTE FUNCTION fn_ghi_lich_su();
CREATE TRIGGER trg_audit_doan_vao AFTER INSERT OR UPDATE OR DELETE ON doan_vao
  FOR EACH ROW EXECUTE FUNCTION fn_ghi_lich_su();
CREATE TRIGGER trg_audit_doan_ra AFTER INSERT OR UPDATE OR DELETE ON doan_ra
  FOR EACH ROW EXECUTE FUNCTION fn_ghi_lich_su();
