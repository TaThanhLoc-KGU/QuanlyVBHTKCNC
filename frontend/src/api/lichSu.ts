import { api } from './client'
import type { LichSuThayDoi } from '../types'

export function layLichSuThayDoi(bang: string, banGhiId: string) {
  return api.get<LichSuThayDoi[]>(`/lich-su/${bang}/${banGhiId}`).then((r) => r.data)
}
