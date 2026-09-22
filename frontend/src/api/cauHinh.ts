import { api } from './client'

export interface CauHinhHeThong {
  ma: string
  giaTri: string
  moTa: string | null
  ngaySua: string
}

export function danhSachCauHinh() {
  return api.get<CauHinhHeThong[]>('/cau-hinh-he-thong').then((r) => r.data)
}

export function suaCauHinh(ma: string, giaTri: string) {
  return api.put<CauHinhHeThong>(`/cau-hinh-he-thong/${ma}`, { giaTri }).then((r) => r.data)
}
