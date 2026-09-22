package com.ttloc.htkhcn.tailieu;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.ttloc.htkhcn.common.exception.BadRequestException;
import com.ttloc.htkhcn.common.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaiLieuDinhKemService {

    private final TaiLieuDinhKemRepository taiLieuDinhKemRepository;

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Transactional
    public TaiLieuDinhKem taiLen(String bang, UUID banGhiId, MultipartFile file, UUID nguoiTaoId) {
        BangDinhKem.tuTen(bang);
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File dinh kem khong duoc de trong");
        }
        try {
            return luuVaoDia(bang, banGhiId, file.getOriginalFilename(), file.getBytes(), file.getContentType(), nguoiTaoId);
        } catch (IOException ex) {
            throw new UncheckedIOException("Khong doc duoc file tai len: " + file.getOriginalFilename(), ex);
        }
    }

    /** Dung khi file lay tu 1 nguon ngoai server (vi du tai ve tu CongVan de
     * dong bo, xem congvan.CongVanImportService) thay vi tu MultipartFile cua
     * request nguoi dung tu tay tai len. */
    @Transactional
    public TaiLieuDinhKem taiLenTuBytes(
            String bang, UUID banGhiId, String tenFileGoc, byte[] noiDung, String loaiMime, UUID nguoiTaoId) {
        BangDinhKem.tuTen(bang);
        if (noiDung == null || noiDung.length == 0) {
            throw new BadRequestException("File dinh kem khong duoc de trong");
        }
        return luuVaoDia(bang, banGhiId, tenFileGoc, noiDung, loaiMime, nguoiTaoId);
    }

    private TaiLieuDinhKem luuVaoDia(
            String bang, UUID banGhiId, String tenFileGocRaw, byte[] noiDung, String loaiMime, UUID nguoiTaoId) {
        String tenFileGoc = sanitize(tenFileGocRaw);
        try {
            Path thuMuc = Path.of(uploadDir, bang, banGhiId.toString());
            Files.createDirectories(thuMuc);
            String tenFileLuu = UUID.randomUUID() + "_" + tenFileGoc;
            Path duongDan = thuMuc.resolve(tenFileLuu);
            Files.write(duongDan, noiDung);

            TaiLieuDinhKem tl = new TaiLieuDinhKem();
            tl.setBang(bang);
            tl.setBanGhiId(banGhiId);
            tl.setTenFile(tenFileGoc);
            tl.setDuongDan(duongDan.toString());
            tl.setKichThuocByte((long) noiDung.length);
            tl.setLoaiMime(loaiMime);
            tl.setNguoiTaoId(nguoiTaoId);
            return taiLieuDinhKemRepository.save(tl);
        } catch (IOException ex) {
            throw new UncheckedIOException("Khong luu duoc file: " + tenFileGoc, ex);
        }
    }

    public List<TaiLieuDinhKem> danhSach(String bang, UUID banGhiId) {
        BangDinhKem.tuTen(bang);
        return taiLieuDinhKemRepository.findByBangAndBanGhiIdOrderByNgayTaoDesc(bang, banGhiId);
    }

    public TaiLieuDinhKem layTheoId(UUID id) {
        return taiLieuDinhKemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay tai lieu dinh kem: " + id));
    }

    @Transactional
    public void xoa(UUID id) {
        TaiLieuDinhKem tl = layTheoId(id);
        taiLieuDinhKemRepository.delete(tl);
        try {
            Files.deleteIfExists(Path.of(tl.getDuongDan()));
        } catch (IOException ex) {
            throw new UncheckedIOException("Khong xoa duoc file tren dia: " + tl.getDuongDan(), ex);
        }
    }

    private String sanitize(String tenFile) {
        if (tenFile == null || tenFile.isBlank()) {
            return "tep-khong-ten";
        }
        return Path.of(tenFile).getFileName().toString().replaceAll("[\\\\/:*?\"<>|]", "_");
    }
}
