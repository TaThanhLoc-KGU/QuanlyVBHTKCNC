import { useState } from 'react'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { Button, Drawer, Form, Input, Popconfirm, Space, Switch, Table, Tag, Typography, message } from 'antd'
import { EditOutlined, PlusOutlined } from '@ant-design/icons'
import * as doiTacCaNhanApi from '../api/doiTacCaNhan'
import type { DoiTacCaNhan, DoiTacCaNhanRequest } from '../types'
import { DoiTacSelect } from '../components/DoiTacSelect'
import { useAuth } from '../auth/AuthContext'
import { thongBaoLoi } from '../api/client'

export function DoiTacCaNhanPage() {
  const { coTheSua } = useAuth()
  const duocSua = coTheSua('DOI_TAC_CA_NHAN')
  const queryClient = useQueryClient()
  const [dangSua, setDangSua] = useState<DoiTacCaNhan | null>(null)
  const [moForm, setMoForm] = useState(false)
  const [form] = Form.useForm<DoiTacCaNhanRequest>()

  const { data, isLoading } = useQuery({ queryKey: ['doi-tac-ca-nhan'], queryFn: doiTacCaNhanApi.danhSach })

  const taoMutation = useMutation({
    mutationFn: doiTacCaNhanApi.tao,
    onSuccess: () => {
      message.success('Đã thêm đối tác cá nhân')
      queryClient.invalidateQueries({ queryKey: ['doi-tac-ca-nhan'] })
      dongForm()
    },
    onError: (err) => message.error(thongBaoLoi(err)),
  })

  const suaMutation = useMutation({
    mutationFn: ({ id, body }: { id: string; body: DoiTacCaNhanRequest }) => doiTacCaNhanApi.sua(id, body),
    onSuccess: () => {
      message.success('Đã lưu thay đổi')
      queryClient.invalidateQueries({ queryKey: ['doi-tac-ca-nhan'] })
      dongForm()
    },
    onError: (err) => message.error(thongBaoLoi(err)),
  })

  const xoaMutation = useMutation({
    mutationFn: doiTacCaNhanApi.xoa,
    onSuccess: () => {
      message.success('Đã xóa')
      queryClient.invalidateQueries({ queryKey: ['doi-tac-ca-nhan'] })
    },
    onError: (err) => message.error(thongBaoLoi(err)),
  })

  function moThem() {
    setDangSua(null)
    form.resetFields()
    form.setFieldsValue({ hoatDong: true })
    setMoForm(true)
  }

  function moSua(d: DoiTacCaNhan) {
    setDangSua(d)
    form.setFieldsValue({ ...d, doiTacId: d.doiTacId ?? undefined })
    setMoForm(true)
  }

  function dongForm() {
    setMoForm(false)
    setDangSua(null)
    form.resetFields()
  }

  function xuLySubmit(values: DoiTacCaNhanRequest) {
    if (dangSua) suaMutation.mutate({ id: dangSua.id, body: values })
    else taoMutation.mutate(values)
  }

  return (
    <div>
      <Typography.Paragraph type="secondary">
        Cá nhân liên hệ (không phải tổ chức) - có thể gắn với 1 đối tác tổ chức nếu có.
      </Typography.Paragraph>

      {duocSua && (
        <Button type="primary" icon={<PlusOutlined />} onClick={moThem} style={{ marginBottom: 16 }}>
          Thêm đối tác cá nhân
        </Button>
      )}

      <Table<DoiTacCaNhan>
        rowKey="id"
        loading={isLoading}
        dataSource={data}
        pagination={false}
        scroll={{ x: 'max-content' }}
        columns={[
          { title: 'Họ tên', dataIndex: 'hoTen' },
          { title: 'Chức vụ', dataIndex: 'chucVu', render: (v: string | null) => v ?? '-' },
          { title: 'Đối tác (tổ chức)', dataIndex: 'tenDoiTac', render: (v: string | null) => v ?? '-' },
          { title: 'Email', dataIndex: 'email', render: (v: string | null) => v ?? '-' },
          { title: 'Điện thoại', dataIndex: 'dienThoai', render: (v: string | null) => v ?? '-' },
          {
            title: 'Hoạt động',
            dataIndex: 'hoatDong',
            width: 110,
            render: (v: boolean) => <Tag color={v ? 'green' : 'default'}>{v ? 'Đang liên hệ' : 'Đã tắt'}</Tag>,
          },
          ...(duocSua
            ? [
                {
                  title: 'Thao tác',
                  width: 100,
                  fixed: 'right' as const,
                  render: (_: unknown, d: DoiTacCaNhan) => (
                    <Space>
                      <Button size="small" icon={<EditOutlined />} onClick={() => moSua(d)} />
                      <Popconfirm title="Xóa đối tác cá nhân này?" onConfirm={() => xoaMutation.mutate(d.id)}>
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

      <Drawer
        title={dangSua ? 'Sửa đối tác cá nhân' : 'Thêm đối tác cá nhân'}
        open={moForm}
        onClose={dongForm}
        width={420}
      >
        <Form form={form} layout="vertical" onFinish={xuLySubmit}>
          <Form.Item name="hoTen" label="Họ tên" rules={[{ required: true, message: 'Bắt buộc' }]}>
            <Input />
          </Form.Item>
          <Form.Item name="chucVu" label="Chức vụ">
            <Input />
          </Form.Item>
          <Form.Item name="doiTacId" label="Đối tác (tổ chức liên quan, nếu có)">
            <DoiTacSelect allowClear />
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
          <Form.Item name="hoatDong" label="Đang liên hệ" valuePropName="checked">
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
