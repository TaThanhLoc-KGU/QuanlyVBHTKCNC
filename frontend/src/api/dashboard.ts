import { api } from './client'
import type { DashboardResponse } from '../types'

export function layDashboard() {
  return api.get<DashboardResponse>('/dashboard').then((r) => r.data)
}
