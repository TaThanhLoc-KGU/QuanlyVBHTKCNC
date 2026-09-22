package com.ttloc.htkhcn.excelimport;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ttloc.htkhcn.common.ModuleKey;
import com.ttloc.htkhcn.doanra.DoanRaRequest;
import com.ttloc.htkhcn.doanra.DoanRaService;
import com.ttloc.htkhcn.doanvao.DoanVaoRequest;
import com.ttloc.htkhcn.doanvao.DoanVaoService;
import com.ttloc.htkhcn.doitac.DoiTac;
import com.ttloc.htkhcn.doitac.DoiTacRepository;
import com.ttloc.htkhcn.doitac.DoiTacRequest;
import com.ttloc.htkhcn.doitac.DoiTacService;
import com.ttloc.htkhcn.mou.MouRequest;
import com.ttloc.htkhcn.mou.MouService;
import com.ttloc.htkhcn.phienimport.PhienImport;
import com.ttloc.htkhcn.phienimport.PhienImportBanGhiService;
import com.ttloc.htkhcn.phienimport.PhienImportRepository;
import com.ttloc.htkhcn.vanbandhkg.VanBanDhkgRequest;
import com.ttloc.htkhcn.vanbandhkg.VanBanDhkgService;
import com.ttloc.htkhcn.vbplvn.VbplVnRequest;
import com.ttloc.htkhcn.vbplvn.VbplVnService;

import lombok.RequiredArgsConstructor;

/**
 * Ghi 1 dong import trong 1 TRANSACTION RIENG (REQUIRES_NEW) - tach khoi
 * transaction cua ExcelImportService.xacNhan(). Neu khong tach, 1 dong loi
 * (vi du trung "So hieu") se danh dau CA transaction ngoai la rollback-only
 * (hanh vi mac dinh cua Spring khi mot @Transactional method ben trong nem
 * RuntimeException), khien TOAN BO phien import bi rollback dù cac dong khac
 * hop le - trai voi thiet ke "bao loi tung dong, dong con lai van import duoc".
 */
@Service
@RequiredArgsConstructor
public class ImportRowWriter {

    private final DoiTacService doiTacService;
    private final VanBanDhkgService vanBanDhkgService;
    private final VbplVnService vbplVnService;
    private final MouService mouService;
    private final DoanVaoService doanVaoService;
    private final DoanRaService doanRaService;
    private final PhienImportBanGhiService phienImportBanGhiService;
    private final PhienImportRepository phienImportRepository;
    private final DoiTacRepository doiTacRepository;

    // REQUIRES_NEW de LUON commit ngay, bat ke co dang o trong 1 transaction
    // bao ngoai (test @Transactional cua BaseIntegrationTest) hay khong - cac
    // dong ghi ben duoi (cung REQUIRES_NEW, tren connection rieng) can thay
    // duoc ban ghi phien_import nay TRUOC KHI no ton tai theo tham chieu FK.
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public PhienImport taoPhien(PhienImport phien) {
        return phienImportRepository.save(phien);
    }

    // Cung REQUIRES_NEW de nhat quan voi taoPhien/ghiMotDong - neu chi la
    // .save() thong thuong trong 1 transaction bao ngoai dang mo (vi du test),
    // ket qua cuoi (soDongThanhCong...) se bi rollback theo test dù cac dong
    // chi tiet da thuc su commit, gay trang thai khong nhat quan.
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void capNhatKetQua(UUID phienId, int soDongThanhCong, int soDongLoi, String chiTietLoiJson) {
        PhienImport phien = phienImportRepository.findById(phienId).orElseThrow();
        phien.setSoDongThanhCong(soDongThanhCong);
        phien.setSoDongLoi(soDongLoi);
        phien.setChiTietLoi(chiTietLoiJson);
        phienImportRepository.save(phien);
    }

    // Dung khi MoU/Doan vao/Doan ra tu tao moi 1 doi tac (khong qua ten da co)
    // trong luc PARSE file (truoc khi vao vong lap ghi tung dong) - phai commit
    // ngay vi trigger REQUIRES_NEW khac se doc lai doi tac nay qua doi_tac_id.
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public DoiTac taoDoiTacMoiNgay(DoiTac doiTac) {
        return doiTacRepository.save(doiTac);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public UUID ghiMotDong(UUID phienId, ModuleKey moDun, Object doiTuong) {
        UUID idMoi = switch (moDun) {
            case DOI_TAC -> doiTacService.tao((DoiTacRequest) doiTuong).id();
            case VAN_BAN_DHKG -> vanBanDhkgService.tao((VanBanDhkgRequest) doiTuong).id();
            case VBPL_VN -> vbplVnService.tao((VbplVnRequest) doiTuong).id();
            case MOU -> mouService.tao((MouRequest) doiTuong).id();
            case DOAN_VAO -> doanVaoService.tao((DoanVaoRequest) doiTuong).id();
            case DOAN_RA -> doanRaService.tao((DoanRaRequest) doiTuong).id();
            case CONG_VAN_DEN -> throw new com.ttloc.htkhcn.common.exception.BadRequestException(
                    "Cong van den khong ho tro nhap tu Excel");
        };
        phienImportBanGhiService.ghiNhan(phienId, moDun, idMoi);
        return idMoi;
    }
}
