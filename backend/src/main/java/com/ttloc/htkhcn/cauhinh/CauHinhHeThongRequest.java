package com.ttloc.htkhcn.cauhinh;

import jakarta.validation.constraints.NotBlank;

public record CauHinhHeThongRequest(@NotBlank(message = "Gia tri khong duoc de trong") String giaTri) {
}
