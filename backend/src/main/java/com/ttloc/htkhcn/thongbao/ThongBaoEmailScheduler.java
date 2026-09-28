package com.ttloc.htkhcn.thongbao;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ttloc.htkhcn.mauemail.MauEmail;
import com.ttloc.htkhcn.mauemail.MauEmailRepository;
import com.ttloc.htkhcn.mauemail.MauEmailService;
import com.ttloc.htkhcn.user.NguoiDung;
import com.ttloc.htkhcn.user.NguoiDungRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Gui email gop cho cac thong_bao chua gui (SPEC muc 4.7.2: "Neu nhieu MoU
 * cung den han trong 1 ngay, gop thanh 1 email tong hop"). Ham PL/pgSQL
 * sp_quet_mou_sap_het_han() (V12) da tao san hang thong_bao voi da_gui_email=
 * false - o day chi con nhiem vu doc + gui + danh dau da gui.
 *
 * Dung polling @Scheduled thay vi LISTEN/NOTIFY that su tren 1 thread rieng -
 * don gian hoa nhat quan voi cach da chon cho MouCanhBaoScheduler (SPEC muc
 * 4.7.2 cung cho phep "polling don gian hon neu chua can SSE ngay").
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ThongBaoEmailScheduler {

    private static final String MA_MAU_THONG_BAO_MOU_GOP = "THONG_BAO_MOU_GOP";

    private final ThongBaoRepository thongBaoRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final MauEmailRepository mauEmailRepository;
    private final MauEmailService mauEmailService;
    private final JavaMailSender mailSender;

    @Value("${app.mail.enabled}")
    private boolean mailEnabled;

    @Value("${app.mail.from}")
    private String mailFrom;

    @Scheduled(fixedRateString = "${app.scheduling.email-thong-bao-fixed-rate-ms}")
    @Transactional
    public void guiEmailThongBaoChuaGui() {
        List<ThongBao> chuaGui = thongBaoRepository.findByDaGuiEmailFalse();
        if (chuaGui.isEmpty()) {
            return;
        }
        Map<UUID, List<ThongBao>> theoNguoiNhan = chuaGui.stream()
                .collect(Collectors.groupingBy(ThongBao::getNguoiNhanId));

        for (Map.Entry<UUID, List<ThongBao>> entry : theoNguoiNhan.entrySet()) {
            NguoiDung nguoiNhan = nguoiDungRepository.findById(entry.getKey()).orElse(null);
            if (nguoiNhan == null || nguoiNhan.getDeletedAt() != null) {
                continue;
            }
            List<ThongBao> danhSach = entry.getValue();
            if (!mailEnabled) {
                log.info("MAIL_ENABLED=false, bo qua gui email cho {} ({} thong bao dang cho)",
                        nguoiNhan.getEmail(), danhSach.size());
                continue;
            }
            try {
                guiEmailGop(nguoiNhan, danhSach);
                danhSach.forEach(tb -> tb.setDaGuiEmail(true));
                thongBaoRepository.saveAll(danhSach);
            } catch (Exception ex) {
                log.error("Gui email thong bao cho {} bi loi: {}", nguoiNhan.getEmail(), ex.getMessage());
            }
        }
    }

    private void guiEmailGop(NguoiDung nguoiNhan, List<ThongBao> danhSach) {
        StringBuilder danhSachChuoi = new StringBuilder();
        for (ThongBao tb : danhSach) {
            danhSachChuoi.append("- [").append(tb.getMucDo()).append("] ").append(tb.getTieuDe());
            if (tb.getNoiDung() != null) {
                danhSachChuoi.append(": ").append(tb.getNoiDung());
            }
            danhSachChuoi.append("\n");
        }
        Map<String, String> giaTri = Map.of(
                "hoTen", nguoiNhan.getHoTen(),
                "soLuong", String.valueOf(danhSach.size()),
                "danhSach", danhSachChuoi.toString());

        // Uu tien mau email tu quan tri (mau_email, quan ly qua man hinh Mau
        // email) - fallback ve noi dung code cung neu chua cau hinh mau, de
        // KHONG lam hong tinh nang gui email dang chay san.
        var mauTuyChon = mauEmailRepository.findByMaAndHoatDongTrue(MA_MAU_THONG_BAO_MOU_GOP);
        String tieuDe;
        String noiDung;
        if (mauTuyChon.isPresent()) {
            MauEmail mau = mauTuyChon.get();
            tieuDe = mauEmailService.thayThe(mau.getTieuDe(), giaTri);
            noiDung = mauEmailService.thayThe(mau.getNoiDung(), giaTri);
        } else {
            tieuDe = "[P.HTKHCN] Ban co " + danhSach.size() + " thong bao MoU can luu y";
            noiDung = "Xin chao " + nguoiNhan.getHoTen() + ",\n\n"
                    + "He thong ghi nhan cac thong bao sau:\n\n" + danhSachChuoi
                    + "\nVui long dang nhap he thong de xem chi tiet.\n";
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailFrom);
        message.setTo(nguoiNhan.getEmail());
        message.setSubject(tieuDe);
        message.setText(noiDung);
        mailSender.send(message);
    }
}
