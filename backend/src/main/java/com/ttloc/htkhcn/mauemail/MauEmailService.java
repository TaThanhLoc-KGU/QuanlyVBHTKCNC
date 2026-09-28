package com.ttloc.htkhcn.mauemail;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ttloc.htkhcn.common.exception.DuplicateResourceException;
import com.ttloc.htkhcn.common.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MauEmailService {

    private static final Pattern PLACEHOLDER = Pattern.compile("\\{\\{(\\w+)\\}\\}");

    private final MauEmailRepository mauEmailRepository;

    @Transactional(readOnly = true)
    public List<MauEmail> danhSach() {
        return mauEmailRepository.findAllByOrderByTenMauAsc();
    }

    @Transactional
    public MauEmail tao(MauEmailRequest request) {
        if (mauEmailRepository.existsByMaIgnoreCase(request.ma())) {
            throw new DuplicateResourceException("Ma mau email da ton tai: " + request.ma());
        }
        MauEmail me = new MauEmail();
        gan(me, request);
        me.setNgayTao(OffsetDateTime.now());
        return mauEmailRepository.save(me);
    }

    @Transactional
    public MauEmail sua(UUID id, MauEmailRequest request) {
        MauEmail me = timHoacLoi(id);
        if (mauEmailRepository.existsByMaIgnoreCaseAndIdNot(request.ma(), id)) {
            throw new DuplicateResourceException("Ma mau email da ton tai: " + request.ma());
        }
        gan(me, request);
        me.setNgaySua(OffsetDateTime.now());
        return mauEmailRepository.save(me);
    }

    @Transactional
    public void xoa(UUID id) {
        mauEmailRepository.delete(timHoacLoi(id));
    }

    private void gan(MauEmail me, MauEmailRequest request) {
        me.setMa(request.ma());
        me.setTenMau(request.tenMau());
        me.setTieuDe(request.tieuDe());
        me.setNoiDung(request.noiDung());
        me.setMoTa(request.moTa());
        me.setHoatDong(request.hoatDong() == null || request.hoatDong());
    }

    private MauEmail timHoacLoi(UUID id) {
        return mauEmailRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay mau email: " + id));
    }

    /** Thay the {{placeholder}} trong 1 chuoi mau bang gia tri tuong ung trong
     * map - placeholder khong co trong map duoc GIU NGUYEN (khong xoa mat) de
     * de nhan ra loi soan mau thieu du lieu. */
    public String thayThe(String mauChuoi, Map<String, String> giaTri) {
        Matcher m = PLACEHOLDER.matcher(mauChuoi);
        StringBuilder ketQua = new StringBuilder();
        while (m.find()) {
            String key = m.group(1);
            String thay = giaTri.containsKey(key) ? giaTri.get(key) : m.group();
            m.appendReplacement(ketQua, Matcher.quoteReplacement(thay));
        }
        m.appendTail(ketQua);
        return ketQua.toString();
    }
}
