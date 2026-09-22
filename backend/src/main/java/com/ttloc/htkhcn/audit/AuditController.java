package com.ttloc.htkhcn.audit;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ttloc.htkhcn.security.SecurityUser;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/lich-su")
@RequiredArgsConstructor
public class AuditController {

    private final AuditService auditService;

    @GetMapping("/{bang}/{banGhiId}")
    public List<LichSuThayDoi> getHistory(
            @PathVariable String bang,
            @PathVariable UUID banGhiId,
            @AuthenticationPrincipal SecurityUser currentUser) {
        return auditService.getHistory(bang, banGhiId, currentUser);
    }
}
