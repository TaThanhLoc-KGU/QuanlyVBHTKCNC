package com.ttloc.htkhcn.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ttloc.htkhcn.user.NguoiDungRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final NguoiDungRepository nguoiDungRepository;

    @Override
    public UserDetails loadUserByUsername(String tenDangNhap) throws UsernameNotFoundException {
        return nguoiDungRepository.findByTenDangNhapAndDeletedAtIsNull(tenDangNhap)
                .map(SecurityUser::new)
                .orElseThrow(() -> new UsernameNotFoundException("Khong tim thay tai khoan: " + tenDangNhap));
    }
}
