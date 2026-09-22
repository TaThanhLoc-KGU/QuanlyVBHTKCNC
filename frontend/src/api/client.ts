import axios, { type AxiosRequestConfig } from 'axios'
import type { LoginResponse } from '../types'

const ACCESS_TOKEN_KEY = 'htkhcn_access_token'
const REFRESH_TOKEN_KEY = 'htkhcn_refresh_token'

export function luuToken(accessToken: string, refreshToken: string) {
  localStorage.setItem(ACCESS_TOKEN_KEY, accessToken)
  localStorage.setItem(REFRESH_TOKEN_KEY, refreshToken)
}

export function xoaToken() {
  localStorage.removeItem(ACCESS_TOKEN_KEY)
  localStorage.removeItem(REFRESH_TOKEN_KEY)
}

export function layAccessToken() {
  return localStorage.getItem(ACCESS_TOKEN_KEY)
}

function layRefreshToken() {
  return localStorage.getItem(REFRESH_TOKEN_KEY)
}

export const api = axios.create({ baseURL: '/api' })

api.interceptors.request.use((config) => {
  const token = layAccessToken()
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

let dangLamMoiToken: Promise<string | null> | null = null

async function lamMoiToken(): Promise<string | null> {
  const refreshToken = layRefreshToken()
  if (!refreshToken) return null
  try {
    const { data } = await axios.post<LoginResponse>('/api/auth/refresh', { refreshToken })
    luuToken(data.accessToken, data.refreshToken)
    return data.accessToken
  } catch {
    xoaToken()
    return null
  }
}

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const cauHinh = error.config as AxiosRequestConfig & { _daThuLai?: boolean }
    const duongDan = cauHinh?.url ?? ''
    const laLoginHoacRefresh = duongDan.includes('/auth/login') || duongDan.includes('/auth/refresh')

    if (error.response?.status === 401 && !laLoginHoacRefresh && !cauHinh._daThuLai) {
      cauHinh._daThuLai = true
      dangLamMoiToken ??= lamMoiToken().finally(() => {
        dangLamMoiToken = null
      })
      const tokenMoi = await dangLamMoiToken
      if (tokenMoi) {
        cauHinh.headers = { ...cauHinh.headers, Authorization: `Bearer ${tokenMoi}` }
        return api.request(cauHinh)
      }
      xoaToken()
      window.location.href = '/dang-nhap'
    }
    return Promise.reject(error)
  },
)

export function thongBaoLoi(error: unknown, macDinh = 'Đã xảy ra lỗi, vui lòng thử lại'): string {
  if (axios.isAxiosError(error)) {
    const message = (error.response?.data as { message?: string } | undefined)?.message
    return message ?? macDinh
  }
  return macDinh
}
