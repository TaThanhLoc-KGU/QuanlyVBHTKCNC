package com.ttloc.htkhcn.excelimport;

import java.util.List;
import java.util.Map;

/**
 * Ket qua parse 1 dong Excel. {@code hopLe} = true khi khong co loi - luc do
 * {@code duLieu} chua cac gia tri da parse (hien thi preview) va {@code doiTuong}
 * la request DTO san sang dua vao *Service.tao() hien co cua module tuong ung.
 */
public record ImportRowResult(
        int soDong,
        Map<String, Object> duLieu,
        List<String> loi,
        Object doiTuong,
        String ghiChuGoiYDoiTac) {

    public boolean hopLe() {
        return loi.isEmpty();
    }
}
