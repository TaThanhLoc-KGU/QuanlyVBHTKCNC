import { api } from './client'
import type { LoaiTuDien, TuDien } from '../types'

export interface TuDienRequest {
  loai: LoaiTuDien
  ma: string | null
  ten: string
  moTa: string | null
  thuTu: number | null
  hoatDong: boolean | null
}

export function danhSach(loai: LoaiTuDien) {
  return api.get<TuDien[]>('/tu-dien', { params: { loai } }).then((r) => r.data)
}

export function tao(request: TuDienRequest) {
  return api.post<TuDien>('/tu-dien', request).then((r) => r.data)
}

export function sua(id: string, request: TuDienRequest) {
  return api.put<TuDien>(`/tu-dien/${id}`, request).then((r) => r.data)
}

export function xoa(id: string) {
  return api.delete(`/tu-dien/${id}`)
}
