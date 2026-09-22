package com.ttloc.htkhcn.baocao;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ttloc.htkhcn.common.SpecUtils;
import com.ttloc.htkhcn.doanra.DoanRa;
import com.ttloc.htkhcn.doanra.DoanRaRepository;
import com.ttloc.htkhcn.doanra.DoanRaResponse;
import com.ttloc.htkhcn.doanra.DoanRaSpecifications;
import com.ttloc.htkhcn.doanvao.DoanVao;
import com.ttloc.htkhcn.doanvao.DoanVaoRepository;
import com.ttloc.htkhcn.doanvao.DoanVaoResponse;
import com.ttloc.htkhcn.doanvao.DoanVaoSpecifications;
import com.ttloc.htkhcn.mou.MouResponse;
import com.ttloc.htkhcn.mou.MouSpecifications;
import com.ttloc.htkhcn.mou.MouTrangThai;
import com.ttloc.htkhcn.mou.MouTrangThaiRepository;
import com.ttloc.htkhcn.mou.TrangThaiMou;

import lombok.RequiredArgsConstructor;

/** 3 bao cao chuyen de (SPEC muc 4.8). */
@Service
@RequiredArgsConstructor
public class BaoCaoService {

    private final MouTrangThaiRepository mouTrangThaiRepository;
    private final DoanVaoRepository doanVaoRepository;
    private final DoanRaRepository doanRaRepository;

    // ---------- 4.8.1 MoU trong nam ----------

    @Transactional(readOnly = true)
    public BaoCaoMouTrongNam.Response mouTrongNam(int nam) {
        int namHienTai = LocalDate.now().getYear();
        LocalDate tu = LocalDate.of(nam, 1, 1);
        LocalDate den = nam == namHienTai ? LocalDate.now() : LocalDate.of(nam, 12, 31);

        List<MouTrangThai> dsHienTai = timMouTheoKhoang(tu, den);

        LocalDate tuNamTruoc = tu.minusYears(1);
        LocalDate denNamTruoc = den.minusYears(1);
        long soCungKyNamTruoc = timMouTheoKhoang(tuNamTruoc, denNamTruoc).size();

        Map<String, Long> theoLoai = dsHienTai.stream()
                .collect(Collectors.groupingBy(m -> String.valueOf(m.getLoaiDoiTac()), Collectors.counting()));
        Map<String, Long> theoLinhVuc = dsHienTai.stream()
                .collect(Collectors.groupingBy(
                        m -> m.getLinhVucHopTac() == null || m.getLinhVucHopTac().isBlank()
                                ? "Chua xac dinh" : m.getLinhVucHopTac(),
                        Collectors.counting()));

        List<BaoCaoMouTrongNam.Dong> chiTiet = dsHienTai.stream()
                .sorted((a, b) -> a.getNgayBanHanh().compareTo(b.getNgayBanHanh()))
                .map(m -> new BaoCaoMouTrongNam.Dong(
                        m.getTenDoiTac(), m.getLoaiDoiTac(), m.getLinhVucHopTac(),
                        m.getNgayBanHanh(), m.getNgayHetHan(), m.getCaNhanDauMoi(), m.getTrangThai()))
                .toList();

        var tomTat = new BaoCaoMouTrongNam.TomTat(dsHienTai.size(), theoLoai, theoLinhVuc, soCungKyNamTruoc);
        return new BaoCaoMouTrongNam.Response(nam, tomTat, chiTiet);
    }

    private List<MouTrangThai> timMouTheoKhoang(LocalDate tu, LocalDate den) {
        Specification<MouTrangThai> spec = SpecUtils.and(
                MouSpecifications.ngayBanHanhTu(tu),
                MouSpecifications.ngayBanHanhDen(den));
        return mouTrangThaiRepository.findAll(spec);
    }

    // ---------- 4.8.2 Doan ra / Doan vao ----------

