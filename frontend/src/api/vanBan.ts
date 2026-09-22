import { api } from './client'
import type { PageResponse, VanBanDhkg, VanBanRequest, VbplVn } from '../types'

export type DuongDanVanBan = 'van-ban-dhkg' | 'vbpl-vn'

export interface VanBanLoc {
  loaiVanBanId?: string
  tinhTrang?: string
  tu?: string
  den?: string
  tuKhoa?: string
  page?: number
  size?: number
}

export function danhSachVanBan<T extends VanBanDhkg | VbplVn>(duongDan: DuongDanVanBan, loc: VanBanLoc) {
  return api.get<PageResponse<T>>(`/${duongDan}`, { params: loc }).then((r) => r.data)
}

export function chiTietVanBan<T extends VanBanDhkg | VbplVn>(duongDan: DuongDanVanBan, id: string) {
  return api.get<T>(`/${duongDan}/${id}`).then((r) => r.data)
}

export function taoVanBan<T extends VanBanDhkg | VbplVn>(duongDan: DuongDanVanBan, body: VanBanRequest) {
  return api.post<T>(`/${duongDan}`, body).then((r) => r.data)
}

export function suaVanBan<T extends VanBanDhkg | VbplVn>(duongDan: DuongDanVanBan, id: string, body: VanBanRequest) {
  return api.put<T>(`/${duongDan}/${id}`, body).then((r) => r.data)
}

export function xoaVanBan(duongDan: DuongDanVanBan, id: string) {
  return api.delete(`/${duongDan}/${id}`)
}

export function xacNhanDoiChieuVbpl(id: string) {
  return api.post<VbplVn>(`/vbpl-vn/${id}/xac-nhan-doi-chieu`).then((r) => r.data)
}
