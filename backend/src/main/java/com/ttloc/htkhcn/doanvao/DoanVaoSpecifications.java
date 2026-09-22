package com.ttloc.htkhcn.doanvao;

import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public final class DoanVaoSpecifications {

    private DoanVaoSpecifications() {
    }

    public static Specification<DoanVao> chuaBiXoa() {
        return (root, query, cb) -> cb.isNull(root.get("deletedAt"));
    }

    public static Specification<DoanVao> namBang(Integer nam) {
        if (nam == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("nam"), nam);
    }

    public static Specification<DoanVao> doiTacBang(UUID doiTacId) {
        if (doiTacId == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("doiTac").get("id"), doiTacId);
    }

    public static Specification<DoanVao> tuKhoaKhongDauTrong(String tuKhoa) {
        if (!StringUtils.hasText(tuKhoa)) {
            return null;
        }
        String pattern = "%" + tuKhoa + "%";
        return (root, query, cb) -> cb.or(
                cb.like(cb.function("fn_unaccent_lower", String.class, root.get("tenDoan")),
                        cb.function("fn_unaccent_lower", String.class, cb.literal(pattern))),
                cb.like(cb.function("fn_unaccent_lower", String.class, root.get("noiDungLamViec")),
                        cb.function("fn_unaccent_lower", String.class, cb.literal(pattern))));
    }
}
