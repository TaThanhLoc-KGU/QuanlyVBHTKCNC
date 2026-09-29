// Cac kieu du lieu khop voi DTO tra ve tu backend (Jackson serialize camelCase).

export type VaiTro = 'ADMIN' | 'EDITOR' | 'VIEWER' | string
export type ModuleKey =
  | 'DOI_TAC'
  | 'VAN_BAN_DHKG'
  | 'VBPL_VN'
  | 'MOU'
  | 'DOAN_VAO'
  | 'DOAN_RA'
  | 'CONG_VAN_DEN'
  | 'TU_DIEN'
  | 'THANH_VIEN_PHU_TRACH'
  | 'MAU_EMAIL'
  | 'DOAN_DIA_PHUONG'
  | 'VISA'
  | 'SU_KIEN'
  | 'DOI_TAC_CA_NHAN'
  | 'DE_TAI_NCKH'

export type LoaiTuDien =
  | 'MUC_TIEU_DOAN_RA'
  | 'MAU_CONG_VAN_QUYET_DINH'
  | 'MUC_DICH_DEN'
  | 'NGUON_KINH_PHI'
  | 'NOI_GUI_CONG_VAN_DEN'
  | 'NOI_GUI_CONG_VAN_DEN_BO_SUNG'
  | 'QUOC_GIA'
  | 'TIEN_TE'
  | 'HOAT_DONG_KY_KET'
  | 'NGON_NGU_KY_KET'
  | 'VAI_TRO_THANH_VIEN'
  | 'LOAI_DOI_TAC'
  | 'LOAI_SU_KIEN'
  | 'LINH_VUC_HOAT_DONG'
  | 'PHAN_LOAI_DE_TAI'
  | 'LOAI_HINH_NGHIEN_CUU'
  | 'VAI_TRO_HOI_DONG'
  | 'VAI_TRO_THANH_VIEN_DE_TAI'
  | 'MUC_XEP_LOAI_DE_TAI'
  | 'LOAI_SAN_PHAM_KHOA_HOC'

export interface TuDien {
  id: string
  loai: LoaiTuDien
  ma: string | null
  ten: string
  moTa: string | null
  thuTu: number
  hoatDong: boolean
  ngayTao: string
  ngaySua: string | null
}

export interface PageResponse<T> {
  content: T[]
  page: number
  size: number
  totalElements: number
  totalPages: number
}

export interface LoginResponse {
  accessToken: string
  refreshToken: string
  id: string
  tenDangNhap: string
  hoTen: string
  vaiTro: string[]
  phaiDoiMatKhau: boolean
}

export interface CurrentUser {
  id: string
  tenDangNhap: string
  hoTen: string
  vaiTro: string[]
  bienTapMoDun: ModuleKey[]
}

export interface NguoiDung {
  id: string
  tenDangNhap: string
  email: string
  hoTen: string
  trangThai: 'HOAT_DONG' | 'TAM_KHOA'
  phaiDoiMatKhau: boolean
  vaiTro: string[]
  bienTapMoDun: ModuleKey[]
  ngayTao: string
}

// ---------- Doi tac ----------

export type LoaiDoiTac = 'TRONG_NUOC' | 'NGOAI_NUOC'

export interface DoiTac {
  id: string
  tenDoiTac: string
  loaiDoiTac: LoaiDoiTac
  quocGia: string | null
  diaChi: string | null
  thongTinLienHe: string | null
  ghiChu: string | null
  loaiTuDienId: string | null
  loaiTuDienTen: string | null
  nguoiTaoId: string | null
  ngayTao: string
  nguoiSuaId: string | null
  ngaySua: string | null
}

export interface DoiTacRequest {
  tenDoiTac: string
  loaiDoiTac: LoaiDoiTac
  quocGia?: string | null
  diaChi?: string | null
  thongTinLienHe?: string | null
  ghiChu?: string | null
  loaiTuDienId?: string | null
}

// ---------- Danh muc loai van ban ----------

