package com.ttloc.htkhcn.thongbao;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.access.AccessDeniedException;

import com.ttloc.htkhcn.common.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

/** Chuong thong bao web (SPEC muc 4.7.2). */
@Service
@RequiredArgsConstructor
public class ThongBaoService {

    private final ThongBaoRepository thongBaoRepository;

    @Transactional(readOnly = true)
    public Page<ThongBao> danhSach(UUID nguoiNhanId, Boolean chiChuaDoc, Pageable pageable) {
        if (chiChuaDoc != null && chiChuaDoc) {
            return thongBaoRepository.findByNguoiNhanIdAndDaDocWebOrderByNgayTaoDesc(nguoiNhanId, false, pageable);
        }
        return thongBaoRepository.findByNguoiNhanIdOrderByNgayTaoDesc(nguoiNhanId, pageable);
    }

    @Transactional(readOnly = true)
    public long soLuongChuaDoc(UUID nguoiNhanId) {
        return thongBaoRepository.countByNguoiNhanIdAndDaDocWebFalse(nguoiNhanId);
    }

    @Transactional
    public void danhDauDaDoc(UUID id, UUID nguoiNhanId) {
        ThongBao tb = thongBaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay thong bao: " + id));
        if (!tb.getNguoiNhanId().equals(nguoiNhanId)) {
            throw new AccessDeniedException("Day khong phai thong bao cua ban");
        }
        tb.setDaDocWeb(true);
        tb.setNgayDoc(Instant.now());
        thongBaoRepository.save(tb);
    }

    @Transactional
    public void danhDauTatCaDaDoc(UUID nguoiNhanId) {
        thongBaoRepository.danhDauTatCaDaDoc(nguoiNhanId);
    }
}
