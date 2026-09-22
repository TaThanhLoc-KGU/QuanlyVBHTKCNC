import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Button, Card, Form, Input, Typography, message } from 'antd'
import { LockOutlined, UserOutlined } from '@ant-design/icons'
import { useAuth } from '../auth/AuthContext'
import { thongBaoLoi } from '../api/client'
import { ChangePasswordModal } from './ChangePasswordModal'

export function LoginPage() {
  const { dangNhap } = useAuth()
  const navigate = useNavigate()
  const [dangGui, setDangGui] = useState(false)
  const [batBuocDoiMatKhau, setBatBuocDoiMatKhau] = useState(false)

  async function xuLySubmit(values: { tenDangNhap: string; matKhau: string }) {
    setDangGui(true)
    try {
      const { phaiDoiMatKhau } = await dangNhap(values.tenDangNhap, values.matKhau)
      if (phaiDoiMatKhau) {
        setBatBuocDoiMatKhau(true)
      } else {
        navigate('/', { replace: true })
      }
    } catch (err) {
      message.error(thongBaoLoi(err, 'Đăng nhập không thành công'))
    } finally {
      setDangGui(false)
    }
  }

  return (
    <div
      style={{
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        minHeight: '100vh',
        background: '#f0f2f5',
        padding: 16,
      }}
    >
      <Card style={{ width: '100%', maxWidth: 380 }}>
        <Typography.Title level={3} style={{ textAlign: 'center' }}>
          P.HTKHCN
        </Typography.Title>
        <Typography.Text type="secondary" style={{ display: 'block', textAlign: 'center', marginBottom: 24 }}>
          Quản lý Hợp tác Khoa học Công nghệ và Quan hệ Quốc tế
        </Typography.Text>
        <Form layout="vertical" onFinish={xuLySubmit}>
          <Form.Item name="tenDangNhap" label="Tên đăng nhập" rules={[{ required: true, message: 'Bắt buộc' }]}>
            <Input prefix={<UserOutlined />} autoFocus />
          </Form.Item>
          <Form.Item name="matKhau" label="Mật khẩu" rules={[{ required: true, message: 'Bắt buộc' }]}>
            <Input.Password prefix={<LockOutlined />} />
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit" block loading={dangGui}>
              Đăng nhập
            </Button>
          </Form.Item>
        </Form>
      </Card>
      <ChangePasswordModal
        open={batBuocDoiMatKhau}
        batBuoc
        onClose={() => {
          setBatBuocDoiMatKhau(false)
          navigate('/', { replace: true })
        }}
      />
    </div>
  )
}
