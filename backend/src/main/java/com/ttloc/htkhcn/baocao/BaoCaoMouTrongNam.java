package com.ttloc.htkhcn.baocao;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.ttloc.htkhcn.doitac.LoaiDoiTac;
import com.ttloc.htkhcn.mou.TrangThaiMou;

/** SPEC muc 4.8.1: Bao cao MoU hop tac trong nam. */
public final class BaoCaoMouTrongNam {

    private BaoCaoMouTrongNam() {
    }

    public record Dong(
            String tenDoiTac,
            LoaiDoiTac loaiDoiTac,
            String linhVucHopTac,
            LocalDate ngayKy,
            LocalDate ngayHetHan,
            String donViDauMoi,
            TrangThaiMou trangThai) {
    }

    public record TomTat(
            long tongSoMouMoiKy,
            Map<String, Long> theoLoaiDoiTac,
            Map<String, Long> theoLinhVucHopTac,
            long tongSoMouCungKyNamTruoc) {
    }

    public record Response(int nam, TomTat tomTat, List<Dong> chiTiet) {
    }
}
