package com.ttloc.htkhcn.crawlphapluat;

import java.util.UUID;

public record TuKhoaResponse(UUID id, String tuKhoa, boolean hoatDong) {
    public static TuKhoaResponse from(TuKhoaCrawlPhapLuat t) {
        return new TuKhoaResponse(t.getId(), t.getTuKhoa(), t.isHoatDong());
    }
}
