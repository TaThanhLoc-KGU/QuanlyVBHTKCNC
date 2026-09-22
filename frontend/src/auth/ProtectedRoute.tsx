import { Navigate, Outlet } from 'react-router-dom'
import { Spin } from 'antd'
import { useAuth } from './AuthContext'

export function ProtectedRoute() {
  const { daDangNhap, dangTaiBanDau } = useAuth()

  if (dangTaiBanDau) {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>
        <Spin size="large" />
      </div>
    )
  }
  if (!daDangNhap) {
    return <Navigate to="/dang-nhap" replace />
  }
  return <Outlet />
}

export function ChiAdmin({ children }: { children: React.ReactNode }) {
  const { laAdmin } = useAuth()
  if (!laAdmin) {
    return <Navigate to="/" replace />
  }
  return <>{children}</>
}
