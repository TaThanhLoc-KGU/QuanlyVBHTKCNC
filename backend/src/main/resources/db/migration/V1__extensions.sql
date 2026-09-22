-- V1: Bat cac extension Postgres can dung (SPEC muc 6.4).
-- pg_trgm, unaccent, pgcrypto la "trusted extension" tu Postgres 13+, nen role
-- so huu database (khong can superuser) van tao duoc, phu hop khi trien khai
-- tren nhieu moi truong (local dev, Docker, ML110) ma khong phai cap quyen superuser
-- cho app role.

CREATE EXTENSION IF NOT EXISTS pg_trgm;
CREATE EXTENSION IF NOT EXISTS unaccent;
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Cho phep dung unaccent() trong generated column / index (yeu cau IMMUTABLE).
-- Can ownership len function unaccent(regdictionary, text) - co duoc vi role hien
-- tai la nguoi tao extension unaccent o tren.
ALTER FUNCTION unaccent(regdictionary, text) IMMUTABLE;

-- Ham tien ich: bo dau + ha thuong, dung lam nen cho tsvector 'simple' va cho
-- pg_trgm similarity khong phan biet dau tieng Viet.
--
-- Ghi chu quan trong: goi ham/dictionary DUOI DANG SCHEMA-QUALIFIED
-- (public.unaccent(...)) - khong chi dung ten tran 'unaccent'. Khi Postgres
-- "inline" ham SQL don gian nay de kiem tra tinh immutable luc CREATE INDEX
-- (functional index/generated column), no dung mot search_path bi gioi han
-- (khong tu dong bao gom 'public'), nen goi khong schema-qualify se bao loi
-- "function unaccent(...) does not exist" hoac "text search dictionary
-- unaccent does not exist" du extension unaccent van dang ton tai binh thuong
-- va goi truc tiep (khong qua generated column/index) van chay dung.
CREATE OR REPLACE FUNCTION fn_unaccent_lower(text)
RETURNS text
LANGUAGE sql
IMMUTABLE
PARALLEL SAFE
AS $$
  SELECT lower(public.unaccent('public.unaccent'::regdictionary, coalesce($1, '')));
$$;

-- Ham tien ich: noi 1 mang text[] thanh 1 chuoi, dung trong tsvector generated
-- column (SPEC "Quoc tich" o module Doan vao cho phep nhieu gia tri).
-- array_to_string() cua Postgres duoc khai bao STABLE (khong IMMUTABLE) nen
-- khong dung truc tiep duoc trong generated column - boc lai qua ham nay va tu
-- khai bao IMMUTABLE (an toan vi ket qua chi phu thuoc gia tri dau vao, khong
-- phu thuoc session/locale voi kieu text[]).
CREATE OR REPLACE FUNCTION fn_array_text_join(text[], text)
RETURNS text
LANGUAGE sql
IMMUTABLE
PARALLEL SAFE
AS $$
  SELECT array_to_string($1, $2);
$$;
