import { api } from './client'
import type { BaoCaoDoanRaVao, BaoCaoMouTrongNam, Mou, PageResponse } from '../types'

export function baoCaoMouTrongNam(nam: number) {
  return api.get<BaoCaoMouTrongNam>('/bao-cao/mou-trong-nam', { params: { nam } }).then((r) => r.data)
}

export function baoCaoDoanRaVao(tu?: string, den?: string, doiTacId?: string) {
  return api
    .get<BaoCaoDoanRaVao>('/bao-cao/doan-ra-vao', { params: { tu, den, doiTacId } })
    .then((r) => r.data)
}

export function baoCaoThoiHanMou(thangToi?: number) {
  return api.get<Mou[]>('/bao-cao/thoi-han-mou', { params: { thangToi } }).then((r) => r.data)
}

export function danhSachLichSuXuat(page = 0, size = 20) {
  return api
    .get<PageResponse<{ id: string; loaiBaoCao: string; nguoiXuatId: string; thoiDiem: string; dinhDang: string }>>(
      '/bao-cao/lich-su-xuat',
      { params: { page, size } },
    )
    .then((r) => r.data)
}

type LoaiBaoCao = 'mou-trong-nam' | 'doan-ra-vao' | 'thoi-han-mou'

export async function xuatBaoCao(
  loai: LoaiBaoCao,
  dinhDang: 'EXCEL' | 'PDF',
  params: Record<string, string | number | undefined>,
  tenFile: string,
) {
  const res = await api.get(`/bao-cao/${loai}`, { params: { ...params, dinhDang }, responseType: 'blob' })
  const url = window.URL.createObjectURL(res.data as Blob)
  const a = document.createElement('a')
  a.href = url
  a.download = tenFile + (dinhDang === 'EXCEL' ? '.xlsx' : '.pdf')
  document.body.appendChild(a)
  a.click()
  a.remove()
  window.URL.revokeObjectURL(url)
}

export function layLichSuThayDoi(bang: string, banGhiId: string) {
  return api.get<LichSuThayDoi[]>(`/lich-su/${bang}/${banGhiId}`).then((r) => r.data)
}