export type PhamViVanBan = 'DHKG' | 'VBPL'

export interface LoaiVanBan {
  id: string
  ma: string
  ten: string
  phamVi: PhamViVanBan
  thuTu: number
}

export type TinhTrangHieuLuc =
  | 'CON_HIEU_LUC'
  | 'HET_HIEU_LUC_TOAN_BO'
  | 'HET_HIEU_LUC_MOT_PHAN'
  | 'BI_THAY_THE'
  | 'DA_BI_BAI_BO'

// ---------- Van ban DHKG / VBPL VN ----------

export interface VanBanDhkg {
  id: string
  soHieu: string
  tenVanBan: string
  loaiVanBanId: string
  tenLoaiVanBan: string
  ngayBanHanh: string
  ngayHieuLuc: string | null
  tinhTrangHieuLuc: TinhTrangHieuLuc
  coQuanBanHanh: string
  ghiChu: string | null
  noiDungChinh: string | null
  nguoiTaoId: string | null
  ngayTao: string
  nguoiSuaId: string | null
  ngaySua: string | null
}

export interface VbplVn extends VanBanDhkg {
  ngayDoiChieuGanNhat: string | null
}

export interface VanBanRequest {
  soHieu: string
  tenVanBan: string
  loaiVanBanId: string
  ngayBanHanh: string
  ngayHieuLuc?: string | null
  tinhTrangHieuLuc: TinhTrangHieuLuc
  coQuanBanHanh: string
  ghiChu?: string | null
  noiDungChinh?: string | null
}

// ---------- MoU ----------

export type PhamViHopTac = 'TOAN_DIEN' | 'THEO_LINH_VUC'
export type TrangThaiMou = 'CON_HIEU_LUC' | 'SAP_HET_HAN' | 'DA_HET_HAN'

export interface Mou {
  id: string
  doiTacId: string
  tenDoiTac: string
  loaiDoiTac: LoaiDoiTac
  doiTacQuocGia: string | null
  doiTacDiaChi: string | null
  tenTaiLieu: string | null
  ngayBanHanh: string
  ngayHetHan: string | null
  caNhanDauMoi: string | null
  donViThucHien: string | null
  phamViHopTac: PhamViHopTac | null
  linhVucHopTac: string | null
  dauMoiGhiTrongMou: string | null
  daiDienKguKy: string | null
  thoiHanHieuLuc: string | null
  soCongVan: string | null
  hoatDongKyKetTuDienId: string | null
  hoatDongKyKetTen: string | null
  ngonNguKyKetTuDienId: string | null
  ngonNguKyKetTen: string | null
  trangThai: TrangThaiMou
  soNgayConLai: number | null
  nguoiTaoId: string | null
  ngayTao: string
  nguoiSuaId: string | null
  ngaySua: string | null
}

export interface MouRequest {
  doiTacId: string
  tenTaiLieu?: string | null
  ngayBanHanh: string
  ngayHetHan?: string | null
  caNhanDauMoi?: string | null
  donViThucHien?: string | null
  phamViHopTac?: PhamViHopTac | null
  linhVucHopTac?: string | null
  dauMoiGhiTrongMou?: string | null
  daiDienKguKy?: string | null
  thoiHanHieuLuc?: string | null
  soCongVan?: string | null
  hoatDongKyKetTuDienId?: string | null
  ngonNguKyKetTuDienId?: string | null
}

// ---------- Doan vao / Doan ra ----------

export interface DoanVao {
  id: string
  tenDoan: string
  doiTacId: string | null
  tenDoiTac: string | null
  thoiGianDen: string
  thoiGianDi: string
  soLuongNguoiNuocNgoai: number
  soLuongNguoiVietNam: number | null
  quocTich: string[]
  noiDungLamViec: string | null
  mucDichTuDienId: string | null
  mucDichTen: string | null
  nam: number
  soNgay: number
  nguoiTaoId: string | null
  ngayTao: string
  nguoiSuaId: string | null
  ngaySua: string | null
}

