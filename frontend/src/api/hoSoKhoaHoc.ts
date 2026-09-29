import { api } from './client'
import type {
  HoatDongNgoaiTruong,
  HoatDongNgoaiTruongRequest,
  LyLichKhoaHoc,
  LyLichKhoaHocRequest,
} from '../types'

export function layLyLich(nguoiDungId: string) {
  return api.get<LyLichKhoaHoc | null>(`/can-bo/${nguoiDungId}/ly-lich-khoa-hoc`).then((r) => r.data)
}
export function luuLyLich(nguoiDungId: string, body: LyLichKhoaHocRequest) {
  return api.put<LyLichKhoaHoc>(`/can-bo/${nguoiDungId}/ly-lich-khoa-hoc`, body).then((r) => r.data)
}
export function danhSachHoatDong(nguoiDungId: string) {
  return api.get<HoatDongNgoaiTruong[]>(`/can-bo/${nguoiDungId}/hoat-dong-ngoai-truong`).then((r) => r.data)
}
export function themHoatDong(nguoiDungId: string, body: HoatDongNgoaiTruongRequest) {
  return api.post<HoatDongNgoaiTruong>(`/can-bo/${nguoiDungId}/hoat-dong-ngoai-truong`, body).then((r) => r.data)
}
export function xoaHoatDong(id: string) {
  return api.delete(`/hoat-dong-ngoai-truong/${id}`)
}
