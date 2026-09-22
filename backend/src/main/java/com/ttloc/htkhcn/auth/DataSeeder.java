package com.ttloc.htkhcn.auth;

import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ttloc.htkhcn.user.NguoiDung;
import com.ttloc.htkhcn.user.NguoiDungRepository;
import com.ttloc.htkhcn.user.VaiTroRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Seed 1 tai khoan Admin mac dinh neu he thong chua co nguoi dung nao - de co
 * the dang nhap lan dau. Doi mat khau ngay sau khi dang nhap (phaiDoiMatKhau=true).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final NguoiDungRepository nguoiDungRepository;
    private final VaiTroRepository vaiTroRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.default-username:admin}")
    private String defaultUsername;

    @Value("${app.admin.default-email:admin@dhkg.edu.vn}")
    private String defaultEmail;

    @Value("${app.admin.default-password:Admin@123}")
    private String defaultPassword;

    @Override
    @Transactional
    public void run(String... args) {
        if (nguoiDungRepository.count() > 0) {
            return;
        }

        var adminRole = vaiTroRepository.findByMaVaiTro("ADMIN")
                .orElseThrow(() -> new IllegalStateException("Chua seed vai tro ADMIN (V2 migration)"));

        NguoiDung admin = new NguoiDung();
        admin.setTenDangNhap(defaultUsername);
        admin.setEmail(defaultEmail);
        admin.setHoTen("Quan tri vien he thong");
        admin.setMatKhauHash(passwordEncoder.encode(defaultPassword));
        admin.setPhaiDoiMatKhau(true);
        admin.setVaiTros(Set.of(adminRole));
        nguoiDungRepository.save(admin);

        log.warn("Da tao tai khoan Admin mac dinh: '{}' / mat khau mac dinh (xem app.admin.default-password) - "
                + "HAY DOI MAT KHAU NGAY sau lan dang nhap dau tien.", defaultUsername);
    }
}
