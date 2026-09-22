package com.ttloc.htkhcn.mou;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record MouRequest(
        @NotNull(message = "Doi tac khong duoc de trong") UUID doiTacId,
        String tenTaiLieu,
        @NotNull(message = "Ngay ban hanh khong duoc de trong") LocalDate ngayBanHanh,
        LocalDate ngayHetHan,
        String caNhanDauMoi,
        String donViThucHien,
        PhamViHopTac phamViHopTac,
        String linhVucHopTac,
        String dauMoiGhiTrongMou,
        String daiDienKguKy,
        String thoiHanHieuLuc,
        String soCongVan) {
}