    @Transactional(readOnly = true)
    public BaoCaoDoanRaVao.Response doanRaVao(LocalDate tu, LocalDate den, UUID doiTacId) {
        Specification<DoanVao> specVao = SpecUtils.and(
                DoanVaoSpecifications.chuaBiXoa(),
                DoanVaoSpecifications.doiTacBang(doiTacId),
                thoiGianTrongKhoang("thoiGianDen", tu, den));
        List<DoanVao> dsVao = doanVaoRepository.findAll(specVao, Sort.by("thoiGianDen"));

        Specification<DoanRa> specRa = SpecUtils.and(
                DoanRaSpecifications.chuaBiXoa(),
                DoanRaSpecifications.doiTacBang(doiTacId),
                thoiGianTrongKhoangDoanRa(tu, den));
        List<DoanRa> dsRa = doanRaRepository.findAll(specRa, Sort.by("thoiGianDi"));

        long tongKhachNuocNgoai = dsVao.stream().mapToLong(d -> d.getSoLuongNguoiNuocNgoai() == null ? 0 : d.getSoLuongNguoiNuocNgoai()).sum();
        long tongLuotCanBo = dsRa.stream().mapToLong(d -> d.getSoLuongDoan() == null ? 0 : d.getSoLuongDoan()).sum();
        long tongNgayCongTac = dsRa.stream().mapToLong(d -> d.getSoNgay() == null ? 0 : d.getSoNgay()).sum();

        Map<String, Long> theoQuocGia = new HashMap<>();
        for (DoanVao d : dsVao) {
            if (d.getQuocTich() != null) {
                for (String qg : d.getQuocTich()) {
                    theoQuocGia.merge(qg, 1L, Long::sum);
                }
            }
        }
        for (DoanRa d : dsRa) {
            if (d.getQuocGiaLamViec() != null) {
                theoQuocGia.merge(d.getQuocGiaLamViec(), 1L, Long::sum);
            }
        }

        Map<String, Long> theoThang = new HashMap<>();
        for (DoanVao d : dsVao) {
            themThang(theoThang, d.getThoiGianDen());
        }
        for (DoanRa d : dsRa) {
            themThang(theoThang, d.getThoiGianDi());
        }

        var tomTat = new BaoCaoDoanRaVao.TomTat(dsVao.size(), tongKhachNuocNgoai, dsRa.size(), tongLuotCanBo,
                tongNgayCongTac, theoQuocGia, theoThang);
        return new BaoCaoDoanRaVao.Response(tomTat, dsVao.stream().map(DoanVaoResponse::from).toList(),
                dsRa.stream().map(DoanRaResponse::from).toList());
    }

    private void themThang(Map<String, Long> map, LocalDate ngay) {
        if (ngay != null) {
            String key = ngay.getYear() + "-" + String.format("%02d", ngay.getMonthValue());
            map.merge(key, 1L, Long::sum);
        }
    }

    private Specification<DoanVao> thoiGianTrongKhoang(String truong, LocalDate tu, LocalDate den) {
        return (root, query, cb) -> {
            var predicates = new ArrayList<jakarta.persistence.criteria.Predicate>();
            if (tu != null) predicates.add(cb.greaterThanOrEqualTo(root.get(truong), tu));
            if (den != null) predicates.add(cb.lessThanOrEqualTo(root.get(truong), den));
            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }

    private Specification<DoanRa> thoiGianTrongKhoangDoanRa(LocalDate tu, LocalDate den) {
        return (root, query, cb) -> {
            var predicates = new ArrayList<jakarta.persistence.criteria.Predicate>();
            if (tu != null) predicates.add(cb.greaterThanOrEqualTo(root.get("thoiGianDi"), tu));
            if (den != null) predicates.add(cb.lessThanOrEqualTo(root.get("thoiGianDi"), den));
            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }

    // ---------- 4.8.3 Thoi han MoU theo doi tac ----------

    @Transactional(readOnly = true)
    public List<MouResponse> thoiHanMouTheoDoiTac(Integer thangToi) {
        Specification<MouTrangThai> spec = SpecUtils.and(
                (root, query, cb) -> cb.notEqual(root.get("trangThai"), TrangThaiMou.DA_HET_HAN));
        List<MouTrangThai> ds = mouTrangThaiRepository.findAll(spec);
        if (thangToi != null) {
            LocalDate gioiHan = LocalDate.now().plusMonths(thangToi);
            ds = ds.stream()
                    .filter(m -> m.getNgayHetHan() == null || !m.getNgayHetHan().isAfter(gioiHan))
                    .toList();
        }
        return ds.stream()
                .sorted((a, b) -> {
                    int cmp = a.getTenDoiTac().compareToIgnoreCase(b.getTenDoiTac());
                    if (cmp != 0) return cmp;
                    if (a.getNgayHetHan() == null) return 1;
                    if (b.getNgayHetHan() == null) return -1;
                    return a.getNgayHetHan().compareTo(b.getNgayHetHan());
                })
                .map(MouResponse::from)
                .toList();
    }
}
