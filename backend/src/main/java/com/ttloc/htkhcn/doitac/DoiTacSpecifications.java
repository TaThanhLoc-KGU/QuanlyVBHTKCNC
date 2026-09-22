package com.ttloc.htkhcn.doitac;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

/**
 * Tim kiem khong phan biet dau tieng Viet bang ham fn_unaccent_lower() da tao
 * o V1 migration - goi truc tiep qua CriteriaBuilder.function() de tai su dung
 * lai logic bo dau/ha thuong dung nhat quan voi cot generated tsv (SPEC 4.4).
 */
public final class DoiTacSpecifications {

    private DoiTacSpecifications() {
    }

    public static Specification<DoiTac> chuaBiXoa() {
        return (root, query, cb) -> cb.isNull(root.get("deletedAt"));
    }

    public static Specification<DoiTac> loaiDoiTacBang(LoaiDoiTac loaiDoiTac) {
        if (loaiDoiTac == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("loaiDoiTac"), loaiDoiTac);
    }

    public static Specification<DoiTac> quocGiaBang(String quocGia) {
        if (!StringUtils.hasText(quocGia)) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("quocGia"), quocGia);
    }

    public static Specification<DoiTac> tuKhoaKhongDauTrong(String tuKhoa) {
        if (!StringUtils.hasText(tuKhoa)) {
            return null;
        }
        String pattern = "%" + tuKhoa + "%";
        return (root, query, cb) -> cb.or(
                cb.like(cb.function("fn_unaccent_lower", String.class, root.get("tenDoiTac")),
                        cb.function("fn_unaccent_lower", String.class, cb.literal(pattern))),
                cb.like(cb.function("fn_unaccent_lower", String.class, root.get("diaChi")),
                        cb.function("fn_unaccent_lower", String.class, cb.literal(pattern))),
                cb.like(cb.function("fn_unaccent_lower", String.class, root.get("ghiChu")),
                        cb.function("fn_unaccent_lower", String.class, cb.literal(pattern))));
    }
}
