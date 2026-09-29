import { api } from './client'
import type {
  HoiDong,
  HoiDongRequest,
  HoiDongThanhVien,
  HoiDongThanhVienRequest,
  YKienHoiDongRequest,
} from '../types'

export function danhSach(deTaiId: string) {
  return api.get<HoiDong[]>(`/de-tai/${deTaiId}/hoi-dong`).then((r) => r.data)
}
export function tao(deTaiId: string, body: HoiDongRequest) {
  return api.post<HoiDong>(`/de-tai/${deTaiId}/hoi-dong`, body).then((r) => r.data)
}
export function sua(id: string, body: HoiDongRequest) {
  return api.put<HoiDong>(`/hoi-dong/${id}`, body).then((r) => r.data)
}
export function xoa(id: string) {
  return api.delete(`/hoi-dong/${id}`)
}

export function danhSachThanhVien(hoiDongId: string) {
  return api.get<HoiDongThanhVien[]>(`/hoi-dong/${hoiDongId}/thanh-vien`).then((r) => r.data)
}
export function themThanhVien(hoiDongId: string, body: HoiDongThanhVienRequest) {
  return api.post<HoiDongThanhVien>(`/hoi-dong/${hoiDongId}/thanh-vien`, body).then((r) => r.data)
}
export function xoaThanhVien(thanhVienId: string) {
  return api.delete(`/hoi-dong/thanh-vien/${thanhVienId}`)
}
export function ghiYKien(thanhVienId: string, body: YKienHoiDongRequest) {
  return api.put<HoiDongThanhVien>(`/hoi-dong/thanh-vien/${thanhVienId}/y-kien`, body).then((r) => r.data)
}
