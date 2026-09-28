import { api } from './client'
import type { PageResponse, SuKien, SuKienRequest } from '../types'

export interface SuKienLoc {
  nam?: number
  loaiSuKienTuDienId?: string
  tuKhoa?: string
  page?: number
  size?: number
}

export function danhSach(loc: SuKienLoc) {
  return api.get<PageResponse<SuKien>>('/su-kien', { params: loc }).then((r) => r.data)
}
export function chiTiet(id: string) {
  return api.get<SuKien>(`/su-kien/${id}`).then((r) => r.data)
}
export function tao(body: SuKienRequest) {
  return api.post<SuKien>('/su-kien', body).then((r) => r.data)
}
export function sua(id: string, body: SuKienRequest) {
  return api.put<SuKien>(`/su-kien/${id}`, body).then((r) => r.data)
}
export function xoa(id: string) {
  return api.delete(`/su-kien/${id}`)
}
