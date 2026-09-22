import { useState } from 'react'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import {
  Button,
  Drawer,
  Form,
  Input,
  Modal,
  Popconfirm,
  Select,
  Space,
  Table,
  Tag,
  Typography,
  message,
} from 'antd'
import { KeyOutlined, LockOutlined, PlusOutlined, UnlockOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import * as nguoiDungApi from '../api/nguoiDung'
import type { NguoiDungRequest } from '../api/nguoiDung'
import type { ModuleKey, NguoiDung } from '../types'
import { thongBaoLoi } from '../api/client'

const TUY_CHON_MODULE: { value: ModuleKey; label: string }[] = [
  { value: 'DOI_TAC', label: 'Đối tác' },
  { value: 'VAN_BAN_DHKG', label: 'Văn bản ĐHKG' },
  { value: 'VBPL_VN', label: 'VBPL VN' },
  { value: 'MOU', label: 'MoU' },
  { value: 'DOAN_VAO', label: 'Đoàn vào' },
  { value: 'DOAN_RA', label: 'Đoàn ra' },
  { value: 'CONG_VAN_DEN', label: 'Công văn đến' },
]

const NHAN_MODULE: Record<ModuleKey, string> = Object.fromEntries(
  TUY_CHON_MODULE.map((m) => [m.value, m.label]),
) as Record<ModuleKey, string>

const NHAN_VAI_TRO: Record<string, string> = {
  ADMIN: 'Quản trị viên',
  EDITOR: 'Biên tập viên',
  VIEWER: 'Người xem',
}

const NHAN_TRANG_THAI_TAI_KHOAN: Record<string, string> = {
  HOAT_DONG: 'Đang hoạt động',
  TAM_KHOA: 'Đã khóa',
}

export function NguoiDungPage() {
  const queryClient = useQueryClient()
  const [trang, setTrang] = useState(1)
  const [moForm, setMoForm] = useState(false)
  const [form] = Form.useForm<NguoiDungRequest>()

  const { data, isLoading } = useQuery({
    queryKey: ['nguoi-dung', trang],
    queryFn: () => nguoiDungApi.danhSachNguoiDung(trang - 1, 20),
  })

  const taoMutation = useMutation({
    mutationFn: nguoiDungApi.taoNguoiDung,
    onSuccess: (res) => {
      queryClient.invalidateQueries({ queryKey: ['nguoi-dung'] })
      setMoForm(false)
      form.resetFields()
      Modal.success({
        title: 'Đã tạo tài khoản',
        content: (
          <div>
            <p>Tài khoản: {res.nguoiDung.tenDangNhap}</p>
            <p>
              Mật khẩu tạm: <Typography.Text code copyable>{res.matKhauTam}</Typography.Text>
            </p>
            <p>Vui lòng gửi mật khẩu này cho người dùng, họ sẽ phải đổi mật khẩu khi đăng nhập lần đầu.</p>
          </div>
        ),
      })
    },
    onError: (err) => message.error(thongBaoLoi(err)),
  })

  const khoaMutation = useMutation({
    mutationFn: nguoiDungApi.khoaTaiKhoan,
    onSuccess: () => {
      message.success('Đã khóa tài khoản')
      queryClient.invalidateQueries({ queryKey: ['nguoi-dung'] })
    },
  })

  const moKhoaMutation = useMutation({
    mutationFn: nguoiDungApi.moKhoaTaiKhoan,
    onSuccess: () => {
      message.success('Đã mở khóa tài khoản')
      queryClient.invalidateQueries({ queryKey: ['nguoi-dung'] })
    },
  })

  const datLaiMatKhauMutation = useMutation({
    mutationFn: nguoiDungApi.datLaiMatKhau,
    onSuccess: (res) => {
      Modal.success({
        title: 'Đã đặt lại mật khẩu',
        content: (
          <Typography.Text code copyable>
            {res.matKhauTam}
          </Typography.Text>
        ),
      })
    },
  })

  return (
    <div>
      <Space style={{ marginBottom: 16 }}>
        <Button
          type="primary"
          icon={<PlusOutlined />}
          onClick={() => {
            form.resetFields()
            setMoForm(true)
          }}
        >
          Tạo tài khoản
        </Button>
      </Space>

      <Table<NguoiDung>
        rowKey="id"
        loading={isLoading}
        scroll={{ x: 'max-content' }}
        dataSource={data?.content}
        pagination={{
          current: trang,
          total: data?.totalElements,
          pageSize: 20,
          onChange: setTrang,
        }}
        columns={[
          { title: 'Tên đăng nhập', dataIndex: 'tenDangNhap' },
          { title: 'Họ tên', dataIndex: 'hoTen' },
          { title: 'Email', dataIndex: 'email' },
          {
            title: 'Vai trò',
            dataIndex: 'vaiTro',
            render: (v: string[]) => v.map((r) => <Tag key={r}>{NHAN_VAI_TRO[r] ?? r}</Tag>),
          },
          {
            title: 'Module được sửa',
            dataIndex: 'bienTapMoDun',
            render: (v: ModuleKey[]) => v.map((m) => <Tag key={m}>{NHAN_MODULE[m] ?? m}</Tag>),
          },
          {
            title: 'Trạng thái',
            dataIndex: 'trangThai',
            render: (v: string) => (
              <Tag color={v === 'HOAT_DONG' ? 'green' : 'red'}>{NHAN_TRANG_THAI_TAI_KHOAN[v] ?? v}</Tag>
            ),
          },
          { title: 'Ngày tạo', dataIndex: 'ngayTao', render: (v: string) => dayjs(v).format('DD/MM/YYYY') },
          {
            title: 'Thao tác',
            width: 160,
            fixed: 'right' as const,
            render: (_, nd) => (
              <Space>
                {nd.trangThai === 'HOAT_DONG' ? (
                  <Popconfirm title="Khóa tài khoản này?" onConfirm={() => khoaMutation.mutate(nd.id)}>
                    <Button size="small" icon={<LockOutlined />} />
                  </Popconfirm>
                ) : (
                  <Button size="small" icon={<UnlockOutlined />} onClick={() => moKhoaMutation.mutate(nd.id)} />
                )}
                <Popconfirm title="Đặt lại mật khẩu, mật khẩu cũ sẽ không dùng được nữa?" onConfirm={() => datLaiMatKhauMutation.mutate(nd.id)}>
                  <Button size="small" icon={<KeyOutlined />} />
                </Popconfirm>
              </Space>
            ),
          },
        ]}
      />

      <Drawer title="Tạo tài khoản" open={moForm} onClose={() => setMoForm(false)} width={420}>
        <Form form={form} layout="vertical" onFinish={(v) => taoMutation.mutate(v)}>
          <Form.Item name="tenDangNhap" label="Tên đăng nhập" rules={[{ required: true, message: 'Bắt buộc' }]}>
            <Input />
          </Form.Item>
          <Form.Item
            name="email"
            label="Email"
            rules={[
              { required: true, message: 'Bắt buộc' },
              { type: 'email', message: 'Email không hợp lệ' },
            ]}
          >
            <Input />
          </Form.Item>
          <Form.Item name="hoTen" label="Họ tên" rules={[{ required: true, message: 'Bắt buộc' }]}>
            <Input />
          </Form.Item>
          <Form.Item name="vaiTroMa" label="Vai trò" rules={[{ required: true, message: 'Bắt buộc' }]}>
            <Select
              mode="multiple"
              options={[
                { value: 'ADMIN', label: 'Quản trị viên' },
                { value: 'EDITOR', label: 'Biên tập viên' },
                { value: 'VIEWER', label: 'Người xem' },
              ]}
            />
          </Form.Item>
          <Form.Item name="bienTapMoDun" label="Module được sửa (Editor)">
            <Select mode="multiple" options={TUY_CHON_MODULE} />
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit" loading={taoMutation.isPending} block>
              Tạo tài khoản
            </Button>
          </Form.Item>
        </Form>
      </Drawer>
    </div>
  )
}
