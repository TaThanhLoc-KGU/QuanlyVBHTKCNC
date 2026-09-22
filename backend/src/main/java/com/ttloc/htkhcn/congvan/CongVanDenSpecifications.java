package com.ttloc.htkhcn.congvan;

import java.time.LocalDate;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public final class CongVanDenSpecifications {

    private CongVanDenSpecifications() {
    }

    public static Specification<CongVanDen> trangThaiBang(String trangThai) {
        if (!StringUtils.hasText(trangThai)) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("trangThai"), trangThai);
    }

    public static Specification<CongVanDen> namBang(Integer nam) {
        if (nam == null) {
            return null;
        }
        LocalDate tu = LocalDate.of(nam, 1, 1);
        LocalDate den = LocalDate.of(nam, 12, 31);
        return (root, query, cb) -> cb.between(root.get("ngayDen"), tu, den);
    }

    public static Specification<CongVanDen> tuKhoaKhongDauTrong(String tuKhoa) {
        if (!StringUtils.hasText(tuKhoa)) {
            return null;
        }
        String pattern = "%" + tuKhoa + "%";
        return (root, query, cb) -> cb.or(
                cb.like(cb.function("fn_unaccent_lower", String.class, root.get("soVanBan")),
                        cb.function("fn_unaccent_lower", String.class, cb.literal(pattern))),
                cb.like(cb.function("fn_unaccent_lower", String.class, root.get("trichYeu")),
                        cb.function("fn_unaccent_lower", String.class, cb.literal(pattern))),
                cb.like(cb.function("fn_unaccent_lower", String.class, root.get("coQuanBanHanh")),
                        cb.function("fn_unaccent_lower", String.class, cb.literal(pattern))));
    }
}
