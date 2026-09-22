import { api } from './client'
import type { PageResponse, VanBanRequest, VbplVn } from '../types'

export type NguonCrawl = 'CONG_BAO_CHINH_PHU' | 'VBPL_VN_PORTAL'
export type TrangThaiUngVien = 'CHUA_XU_LY' | 'DA_NHAN' | 'DA_BO_QUA'

export interface UngVienPhapLuat {
  id: string
  nguon: NguonCrawl
  soHieu: string
  tenVanBan: string
  loaiVanBanText: string | null
  ngayBanHanh: string | null
  coQuanBanHanh: string | null
  urlNguon: string
  tuKhoaKhop: string | null
  trangThai: TrangThaiUngVien
  ngayCrawl: string
}

export interface TuKhoaCrawl {
  id: string
  tuKhoa: string
  hoatDong: boolean
}

export interface CrawlKetQua {
  soUngVienMoi: number
  soTuKhoaDaQuet: number
}

export function chayCrawlNgay() {
  return api.post<CrawlKetQua>('/crawl-phap-luat/chay-ngay').then((r) => r.data)
}

export function danhSachUngVien(trangThai: TrangThaiUngVien | undefined, page: number, size = 20) {
  return api
    .get<PageResponse<UngVienPhapLuat>>('/crawl-phap-luat/ung-vien', { params: { trangThai, page, size } })
    .then((r) => r.data)
}

export function nhanUngVien(id: string, body: VanBanRequest) {
  return api.post<VbplVn>(`/crawl-phap-luat/ung-vien/${id}/nhan`, body).then((r) => r.data)
}

export function boQuaUngVien(id: string) {
  return api.post(`/crawl-phap-luat/ung-vien/${id}/bo-qua`)
}

export function danhSachTuKhoa() {
  return api.get<TuKhoaCrawl[]>('/crawl-phap-luat/tu-khoa').then((r) => r.data)
}

export function themTuKhoa(tuKhoa: string) {
  return api.post<TuKhoaCrawl>('/crawl-phap-luat/tu-khoa', { tuKhoa }).then((r) => r.data)
}

export function doiTrangThaiTuKhoa(id: string, hoatDong: boolean) {
  return api.put<TuKhoaCrawl>(`/crawl-phap-luat/tu-khoa/${id}`, null, { params: { hoatDong } }).then((r) => r.data)
}

export function xoaTuKhoa(id: string) {
  return api.delete(`/crawl-phap-luat/tu-khoa/${id}`)
}
