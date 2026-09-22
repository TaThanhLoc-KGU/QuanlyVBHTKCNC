package com.ttloc.htkhcn.crawlphapluat;

import jakarta.validation.constraints.NotBlank;

public record TuKhoaRequest(@NotBlank(message = "Tu khoa khong duoc de trong") String tuKhoa) {
}
