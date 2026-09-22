package com.ttloc.htkhcn.user;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ttloc.htkhcn.common.PageResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/** Chi Admin duoc quan ly tai khoan (SPEC muc 2). */
@RestController
@RequestMapping("/api/nguoi-dung")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class NguoiDungController {

    private final NguoiDungService nguoiDungService;

    @GetMapping
    public PageResponse<NguoiDungResponse> danhSach(Pageable pageable) {
        return PageResponse.of(nguoiDungService.danhSach(pageable));
    }

    @GetMapping("/{id}")
    public NguoiDungResponse chiTiet(@PathVariable UUID id) {
        return nguoiDungService.layTheoId(id);
    }

    @PostMapping
    public NguoiDungService.NguoiDungCreatedResult tao(@Valid @RequestBody NguoiDungRequest request) {
        return nguoiDungService.tao(request);
    }

    @PutMapping("/{id}")
    public NguoiDungResponse capNhat(@PathVariable UUID id, @Valid @RequestBody NguoiDungRequest request) {
        return nguoiDungService.capNhatVaiTroVaModule(id, request);
    }

    @PostMapping("/{id}/khoa")
    public void khoa(@PathVariable UUID id) {
        nguoiDungService.khoaTaiKhoan(id);
    }

    @PostMapping("/{id}/mo-khoa")
    public void moKhoa(@PathVariable UUID id) {
        nguoiDungService.moKhoaTaiKhoan(id);
    }

    @PostMapping("/{id}/dat-lai-mat-khau")
    public MatKhauTamResponse datLaiMatKhau(@PathVariable UUID id) {
        return new MatKhauTamResponse(nguoiDungService.datLaiMatKhau(id));
    }

    public record MatKhauTamResponse(String matKhauTam) {
    }
}
