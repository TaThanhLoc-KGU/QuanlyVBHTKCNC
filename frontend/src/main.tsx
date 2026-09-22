import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { BrowserRouter } from 'react-router-dom'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { ConfigProvider, App as AntdApp } from 'antd'
import viVN from 'antd/locale/vi_VN'
import './index.css'
import App from './App.tsx'
import { AuthProvider } from './auth/AuthContext'

const queryClient = new QueryClient({
  defaultOptions: { queries: { retry: 1, staleTime: 10_000 } },
})

const theme = {
  token: {
    colorPrimary: '#155E75',
    colorInfo: '#155E75',
    colorLink: '#155E75',
    colorSuccess: '#16794F',
    colorWarning: '#B45309',
    colorError: '#C0362C',
    colorTextBase: '#101828',
    colorBgLayout: '#F4F6F8',
    colorBorderSecondary: '#E7EBEF',
    fontFamily:
      "'Be Vietnam Pro', -apple-system, 'Segoe UI', Roboto, sans-serif",
    fontSize: 14,
    borderRadius: 8,
    borderRadiusLG: 10,
    boxShadowTertiary:
      '0 1px 2px rgba(16, 24, 40, 0.06), 0 1px 3px rgba(16, 24, 40, 0.08)',
    controlHeight: 36,
  },
  components: {
    Layout: {
      siderBg: '#0B2B3B',
      headerBg: '#FFFFFF',
      bodyBg: '#F4F6F8',
    },
    Menu: {
      darkItemBg: '#0B2B3B',
      darkItemSelectedBg: '#155E75',
      darkItemHoverBg: 'rgba(255,255,255,0.06)',
      darkSubMenuItemBg: '#0B2B3B',
    },
    Card: {
      boxShadowTertiary:
        '0 1px 2px rgba(16, 24, 40, 0.06), 0 1px 3px rgba(16, 24, 40, 0.08)',
    },
    Table: {
      headerBg: '#F8FAFB',
      headerColor: '#475467',
      borderColor: '#EAECEF',
      rowHoverBg: '#F4F8FA',
    },
    Button: {
      controlHeight: 36,
      fontWeight: 500,
    },
    Statistic: {
      titleFontSize: 13,
    },
  },
}

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <ConfigProvider locale={viVN} theme={theme}>
      <AntdApp>
        <QueryClientProvider client={queryClient}>
          <BrowserRouter>
            <AuthProvider>
              <App />
            </AuthProvider>
          </BrowserRouter>
        </QueryClientProvider>
      </AntdApp>
    </ConfigProvider>
  </StrictMode>,
)
