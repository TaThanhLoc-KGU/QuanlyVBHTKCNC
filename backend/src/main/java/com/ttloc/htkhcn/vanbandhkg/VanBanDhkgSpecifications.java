package com.ttloc.htkhcn.vanbandhkg;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.ttloc.htkhcn.common.TinhTrangHieuLuc;

public final class VanBanDhkgSpecifications {

    private VanBanDhkgSpecifications() {
    }

    public static Specification<VanBanDhkg> chuaBiXoa() {
        return (root, query, cb) -> cb.isNull(root.get("deletedAt"));
    }

    public static Specification<VanBanDhkg> loaiVanBanBang(UUID loaiVanBanId) {
        if (loaiVanBanId == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("loaiVanBan").get("id"), loaiVanBanId);
    }

    public static Specification<VanBanDhkg> tinhTrangBang(TinhTrangHieuLuc tinhTrang) {
        if (tinhTrang == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("tinhTrangHieuLuc"), tinhTrang);
    }

    public static Specification<VanBanDhkg> ngayBanHanhTu(LocalDate tu) {
        if (tu == null) {
            return null;
        }
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("ngayBanHanh"), tu);
    }

    public static Specification<VanBanDhkg> ngayBanHanhDen(LocalDate den) {
        if (den == null) {
            return null;
        }
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("ngayBanHanh"), den);
    }

    public static Specification<VanBanDhkg> tuKhoaKhongDauTrong(String tuKhoa) {
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