export interface DoanVaoRequest {
  tenDoan: string
  doiTacId?: string | null
  thoiGianDen: string
  thoiGianDi: string
  soLuongNguoiNuocNgoai: number
  soLuongNguoiVietNam?: number | null
  quocTich: string[]
  noiDungLamViec?: string | null
  mucDichTuDienId?: string | null
}

export interface DoanRa {
  id: string
  doiTacId: string
  tenDoiTac: string
  thoiGianDi: string
  thoiGianVe: string
  diaDiemDi: string | null
  diaDiemDen: string | null
  soLuongDoan: number
  thanhPhan: string | null
  quocGiaLamViec: string
  noiDungLamViec: string | null
  mucTieuTuDienId: string | null
  mucTieuTen: string | null
  nguonKinhPhiTuDienId: string | null
  nguonKinhPhiTen: string | null
  nam: number
  soNgay: number
  nguoiTaoId: string | null
  ngayTao: string
  nguoiSuaId: string | null
  ngaySua: string | null
}

export interface DoanRaRequest {
  doiTacId: string
  thoiGianDi: string
  thoiGianVe: string
  diaDiemDi?: string | null
  diaDiemDen?: string | null
  soLuongDoan: number
  thanhPhan?: string | null
  quocGiaLamViec: string
  noiDungLamViec?: string | null
  mucTieuTuDienId?: string | null
  nguonKinhPhiTuDienId?: string | null
}

// ---------- Dinh kem ----------

export interface TaiLieuDinhKem {
  id: string
  bang: string
  banGhiId: string
  tenFile: string
  duongDan: string
  kichThuocByte: number | null
  loaiMime: string | null
  nguoiTaoId: string | null
  ngayTao: string
}

// ---------- Thong bao ----------

export type MucDoCanhBao = 'INFO' | 'WARNING' | 'CRITICAL'

export interface ThongBao {
  id: string
  loai: string
  mucDo: MucDoCanhBao
  tieuDe: string
  noiDung: string | null
  bangLienQuan: string | null
  banGhiLienQuanId: string | null
  nguoiNhanId: string
  daDocWeb: boolean
  daGuiEmail: boolean
  ngayTao: string
  ngayDoc: string | null
}

// ---------- Import Excel ----------

export interface ImportRowResult {
  soDong: number
  duLieu: Record<string, unknown>
  loi: string[]
  doiTuong: unknown
  ghiChuGoiYDoiTac: string | null
}

export interface ImportPreviewResponse {
  tongSoDong: number
  soDongHopLe: number
  soDongLoi: number
  dong: ImportRowResult[]
}

export interface ImportConfirmResponse {
  phienImportId: string
  tongSoDong: number
  soDongThanhCong: number
  soDongLoi: number
  loi: { soDong: number; loi: string }[]
}

export interface PhienImport {
  id: string
  moDun: ModuleKey
  tenFile: string
  nguoiThucHienId: string | null
  thoiDiem: string
  tongSoDong: number | null
  soDongThanhCong: number | null
  soDongLoi: number | null
  trangThai: string
  coTheRollback: boolean
  daRollback: boolean
}

// ---------- Bao cao ----------

export interface BaoCaoMouTrongNamDong {
  tenDoiTac: string
  loaiDoiTac: LoaiDoiTac
  linhVucHopTac: string | null
  ngayKy: string
  ngayHetHan: string | null
  donViDauMoi: string | null
  trangThai: TrangThaiMou
}

export interface BaoCaoMouTrongNam {
  nam: number
  tomTat: {
    tongSoMouMoiKy: number
    theoLoaiDoiTac: Record<string, number>
    theoLinhVucHopTac: Record<string, number>
    tongSoMouCungKyNamTruoc: number
  }
  chiTiet: BaoCaoMouTrongNamDong[]
}

