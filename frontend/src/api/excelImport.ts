import { api } from './client'
import type { ImportConfirmResponse, ImportPreviewResponse, ModuleKey } from '../types'

export function xemTruocImport(moDun: ModuleKey, file: File) {
  const form = new FormData()
  form.append('file', file)
  return api.post<ImportPreviewResponse>(`/import/${moDun}/xem-truoc`, form).then((r) => r.data)
}

export function xacNhanImport(moDun: ModuleKey, file: File) {
  const form = new FormData()
  form.append('file', file)
  return api.post<ImportConfirmResponse>(`/import/${moDun}/xac-nhan`, form).then((r) => r.data)
}
