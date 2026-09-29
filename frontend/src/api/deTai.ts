import { api } from './client'
import type {
  CapNhatTrangThaiDeTaiRequest,
  DeTai,
  DeTaiRequest,
  DeTaiThanhVien,
  DeTaiThanhVienRequest,
  PageResponse,
  TrangThaiDeTai,
} from '../types'

export interface DeTaiLoc {
  namDeXuat?: number
  trangThai?: TrangThaiDeTai
  chuNhiemId?: string
  linhVucId?: string
  tuKhoa?: string
  page?: number
  size?: number
}

export function danhSach(loc: DeTaiLoc) {
  return api.get<PageResponse<DeTai>>('/de-tai', { params: loc }).then((r) => r.data)
}
export function chiTiet(id: string) {
  return api.get<DeTai>(`/de-tai/${id}`).then((r) => r.data)
}
export function tao(body: DeTaiRequest) {
  return api.post<DeTai>('/de-tai', body).then((r) => r.data)
}
export function sua(id: string, body: DeTaiRequest) {
  return api.put<DeTai>(`/de-tai/${id}`, body).then((r) => r.data)
}
export function capNhatTrangThai(id: string, body: CapNhatTrangThaiDeTaiRequest) {
  return api.patch<DeTai>(`/de-tai/${id}/trang-thai`, body).then((r) => r.data)
}
export function xoa(id: string) {
  return api.delete(`/de-tai/${id}`)
}

export function danhSachThanhVien(deTaiId: string) {
  return api.get<DeTaiThanhVien[]>(`/de-tai/${deTaiId}/thanh-vien`).then((r) => r.data)
}
export function themThanhVien(deTaiId: string, body: DeTaiThanhVienRequest) {
  return api.post<DeTaiThanhVien>(`/de-tai/${deTaiId}/thanh-vien`, body).then((r) => r.data)
}
export function xoaThanhVien(deTaiId: string, id: string) {
  return api.delete(`/de-tai/${deTaiId}/thanh-vien/${id}`)
}
