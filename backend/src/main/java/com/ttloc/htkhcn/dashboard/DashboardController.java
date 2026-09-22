package com.ttloc.htkhcn.dashboard;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

/** SPEC muc 4.6: trang tong quan Dashboard. */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'EDITOR', 'VIEWER')")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public DashboardResponse tongQuan() {
        return dashboardService.layTongQuan();
    }
}
