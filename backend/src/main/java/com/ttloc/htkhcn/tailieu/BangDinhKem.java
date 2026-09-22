package com.ttloc.htkhcn.tailieu;

import com.ttloc.htkhcn.common.ModuleKey;
import com.ttloc.htkhcn.common.exception.BadRequestException;

/** Cac bang duoc phep dinh kem tai lieu (khop CHECK constraint cua tai_lieu_dinh_kem, V10). */
public enum BangDinhKem {
    van_ban_dhkg(ModuleKey.VAN_BAN_DHKG),
    vbpl_vn(ModuleKey.VBPL_VN),
    mou(ModuleKey.MOU),
    cong_van_den(ModuleKey.CONG_VAN_DEN);

    private final ModuleKey moduleKey;

    BangDinhKem(ModuleKey moduleKey) {
        this.moduleKey = moduleKey;
    }

    public ModuleKey toModuleKey() {
        return moduleKey;
    }

    public static BangDinhKem tuTen(String bang) {
        try {
            return BangDinhKem.valueOf(bang);
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Bang khong hop le de dinh kem: " + bang);
        }
    }
}
