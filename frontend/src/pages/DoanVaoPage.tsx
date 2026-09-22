import { useState } from 'react'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import {
  Button,
  DatePicker,
  Drawer,
  Form,
  Input,
  InputNumber,
  Popconfirm,
  Select,
  Space,
  Table,
  Typography,
  message,
} from 'antd'
import { DeleteOutlined, EditOutlined, HistoryOutlined, PlusOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import * as doanApi from '../api/doan'
import type { DoanVao, DoanVaoRequest } from '../types'
import { useAuth } from '../auth/AuthContext'
import { thongBaoLoi } from '../api/client'
import { AuditHistoryDrawer } from '../components/AuditHistoryDrawer'
import { DoiTacSelect } from '../components/DoiTacSelect'

export function DoanVaoPage() {
  const { coTheSua } = useAuth()
  const duocSua = coTheSua('DOAN_VAO')
  const queryClient = useQueryClient()

  const [trang, setTrang] = useState(1)
  const [tuKhoa, setTuKhoa] = useState('')
  const [dangSua, setDangSua] = useState<DoanVao | null>(null)
  const [moForm, setMoForm] = useState(false)
  const [lichSuId, setLichSuId] = useState<string | null>(null)
  const [form] = Form.useForm()

  const { data, isLoading } = useQuery({
    queryKey: ['doan-vao', trang, tuKhoa],
    queryFn: () => doanApi.danhSachDoanVao({ page: trang - 1, size: 20, tuKhoa: tuKhoa || undefined }),
  })

  const luuMutation = useMutation({
    mutationFn: (body: DoanVaoRequest) =>
      dangSua ? doanApi.suaDoanVao(dangSua.id, body) : doanApi.taoDoanVao(body),
    onSuccess: () => {
      message.success(dangSua ? 'Đã cập nhật' : 'Đã tạo mới')
      queryClient.invalidateQueries({ queryKey: ['doan-vao'] })
      dongForm()
    },
    onError: (err) => message.error(thongBaoLoi(err)),
  })

  const xoaMutation = useMutation({
    mutationFn: doanApi.xoaDoanVao,
    onSuccess: () => {
      message.success('Đã xóa')
      queryClient.invalidateQueries({ queryKey: ['doan-vao'] })
    },
    onError: (err) => message.error(thongBaoLoi(err)),
  })

  function moFormTao() {
    setDangSua(null)
    form.resetFields()
    setMoForm(true)
  }

  function moFormSua(d: DoanVao) {
    setDangSua(d)
    form.setFieldsValue({
      ...d,
      thoiGianDen: dayjs(d.thoiGianDen),
      thoiGianDi: dayjs(d.thoiGianDi),
    })
    setMoForm(true)
  }

  function dongForm() {
    setMoForm(false)
    setDangSua(null)
    form.resetFields()
  }

  function xuLySubmit(values: Record<string, unknown>) {
    const body: DoanVaoRequest = {
      ...(values as DoanVaoRequest),
      thoiGianDen: dayjs(values.thoiGianDen as dayjs.Dayjs).format('YYYY-MM-DD'),
      thoiGianDi: dayjs(values.thoiGianDi as dayjs.Dayjs).format('YYYY-MM-DD'),
    }
    luuMutation.mutate(body)
  }

  return (
    <div>
      <Typography.Text type="secondary" style={{ display: 'block', marginBottom: 16 }}>
        Khách nước ngoài đến làm việc
      </Typography.Text>
      <Space style={{ marginBottom: 16 }} wrap>
        <Input.Search
          placeholder="Tìm theo tên đoàn, nội dung..."
          allowClear
          style={{ width: 280 }}
          onSearch={(v) => {
            setTuKhoa(v)
            setTrang(1)
          }}
        />
        {duocSua && (
          <Button type="primary" icon={<PlusOutlined />} onClick={moFormTao}>
            Thêm đoàn vào
          </Button>
        )}
      </Space>

      <Table<DoanVao>
        rowKey="id"
        loading={isLoading}
        scroll={{ x: 'max-content' }}
        dataSource={data?.content}
        pagination={{
          current: trang,
          total: data?.totalElements,
          pageSize: 20,
          onChange: setTrang,
          showTotal: (t) => `Tổng ${t} đoàn`,
        }}
        columns={[
          { title: 'Tên đoàn', dataIndex: 'tenDoan' },
          { title: 'Đối tác', dataIndex: 'tenDoiTac', render: (v: string | null) => v ?? '-' },
          {
            title: 'Thời gian đến',
            dataIndex: 'thoiGianDen',
            width: 110,
            render: (v: string) => dayjs(v).format('DD/MM/YYYY'),
          },
          {
            title: 'Thời gian đi',
            dataIndex: 'thoiGianDi',
            width: 110,
            render: (v: string) => dayjs(v).format('DD/MM/YYYY'),
          },
          { title: 'Số ngày', dataIndex: 'soNgay', width: 80 },
          { title: 'Quốc tịch', dataIndex: 'quocTich', render: (v: string[]) => v.join(', ') },
          { title: 'Khách NN', dataIndex: 'soLuongNguoiNuocNgoai', width: 90 },
          {
            title: 'Thao tác',
            width: 160,
            fixed: 'right' as const,
            render: (_, d) => (
              <Space>
                <Button size="small" icon={<HistoryOutlined />} onClick={() => setLichSuId(d.id)} />
                {duocSua && (
                  <>
                    <Button size="small" icon={<EditOutlined />} onClick={() => moFormSua(d)} />
                    <Popconfirm title="Xóa đoàn này?" onConfirm={() => xoaMutation.mutate(d.id)}>
                      <Button size="small" danger icon={<DeleteOutlined />} />
                    </Popconfirm>
                  </>
                )}
              </Space>
            ),
          },
        ]}
      />

      <Drawer title={dangSua ? 'Sửa đoàn vào' : 'Thêm đoàn vào'} open={moForm} onClose={dongForm} width={520}>
        <Form form={form} layout="vertical" onFinish={xuLySubmit}>
          <Form.Item name="tenDoan" label="Tên đoàn" rules={[{ required: true, message: 'Bắt buộc' }]}>
            <Input />
          </Form.Item>
          <Form.Item name="doiTacId" label="Đối tác liên quan (nếu có)">
            <DoiTacSelect allowClear />
          </Form.Item>
          <Space size={16}>
            <Form.Item name="thoiGianDen" label="Thời gian đến" rules={[{ required: true, message: 'Bắt buộc' }]}>
              <DatePicker format="DD/MM/YYYY" />
            </Form.Item>
            <Form.Item name="thoiGianDi" label="Thời gian đi" rules={[{ required: true, message: 'Bắt buộc' }]}>
              <DatePicker format="DD/MM/YYYY" />
            </Form.Item>
          </Space>
          <Space size={16}>
            <Form.Item
              name="soLuongNguoiNuocNgoai"
              label="Số lượng người nước ngoài"
              rules={[{ required: true, message: 'Bắt buộc' }]}
            >
              <InputNumber min={0} />
            </Form.Item>
            <Form.Item name="soLuongNguoiVietNam" label="Số lượng người Việt Nam">
              <InputNumber min={0} />
            </Form.Item>
          </Space>
          <Form.Item name="quocTich" label="Quốc tịch" rules={[{ required: true, message: 'Bắt buộc' }]}>
            <Select mode="tags" placeholder="Nhập và Enter để thêm quốc tịch" />
          </Form.Item>
          <Form.Item name="noiDungLamViec" label="Nội dung làm việc">
            <Input.TextArea rows={3} />
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit" loading={luuMutation.isPending} block>
              Lưu
            </Button>
          </Form.Item>
        </Form>
      </Drawer>

      <AuditHistoryDrawer open={!!lichSuId} onClose={() => setLichSuId(null)} bang="doan_vao" banGhiId={lichSuId} />
    </div>
  )
}
