import { useState } from 'react'
import { Form, Input, Modal, message } from 'antd'
import * as authApi from '../api/auth'
import { thongBaoLoi } from '../api/client'

interface Props {
  open: boolean
  onClose: () => void
  batBuoc: boolean
}

export function ChangePasswordModal({ open, onClose, batBuoc }: Props) {
  const [form] = Form.useForm()
  const [dangGui, setDangGui] = useState(false)

  async function xuLySubmit() {
    const values = await form.validateFields()
    setDangGui(true)
    try {
      await authApi.doiMatKhau(values.matKhauCu, values.matKhauMoi)
      message.success('Đổi mật khẩu thành công')
      form.resetFields()
      onClose()
    } catch (err) {
      message.error(thongBaoLoi(err, 'Đổi mật khẩu không thành công'))
    } finally {
      setDangGui(false)
    }
  }

  return (
    <Modal
      title="Đổi mật khẩu"
      open={open}
      onOk={xuLySubmit}
      onCancel={batBuoc ? undefined : onClose}
      closable={!batBuoc}
      maskClosable={!batBuoc}
      confirmLoading={dangGui}
      okText="Đổi mật khẩu"
    >
      {batBuoc && (
        <p style={{ color: '#cf1322' }}>
          Bạn đang dùng mật khẩu tạm thời - vui lòng đổi mật khẩu mới trước khi tiếp tục.
        </p>
      )}
      <Form form={form} layout="vertical">
        <Form.Item name="matKhauCu" label="Mật khẩu hiện tại" rules={[{ required: true, message: 'Bắt buộc' }]}>
          <Input.Password />
        </Form.Item>
        <Form.Item
          name="matKhauMoi"
          label="Mật khẩu mới"
          rules={[
            { required: true, message: 'Bắt buộc' },
            { min: 8, message: 'Ít nhất 8 ký tự' },
          ]}
        >
          <Input.Password />
        </Form.Item>
        <Form.Item
          name="xacNhanMatKhauMoi"
          label="Xác nhận mật khẩu mới"
          dependencies={['matKhauMoi']}
          rules={[
            { required: true, message: 'Bắt buộc' },
            ({ getFieldValue }) => ({
              validator(_, value) {
                if (!value || value === getFieldValue('matKhauMoi')) return Promise.resolve()
                return Promise.reject(new Error('Mật khẩu xác nhận không khớp'))
              },
            }),
          ]}
        >
          <Input.Password />
        </Form.Item>
      </Form>
    </Modal>
  )
}