export interface BaoCaoDoanRaVao {
  tomTat: {
    tongSoDoanVao: number
    tongKhachNuocNgoaiDaDen: number
    tongSoDoanRa: number
    tongLuotCanBoDiCongTac: number
    tongSoNgayCongTacNuocNgoai: number
    theoQuocGia: Record<string, number>
    theoThang: Record<string, number>
  }
  doanVao: DoanVao[]
  doanRa: DoanRa[]
}

// ---------- Dashboard ----------

export interface MouWidgetDto {
  id: string
  tenDoiTac: string
  ngayBanHanh: string
  ngayHetHan: string
  soNgayConLai: number | null
  trangThai: TrangThaiMou
  phanTramThoiGianDaQua: number | null
}

export interface DashboardResponse {
  tongVanBanDhkgHieuLuc: number
  tongVbplVnHieuLuc: number
  tongMouConHieuLuc: number
  tongMouSapHetHan: number
  tongMouDaHetHan: number
  tongDoanVaoNamHienTai: number
  tongKhachNuocNgoaiNamHienTai: number
  tongDoanRaNamHienTai: number
  tongLuotCanBoDiCongTacNamHienTai: number
  tongVisaConHieuLuc: number
  tongVisaNamHienTai: number
  tongSuKienNamHienTai: number
  tongDoanDiaPhuongNamHienTai: number
  lamMoiLuc: string | null
  mouDenHanTheoThang: { thang: string; soLuong: number }[]
  widgetMouSapHetHan: MouWidgetDto[]
}

// ---------- Thanh vien phu trach ----------

export interface ThanhVienPhuTrach {
  id: string
  hoTen: string
  chucVu: string | null
  donVi: string | null
  email: string | null
  dienThoai: string | null
  vaiTroTuDienId: string | null
  vaiTroTen: string | null
  ghiChu: string | null
  hoatDong: boolean
  ngayTao: string
  ngaySua: string | null
}

export interface ThanhVienPhuTrachRequest {
  hoTen: string
  chucVu?: string | null
  donVi?: string | null
  email?: string | null
  dienThoai?: string | null
  vaiTroTuDienId?: string | null
  ghiChu?: string | null
  hoatDong?: boolean | null
}

// ---------- Mau email ----------

export interface MauEmail {
  id: string
  ma: string
  tenMau: string
  tieuDe: string
  noiDung: string
  moTa: string | null
  hoatDong: boolean
  ngayTao: string
  ngaySua: string | null
}

export interface MauEmailRequest {
  ma: string
  tenMau: string
  tieuDe: string
  noiDung: string
  moTa?: string | null
  hoatDong?: boolean | null
}

// ---------- Doan di dia phuong ----------

export interface DoanDiaPhuong {
  id: string
  tenDoan: string
  doiTacId: string | null
  tenDoiTac: string | null
  thoiGianDi: string
  thoiGianVe: string
  diaDiem: string | null
  mucTieuTuDienId: string | null
  mucTieuTen: string | null
  nguonKinhPhiTuDienId: string | null
  nguonKinhPhiTen: string | null
  thanhPhan: string | null
  noiDungLamViec: string | null
  ghiChu: string | null
  nam: number
  nguoiTaoId: string | null
  ngayTao: string
  nguoiSuaId: string | null
  ngaySua: string | null
}

export interface DoanDiaPhuongRequest {
  tenDoan: string
  doiTacId?: string | null
  thoiGianDi: string
  thoiGianVe: string
  diaDiem?: string | null
  mucTieuTuDienId?: string | null
  nguonKinhPhiTuDienId?: string | null
  thanhPhan?: string | null
  noiDungLamViec?: string | null
  ghiChu?: string | null
}

// ---------- Visa ----------

export type LoaiCapVisa = 'MOI' | 'GIA_HAN'

export interface Visa {
  id: string
  hoTen: string
  quocTich: string | null
  loaiCap: LoaiCapVisa
  ngayCap: string
  ngayHetHan: string | null
  coQuanCap: string | null
  mucDichTuDienId: string | null
  mucDichTen: string | null
  doanVaoId: string | null
  doanVaoTen: string | null
  ghiChu: string | null
  nam: number
  nguoiTaoId: string | null
  ngayTao: string
  nguoiSuaId: string | null
  ngaySua: string | null
}

