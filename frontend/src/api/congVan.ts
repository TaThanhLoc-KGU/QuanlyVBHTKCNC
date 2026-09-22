import { api } from './client'
import type { PageResponse, VanBanDhkg, VanBanRequest } from '../types'
import type { TrangThaiUngVien } from './crawlPhapLuat'

export interface UngVienDhkg {
  id: string
  soHieu: string
  tieuDe: string
  noiDung: string | null
  ngayBanHanh: string | null
  nguoiKy: string | null
  soFile: number
  trangThai: TrangThaiUngVien
  ngayDongBo: string
}

export interface DongBoKetQua {
  tongSoTuCongVan: number
  soUngVienMoi: number
}

export function dongBoNgay() {
  return api.post<DongBoKetQua>('/congvan-dhkg/dong-bo-ngay').then((r) => r.data)
}

export function danhSachUngVien(trangThai: TrangThaiUngVien | undefined, page: number, size = 20) {
  return api
    .get<PageResponse<UngVienDhkg>>('/congvan-dhkg/ung-vien', { params: { trangThai, page, size } })
    .then((r) => r.data)
}

export function nhanUngVien(id: string, body: VanBanRequest) {
  return api.post<VanBanDhkg>(`/congvan-dhkg/ung-vien/${id}/nhan`, body).then((r) => r.data)
}

export function boQuaUngVien(id: string) {
  return api.post(`/congvan-dhkg/ung-vien/${id}/bo-qua`)
}
