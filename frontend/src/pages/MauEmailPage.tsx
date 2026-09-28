import { useState } from 'react'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { Alert, Button, Drawer, Form, Input, Popconfirm, Space, Switch, Table, Tag, Typography, message } from 'antd'
import { EditOutlined, PlusOutlined } from '@ant-design/icons'
import * as mauEmailApi from '../api/mauEmail'
import type { MauEmail, MauEmailRequest } from '../types'
import { useAuth } from '../auth/AuthContext'
import { thongBaoLoi } from '../api/client'

export function MauEmailPage() {
  const { coTheSua } = useAuth()
  const duocSua = coTheSua('MAU_EMAIL')
  const queryClient = useQueryClient()
  const [dangSua, setDangSua] = useState<MauEmail | null>(null)
  const [moForm, setMoForm] = useState(false)
  const [form] = Form.useForm<MauEmailRequest>()

  const { data, isLoading } = useQuery({ queryKey: ['mau-email'], queryFn: mauEmailApi.danhSach })

  const taoMutation = useMutation({
    mutationFn: mauEmailApi.tao,
    onSuccess: () => {
      message.success('Đã thêm mẫu email')
      queryClient.invalidateQueries({ queryKey: ['mau-email'] })
      dongForm()
    },
    onError: (err) => message.error(thongBaoLoi(err)),
  })

  const suaMutation = useMutation({
    mutationFn: ({ id, body }: { id: string; body: MauEmailRequest }) => mauEmailApi.sua(id, body),
    onSuccess: () => {
      message.success('Đã lưu thay đổi')
      queryClient.invalidateQueries({ queryKey: ['mau-email'] })
      dongForm()
    },
    onError: (err) => message.error(thongBaoLoi(err)),
  })

  const xoaMutation = useMutation({
    mutationFn: mauEmailApi.xoa,
    onSuccess: () => {
      message.success('Đã xóa')
      queryClient.invalidateQueries({ queryKey: ['mau-email'] })
    },
    onError: (err) => message.error(thongBaoLoi(err)),
  })

  function moThem() {
    setDangSua(null)
    form.resetFields()
    form.setFieldsValue({ hoatDong: true })
    setMoForm(true)
  }

  function moSua(me: MauEmail) {
    setDangSua(me)
    form.setFieldsValue(me)
    setMoForm(true)
  }

  function dongForm() {
    setMoForm(false)
    setDangSua(null)
    form.resetFields()
  }

  function xuLySubmit(values: MauEmailRequest) {
    if (dangSua) suaMutation.mutate({ id: dangSua.id, body: values })
    else taoMutation.mutate(values)
  }

  return (
    <div>
      <Typography.Paragraph type="secondary">
        Mẫu nội dung email hệ thống tự động gửi (vd thông báo MoU sắp hết hạn). Dùng{' '}
        <Typography.Text code>{'{{ten_placeholder}}'}</Typography.Text> trong tiêu đề/nội dung để chèn dữ liệu động.
      </Typography.Paragraph>

      {duocSua && (
        <Button type="primary" icon={<PlusOutlined />} onClick={moThem} style={{ marginBottom: 16 }}>
          Thêm mẫu email
        </Button>
      )}

      <Table<MauEmail>
        rowKey="id"
        loading={isLoading}
        dataSource={data}
        pagination={false}
        scroll={{ x: 'max-content' }}
        columns={[
          { title: 'Mã', dataIndex: 'ma', width: 200, render: (v: string) => <Tag>{v}</Tag> },
          { title: 'Tên mẫu', dataIndex: 'tenMau' },
          { title: 'Tiêu đề', dataIndex: 'tieuDe', ellipsis: true },
          {
            title: 'Hoạt động',
            dataIndex: 'hoatDong',
            width: 110,
            render: (v: boolean) => <Tag color={v ? 'green' : 'default'}>{v ? 'Đang dùng' : 'Đã tắt'}</Tag>,
          },
          ...(duocSua
            ? [
                {
                  title: 'Thao tác',
                  width: 100,
                  fixed: 'right' as const,
                  render: (_: unknown, me: MauEmail) => (
                    <Space>
                      <Button size="small" icon={<EditOutlined />} onClick={() => moSua(me)} />
                      <Popconfirm title="Xóa mẫu email này?" onConfirm={() => xoaMutation.mutate(me.id)}>
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

      <Drawer title={dangSua ? 'Sửa mẫu email' : 'Thêm mẫu email'} open={moForm} onClose={dongForm} width={560}>
        <Form form={form} layout="vertical" onFinish={xuLySubmit}>
          <Form.Item
            name="ma"
            label="Mã (định danh duy nhất)"
            rules={[{ required: true, message: 'Bắt buộc' }]}
            extra="Vd: THONG_BAO_MOU_GOP - hệ thống tra theo mã này khi gửi email."
          >
            <Input disabled={!!dangSua} />
          </Form.Item>
          <Form.Item name="tenMau" label="Tên mẫu" rules={[{ required: true, message: 'Bắt buộc' }]}>
            <Input />
          </Form.Item>
          <Form.Item name="tieuDe" label="Tiêu đề email" rules={[{ required: true, message: 'Bắt buộc' }]}>
            <Input />
          </Form.Item>
          <Form.Item name="noiDung" label="Nội dung email" rules={[{ required: true, message: 'Bắt buộc' }]}>
            <Input.TextArea rows={8} />
          </Form.Item>
          <Form.Item name="moTa" label="Mô tả / ghi chú placeholder">
            <Input.TextArea rows={2} />
          </Form.Item>
          <Form.Item name="hoatDong" label="Hoạt động" valuePropName="checked">
            <Switch />
          </Form.Item>
          {!dangSua && (
            <Alert
              type="info"
              showIcon
              style={{ marginBottom: 16 }}
              message="Nếu mã trùng với mã hệ thống đang dùng (vd THONG_BAO_MOU_GOP), mẫu này sẽ thay thế nội dung mặc định ngay khi lưu."
            />
          )}
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
