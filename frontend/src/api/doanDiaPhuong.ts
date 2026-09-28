import { api } from './client'
import type { DoanDiaPhuong, DoanDiaPhuongRequest, PageResponse } from '../types'

export interface DoanDiaPhuongLoc {
  nam?: number
  doiTacId?: string
  tuKhoa?: string
  page?: number
  size?: number
}

export function danhSach(loc: DoanDiaPhuongLoc) {
  return api.get<PageResponse<DoanDiaPhuong>>('/doan-dia-phuong', { params: loc }).then((r) => r.data)
}
export function chiTiet(id: string) {
  return api.get<DoanDiaPhuong>(`/doan-dia-phuong/${id}`).then((r) => r.data)
}
export function tao(body: DoanDiaPhuongRequest) {
  return api.post<DoanDiaPhuong>('/doan-dia-phuong', body).then((r) => r.data)
}
export function sua(id: string, body: DoanDiaPhuongRequest) {
  return api.put<DoanDiaPhuong>(`/doan-dia-phuong/${id}`, body).then((r) => r.data)
}
export function xoa(id: string) {
  return api.delete(`/doan-dia-phuong/${id}`)
}
