import { api } from './client'
import type { LoaiCapVisa, PageResponse, Visa, VisaRequest } from '../types'

export interface VisaLoc {
  nam?: number
  loaiCap?: LoaiCapVisa
  tuKhoa?: string
  page?: number
  size?: number
}

export function danhSach(loc: VisaLoc) {
  return api.get<PageResponse<Visa>>('/visa', { params: loc }).then((r) => r.data)
}
export function chiTiet(id: string) {
  return api.get<Visa>(`/visa/${id}`).then((r) => r.data)
}
export function tao(body: VisaRequest) {
  return api.post<Visa>('/visa', body).then((r) => r.data)
}
export function sua(id: string, body: VisaRequest) {
  return api.put<Visa>(`/visa/${id}`, body).then((r) => r.data)
}
export function xoa(id: string) {
  return api.delete(`/visa/${id}`)
}
