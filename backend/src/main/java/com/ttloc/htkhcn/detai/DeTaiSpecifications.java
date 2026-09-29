package com.ttloc.htkhcn.detai;

import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public final class DeTaiSpecifications {

    private DeTaiSpecifications() {
    }

    public static Specification<DeTai> chuaBiXoa() {
        return (root, query, cb) -> cb.isNull(root.get("deletedAt"));
    }

    public static Specification<DeTai> namDeXuatBang(Integer nam) {
        if (nam == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("namDeXuat"), nam);
    }

    public static Specification<DeTai> trangThaiBang(TrangThaiDeTai trangThai) {
        if (trangThai == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("trangThai"), trangThai);
    }

    public static Specification<DeTai> chuNhiemBang(UUID chuNhiemId) {
        if (chuNhiemId == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("chuNhiem").get("id"), chuNhiemId);
    }

    public static Specification<DeTai> linhVucBang(UUID linhVucId) {
        if (linhVucId == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("linhVuc").get("id"), linhVucId);
    }

    public static Specification<DeTai> tuKhoaKhongDauTrong(String tuKhoa) {
        if (!StringUtils.hasText(tuKhoa)) {
            return null;
        }
        String pattern = "%" + tuKhoa + "%";
        return (root, query, cb) -> cb.or(
                cb.like(cb.function("fn_unaccent_lower", String.class, root.get("tenDeTai")),
                        cb.function("fn_unaccent_lower", String.class, cb.literal(pattern))),
                cb.like(cb.function("fn_unaccent_lower", String.class, root.get("maDeTai")),
                        cb.function("fn_unaccent_lower", String.class, cb.literal(pattern))));
    }
}
