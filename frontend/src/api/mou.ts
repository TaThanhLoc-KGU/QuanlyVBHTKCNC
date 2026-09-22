import { api } from './client'
import type { Mou, MouRequest, PageResponse } from '../types'

export interface MouLoc {
  doiTacId?: string
  trangThai?: string
  tu?: string
  den?: string
  tuKhoa?: string
  page?: number
  size?: number
}

export function danhSachMou(loc: MouLoc) {
  return api.get<PageResponse<Mou>>('/mou', { params: loc }).then((r) => r.data)
}

export function chiTietMou(id: string) {
  return api.get<Mou>(`/mou/${id}`).then((r) => r.data)
}

export function taoMou(body: MouRequest) {
  return api.post<Mou>('/mou', body).then((r) => r.data)
}

export function suaMou(id: string, body: MouRequest) {
  return api.put<Mou>(`/mou/${id}`, body).then((r) => r.data)
}

export function xoaMou(id: string) {
  return api.delete(`/mou/${id}`)
}
