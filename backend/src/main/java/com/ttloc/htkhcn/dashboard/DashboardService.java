package com.ttloc.htkhcn.dashboard;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ttloc.htkhcn.mou.MouTrangThai;
import com.ttloc.htkhcn.mou.MouTrangThaiRepository;
import com.ttloc.htkhcn.mou.TrangThaiMou;

import lombok.RequiredArgsConstructor;

/** SPEC muc 4.6: trang tong quan Dashboard. */
@Service
@RequiredArgsConstructor
public class DashboardService {

    private static final int SO_LUONG_WIDGET = 15;
    private static final DateTimeFormatter THANG_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM");

    private final DashboardTongQuanRepository dashboardTongQuanRepository;
    private final MouDenHanTheoThangRepository mouDenHanTheoThangRepository;
    private final MouTrangThaiRepository mouTrangThaiRepository;

    @Transactional(readOnly = true)
    public DashboardResponse layTongQuan() {
        DashboardTongQuan tq = dashboardTongQuanRepository.findAll().stream().findFirst().orElse(null);

        List<DashboardResponse.MouTheoThangDto> theoThang = mouDenHanTheoThangRepository.findAllByOrderByThangAsc()
                .stream()
                .map(m -> new DashboardResponse.MouTheoThangDto(
                        m.getThang().format(THANG_FORMAT), m.getSoLuongMouDenHan() == null ? 0 : m.getSoLuongMouDenHan()))
                .toList();

        List<MouWidgetDto> widget = layWidgetMouSapHetHan();

        if (tq == null) {
            return new DashboardResponse(0, 0, 0, 0, 0, 0, 0, 0, 0, null, theoThang, widget);
        }
        return new DashboardResponse(
                gt(tq.getTongVanBanDhkgHieuLuc()), gt(tq.getTongVbplVnHieuLuc()), gt(tq.getTongMouConHieuLuc()),
                gt(tq.getTongMouSapHetHan()), gt(tq.getTongMouDaHetHan()), gt(tq.getTongDoanVaoNamHienTai()),
                gt(tq.getTongKhachNuocNgoaiNamHienTai()), gt(tq.getTongDoanRaNamHienTai()),
                gt(tq.getTongLuotCanBoDiCongTacNamHienTai()), tq.getLamMoiLuc(), theoThang, widget);
    }

    private List<MouWidgetDto> layWidgetMouSapHetHan() {
        Specification<MouTrangThai> spec = (root, query, cb) -> cb.and(
                cb.isNotNull(root.get("ngayHetHan")),
                cb.notEqual(root.get("trangThai"), TrangThaiMou.DA_HET_HAN));
        return mouTrangThaiRepository.findAll(spec, Sort.by(Sort.Direction.ASC, "soNgayConLai")).stream()
                .limit(SO_LUONG_WIDGET)
                .map(this::toWidgetDto)
                .toList();
    }

    private MouWidgetDto toWidgetDto(MouTrangThai m) {
        Integer phanTram = tinhPhanTramThoiGianDaQua(m.getNgayBanHanh(), m.getNgayHetHan());
        return new MouWidgetDto(m.getId(), m.getTenDoiTac(), m.getNgayBanHanh(), m.getNgayHetHan(),
                m.getSoNgayConLai(), m.getTrangThai(), phanTram);
    }

    private Integer tinhPhanTramThoiGianDaQua(LocalDate tu, LocalDate den) {
        if (tu == null || den == null || !den.isAfter(tu)) {
            return null;
        }
        long tongSo = den.toEpochDay() - tu.toEpochDay();
        long daQua = LocalDate.now().toEpochDay() - tu.toEpochDay();
        int phanTram = (int) Math.round(100.0 * daQua / tongSo);
        return Math.max(0, Math.min(100, phanTram));
    }

    private long gt(Long v) {
        return v == null ? 0 : v;
    }
}
