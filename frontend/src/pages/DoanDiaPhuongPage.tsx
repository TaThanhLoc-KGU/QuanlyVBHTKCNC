import { useState } from 'react'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import {
  Button,
  DatePicker,
  Drawer,
  Form,
  Input,
  Popconfirm,
  Space,
  Table,
  Tabs,
  Typography,
  message,
} from 'antd'
import { DeleteOutlined, EditOutlined, HistoryOutlined, PlusOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import * as doanDiaPhuongApi from '../api/doanDiaPhuong'
import type { DoanDiaPhuong, DoanDiaPhuongRequest } from '../types'
import { useAuth } from '../auth/AuthContext'
import { thongBaoLoi } from '../api/client'
import { AuditHistoryDrawer } from '../components/AuditHistoryDrawer'
import { AttachmentPanel } from '../components/AttachmentPanel'
import { DoiTacSelect } from '../components/DoiTacSelect'
import { TuDienSelect } from '../components/TuDienSelect'

export function DoanDiaPhuongPage() {
  const { coTheSua } = useAuth()
  const duocSua = coTheSua('DOAN_DIA_PHUONG')
  const queryClient = useQueryClient()

  const [trang, setTrang] = useState(1)
  const [tuKhoa, setTuKhoa] = useState('')
  const [dangSua, setDangSua] = useState<DoanDiaPhuong | null>(null)
  const [moForm, setMoForm] = useState(false)
  const [lichSuId, setLichSuId] = useState<string | null>(null)
  const [form] = Form.useForm()

  const { data, isLoading } = useQuery({
    queryKey: ['doan-dia-phuong', trang, tuKhoa],
    queryFn: () => doanDiaPhuongApi.danhSach({ page: trang - 1, size: 20, tuKhoa: tuKhoa || undefined }),
  })

  const luuMutation = useMutation({
    mutationFn: (body: DoanDiaPhuongRequest) =>
      dangSua ? doanDiaPhuongApi.sua(dangSua.id, body) : doanDiaPhuongApi.tao(body),
    onSuccess: () => {
      message.success(dangSua ? 'Đã cập nhật' : 'Đã tạo mới')
      queryClient.invalidateQueries({ queryKey: ['doan-dia-phuong'] })
      dongForm()
    },
    onError: (err) => message.error(thongBaoLoi(err)),
  })

  const xoaMutation = useMutation({
    mutationFn: doanDiaPhuongApi.xoa,
    onSuccess: () => {
      message.success('Đã xóa')
      queryClient.invalidateQueries({ queryKey: ['doan-dia-phuong'] })
    },
    onError: (err) => message.error(thongBaoLoi(err)),
  })

  function moFormTao() {
    setDangSua(null)
    form.resetFields()
    setMoForm(true)
  }

  function moFormSua(d: DoanDiaPhuong) {
    setDangSua(d)
    form.setFieldsValue({
      ...d,
      thoiGianDi: dayjs(d.thoiGianDi),
      thoiGianVe: dayjs(d.thoiGianVe),
      doiTacId: d.doiTacId ?? undefined,
      mucTieuTuDienId: d.mucTieuTuDienId ?? undefined,
      nguonKinhPhiTuDienId: d.nguonKinhPhiTuDienId ?? undefined,
    })
    setMoForm(true)
  }

  function dongForm() {
    setMoForm(false)
    setDangSua(null)
    form.resetFields()
  }

  function xuLySubmit(values: any) {
    const body: DoanDiaPhuongRequest = {
      ...(values as DoanDiaPhuongRequest),
      thoiGianDi: dayjs(values.thoiGianDi as dayjs.Dayjs).format('YYYY-MM-DD'),
      thoiGianVe: dayjs(values.thoiGianVe as dayjs.Dayjs).format('YYYY-MM-DD'),
    }
    luuMutation.mutate(body)
  }

  return (
    <div>
      <Typography.Text type="secondary" style={{ display: 'block', marginBottom: 16 }}>
        Đoàn đi làm việc trong nước (khác "Đoàn ra" - đi công tác nước ngoài)
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
            Thêm đoàn đi địa phương
          </Button>
        )}
      </Space>

      <Table<DoanDiaPhuong>
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
          { title: 'Địa điểm', dataIndex: 'diaDiem', render: (v: string | null) => v ?? '-' },
          { title: 'Mục tiêu', dataIndex: 'mucTieuTen', render: (v: string | null) => v ?? '-' },
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

      <Drawer
        title={dangSua ? 'Sửa đoàn đi địa phương' : 'Thêm đoàn đi địa phương'}
        open={moForm}
        onClose={dongForm}
        width={560}
      >
        <Tabs
          items={[
            {
              key: 'thong-tin',
              label: 'Thông tin',
              children: (
                <Form form={form} layout="vertical" onFinish={xuLySubmit}>
                  <Form.Item name="tenDoan" label="Tên đoàn" rules={[{ required: true, message: 'Bắt buộc' }]}>
                    <Input />
                  </Form.Item>
                  <Form.Item name="doiTacId" label="Đối tác liên quan (nếu có)">
                    <DoiTacSelect allowClear />
                  </Form.Item>
                  <Space size={16}>
                    <Form.Item name="thoiGianDi" label="Thời gian đi" rules={[{ required: true, message: 'Bắt buộc' }]}>
                      <DatePicker format="DD/MM/YYYY" />
                    </Form.Item>
                    <Form.Item name="thoiGianVe" label="Thời gian về" rules={[{ required: true, message: 'Bắt buộc' }]}>
                      <DatePicker format="DD/MM/YYYY" />
                    </Form.Item>
                  </Space>
                  <Form.Item name="diaDiem" label="Địa điểm">
                    <Input />
                  </Form.Item>
                  <Form.Item name="mucTieuTuDienId" label="Mục tiêu">
                    <TuDienSelect loai="MUC_TIEU_DOAN_RA" placeholder="Chọn mục tiêu" />
                  </Form.Item>
                  <Form.Item name="nguonKinhPhiTuDienId" label="Nguồn kinh phí">
                    <TuDienSelect loai="NGUON_KINH_PHI" placeholder="Chọn nguồn kinh phí" />
                  </Form.Item>
                  <Form.Item name="thanhPhan" label="Thành phần (danh sách thành viên)">
                    <Input.TextArea rows={2} />
                  </Form.Item>
                  <Form.Item name="noiDungLamViec" label="Nội dung làm việc">
                    <Input.TextArea rows={3} />
                  </Form.Item>
                  <Form.Item name="ghiChu" label="Ghi chú">
                    <Input.TextArea rows={2} />
                  </Form.Item>
                  <Form.Item>
                    <Button type="primary" htmlType="submit" loading={luuMutation.isPending} block>
                      Lưu
                    </Button>
                  </Form.Item>
                  {!dangSua && (
                    <Typography.Text type="secondary" style={{ fontSize: 12 }}>
                      Sau khi lưu, mở lại để đính kèm file ở tab "File đính kèm".
                    </Typography.Text>
                  )}
                </Form>
              ),
            },
            ...(dangSua
              ? [
                  {
                    key: 'dinh-kem',
                    label: 'File đính kèm',
                    children: <AttachmentPanel bang="doan_dia_phuong" banGhiId={dangSua.id} choPhepSua={duocSua} />,
                  },
                ]
              : []),
          ]}
        />
      </Drawer>

      <AuditHistoryDrawer open={!!lichSuId} onClose={() => setLichSuId(null)} bang="doan_dia_phuong" banGhiId={lichSuId} />
    </div>
  )
}
