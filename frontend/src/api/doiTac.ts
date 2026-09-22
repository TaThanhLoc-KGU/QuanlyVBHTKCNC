import { api } from './client'
import type { DoiTac, DoiTacRequest, PageResponse } from '../types'

export interface DoiTacLoc {
  loaiDoiTac?: string
  quocGia?: string
  tuKhoa?: string
  page?: number
  size?: number
}

export function danhSachDoiTac(loc: DoiTacLoc) {
  return api.get<PageResponse<DoiTac>>('/doi-tac', { params: loc }).then((r) => r.data)
}

export function chiTietDoiTac(id: string) {
  return api.get<DoiTac>(`/doi-tac/${id}`).then((r) => r.data)
}

export function goiYTrungTenDoiTac(ten: string) {
  return api.get<DoiTac[]>('/doi-tac/goi-y-trung-ten', { params: { ten } }).then((r) => r.data)
}

export function taoDoiTac(body: DoiTacRequest) {
  return api.post<DoiTac>('/doi-tac', body).then((r) => r.data)
}

export function suaDoiTac(id: string, body: DoiTacRequest) {
  return api.put<DoiTac>(`/doi-tac/${id}`, body).then((r) => r.data)
}

export function xoaDoiTac(id: string) {
  return api.delete(`/doi-tac/${id}`)
}

export function khoiPhucDoiTac(id: string) {
  return api.post(`/doi-tac/${id}/khoi-phuc`)
}
