package com.ttloc.htkhcn.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.ttloc.htkhcn.common.ModuleKey;

/**
 * Dung trong @PreAuthorize de kiem tra Editor co duoc phan cong sua module do
 * khong (SPEC muc 2: "Co the phan quyen Editor theo tung module"). Admin luon
 * qua duoc, Viewer khong bao gio qua (chi ap dung cho cac endpoint ghi).
 */
@Component("moduleAccess")
public class ModuleAccessEvaluator {

    public boolean canEdit(Authentication authentication, String moduleKey) {
        if (!(authentication.getPrincipal() instanceof SecurityUser user)) {
            return false;
        }
        return user.coTheSuaModule(ModuleKey.valueOf(moduleKey));
    }
}
