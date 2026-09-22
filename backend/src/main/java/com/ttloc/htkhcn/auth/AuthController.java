package com.ttloc.htkhcn.auth;

import java.util.UUID;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ttloc.htkhcn.security.SecurityUser;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    public LoginResponse refresh(@Valid @RequestBody RefreshRequest request) {
        return authService.refresh(request);
    }

    @PostMapping("/doi-mat-khau")
    public void doiMatKhau(
            @AuthenticationPrincipal SecurityUser currentUser,
            @Valid @RequestBody ChangePasswordRequest request) {
        authService.doiMatKhau(currentUser.getId(), request);
    }

    @org.springframework.web.bind.annotation.GetMapping("/me")
    public CurrentUserResponse me(@AuthenticationPrincipal SecurityUser currentUser) {
        return new CurrentUserResponse(
                currentUser.getId(),
                currentUser.getTenDangNhap(),
                currentUser.getHoTen(),
                currentUser.vaiTroCodesList(),
                currentUser.getBienTapMoDun());
    }

    public record CurrentUserResponse(
            UUID id,
            String tenDangNhap,
            String hoTen,
            java.util.List<String> vaiTro,
            java.util.Set<com.ttloc.htkhcn.common.ModuleKey> bienTapMoDun) {
    }
}