export interface VisaRequest {
  hoTen: string
  quocTich?: string | null
  loaiCap: LoaiCapVisa
  ngayCap: string
  ngayHetHan?: string | null
  coQuanCap?: string | null
  mucDichTuDienId?: string | null
  doanVaoId?: string | null
  ghiChu?: string | null
}

// ---------- Su kien (gop Hoi nghi/Hoi thao + Thong tin su kien) ----------

export interface SuKien {
  id: string
  tenSuKien: string
  loaiSuKienTuDienId: string | null
  loaiSuKienTen: string | null
  linhVucTuDienId: string | null
  linhVucTen: string | null
  thoiGianBatDau: string
  thoiGianKetThuc: string | null
  diaDiem: string | null
  donViToChuc: string | null
  soLuongThamGia: number | null
  noiDung: string | null
  ghiChu: string | null
  nam: number
  nguoiTaoId: string | null
  ngayTao: string
  nguoiSuaId: string | null
  ngaySua: string | null
}

export interface SuKienRequest {
  tenSuKien: string
  loaiSuKienTuDienId?: string | null
  linhVucTuDienId?: string | null
  thoiGianBatDau: string
  thoiGianKetThuc?: string | null
  diaDiem?: string | null
  donViToChuc?: string | null
  soLuongThamGia?: number | null
  noiDung?: string | null
  ghiChu?: string | null
}

// ---------- Doi tac ca nhan ----------

export interface DoiTacCaNhan {
  id: string
  hoTen: string
  chucVu: string | null
  doiTacId: string | null
  tenDoiTac: string | null
  email: string | null
  dienThoai: string | null
  ghiChu: string | null
  hoatDong: boolean
  ngayTao: string
  ngaySua: string | null
}

export interface DoiTacCaNhanRequest {
  hoTen: string
  chucVu?: string | null
  doiTacId?: string | null
  email?: string | null
  dienThoai?: string | null
  ghiChu?: string | null
  hoatDong?: boolean | null
}

// ---------- Lich su thay doi ----------

export interface LichSuThayDoi {
  id: number
  bang: string
  banGhiId: string
  hanhDong: 'INSERT' | 'UPDATE' | 'DELETE'
  duLieuTruoc: string | null
  duLieuSau: string | null
  nguoiThucHienId: string | null
  thoiDiem: string
}

// ---------- De tai du an nghien cuu khoa hoc ----------

export interface LinhVucNghienCuu {
  id: string
  ma: string | null
  ten: string
  cap: 1 | 2 | 3
  chaId: string | null
  chaTen: string | null
  thuTu: number
  hoatDong: boolean
}

export interface LinhVucNghienCuuRequest {
  ma?: string | null
  ten: string
  cap: 1 | 2 | 3
  chaId?: string | null
  thuTu?: number | null
  hoatDong?: boolean | null
}

export type TrangThaiDeTai =
  | 'DE_XUAT'
  | 'DANG_THAM_DINH_KHOA'
  | 'DANG_THAM_DINH_CHUYEN_MON'
  | 'DA_TRUNG_TUYEN'
  | 'KHONG_TRUNG_TUYEN'
  | 'DA_KY_HOP_DONG'
  | 'DANG_THUC_HIEN'
  | 'CHO_NGHIEM_THU_CO_SO'
  | 'CHO_NGHIEM_THU_CHINH_THUC'
  | 'DA_NGHIEM_THU'
  | 'DA_THANH_LY'
  | 'BI_HUY'

