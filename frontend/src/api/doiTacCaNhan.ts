import { api } from './client'
import type { DoiTacCaNhan, DoiTacCaNhanRequest } from '../types'

export function danhSach() {
  return api.get<DoiTacCaNhan[]>('/doi-tac-ca-nhan').then((r) => r.data)
}
export function tao(body: DoiTacCaNhanRequest) {
  return api.post<DoiTacCaNhan>('/doi-tac-ca-nhan', body).then((r) => r.data)
}
export function sua(id: string, body: DoiTacCaNhanRequest) {
  return api.put<DoiTacCaNhan>(`/doi-tac-ca-nhan/${id}`, body).then((r) => r.data)
}
export function xoa(id: string) {
  return api.delete(`/doi-tac-ca-nhan/${id}`)
}
