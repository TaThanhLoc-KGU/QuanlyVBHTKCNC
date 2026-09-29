import { api } from './client'
import type { LinhVucNghienCuu, LinhVucNghienCuuRequest } from '../types'

export function danhSach() {
  return api.get<LinhVucNghienCuu[]>('/linh-vuc-nghien-cuu').then((r) => r.data)
}
export function tao(body: LinhVucNghienCuuRequest) {
  return api.post<LinhVucNghienCuu>('/linh-vuc-nghien-cuu', body).then((r) => r.data)
}
export function sua(id: string, body: LinhVucNghienCuuRequest) {
  return api.put<LinhVucNghienCuu>(`/linh-vuc-nghien-cuu/${id}`, body).then((r) => r.data)
}
