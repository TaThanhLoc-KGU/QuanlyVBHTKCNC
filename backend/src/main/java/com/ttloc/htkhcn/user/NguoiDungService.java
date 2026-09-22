package com.ttloc.htkhcn.user;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ttloc.htkhcn.common.exception.BadRequestException;
import com.ttloc.htkhcn.common.exception.DuplicateResourceException;
import com.ttloc.htkhcn.common.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

/** Quan ly tai khoan boi Admin (SPEC muc 2, 4.1). */
@Service
@RequiredArgsConstructor
public class NguoiDungService {

    private static final String BANG_CHU = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789";
    private final SecureRandom secureRandom = new SecureRandom();

    private final NguoiDungRepository nguoiDungRepository;
    private final VaiTroRepository vaiTroRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public Page<NguoiDungResponse> danhSach(Pageable pageable) {
        return nguoiDungRepository.findByDeletedAtIsNull(pageable).map(NguoiDungResponse::from);
    }

    @Transactional(readOnly = true)
    public NguoiDungResponse layTheoId(UUID id) {
        return NguoiDungResponse.from(timHoacLoi(id));
    }

    /** Tao tai khoan moi voi mat khau ngau nhien, tra ve mat khau tam thoi de Admin gui cho nguoi dung. */
    @Transactional
    public NguoiDungCreatedResult tao(NguoiDungRequest request) {
        if (nguoiDungRepository.existsByTenDangNhap(request.tenDangNhap())) {
            throw new DuplicateResourceException("Ten dang nhap da ton tai: " + request.tenDangNhap());
        }
        if (nguoiDungRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Email da ton tai: " + request.email());
        }

        NguoiDung u = new NguoiDung();
        u.setTenDangNhap(request.tenDangNhap());
        u.setEmail(request.email());
        u.setHoTen(request.hoTen());
        u.setVaiTros(layVaiTros(request.vaiTroMa()));
        u.setBienTapMoDun(request.bienTapMoDun() != null ? new HashSet<>(request.bienTapMoDun()) : Set.of());
        u.setPhaiDoiMatKhau(true);

        String matKhauTam = sinhMatKhauTam();
        u.setMatKhauHash(passwordEncoder.encode(matKhauTam));

        NguoiDung saved = nguoiDungRepository.save(u);
        return new NguoiDungCreatedResult(NguoiDungResponse.from(saved), matKhauTam);
    }

    @Transactional
    public NguoiDungResponse capNhatVaiTroVaModule(UUID id, NguoiDungRequest request) {
        NguoiDung u = timHoacLoi(id);
        u.setHoTen(request.hoTen());
        u.setVaiTros(layVaiTros(request.vaiTroMa()));
        u.setBienTapMoDun(request.bienTapMoDun() != null ? new HashSet<>(request.bienTapMoDun()) : Set.of());
        return NguoiDungResponse.from(nguoiDungRepository.save(u));
    }

    @Transactional
    public void khoaTaiKhoan(UUID id) {
        NguoiDung u = timHoacLoi(id);
        u.setTrangThai(TrangThaiTaiKhoan.TAM_KHOA);
        nguoiDungRepository.save(u);
    }

    @Transactional
    public void moKhoaTaiKhoan(UUID id) {
        NguoiDung u = timHoacLoi(id);
        u.setTrangThai(TrangThaiTaiKhoan.HOAT_DONG);
        u.setSoLanDangNhapSai(0);
        u.setKhoaDen(null);
        nguoiDungRepository.save(u);
    }

    @Transactional
    public String datLaiMatKhau(UUID id) {
        NguoiDung u = timHoacLoi(id);
        String matKhauMoi = sinhMatKhauTam();
        u.setMatKhauHash(passwordEncoder.encode(matKhauMoi));
        u.setPhaiDoiMatKhau(true);
        nguoiDungRepository.save(u);
        return matKhauMoi;
    }

    @Transactional
    public void xoa(UUID id) {
        NguoiDung u = timHoacLoi(id);
        u.setDeletedAt(Instant.now());
        nguoiDungRepository.save(u);
    }

    private Set<VaiTro> layVaiTros(List<String> maVaiTros) {
        Set<VaiTro> vaiTros = new HashSet<>();
        for (String ma : maVaiTros) {
            vaiTros.add(vaiTroRepository.findByMaVaiTro(ma)
                    .orElseThrow(() -> new BadRequestException("Vai tro khong ton tai: " + ma)));
        }
        return vaiTros;
    }

    private NguoiDung timHoacLoi(UUID id) {
        return nguoiDungRepository.findById(id)
                .filter(u -> u.getDeletedAt() == null)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay tai khoan: " + id));
    }

    private String sinhMatKhauTam() {
        StringBuilder sb = new StringBuilder(12);
        for (int i = 0; i < 12; i++) {
            sb.append(BANG_CHU.charAt(secureRandom.nextInt(BANG_CHU.length())));
        }
        return sb.toString();
    }

    public record NguoiDungCreatedResult(NguoiDungResponse nguoiDung, String matKhauTam) {
    }
}
