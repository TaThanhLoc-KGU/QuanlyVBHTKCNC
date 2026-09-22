import { api } from './client'
import type { PageResponse, ThongBao } from '../types'

export function danhSachThongBao(chiChuaDoc?: boolean, page = 0, size = 20) {
  return api
    .get<PageResponse<ThongBao>>('/thong-bao', { params: { chiChuaDoc, page, size } })
    .then((r) => r.data)
}

export function soLuongChuaDoc() {
  return api.get<{ soLuong: number }>('/thong-bao/chua-doc-so-luong').then((r) => r.data.soLuong)
}

export function danhDauDaDoc(id: string) {
  return api.post(`/thong-bao/${id}/danh-dau-da-doc`)
}

export function danhDauTatCaDaDoc() {
  return api.post('/thong-bao/danh-dau-tat-ca-da-doc')
}
