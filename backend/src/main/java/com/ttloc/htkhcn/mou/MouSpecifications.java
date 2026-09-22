package com.ttloc.htkhcn.mou;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public final class MouSpecifications {

    private MouSpecifications() {
    }

    public static Specification<MouTrangThai> doiTacBang(UUID doiTacId) {
        if (doiTacId == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("doiTacId"), doiTacId);
    }

    public static Specification<MouTrangThai> trangThaiBang(TrangThaiMou trangThai) {
        if (trangThai == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("trangThai"), trangThai);
    }

    public static Specification<MouTrangThai> ngayBanHanhTu(LocalDate tu) {
        if (tu == null) {
            return null;
        }
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("ngayBanHanh"), tu);
    }

    public static Specification<MouTrangThai> ngayBanHanhDen(LocalDate den) {
        if (den == null) {
            return null;
        }
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("ngayBanHanh"), den);
    }

    public static Specification<MouTrangThai> tuKhoaKhongDauTrong(String tuKhoa) {
        if (!StringUtils.hasText(tuKhoa)) {
            return null;
        }
        String pattern = "%" + tuKhoa + "%";
        return (root, query, cb) -> cb.or(
                cb.like(cb.function("fn_unaccent_lower", String.class, root.get("tenTaiLieu")),
                        cb.function("fn_unaccent_lower", String.class, cb.literal(pattern))),
                cb.like(cb.function("fn_unaccent_lower", String.class, root.get("tenDoiTac")),
                        cb.function("fn_unaccent_lower", String.class, cb.literal(pattern))),
                cb.like(cb.function("fn_unaccent_lower", String.class, root.get("linhVucHopTac")),
                        cb.function("fn_unaccent_lower", String.class, cb.literal(pattern))));
    }
}
