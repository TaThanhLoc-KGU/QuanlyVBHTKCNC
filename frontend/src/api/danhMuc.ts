import { api } from './client'
import type { LoaiVanBan, PhamViVanBan } from '../types'

export function danhSachLoaiVanBan(phamVi: PhamViVanBan) {
  return api.get<LoaiVanBan[]>('/danh-muc/loai-van-ban', { params: { phamVi } }).then((r) => r.data)
}

export function themLoaiVanBan(ma: string, ten: string, phamVi: PhamViVanBan, thuTu?: number) {
  return api.post<LoaiVanBan>('/danh-muc/loai-van-ban', { ma, ten, phamVi, thuTu }).then((r) => r.data)
}
