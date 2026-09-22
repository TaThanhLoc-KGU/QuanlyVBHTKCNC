package com.ttloc.htkhcn.common;

import org.springframework.data.jpa.domain.Specification;

/**
 * {@code Specification.allOf(...)} tu Spring Data JPA khong chap nhan phan tu
 * null trong danh sach (bao "Other specification must not be null"), nhung
 * cac Specification loc theo tham so tuy chon (VD: loaiDoiTac == null) trong
 * cac *Specifications class cua du an nay CHU DONG tra ve null khi khong can
 * loc - dung ham nay de gop lai an toan, bo qua moi phan tu null.
 */
public final class SpecUtils {

    private SpecUtils() {
    }

    @SafeVarargs
    public static <T> Specification<T> and(Specification<T>... specs) {
        Specification<T> result = Specification.where((root, query, cb) -> cb.conjunction());
        for (Specification<T> spec : specs) {
            if (spec != null) {
                result = result.and(spec);
            }
        }
        return result;
    }
}