export interface DeTai {
  id: string
  maDeTai: string | null
  tenDeTai: string
  chuNhiemId: string | null
  chuNhiemTen: string | null
  chuNhiemNgoai: string | null
  donViThucHien: string | null
  donViChuQuan: string | null
  phanLoaiTuDienId: string | null
  phanLoaiTen: string | null
  loaiHinhTuDienId: string | null
  loaiHinhTen: string | null
  nguonKinhPhiTuDienId: string | null
  nguonKinhPhiTen: string | null
  linhVucId: string | null
  linhVucTen: string | null
  namDeXuat: number | null
  thoiGianBatDau: string | null
  thoiGianKetThuc: string | null
  kinhPhiDeXuat: number | null
  kinhPhiDuyet: number | null
  mucTieu: string | null
  noiDung: string | null
  sanPhamDuKien: string | null
  trangThai: TrangThaiDeTai
  mucXepLoaiTuDienId: string | null
  mucXepLoaiTen: string | null
  lyDoHuy: string | null
  ghiChu: string | null
  nguoiTaoId: string | null
  ngayTao: string
  nguoiSuaId: string | null
  ngaySua: string | null
}

export interface DeTaiRequest {
  maDeTai?: string | null
  tenDeTai: string
  chuNhiemId?: string | null
  chuNhiemNgoai?: string | null
  donViThucHien?: string | null
  donViChuQuan?: string | null
  phanLoaiTuDienId?: string | null
  loaiHinhTuDienId?: string | null
  nguonKinhPhiTuDienId?: string | null
  linhVucId?: string | null
  namDeXuat?: number | null
  thoiGianBatDau?: string | null
  thoiGianKetThuc?: string | null
  kinhPhiDeXuat?: number | null
  kinhPhiDuyet?: number | null
  mucTieu?: string | null
  noiDung?: string | null
  sanPhamDuKien?: string | null
  mucXepLoaiTuDienId?: string | null
  ghiChu?: string | null
}

export interface CapNhatTrangThaiDeTaiRequest {
  trangThai: TrangThaiDeTai
  lyDoHuy?: string | null
}

export interface DeTaiThanhVien {
  id: string
  nguoiDungId: string | null
  hoTen: string | null
  hoTenNgoai: string | null
  vaiTroTuDienId: string | null
  vaiTroTen: string | null
  thuTu: number
}

export interface DeTaiThanhVienRequest {
  nguoiDungId?: string | null
  hoTenNgoai?: string | null
  vaiTroTuDienId?: string | null
  thuTu?: number | null
}

export type LoaiHoiDong =
  | 'HOI_DONG_KHOA_VIEN'
  | 'TIEU_BAN_CHUYEN_MON'
  | 'NGHIEM_THU_CO_SO'
  | 'NGHIEM_THU_CHINH_THUC'

export type KetQuaHoiDong = 'CHUA_CO_KET_QUA' | 'DAT' | 'DAT_CO_SUA_CHUA' | 'KHONG_DAT'

export interface HoiDong {
  id: string
  deTaiId: string
  loai: LoaiHoiDong
  ngayHop: string | null
  diaDiem: string | null
  ketQua: KetQuaHoiDong
  diemTrungBinh: number | null
  ghiChu: string | null
  nguoiTaoId: string | null
  ngayTao: string
}

export interface HoiDongRequest {
  loai: LoaiHoiDong
  ngayHop?: string | null
  diaDiem?: string | null
  ketQua?: KetQuaHoiDong | null
  diemTrungBinh?: number | null
  ghiChu?: string | null
}

export interface HoiDongThanhVien {
  id: string
  hoiDongId: string
  nguoiDungId: string | null
  hoTen: string | null
  hoTenNgoai: string | null
  vaiTroTuDienId: string | null
  vaiTroTen: string | null
  yKien: string | null
  diem: number | null
  dongY: boolean | null
  ngayChoYKien: string | null
}

export interface HoiDongThanhVienRequest {
  nguoiDungId?: string | null
  hoTenNgoai?: string | null
  vaiTroTuDienId?: string | null
}

export interface YKienHoiDongRequest {
  yKien?: string | null
  diem?: number | null
  dongY?: boolean | null
}

export type TrangThaiDuyet = 'CHO_DUYET' | 'DA_DUYET' | 'TU_CHOI'

