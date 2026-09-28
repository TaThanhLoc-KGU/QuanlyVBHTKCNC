package com.ttloc.htkhcn.visa;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public final class VisaSpecifications {

    private VisaSpecifications() {
    }

    public static Specification<Visa> chuaBiXoa() {
        return (root, query, cb) -> cb.isNull(root.get("deletedAt"));
    }

    public static Specification<Visa> namBang(Integer nam) {
        if (nam == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("nam"), nam);
    }

    public static Specification<Visa> loaiCapBang(LoaiCapVisa loaiCap) {
        if (loaiCap == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("loaiCap"), loaiCap);
    }

    public static Specification<Visa> tuKhoaKhongDauTrong(String tuKhoa) {
        if (!StringUtils.hasText(tuKhoa)) {
            return null;
        }
        String pattern = "%" + tuKhoa + "%";
        return (root, query, cb) -> cb.or(
                cb.like(cb.function("fn_unaccent_lower", String.class, root.get("hoTen")),
                        cb.function("fn_unaccent_lower", String.class, cb.literal(pattern))),
                cb.like(cb.function("fn_unaccent_lower", String.class, root.get("quocTich")),
                        cb.function("fn_unaccent_lower", String.class, cb.literal(pattern))));
    }
}
