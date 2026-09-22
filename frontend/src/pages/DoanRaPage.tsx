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
  Space,
  Table,
  Typography,
  message,
} from 'antd'
import { DeleteOutlined, EditOutlined, HistoryOutlined, PlusOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import * as doanApi from '../api/doan'
import type { DoanRa, DoanRaRequest } from '../types'
import { useAuth } from '../auth/AuthContext'
import { thongBaoLoi } from '../api/client'
import { AuditHistoryDrawer } from '../components/AuditHistoryDrawer'
import { DoiTacSelect } from '../components/DoiTacSelect'

export function DoanRaPage() {
  const { coTheSua } = useAuth()
  const duocSua = coTheSua('DOAN_RA')
  const queryClient = useQueryClient()

  const [trang, setTrang] = useState(1)
  const [tuKhoa, setTuKhoa] = useState('')
  const [dangSua, setDangSua] = useState<DoanRa | null>(null)
  const [moForm, setMoForm] = useState(false)
  const [lichSuId, setLichSuId] = useState<string | null>(null)
  const [form] = Form.useForm()

  const { data, isLoading } = useQuery({
    queryKey: ['doan-ra', trang, tuKhoa],
    queryFn: () => doanApi.danhSachDoanRa({ page: trang - 1, size: 20, tuKhoa: tuKhoa || undefined }),
  })

  const luuMutation = useMutation({
    mutationFn: (body: DoanRaRequest) => (dangSua ? doanApi.suaDoanRa(dangSua.id, body) : doanApi.taoDoanRa(body)),
    onSuccess: () => {
      message.success(dangSua ? 'Đã cập nhật' : 'Đã tạo mới')
      queryClient.invalidateQueries({ queryKey: ['doan-ra'] })
      dongForm()
    },
    onError: (err) => message.error(thongBaoLoi(err)),
  })

  const xoaMutation = useMutation({
    mutationFn: doanApi.xoaDoanRa,
    onSuccess: () => {
      message.success('Đã xóa')
      queryClient.invalidateQueries({ queryKey: ['doan-ra'] })
    },
    onError: (err) => message.error(thongBaoLoi(err)),
  })

  function moFormTao() {
    setDangSua(null)
    form.resetFields()
    setMoForm(true)
  }

  function moFormSua(d: DoanRa) {
    setDangSua(d)
    form.setFieldsValue({
      ...d,
      thoiGianDi: dayjs(d.thoiGianDi),
      thoiGianVe: dayjs(d.thoiGianVe),
    })
    setMoForm(true)
  }

  function dongForm() {
    setMoForm(false)
    setDangSua(null)
    form.resetFields()
  }

  function xuLySubmit(values: Record<string, unknown>) {
    const body: DoanRaRequest = {
      ...(values as DoanRaRequest),
      thoiGianDi: dayjs(values.thoiGianDi as dayjs.Dayjs).format('YYYY-MM-DD'),
      thoiGianVe: dayjs(values.thoiGianVe as dayjs.Dayjs).format('YYYY-MM-DD'),
    }
    luuMutation.mutate(body)
  }

  return (
    <div>
      <Typography.Text type="secondary" style={{ display: 'block', marginBottom: 16 }}>
        Cán bộ đi công tác nước ngoài
      </Typography.Text>
      <Space style={{ marginBottom: 16 }} wrap>
        <Input.Search
          placeholder="Tìm theo thành phần, nội dung..."
          allowClear
          style={{ width: 280 }}
          onSearch={(v) => {
            setTuKhoa(v)
            setTrang(1)
          }}
        />
        {duocSua && (
          <Button type="primary" icon={<PlusOutlined />} onClick={moFormTao}>
            Thêm đoàn ra
          </Button>
        )}
      </Space>

      <Table<DoanRa>
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
          { title: 'Đơn vị làm việc', dataIndex: 'tenDoiTac' },
          { title: 'Quốc gia', dataIndex: 'quocGiaLamViec', width: 130 },
          {
            title: 'Thời gian đi',
            dataIndex: 'thoiGianDi',
            width: 110,
            render: (v: string) => dayjs(v).format('DD/MM/YYYY'),
          },
          {
            title: 'Thời gian về',
            dataIndex: 'thoiGianVe',
            width: 110,
            render: (v: string) => dayjs(v).format('DD/MM/YYYY'),
          },
          { title: 'Số ngày', dataIndex: 'soNgay', width: 80 },
          { title: 'Số lượng đoàn', dataIndex: 'soLuongDoan', width: 100 },
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

      <Drawer title={dangSua ? 'Sửa đoàn ra' : 'Thêm đoàn ra'} open={moForm} onClose={dongForm} width={520}>
        <Form form={form} layout="vertical" onFinish={xuLySubmit}>
          <Form.Item name="doiTacId" label="Đơn vị làm việc (đối tác)" rules={[{ required: true, message: 'Bắt buộc' }]}>
            <DoiTacSelect />
          </Form.Item>
          <Space size={16}>
            <Form.Item name="thoiGianDi" label="Thời gian đi" rules={[{ required: true, message: 'Bắt buộc' }]}>
              <DatePicker format="DD/MM/YYYY" />
            </Form.Item>
            <Form.Item name="thoiGianVe" label="Thời gian về" rules={[{ required: true, message: 'Bắt buộc' }]}>
              <DatePicker format="DD/MM/YYYY" />
            </Form.Item>
          </Space>
          <Space size={16}>
            <Form.Item name="diaDiemDi" label="Địa điểm đi">
              <Input />
            </Form.Item>
            <Form.Item name="diaDiemDen" label="Địa điểm đến">
              <Input />
            </Form.Item>
          </Space>
          <Form.Item name="quocGiaLamViec" label="Quốc gia làm việc" rules={[{ required: true, message: 'Bắt buộc' }]}>
            <Input />
          </Form.Item>
          <Form.Item name="soLuongDoan" label="Số lượng đoàn" rules={[{ required: true, message: 'Bắt buộc' }]}>
            <InputNumber min={1} />
          </Form.Item>
          <Form.Item name="thanhPhan" label="Thành phần (danh sách thành viên)">
            <Input.TextArea rows={3} />
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

      <AuditHistoryDrawer open={!!lichSuId} onClose={() => setLichSuId(null)} bang="doan_ra" banGhiId={lichSuId} />
    </div>
  )
}
