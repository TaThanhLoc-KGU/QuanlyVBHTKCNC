import { useEffect, useMemo, useState } from 'react'
import { Link, Outlet, useLocation, useNavigate } from 'react-router-dom'
import { Avatar, Button, Drawer, Dropdown, Layout, Menu, Typography } from 'antd'
import {
  BankOutlined,
  CloudSyncOutlined,
  DashboardOutlined,
  DownOutlined,
  FileExcelOutlined,
  FileTextOutlined,
  GlobalOutlined,
  InboxOutlined,
  LockOutlined,
  LogoutOutlined,
  MenuOutlined,
  SendOutlined,
  SettingOutlined,
  SolutionOutlined,
  TeamOutlined,
  UserOutlined,
} from '@ant-design/icons'
import { useAuth } from '../auth/AuthContext'
import { NotificationBell } from './NotificationBell'
import { ChangePasswordModal } from '../pages/ChangePasswordModal'

const { Header, Sider, Content } = Layout

const NHAN_VAI_TRO: Record<string, string> = {
  ADMIN: 'Quản trị viên',
  EDITOR: 'Biên tập viên',
  VIEWER: 'Người xem',
}

function useLaManHinhNho() {
  const [laManHinhNho, setLaManHinhNho] = useState(() => window.innerWidth < 768)
  useEffect(() => {
    const xuLy = () => setLaManHinhNho(window.innerWidth < 768)
    window.addEventListener('resize', xuLy)
    return () => window.removeEventListener('resize', xuLy)
  }, [])
  return laManHinhNho
}

