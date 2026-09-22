package com.ttloc.htkhcn.excelimport;

import java.util.List;
import java.util.UUID;

public record ImportConfirmResponse(
        UUID phienImportId,
        int tongSoDong,
        int soDongThanhCong,
        int soDongLoi,
        List<RowError> loi) {

    public record RowError(int soDong, String loi) {
    }
}
