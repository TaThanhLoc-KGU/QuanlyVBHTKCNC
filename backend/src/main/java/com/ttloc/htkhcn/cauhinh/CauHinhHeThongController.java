package com.ttloc.htkhcn.cauhinh;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ttloc.htkhcn.common.exception.ResourceNotFoundException;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/** Quan ly cau hinh he thong (SPEC muc 10) - chi Admin xem/sua duoc, cac ma
 * cau hinh la co dinh (do PL/pgSQL doc theo ma cu the) nen khong ho tro tao moi. */
@RestController
@RequestMapping("/api/cau-hinh-he-thong")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class CauHinhHeThongController {

    private final CauHinhHeThongRepository repository;

    @GetMapping
    public List<CauHinhHeThong> danhSach() {
        return repository.findAll(Sort.by("ma"));
    }

    @PutMapping("/{ma}")
    public CauHinhHeThong sua(@PathVariable String ma, @Valid @RequestBody CauHinhHeThongRequest request) {
        CauHinhHeThong ch = repository.findById(ma)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay cau hinh: " + ma));
        ch.setGiaTri(request.giaTri());
        ch.setNgaySua(OffsetDateTime.now());
        return repository.save(ch);
    }
}
