package com.ttloc.htkhcn.baocao;

import java.util.List;
import java.util.Map;

import com.ttloc.htkhcn.doanra.DoanRaResponse;
import com.ttloc.htkhcn.doanvao.DoanVaoResponse;

/** SPEC muc 4.8.2: Bao cao doan ra va doan khach vao truong. */
public final class BaoCaoDoanRaVao {

    private BaoCaoDoanRaVao() {
    }

    public record TomTat(
            long tongSoDoanVao,
            long tongKhachNuocNgoaiDaDen,
            long tongSoDoanRa,
            long tongLuotCanBoDiCongTac,
            long tongSoNgayCongTacNuocNgoai,
            Map<String, Long> theoQuocGia,
            Map<String, Long> theoThang) {
    }

    public record Response(TomTat tomTat, List<DoanVaoResponse> doanVao, List<DoanRaResponse> doanRa) {
    }
}
