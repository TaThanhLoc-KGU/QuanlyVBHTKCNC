package com.ttloc.htkhcn.security;

import java.io.IOException;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ttloc.htkhcn.user.NguoiDungRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final NguoiDungRepository nguoiDungRepository;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws IOException, jakarta.servlet.ServletException {

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);
        if (jwtService.isTokenValid(token) && !jwtService.isRefreshToken(token)
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            var userId = jwtService.extractUserId(token);
            nguoiDungRepository.findById(userId)
                    .filter(nd -> nd.getDeletedAt() == null)
                    .map(SecurityUser::new)
                    // Kiem tra lai isEnabled() moi request: neu Admin khoa tai khoan
                    // SAU KHI token da duoc phat, token cu (con han) khong duoc dung
                    // tiep de goi API nua - chi khoa dang nhap moi la chua du.
                    .filter(SecurityUser::isEnabled)
                    .ifPresent(userDetails -> authenticate(request, userDetails));
        }

        filterChain.doFilter(request, response);
    }

    private void authenticate(HttpServletRequest request, UserDetails userDetails) {
        var authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}
