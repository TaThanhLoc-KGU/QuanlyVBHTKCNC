package com.ttloc.htkhcn.auth;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ttloc.htkhcn.common.exception.AccountLockedException;
import com.ttloc.htkhcn.common.exception.BadRequestException;
import com.ttloc.htkhcn.security.JwtService;
import com.ttloc.htkhcn.security.SecurityUser;
import com.ttloc.htkhcn.user.NguoiDung;
import com.ttloc.htkhcn.user.NguoiDungRepository;
import com.ttloc.htkhcn.user.TrangThaiTaiKhoan;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final NguoiDungRepository nguoiDungRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Value("${app.security.max-failed-login-attempts}")
    private int maxFailedLoginAttempts;

    @Value("${app.security.lockout-minutes}")
    private int lockoutMinutes;

    @Transactional
    public LoginResponse login(LoginRequest request) {
        NguoiDung user = nguoiDungRepository.findByTenDangNhapAndDeletedAtIsNull(request.tenDangNhap())
                .orElseThrow(() -> new BadCredentialsException("Sai ten dang nhap hoac mat khau"));

        // TAM_KHOA: Admin tu tay khoa tai khoan (SPEC muc 2) - khac voi khoaDen
        // ben duoi la khoa TAM THOI tu dong do dang nhap sai nhieu lan.
        if (user.getTrangThai() == TrangThaiTaiKhoan.TAM_KHOA) {
            throw new AccountLockedException("Tai khoan da bi quan tri vien khoa. Vui long lien he Admin.");
        }

        if (user.getKhoaDen() != null && user.getKhoaDen().isAfter(Instant.now())) {
            throw new AccountLockedException(
                    "Tai khoan dang bi khoa tam thoi do dang nhap sai nhieu lan. Vui long thu lai sau.");
        }

        if (!passwordEncoder.matches(request.matKhau(), user.getMatKhauHash())) {
            xuLyDangNhapSai(user);
            throw new BadCredentialsException("Sai ten dang nhap hoac mat khau");
        }

        user.setSoLanDangNhapSai(0);
        user.setKhoaDen(null);
        nguoiDungRepository.save(user);

        SecurityUser securityUser = new SecurityUser(user);
        return buildLoginResponse(securityUser, user.isPhaiDoiMatKhau());
    }

    private void xuLyDangNhapSai(NguoiDung user) {
        int soLan = user.getSoLanDangNhapSai() + 1;
        user.setSoLanDangNhapSai(soLan);
        if (soLan >= maxFailedLoginAttempts) {
            user.setKhoaDen(Instant.now().plus(lockoutMinutes, ChronoUnit.MINUTES));
        }
        nguoiDungRepository.save(user);
    }

    @Transactional
    public LoginResponse refresh(RefreshRequest request) {
        String token = request.refreshToken();
        if (!jwtService.isTokenValid(token) || !jwtService.isRefreshToken(token)) {
            throw new BadRequestException("Refresh token khong hop le hoac da het han");
        }
        NguoiDung user = nguoiDungRepository.findById(jwtService.extractUserId(token))
                .filter(nd -> nd.getDeletedAt() == null)
                .orElseThrow(() -> new BadRequestException("Tai khoan khong ton tai"));
        if (user.getTrangThai() == TrangThaiTaiKhoan.TAM_KHOA) {
            throw new AccountLockedException("Tai khoan da bi quan tri vien khoa. Vui long lien he Admin.");
        }

        SecurityUser securityUser = new SecurityUser(user);
        return buildLoginResponse(securityUser, user.isPhaiDoiMatKhau());
    }

    private LoginResponse buildLoginResponse(SecurityUser securityUser, boolean phaiDoiMatKhau) {
        String accessToken = jwtService.generateAccessToken(securityUser);
        String refreshToken = jwtService.generateRefreshToken(securityUser);
        return new LoginResponse(
                accessToken,
                refreshToken,
                securityUser.getId(),
                securityUser.getTenDangNhap(),
                securityUser.getHoTen(),
                securityUser.vaiTroCodesList(),
                phaiDoiMatKhau);
    }

    @Transactional
    public void doiMatKhau(java.util.UUID userId, ChangePasswordRequest request) {
        NguoiDung user = nguoiDungRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("Tai khoan khong ton tai"));
        if (!passwordEncoder.matches(request.matKhauCu(), user.getMatKhauHash())) {
            throw new BadRequestException("Mat khau hien tai khong dung");
        }
        user.setMatKhauHash(passwordEncoder.encode(request.matKhauMoi()));
        user.setPhaiDoiMatKhau(false);
        nguoiDungRepository.save(user);
    }
}
