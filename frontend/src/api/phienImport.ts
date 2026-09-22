import { api } from './client'
import type { PageResponse, PhienImport } from '../types'

export function danhSachPhienImport(page = 0, size = 20) {
  return api.get<PageResponse<PhienImport>>('/phien-import', { params: { page, size } }).then((r) => r.data)
}

export function rollbackPhienImport(id: string) {
  return api.post<{ soBanGhiDaXoa: number }>(`/phien-import/${id}/rollback`).then((r) => r.data)
}
