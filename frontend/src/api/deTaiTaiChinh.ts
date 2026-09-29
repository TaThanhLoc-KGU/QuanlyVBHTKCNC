import { api } from './client'
import type {
  BaoCaoTienDo,
  BaoCaoTienDoRequest,
  DuToanNam,
  DuToanNamRequest,
  DuyetRequest,
  QuyetToan,
  QuyetToanRequest,
  TamUng,
  TamUngRequest,
  ThanhToan,
  ThanhToanRequest,
} from '../types'

// ---------- Du toan nam ----------
export function danhSachDuToanNam(deTaiId: string) {
  return api.get<DuToanNam[]>(`/de-tai/${deTaiId}/du-toan-nam`).then((r) => r.data)
}
export function taoDuToanNam(deTaiId: string, body: DuToanNamRequest) {
  return api.post<DuToanNam>(`/de-tai/${deTaiId}/du-toan-nam`, body).then((r) => r.data)
}
export function suaDuToanNam(id: string, body: DuToanNamRequest) {
  return api.put<DuToanNam>(`/du-toan-nam/${id}`, body).then((r) => r.data)
}
export function duyetDuToanNam(id: string, body: DuyetRequest) {
  return api.put<DuToanNam>(`/du-toan-nam/${id}/duyet`, body).then((r) => r.data)
}
export function xoaDuToanNam(id: string) {
  return api.delete(`/du-toan-nam/${id}`)
}

// ---------- Tam ung ----------
export function danhSachTamUng(deTaiId: string) {
  return api.get<TamUng[]>(`/de-tai/${deTaiId}/tam-ung`).then((r) => r.data)
}
export function taoTamUng(deTaiId: string, body: TamUngRequest) {
  return api.post<TamUng>(`/de-tai/${deTaiId}/tam-ung`, body).then((r) => r.data)
}
export function suaTamUng(id: string, body: TamUngRequest) {
  return api.put<TamUng>(`/tam-ung/${id}`, body).then((r) => r.data)
}
export function duyetTamUng(id: string, body: DuyetRequest) {
  return api.put<TamUng>(`/tam-ung/${id}/duyet`, body).then((r) => r.data)
}
export function xoaTamUng(id: string) {
  return api.delete(`/tam-ung/${id}`)
}

// ---------- Thanh toan ----------
export function danhSachThanhToan(deTaiId: string) {
  return api.get<ThanhToan[]>(`/de-tai/${deTaiId}/thanh-toan`).then((r) => r.data)
}
export function taoThanhToan(deTaiId: string, body: ThanhToanRequest) {
  return api.post<ThanhToan>(`/de-tai/${deTaiId}/thanh-toan`, body).then((r) => r.data)
}
export function suaThanhToan(id: string, body: ThanhToanRequest) {
  return api.put<ThanhToan>(`/thanh-toan/${id}`, body).then((r) => r.data)
}
export function duyetThanhToan(id: string, body: DuyetRequest) {
  return api.put<ThanhToan>(`/thanh-toan/${id}/duyet`, body).then((r) => r.data)
}
export function xoaThanhToan(id: string) {
  return api.delete(`/thanh-toan/${id}`)
}

// ---------- Quyet toan ----------
export function layQuyetToan(deTaiId: string) {
  return api.get<QuyetToan | null>(`/de-tai/${deTaiId}/quyet-toan`).then((r) => r.data)
}
export function luuQuyetToan(deTaiId: string, body: QuyetToanRequest) {
  return api.put<QuyetToan>(`/de-tai/${deTaiId}/quyet-toan`, body).then((r) => r.data)
}
export function duyetQuyetToan(deTaiId: string, body: DuyetRequest) {
  return api.put<QuyetToan>(`/de-tai/${deTaiId}/quyet-toan/duyet`, body).then((r) => r.data)
}

// ---------- Bao cao tien do ----------
export function danhSachBaoCaoTienDo(deTaiId: string) {
  return api.get<BaoCaoTienDo[]>(`/de-tai/${deTaiId}/bao-cao-tien-do`).then((r) => r.data)
}
export function taoBaoCaoTienDo(deTaiId: string, body: BaoCaoTienDoRequest) {
  return api.post<BaoCaoTienDo>(`/de-tai/${deTaiId}/bao-cao-tien-do`, body).then((r) => r.data)
}
export function suaBaoCaoTienDo(id: string, body: BaoCaoTienDoRequest) {
  return api.put<BaoCaoTienDo>(`/bao-cao-tien-do/${id}`, body).then((r) => r.data)
}
export function duyetBaoCaoTienDo(id: string, body: DuyetRequest) {
  return api.put<BaoCaoTienDo>(`/bao-cao-tien-do/${id}/duyet`, body).then((r) => r.data)
}
export function xoaBaoCaoTienDo(id: string) {
  return api.delete(`/bao-cao-tien-do/${id}`)
}
