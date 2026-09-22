import { createContext, useContext, useEffect, useState, type ReactNode } from 'react'
import * as authApi from '../api/auth'
import { layAccessToken, luuToken, xoaToken } from '../api/client'
import type { CurrentUser, ModuleKey } from '../types'

interface AuthContextValue {
  nguoiDung: CurrentUser | null
  dangTaiBanDau: boolean
  daDangNhap: boolean
  laAdmin: boolean
  laEditor: boolean
  laViewer: boolean
  coTheSua: (module: ModuleKey) => boolean
  dangNhap: (tenDangNhap: string, matKhau: string) => Promise<{ phaiDoiMatKhau: boolean }>
  dangXuat: () => void
}

const AuthContext = createContext<AuthContextValue | null>(null)

export function AuthProvider({ children }: { children: ReactNode }) {
  const [nguoiDung, setNguoiDung] = useState<CurrentUser | null>(null)
  const [dangTaiBanDau, setDangTaiBanDau] = useState(true)

  useEffect(() => {
    const token = layAccessToken()
    if (!token) {
      setDangTaiBanDau(false)
      return
    }
    authApi
      .layThongTinCaNhan()
      .then(setNguoiDung)
      .catch(() => xoaToken())
      .finally(() => setDangTaiBanDau(false))
  }, [])

  async function dangNhap(tenDangNhap: string, matKhau: string) {
    const res = await authApi.dangNhap(tenDangNhap, matKhau)
    luuToken(res.accessToken, res.refreshToken)
    // Goi /me de lay day du bienTapMoDun (LoginResponse khong co truong nay).
    const thongTinDayDu = await authApi.layThongTinCaNhan()
    setNguoiDung(thongTinDayDu)
    return { phaiDoiMatKhau: res.phaiDoiMatKhau }
  }

  function dangXuat() {
    xoaToken()
    setNguoiDung(null)
  }

  const vaiTro = nguoiDung?.vaiTro ?? []
  const laAdmin = vaiTro.includes('ADMIN')
  const laEditor = vaiTro.includes('EDITOR')
  const value: AuthContextValue = {
    nguoiDung,
    dangTaiBanDau,
    daDangNhap: nguoiDung !== null,
    laAdmin,
    laEditor,
    laViewer: vaiTro.includes('VIEWER'),
    coTheSua: (module) => laAdmin || (laEditor && (nguoiDung?.bienTapMoDun ?? []).includes(module)),
    dangNhap,
    dangXuat,
  }

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth phai dung trong AuthProvider')
  return ctx
}