export function AppLayout() {
  const { nguoiDung, laAdmin, coTheSua, dangXuat } = useAuth()
  const location = useLocation()
  const navigate = useNavigate()
  const [moDoiMatKhau, setMoDoiMatKhau] = useState(false)
  const [moMenuDiDong, setMoMenuDiDong] = useState(false)
  const laManHinhNho = useLaManHinhNho()

  const menuItems = useMemo(
    () => [
      { key: '/', icon: <DashboardOutlined />, label: <Link to="/">Tổng quan</Link> },
      { key: '/doi-tac', icon: <TeamOutlined />, label: <Link to="/doi-tac">Đối tác</Link> },
      { key: '/van-ban-dhkg', icon: <FileTextOutlined />, label: <Link to="/van-ban-dhkg">Văn bản ĐHKG</Link> },
      { key: '/cong-van-den', icon: <InboxOutlined />, label: <Link to="/cong-van-den">Công văn đến</Link> },
      ...(coTheSua('VAN_BAN_DHKG')
        ? [
            {
              key: '/dong-bo-congvan',
              icon: <CloudSyncOutlined />,
              label: <Link to="/dong-bo-congvan">Đồng bộ CongVan</Link>,
            },
          ]
        : []),
      { key: '/vbpl-vn', icon: <FileTextOutlined />, label: <Link to="/vbpl-vn">VBPL VN</Link> },
      ...(coTheSua('VBPL_VN')
        ? [
            {
              key: '/crawl-phap-luat',
              icon: <CloudSyncOutlined />,
              label: <Link to="/crawl-phap-luat">Cập nhật pháp luật</Link>,
            },
          ]
        : []),
      { key: '/mou', icon: <SolutionOutlined />, label: <Link to="/mou">MoU</Link> },
      { key: '/doan-vao', icon: <GlobalOutlined />, label: <Link to="/doan-vao">Đoàn vào</Link> },
      { key: '/doan-ra', icon: <SendOutlined />, label: <Link to="/doan-ra">Đoàn ra</Link> },
      { key: '/import', icon: <FileExcelOutlined />, label: <Link to="/import">Nhập Excel</Link> },
      { key: '/bao-cao', icon: <BankOutlined />, label: <Link to="/bao-cao">Báo cáo</Link> },
      ...(laAdmin
        ? [
            { key: '/nguoi-dung', icon: <UserOutlined />, label: <Link to="/nguoi-dung">Tài khoản</Link> },
            { key: '/cau-hinh', icon: <SettingOutlined />, label: <Link to="/cau-hinh">Cấu hình hệ thống</Link> },
          ]
        : []),
    ],
    [laAdmin, coTheSua],
  )

  const tieuDeTrang = useMemo(() => {
    const khop = [...menuItems]
      .reverse()
      .find((m) => location.pathname === m.key || (m.key !== '/' && location.pathname.startsWith(m.key)))
    const labelEl = khop?.label as { props?: { children?: string } } | undefined
    return labelEl?.props?.children ?? 'Tổng quan'
  }, [menuItems, location.pathname])

  const menuNguoiDung = {
    items: [
      { key: 'doi-mat-khau', icon: <LockOutlined />, label: 'Đổi mật khẩu' },
      { key: 'dang-xuat', icon: <LogoutOutlined />, label: 'Đăng xuất' },
    ],
    onClick: ({ key }: { key: string }) => {
      if (key === 'dang-xuat') {
        dangXuat()
        navigate('/dang-nhap')
      } else if (key === 'doi-mat-khau') {
        setMoDoiMatKhau(true)
      }
    },
  }

  const logoBrand = (
    <div
      style={{
        display: 'flex',
        alignItems: 'center',
        gap: 10,
        padding: '18px 20px',
        marginBottom: 4,
      }}
    >
      <div
        style={{
          width: 34,
          height: 34,
          borderRadius: 9,
          background: 'linear-gradient(135deg, #1D8AAE 0%, #155E75 100%)',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          color: '#fff',
          fontWeight: 800,
          fontSize: 15,
          flexShrink: 0,
          boxShadow: '0 2px 6px rgba(0,0,0,0.25)',
        }}
      >
        P
      </div>
      <div style={{ lineHeight: 1.25, minWidth: 0 }}>
        <div style={{ color: '#fff', fontWeight: 700, fontSize: 14.5 }}>P.HTKHCN</div>
        <div
          style={{
            color: 'rgba(255,255,255,0.55)',
            fontSize: 11,
            whiteSpace: 'nowrap',
            overflow: 'hidden',
            textOverflow: 'ellipsis',
          }}
        >
          Hợp tác KHCN &amp; QHQT
        </div>
      </div>
    </div>
  )

  const menu = (
    <Menu
      theme="dark"
      mode="inline"
      selectedKeys={[location.pathname]}
      items={menuItems}
      style={{ borderInlineEnd: 'none', background: 'transparent' }}
      onClick={() => laManHinhNho && setMoMenuDiDong(false)}
    />
  )

  return (
    <Layout style={{ minHeight: '100vh' }}>
      {laManHinhNho ? (
        <Drawer
          placement="left"
          open={moMenuDiDong}
          onClose={() => setMoMenuDiDong(false)}
          width={252}
          closable={false}
          styles={{ body: { padding: 0, background: '#0B2B3B' } }}
        >
          {logoBrand}
          {menu}
        </Drawer>
      ) : (
        <Sider breakpoint="lg" collapsible width={236}>
          {logoBrand}
          {menu}
        </Sider>
      )}
      <Layout>
        <Header
          style={{
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
            gap: 16,
            paddingInline: laManHinhNho ? 12 : 24,
            borderBottom: '1px solid #E7EBEF',
            boxShadow: '0 1px 2px rgba(16,24,40,0.04)',
            zIndex: 10,
          }}
        >
          {laManHinhNho ? (
            <Button type="text" icon={<MenuOutlined />} onClick={() => setMoMenuDiDong(true)} />
          ) : (
            <Typography.Text strong style={{ fontSize: 16, color: '#101828' }}>
              {tieuDeTrang}
            </Typography.Text>
          )}
          <div style={{ display: 'flex', alignItems: 'center', gap: 20 }}>
            <NotificationBell />
            <div style={{ width: 1, height: 24, background: '#E7EBEF' }} />
            <Dropdown menu={menuNguoiDung} placement="bottomRight" trigger={['click']}>
              <span style={{ cursor: 'pointer', display: 'flex', alignItems: 'center', gap: 10 }}>
                <Avatar
                  size={32}
                  style={{ background: '#155E75', fontWeight: 600, fontSize: 13 }}
                >
                  {(nguoiDung?.hoTen ?? '?').trim().charAt(0).toUpperCase()}
                </Avatar>
                {!laManHinhNho && (
                  <span style={{ lineHeight: 1.25 }}>
                    <div style={{ fontSize: 13.5, fontWeight: 600, color: '#101828' }}>{nguoiDung?.hoTen}</div>
                    <div style={{ fontSize: 11.5, color: '#667085' }}>
                      {nguoiDung?.vaiTro?.map((v) => NHAN_VAI_TRO[v] ?? v).join(', ')}
                    </div>
                  </span>
                )}
                {!laManHinhNho && <DownOutlined style={{ fontSize: 10, color: '#98A2B3' }} />}
              </span>
            </Dropdown>
          </div>
        </Header>
        <Content style={{ margin: laManHinhNho ? 12 : 24, overflowX: 'auto' }}>
          <Outlet />
        </Content>
      </Layout>
      <ChangePasswordModal open={moDoiMatKhau} onClose={() => setMoDoiMatKhau(false)} batBuoc={false} />
    </Layout>
  )
}
