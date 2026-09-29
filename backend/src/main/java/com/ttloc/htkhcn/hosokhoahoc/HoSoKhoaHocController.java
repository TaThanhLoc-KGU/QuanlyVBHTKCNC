package com.ttloc.htkhcn.hosokhoahoc;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'EDITOR', 'VIEWER')")
public class HoSoKhoaHocController {

    private final LyLichKhoaHocService lyLichKhoaHocService;
    private final HoatDongNgoaiTruongService hoatDongNgoaiTruongService;

    @GetMapping("/api/can-bo/{nguoiDungId}/ly-lich-khoa-hoc")
    public LyLichKhoaHocResponse layLyLich(@PathVariable UUID nguoiDungId) {
        return lyLichKhoaHocService.layTheoNguoiDung(nguoiDungId);
    }

    @PutMapping("/api/can-bo/{nguoiDungId}/ly-lich-khoa-hoc")
    public LyLichKhoaHocResponse luuLyLich(@PathVariable UUID nguoiDungId, @RequestBody LyLichKhoaHocRequest request) {
        return lyLichKhoaHocService.luu(nguoiDungId, request);
    }

    @GetMapping("/api/can-bo/{nguoiDungId}/hoat-dong-ngoai-truong")
    public List<HoatDongNgoaiTruongResponse> danhSachHoatDong(@PathVariable UUID nguoiDungId) {
        return hoatDongNgoaiTruongService.danhSach(nguoiDungId);
    }

    @PostMapping("/api/can-bo/{nguoiDungId}/hoat-dong-ngoai-truong")
    public HoatDongNgoaiTruongResponse themHoatDong(@PathVariable UUID nguoiDungId,
            @Valid @RequestBody HoatDongNgoaiTruongRequest request) {
        return hoatDongNgoaiTruongService.tao(nguoiDungId, request);
    }

    @DeleteMapping("/api/hoat-dong-ngoai-truong/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void xoaHoatDong(@PathVariable UUID id) {
        hoatDongNgoaiTruongService.xoa(id);
    }
}
