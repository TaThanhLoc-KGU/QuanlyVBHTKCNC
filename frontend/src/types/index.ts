// Cac kieu du lieu khop voi DTO tra ve tu backend (Jackson serialize camelCase).

export type VaiTro = 'ADMIN' | 'EDITOR' | 'VIEWER' | string
export type ModuleKey = 'DOI_TAC' | 'VAN_BAN_DHKG' | 'VBPL_VN' | 'MOU' | 'DOAN_VAO' | 'DOAN_RA' | 'CONG_VAN_DEN'

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
  lamMoiLuc: string | null
  mouDenHanTheoThang: { thang: string; soLuong: number }[]
  widgetMouSapHetHan: MouWidgetDto[]
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
