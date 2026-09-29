package com.ttloc.htkhcn.detai;

import java.util.UUID;

public record LinhVucNghienCuuResponse(
        UUID id,
        String ma,
        String ten,
        short cap,
        UUID chaId,
        String chaTen,
        int thuTu,
        boolean hoatDong) {

    public static LinhVucNghienCuuResponse from(LinhVucNghienCuu l) {
        return new LinhVucNghienCuuResponse(
                l.getId(), l.getMa(), l.getTen(), l.getCap(),
                l.getCha() != null ? l.getCha().getId() : null,
                l.getCha() != null ? l.getCha().getTen() : null,
                l.getThuTu(), l.isHoatDong());
    }
}
