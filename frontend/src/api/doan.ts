import { api } from './client'
import type { DoanRa, DoanRaRequest, DoanVao, DoanVaoRequest, PageResponse } from '../types'

export interface DoanLoc {
  nam?: number
  doiTacId?: string
  tuKhoa?: string
  page?: number
  size?: number
}

export function danhSachDoanVao(loc: DoanLoc) {
  return api.get<PageResponse<DoanVao>>('/doan-vao', { params: loc }).then((r) => r.data)
}
export function chiTietDoanVao(id: string) {
  return api.get<DoanVao>(`/doan-vao/${id}`).then((r) => r.data)
}
export function taoDoanVao(body: DoanVaoRequest) {
  return api.post<DoanVao>('/doan-vao', body).then((r) => r.data)
}
export function suaDoanVao(id: string, body: DoanVaoRequest) {
  return api.put<DoanVao>(`/doan-vao/${id}`, body).then((r) => r.data)
}
export function xoaDoanVao(id: string) {
  return api.delete(`/doan-vao/${id}`)
}

export function danhSachDoanRa(loc: DoanLoc) {
  return api.get<PageResponse<DoanRa>>('/doan-ra', { params: loc }).then((r) => r.data)
}
export function chiTietDoanRa(id: string) {
  return api.get<DoanRa>(`/doan-ra/${id}`).then((r) => r.data)
}
export function taoDoanRa(body: DoanRaRequest) {
  return api.post<DoanRa>('/doan-ra', body).then((r) => r.data)
}
export function suaDoanRa(id: string, body: DoanRaRequest) {
  return api.put<DoanRa>(`/doan-ra/${id}`, body).then((r) => r.data)
}
export function xoaDoanRa(id: string) {
  return api.delete(`/doan-ra/${id}`)
}
