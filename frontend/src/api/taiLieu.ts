import { api } from './client'
import type { TaiLieuDinhKem } from '../types'

export type BangDinhKem = 'van_ban_dhkg' | 'vbpl_vn' | 'mou' | 'cong_van_den'

export function danhSachDinhKem(bang: BangDinhKem, banGhiId: string) {
  return api.get<TaiLieuDinhKem[]>('/tai-lieu-dinh-kem', { params: { bang, banGhiId } }).then((r) => r.data)
}

export function taiLenDinhKem(bang: BangDinhKem, banGhiId: string, file: File) {
  const form = new FormData()
  form.append('file', file)
  return api
    .post<TaiLieuDinhKem>('/tai-lieu-dinh-kem', form, { params: { bang, banGhiId } })
    .then((r) => r.data)
}

export function xoaDinhKem(id: string) {
  return api.delete(`/tai-lieu-dinh-kem/${id}`)
}

/** Endpoint tai xuong yeu cau Bearer token nen khong the dung <a href> tran -
 * fetch qua axios (co interceptor gan token) roi tu tao Blob URL de trinh
 * duyet luu file. */
export async function taiXuongDinhKem(id: string, tenFile: string) {
  const res = await api.get(`/tai-lieu-dinh-kem/${id}/tai-xuong`, { responseType: 'blob' })
  const url = window.URL.createObjectURL(res.data as Blob)
  const a = document.createElement('a')
  a.href = url
  a.download = tenFile
  document.body.appendChild(a)
  a.click()
  a.remove()
  window.URL.revokeObjectURL(url)
}
