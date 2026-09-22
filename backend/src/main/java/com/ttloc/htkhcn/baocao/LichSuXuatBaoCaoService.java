package com.ttloc.htkhcn.baocao;

import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LichSuXuatBaoCaoService {

    private final LichSuXuatBaoCaoRepository lichSuXuatBaoCaoRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public void ghiLai(String loaiBaoCao, UUID nguoiXuatId, DinhDangBaoCao dinhDang, Map<String, String> thamSo) {
        LichSuXuatBaoCao lich = new LichSuXuatBaoCao();
        lich.setLoaiBaoCao(loaiBaoCao);
        lich.setNguoiXuatId(nguoiXuatId);
        lich.setDinhDang(dinhDang.name());
        try {
            lich.setThamSo(objectMapper.writeValueAsString(thamSo));
        } catch (Exception ex) {
            lich.setThamSo("{}");
        }
        lichSuXuatBaoCaoRepository.save(lich);
    }

    @Transactional(readOnly = true)
    public Page<LichSuXuatBaoCao> danhSach(Pageable pageable) {
        return lichSuXuatBaoCaoRepository.findAllByOrderByThoiDiemDesc(pageable);
    }
}
