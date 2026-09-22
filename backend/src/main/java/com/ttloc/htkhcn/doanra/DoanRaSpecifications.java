package com.ttloc.htkhcn.doanra;

import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public final class DoanRaSpecifications {

    private DoanRaSpecifications() {
    }

    public static Specification<DoanRa> chuaBiXoa() {
        return (root, query, cb) -> cb.isNull(root.get("deletedAt"));
    }

    public static Specification<DoanRa> namBang(Integer nam) {
        if (nam == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("nam"), nam);
    }

    public static Specification<DoanRa> doiTacBang(UUID doiTacId) {
        if (doiTacId == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("doiTac").get("id"), doiTacId);
    }

    public static Specification<DoanRa> tuKhoaKhongDauTrong(String tuKhoa) {
        if (!StringUtils.hasText(tuKhoa)) {
            return null;
        }
        String pattern = "%" + tuKhoa + "%";
        return (root, query, cb) -> cb.or(
                cb.like(cb.function("fn_unaccent_lower", String.class, root.get("thanhPhan")),
                        cb.function("fn_unaccent_lower", String.class, cb.literal(pattern))),
                cb.like(cb.function("fn_unaccent_lower", String.class, root.get("noiDungLamViec")),
                        cb.function("fn_unaccent_lower", String.class, cb.literal(pattern))),
                cb.like(cb.function("fn_unaccent_lower", String.class, root.get("quocGiaLamViec")),
                        cb.function("fn_unaccent_lower", String.class, cb.literal(pattern))));
    }
}
