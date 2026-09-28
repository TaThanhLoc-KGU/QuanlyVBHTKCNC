package com.ttloc.htkhcn.sukien;

import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public final class SuKienSpecifications {

    private SuKienSpecifications() {
    }

    public static Specification<SuKien> chuaBiXoa() {
        return (root, query, cb) -> cb.isNull(root.get("deletedAt"));
    }

    public static Specification<SuKien> namBang(Integer nam) {
        if (nam == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("nam"), nam);
    }

    public static Specification<SuKien> loaiSuKienBang(UUID loaiSuKienTuDienId) {
        if (loaiSuKienTuDienId == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("loaiSuKien").get("id"), loaiSuKienTuDienId);
    }

    public static Specification<SuKien> tuKhoaKhongDauTrong(String tuKhoa) {
        if (!StringUtils.hasText(tuKhoa)) {
            return null;
        }
        String pattern = "%" + tuKhoa + "%";
        return (root, query, cb) -> cb.or(
                cb.like(cb.function("fn_unaccent_lower", String.class, root.get("tenSuKien")),
                        cb.function("fn_unaccent_lower", String.class, cb.literal(pattern))),
                cb.like(cb.function("fn_unaccent_lower", String.class, root.get("donViToChuc")),
                        cb.function("fn_unaccent_lower", String.class, cb.literal(pattern))));
    }
}
