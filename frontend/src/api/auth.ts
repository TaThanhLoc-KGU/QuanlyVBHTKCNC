import { api } from './client'
import type { CurrentUser, LoginResponse } from '../types'

export function dangNhap(tenDangNhap: string, matKhau: string) {
  return api.post<LoginResponse>('/auth/login', { tenDangNhap, matKhau }).then((r) => r.data)
}

export function layThongTinCaNhan() {
  return api.get<CurrentUser>('/auth/me').then((r) => r.data)
}

export function doiMatKhau(matKhauCu: string, matKhauMoi: string) {
  return api.post('/auth/doi-mat-khau', { matKhauCu, matKhauMoi }).then((r) => r.data)
}
