import { useState } from 'react'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { Button, Drawer, Form, Input, Popconfirm, Space, Switch, Table, Tag, Typography, message } from 'antd'
import { EditOutlined, PlusOutlined } from '@ant-design/icons'
import * as thanhVienApi from '../api/thanhVien'
import type { ThanhVienPhuTrach, ThanhVienPhuTrachRequest } from '../types'
import { TuDienSelect } from '../components/TuDienSelect'
import { useAuth } from '../auth/AuthContext'
import { thongBaoLoi } from '../api/client'

export function ThanhVienPhuTrachPage() {
  const { coTheSua } = useAuth()
  const duocSua = coTheSua('THANH_VIEN_PHU_TRACH')
  const queryClient = useQueryClient()
  const [dangSua, setDangSua] = useState<ThanhVienPhuTrach | null>(null)
  const [moForm, setMoForm] = useState(false)
  const [form] = Form.useForm<ThanhVienPhuTrachRequest>()

  const { data, isLoading } = useQuery({ queryKey: ['thanh-vien-phu-trach'], queryFn: thanhVienApi.danhSach })

  const taoMutation = useMutation({
    mutationFn: thanhVienApi.tao,
    onSuccess: () => {
      message.success('Đã thêm thành viên')
      queryClient.invalidateQueries({ queryKey: ['thanh-vien-phu-trach'] })
      dongForm()
    },
    onError: (err) => message.error(thongBaoLoi(err)),
  })

  const suaMutation = useMutation({
    mutationFn: ({ id, body }: { id: string; body: ThanhVienPhuTrachRequest }) => thanhVienApi.sua(id, body),
    onSuccess: () => {
      message.success('Đã lưu thay đổi')
      queryClient.invalidateQueries({ queryKey: ['thanh-vien-phu-trach'] })
      dongForm()
    },
    onError: (err) => message.error(thongBaoLoi(err)),
  })

  const xoaMutation = useMutation({
    mutationFn: thanhVienApi.xoa,
    onSuccess: () => {
      message.success('Đã xóa')
      queryClient.invalidateQueries({ queryKey: ['thanh-vien-phu-trach'] })
    },
    onError: (err) => message.error(thongBaoLoi(err)),
  })

  function moThem() {
    setDangSua(null)
    form.resetFields()
    form.setFieldsValue({ hoatDong: true })
    setMoForm(true)
  }

  function moSua(tv: ThanhVienPhuTrach) {
    setDangSua(tv)
    form.setFieldsValue({ ...tv, vaiTroTuDienId: tv.vaiTroTuDienId ?? undefined })
    setMoForm(true)
  }

  function dongForm() {
    setMoForm(false)
    setDangSua(null)
    form.resetFields()
  }

  function xuLySubmit(values: ThanhVienPhuTrachRequest) {
    if (dangSua) suaMutation.mutate({ id: dangSua.id, body: values })
    else taoMutation.mutate(values)
  }

  return (
    <div>
      <Typography.Paragraph type="secondary">
        Danh sách nhân sự phụ trách công tác Hợp tác quốc tế của đơn vị.
      </Typography.Paragraph>

      {duocSua && (
        <Button type="primary" icon={<PlusOutlined />} onClick={moThem} style={{ marginBottom: 16 }}>
          Thêm thành viên
        </Button>
      )}

      <Table<ThanhVienPhuTrach>
        rowKey="id"
        loading={isLoading}
        dataSource={data}
        pagination={false}
        scroll={{ x: 'max-content' }}
        columns={[
          { title: 'Họ tên', dataIndex: 'hoTen' },
          { title: 'Chức vụ', dataIndex: 'chucVu', render: (v: string | null) => v ?? '-' },
          { title: 'Đơn vị', dataIndex: 'donVi', render: (v: string | null) => v ?? '-' },
          { title: 'Vai trò', dataIndex: 'vaiTroTen', render: (v: string | null) => (v ? <Tag>{v}</Tag> : '-') },
          { title: 'Email', dataIndex: 'email', render: (v: string | null) => v ?? '-' },
          { title: 'Điện thoại', dataIndex: 'dienThoai', render: (v: string | null) => v ?? '-' },
          {
            title: 'Hoạt động',
            dataIndex: 'hoatDong',
            width: 110,
            render: (v: boolean) => <Tag color={v ? 'green' : 'default'}>{v ? 'Đang công tác' : 'Đã tắt'}</Tag>,
          },
          ...(duocSua
            ? [
                {
                  title: 'Thao tác',
                  width: 100,
                  fixed: 'right' as const,
                  render: (_: unknown, tv: ThanhVienPhuTrach) => (
                    <Space>
                      <Button size="small" icon={<EditOutlined />} onClick={() => moSua(tv)} />
                      <Popconfirm title="Xóa thành viên này?" onConfirm={() => xoaMutation.mutate(tv.id)}>
                        <Button size="small" danger>
                          Xóa
                        </Button>
                      </Popconfirm>
                    </Space>
                  ),
                },
              ]
            : []),
        ]}
      />

      <Drawer title={dangSua ? 'Sửa thành viên' : 'Thêm thành viên'} open={moForm} onClose={dongForm} width={420}>
        <Form form={form} layout="vertical" onFinish={xuLySubmit}>
          <Form.Item name="hoTen" label="Họ tên" rules={[{ required: true, message: 'Bắt buộc' }]}>
            <Input />
          </Form.Item>
          <Form.Item name="chucVu" label="Chức vụ">
            <Input />
          </Form.Item>
          <Form.Item name="donVi" label="Đơn vị">
            <Input />
          </Form.Item>
          <Form.Item name="vaiTroTuDienId" label="Vai trò">
            <TuDienSelect loai="VAI_TRO_THANH_VIEN" placeholder="Chọn vai trò" />
          </Form.Item>
          <Form.Item name="email" label="Email" rules={[{ type: 'email', message: 'Email không hợp lệ' }]}>
            <Input />
          </Form.Item>
          <Form.Item name="dienThoai" label="Điện thoại">
            <Input />
          </Form.Item>
          <Form.Item name="ghiChu" label="Ghi chú">
            <Input.TextArea rows={2} />
          </Form.Item>
          <Form.Item name="hoatDong" label="Đang công tác" valuePropName="checked">
            <Switch />
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit" loading={taoMutation.isPending || suaMutation.isPending} block>
              Lưu
            </Button>
          </Form.Item>
        </Form>
      </Drawer>
    </div>
  )
}
