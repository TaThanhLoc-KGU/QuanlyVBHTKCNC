import { api } from './client'
import type { TaiLieuDinhKem } from '../types'

export type BangDinhKem =
  | 'van_ban_dhkg'
  | 'vbpl_vn'
  | 'mou'
  | 'cong_van_den'
  | 'doan_vao'
  | 'doan_ra'
  | 'doan_dia_phuong'
  | 'visa'
  | 'su_kien'

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

/** Cac loai file trinh duyet tu render duoc truc tiep (PDF, anh) - cac loai
 * khac (docx, xlsx...) trinh duyet se tu tai xuong du server tra ve inline. */
export function coTheXemTrucTuyen(loaiMime: string | null) {
  if (!loaiMime) return false
  return loaiMime === 'application/pdf' || loaiMime.startsWith('image/')
}

/** Mo tab moi TRUOC (dong bo, trong luc con trong tieng dong click cua nguoi
 * dung) roi moi gan URL sau khi fetch xong blob - neu goi window.open() sau
 * khi await xong, trinh duyet coi la khong con trong "user gesture" nua va
 * chan popup. */
export async function xemDinhKem(id: string) {
  const tabMoi = window.open('', '_blank')
  try {
    const res = await api.get(`/tai-lieu-dinh-kem/${id}/xem`, { responseType: 'blob' })
    const url = window.URL.createObjectURL(res.data as Blob)
    if (tabMoi) {
      tabMoi.location.href = url
    } else {
      window.open(url, '_blank')
    }
  } catch (err) {
    tabMoi?.close()
    throw err
  }
}
