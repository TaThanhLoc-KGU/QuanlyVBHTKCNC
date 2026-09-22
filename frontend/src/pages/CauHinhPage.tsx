import { useState } from 'react'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { Button, Form, Input, Space, Table, Tag, Typography, message } from 'antd'
import { EditOutlined, SaveOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import * as cauHinhApi from '../api/cauHinh'
import type { CauHinhHeThong } from '../api/cauHinh'
import { thongBaoLoi } from '../api/client'

export function CauHinhPage() {
  const queryClient = useQueryClient()
  const [dangSuaMa, setDangSuaMa] = useState<string | null>(null)
  const [form] = Form.useForm<{ giaTri: string }>()

  const { data, isLoading } = useQuery({
    queryKey: ['cau-hinh-he-thong'],
    queryFn: cauHinhApi.danhSachCauHinh,
  })

  const luuMutation = useMutation({
    mutationFn: ({ ma, giaTri }: { ma: string; giaTri: string }) => cauHinhApi.suaCauHinh(ma, giaTri),
    onSuccess: () => {
      message.success('Đã lưu cấu hình')
      queryClient.invalidateQueries({ queryKey: ['cau-hinh-he-thong'] })
      setDangSuaMa(null)
    },
    onError: (err) => message.error(thongBaoLoi(err)),
  })

  function moSua(ch: CauHinhHeThong) {
    setDangSuaMa(ch.ma)
    form.setFieldsValue({ giaTri: ch.giaTri })
  }

  return (
    <div>
      <Typography.Paragraph type="secondary">
        Các thông số dùng chung cho hệ thống (ngưỡng cảnh báo MoU sắp hết hạn...). Chỉ Quản trị viên được sửa.
      </Typography.Paragraph>

      <Table<CauHinhHeThong>
        rowKey="ma"
        loading={isLoading}
        dataSource={data}
        pagination={false}
        scroll={{ x: 'max-content' }}
        columns={[
          { title: 'Mã', dataIndex: 'ma', width: 260, render: (v: string) => <Tag>{v}</Tag> },
          { title: 'Mô tả', dataIndex: 'moTa' },
          {
            title: 'Giá trị',
            dataIndex: 'giaTri',
            width: 260,
            render: (v: string, ch) =>
              dangSuaMa === ch.ma ? (
                <Form form={form} layout="inline" onFinish={(vals) => luuMutation.mutate({ ma: ch.ma, giaTri: vals.giaTri })}>
                  <Form.Item name="giaTri" rules={[{ required: true, message: 'Bắt buộc' }]} style={{ marginBottom: 0 }}>
                    <Input autoFocus style={{ width: 160 }} />
                  </Form.Item>
                  <Form.Item style={{ marginBottom: 0 }}>
                    <Space>
                      <Button size="small" type="primary" htmlType="submit" icon={<SaveOutlined />} loading={luuMutation.isPending} />
                      <Button size="small" onClick={() => setDangSuaMa(null)}>
                        Hủy
                      </Button>
                    </Space>
                  </Form.Item>
                </Form>
              ) : (
                <Typography.Text code>{v}</Typography.Text>
              ),
          },
          {
            title: 'Cập nhật lúc',
            dataIndex: 'ngaySua',
            width: 160,
            render: (v: string) => dayjs(v).format('DD/MM/YYYY HH:mm'),
          },
          {
            title: 'Thao tác',
            width: 100,
            fixed: 'right' as const,
            render: (_, ch) =>
              dangSuaMa === ch.ma ? null : <Button size="small" icon={<EditOutlined />} onClick={() => moSua(ch)} />,
          },
        ]}
      />
    </div>
  )
}
