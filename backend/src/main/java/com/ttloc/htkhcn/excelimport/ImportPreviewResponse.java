package com.ttloc.htkhcn.excelimport;

import java.util.List;

public record ImportPreviewResponse(
        int tongSoDong,
        int soDongHopLe,
        int soDongLoi,
        List<ImportRowResult> dong) {
}
