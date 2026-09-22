import { api } from './client'
import type { ModuleKey, NguoiDung, PageResponse } from '../types'

export interface NguoiDungRequest {
  tenDangNhap: string
  email: string
  hoTen: string
  vaiTroMa: string[]
  bienTapMoDun?: ModuleKey[]
}

export interface NguoiDungCreatedResult {
  nguoiDung: NguoiDung
  matKhauTam: string
}

export function danhSachNguoiDung(page = 0, size = 20) {
  return api.get<PageResponse<NguoiDung>>('/nguoi-dung', { params: { page, size } }).then((r) => r.data)
}

export function taoNguoiDung(body: NguoiDungRequest) {
  return api.post<NguoiDungCreatedResult>('/nguoi-dung', body).then((r) => r.data)
}

export function capNhatNguoiDung(id: string, body: NguoiDungRequest) {
  return api.put<NguoiDung>(`/nguoi-dung/${id}`, body).then((r) => r.data)
}

export function khoaTaiKhoan(id: string) {
  return api.post(`/nguoi-dung/${id}/khoa`)
}

export function moKhoaTaiKhoan(id: string) {
  return api.post(`/nguoi-dung/${id}/mo-khoa`)
}

export function datLaiMatKhau(id: string) {
  return api.post<{ matKhauTam: string }>(`/nguoi-dung/${id}/dat-lai-mat-khau`).then((r) => r.data)
}