export interface DuyetRequest {
  trangThai: TrangThaiDuyet
  ghiChu?: string | null
}

export interface DuToanNam {
  id: string
  deTaiId: string
  nam: number
  kinhPhiDeXuat: number | null
  kinhPhiDuyet: number | null
  daGuiBo: boolean
  trangThai: TrangThaiDuyet
  nguoiDuyetId: string | null
  ngayDuyet: string | null
  ghiChu: string | null
}

export interface DuToanNamRequest {
  nam: number
  kinhPhiDeXuat?: number | null
  kinhPhiDuyet?: number | null
  daGuiBo?: boolean | null
  ghiChu?: string | null
}

export interface TamUng {
  id: string
  deTaiId: string
  nam: number | null
  soTien: number
  lyDo: string | null
  ngayDeNghi: string
  trangThai: TrangThaiDuyet
  nguoiDuyetId: string | null
  ngayDuyet: string | null
  ghiChu: string | null
}

export interface TamUngRequest {
  nam?: number | null
  soTien: number
  lyDo?: string | null
  ngayDeNghi: string
  ghiChu?: string | null
}

export interface ThanhToan {
  id: string
  deTaiId: string
  nam: number
  soTien: number
  ngayThanhToan: string | null
  trangThai: TrangThaiDuyet
  nguoiDuyetId: string | null
  ngayDuyet: string | null
  ghiChu: string | null
}

export interface ThanhToanRequest {
  nam: number
  soTien: number
  ngayThanhToan?: string | null
  ghiChu?: string | null
}

export interface QuyetToan {
  id: string
  deTaiId: string
  tongKinhPhiDaCap: number | null
  tongKinhPhiDaSuDung: number | null
  ngayQuyetToan: string | null
  trangThai: TrangThaiDuyet
  nguoiDuyetId: string | null
  ngayDuyet: string | null
  ghiChu: string | null
}

export interface QuyetToanRequest {
  tongKinhPhiDaCap?: number | null
  tongKinhPhiDaSuDung?: number | null
  ngayQuyetToan?: string | null
  ghiChu?: string | null
}

export interface BaoCaoTienDo {
  id: string
  deTaiId: string
  kyBaoCao: string | null
  hanNop: string | null
  ngayNop: string | null
  noiDung: string | null
  trangThai: TrangThaiDuyet
  nguoiDuyetId: string | null
  ngayDuyet: string | null
  ghiChu: string | null
}

export interface BaoCaoTienDoRequest {
  kyBaoCao?: string | null
  hanNop?: string | null
  ngayNop?: string | null
  noiDung?: string | null
  ghiChu?: string | null
}

// ---------- Ho so khoa hoc can bo ----------

export interface LyLichKhoaHoc {
  id: string
  nguoiDungId: string
  hoTen: string
  hocHam: string | null
  hocVi: string | null
  chuyenNganh: string | null
  quaTrinhCongTac: string | null
  ghiChu: string | null
  ngaySua: string
}

export interface LyLichKhoaHocRequest {
  hocHam?: string | null
  hocVi?: string | null
  chuyenNganh?: string | null
  quaTrinhCongTac?: string | null
  ghiChu?: string | null
}

export type LoaiHoatDongNgoaiTruong = 'DE_TAI_DU_AN' | 'SACH_GIAO_TRINH'

export interface HoatDongNgoaiTruong {
  id: string
  nguoiDungId: string
  loai: LoaiHoatDongNgoaiTruong
  ten: string
  donViPhoiHop: string | null
  vaiTro: string | null
  thoiGianBatDau: string | null
  thoiGianKetThuc: string | null
  ghiChu: string | null
  ngayTao: string
}

export interface HoatDongNgoaiTruongRequest {
  loai: LoaiHoatDongNgoaiTruong
  ten: string
  donViPhoiHop?: string | null
  vaiTro?: string | null
  thoiGianBatDau?: string | null
  thoiGianKetThuc?: string | null
  ghiChu?: string | null
}
