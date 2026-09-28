import { api } from './client'
import type { ThanhVienPhuTrach, ThanhVienPhuTrachRequest } from '../types'

export function danhSach() {
  return api.get<ThanhVienPhuTrach[]>('/thanh-vien-phu-trach').then((r) => r.data)
}
export function tao(body: ThanhVienPhuTrachRequest) {
  return api.post<ThanhVienPhuTrach>('/thanh-vien-phu-trach', body).then((r) => r.data)
}
export function sua(id: string, body: ThanhVienPhuTrachRequest) {
  return api.put<ThanhVienPhuTrach>(`/thanh-vien-phu-trach/${id}`, body).then((r) => r.data)
}
export function xoa(id: string) {
  return api.delete(`/thanh-vien-phu-trach/${id}`)
}
