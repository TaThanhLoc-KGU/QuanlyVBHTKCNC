package com.ttloc.htkhcn.vbplvn;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.ttloc.htkhcn.common.TinhTrangHieuLuc;

public final class VbplVnSpecifications {

    private VbplVnSpecifications() {
    }

    public static Specification<VbplVn> chuaBiXoa() {
        return (root, query, cb) -> cb.isNull(root.get("deletedAt"));
    }

    public static Specification<VbplVn> loaiVanBanBang(UUID loaiVanBanId) {
        if (loaiVanBanId == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("loaiVanBan").get("id"), loaiVanBanId);
    }

    public static Specification<VbplVn> tinhTrangBang(TinhTrangHieuLuc tinhTrang) {
        if (tinhTrang == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("tinhTrangHieuLuc"), tinhTrang);
    }

    public static Specification<VbplVn> ngayBanHanhTu(LocalDate tu) {
        if (tu == null) {
            return null;
        }
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("ngayBanHanh"), tu);
    }

    public static Specification<VbplVn> ngayBanHanhDen(LocalDate den) {
        if (den == null) {
            return null;
        }
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("ngayBanHanh"), den);
    }

    public static Specification<VbplVn> tuKhoaKhongDauTrong(String tuKhoa) {
        if (!StringUtils.hasText(tuKhoa)) {
            return null;
        }
        String pattern = "%" + tuKhoa + "%";
        return (root, query, cb) -> cb.or(
                cb.like(cb.function("fn_unaccent_lower", String.class, root.get("soHieu")),
                        cb.function("fn_unaccent_lower", String.class, cb.literal(pattern))),
                cb.like(cb.function("fn_unaccent_lower", String.class, root.get("tenVanBan")),
                        cb.function("fn_unaccent_lower", String.class, cb.literal(pattern))),
                cb.like(cb.function("fn_unaccent_lower", String.class, root.get("noiDungChinh")),
                        cb.function("fn_unaccent_lower", String.class, cb.literal(pattern))));
    }
}
