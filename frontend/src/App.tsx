import { Navigate, Route, Routes } from 'react-router-dom'
import { AppLayout } from './layout/AppLayout'
import { ProtectedRoute, ChiAdmin } from './auth/ProtectedRoute'
import { LoginPage } from './pages/LoginPage'
import { DashboardPage } from './pages/DashboardPage'
import { DoiTacPage } from './pages/DoiTacPage'
import { VanBanPage } from './pages/VanBanPage'
import { MouPage } from './pages/MouPage'
import { DoanVaoPage } from './pages/DoanVaoPage'
import { DoanRaPage } from './pages/DoanRaPage'
import { ImportExcelPage } from './pages/ImportExcelPage'
import { BaoCaoPage } from './pages/BaoCaoPage'
import { NguoiDungPage } from './pages/NguoiDungPage'
import { CauHinhPage } from './pages/CauHinhPage'
import { CrawlPhapLuatPage } from './pages/CrawlPhapLuatPage'
import { CongVanDongBoPage } from './pages/CongVanDongBoPage'
import { CongVanDenPage } from './pages/CongVanDenPage'

export default function App() {
  return (
    <Routes>
      <Route path="/dang-nhap" element={<LoginPage />} />
      <Route element={<ProtectedRoute />}>
        <Route element={<AppLayout />}>
          <Route path="/" element={<DashboardPage />} />
          <Route path="/doi-tac" element={<DoiTacPage />} />
          <Route path="/van-ban-dhkg" element={<VanBanPage duongDan="van-ban-dhkg" />} />
          <Route path="/vbpl-vn" element={<VanBanPage duongDan="vbpl-vn" />} />
          <Route path="/mou" element={<MouPage />} />
          <Route path="/doan-vao" element={<DoanVaoPage />} />
          <Route path="/doan-ra" element={<DoanRaPage />} />
          <Route path="/import" element={<ImportExcelPage />} />
          <Route path="/bao-cao" element={<BaoCaoPage />} />
          <Route
            path="/nguoi-dung"
            element={
              <ChiAdmin>
                <NguoiDungPage />
              </ChiAdmin>
            }
          />
          <Route
            path="/cau-hinh"
            element={
              <ChiAdmin>
                <CauHinhPage />
              </ChiAdmin>
            }
          />
          <Route path="/crawl-phap-luat" element={<CrawlPhapLuatPage />} />
          <Route path="/dong-bo-congvan" element={<CongVanDongBoPage />} />
          <Route path="/cong-van-den" element={<CongVanDenPage />} />
        </Route>
      </Route>
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  )
}
