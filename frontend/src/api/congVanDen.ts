import { api } from './client'
import type { PageResponse } from '../types'

export interface CongVanDen {
  id: string
  congvanId: number
  soVanBan: string | null
  soDen: string | null
  ngayDen: string | null
  ngayBanHanh: string | null
  trichYeu: string
  coQuanBanHanh: string | null
  loaiVanBan: string | null
  nguoiKy: string | null
  donViXuLyChinh: string | null
  hanXuLy: string | null
  ngayHoanThanh: string | null
  trangThai: string | null
  trangThaiText: string | null
  ghiChu: string | null
  coFile: boolean
  ngayDongBo: string
}

export interface DongBoKetQuaDen {
  tongSoTuCongVan: number
  soMoi: number
  soCapNhat: number
}

export function danhSach(params: { trangThai?: string; nam?: number; tuKhoa?: string; page: number; size?: number }) {
  return api
    .get<PageResponse<CongVanDen>>('/cong-van-den', { params: { size: 20, ...params } })
    .then((r) => r.data)
}

export function chiTiet(id: string) {
  return api.get<CongVanDen>(`/cong-van-den/${id}`).then((r) => r.data)
}

export function dongBoNgay() {
  return api.post<DongBoKetQuaDen>('/cong-van-den/dong-bo-ngay').then((r) => r.data)
}
