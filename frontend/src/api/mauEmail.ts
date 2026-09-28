import { api } from './client'
import type { MauEmail, MauEmailRequest } from '../types'

export function danhSach() {
  return api.get<MauEmail[]>('/mau-email').then((r) => r.data)
}
export function tao(body: MauEmailRequest) {
  return api.post<MauEmail>('/mau-email', body).then((r) => r.data)
}
export function sua(id: string, body: MauEmailRequest) {
  return api.put<MauEmail>(`/mau-email/${id}`, body).then((r) => r.data)
}
export function xoa(id: string) {
  return api.delete(`/mau-email/${id}`)
}
