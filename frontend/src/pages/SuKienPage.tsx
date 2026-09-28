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
  Tabs,
  Tag,
  Typography,
  message,
} from 'antd'
import { DeleteOutlined, EditOutlined, PlusOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import * as suKienApi from '../api/suKien'
import type { SuKien, SuKienRequest } from '../types'
import { useAuth } from '../auth/AuthContext'
import { thongBaoLoi } from '../api/client'
import { AttachmentPanel } from '../components/AttachmentPanel'
import { TuDienSelect } from '../components/TuDienSelect'

export function SuKienPage() {
  const { coTheSua } = useAuth()
  const duocSua = coTheSua('SU_KIEN')
  const queryClient = useQueryClient()

  const [trang, setTrang] = useState(1)
  const [tuKhoa, setTuKhoa] = useState('')
  const [dangSua, setDangSua] = useState<SuKien | null>(null)
  const [moForm, setMoForm] = useState(false)
  const [form] = Form.useForm()

  const { data, isLoading } = useQuery({
    queryKey: ['su-kien', trang, tuKhoa],
    queryFn: () => suKienApi.danhSach({ page: trang - 1, size: 20, tuKhoa: tuKhoa || undefined }),
  })

  const luuMutation = useMutation({
    mutationFn: (body: SuKienRequest) => (dangSua ? suKienApi.sua(dangSua.id, body) : suKienApi.tao(body)),
    onSuccess: () => {
      message.success(dangSua ? 'Đã cập nhật' : 'Đã tạo mới')
      queryClient.invalidateQueries({ queryKey: ['su-kien'] })
      dongForm()
    },
    onError: (err) => message.error(thongBaoLoi(err)),
  })

  const xoaMutation = useMutation({
    mutationFn: suKienApi.xoa,
    onSuccess: () => {
      message.success('Đã xóa')
      queryClient.invalidateQueries({ queryKey: ['su-kien'] })
    },
    onError: (err) => message.error(thongBaoLoi(err)),
  })

  function moFormTao() {
    setDangSua(null)
    form.resetFields()
    setMoForm(true)
  }

  function moFormSua(s: SuKien) {
    setDangSua(s)
    form.setFieldsValue({
      ...s,
      thoiGianBatDau: dayjs(s.thoiGianBatDau),
      thoiGianKetThuc: s.thoiGianKetThuc ? dayjs(s.thoiGianKetThuc) : undefined,
      loaiSuKienTuDienId: s.loaiSuKienTuDienId ?? undefined,
      linhVucTuDienId: s.linhVucTuDienId ?? undefined,
    })
    setMoForm(true)
  }

  function dongForm() {
    setMoForm(false)
    setDangSua(null)
    form.resetFields()
  }

  function xuLySubmit(values: any) {
    const body: SuKienRequest = {
      ...(values as SuKienRequest),
      thoiGianBatDau: dayjs(values.thoiGianBatDau as dayjs.Dayjs).format('YYYY-MM-DD'),
      thoiGianKetThuc: values.thoiGianKetThuc
        ? dayjs(values.thoiGianKetThuc as dayjs.Dayjs).format('YYYY-MM-DD')
        : null,
    }
    luuMutation.mutate(body)
  }

  return (
    <div>
      <Typography.Text type="secondary" style={{ display: 'block', marginBottom: 16 }}>
        Hội nghị, hội thảo, tọa đàm, lễ ký kết... (phân loại qua "Loại sự kiện")
      </Typography.Text>
      <Space style={{ marginBottom: 16 }} wrap>
        <Input.Search
          placeholder="Tìm theo tên sự kiện, đơn vị tổ chức..."
          allowClear
          style={{ width: 300 }}
          onSearch={(v) => {
            setTuKhoa(v)
            setTrang(1)
          }}
        />
        {duocSua && (
          <Button type="primary" icon={<PlusOutlined />} onClick={moFormTao}>
            Thêm sự kiện
          </Button>
        )}
      </Space>

      <Table<SuKien>
        rowKey="id"
        loading={isLoading}
        scroll={{ x: 'max-content' }}
        dataSource={data?.content}
        pagination={{
          current: trang,
          total: data?.totalElements,
          pageSize: 20,
          onChange: setTrang,
          showTotal: (t) => `Tổng ${t} sự kiện`,
        }}
        columns={[
          { title: 'Tên sự kiện', dataIndex: 'tenSuKien', ellipsis: true },
          {
            title: 'Loại',
            dataIndex: 'loaiSuKienTen',
            width: 130,
            render: (v: string | null) => (v ? <Tag>{v}</Tag> : '-'),
          },
          { title: 'Đơn vị tổ chức', dataIndex: 'donViToChuc', render: (v: string | null) => v ?? '-' },
          {
            title: 'Bắt đầu',
            dataIndex: 'thoiGianBatDau',
            width: 110,
            render: (v: string) => dayjs(v).format('DD/MM/YYYY'),
          },
          {
            title: 'Kết thúc',
            dataIndex: 'thoiGianKetThuc',
            width: 110,
            render: (v: string | null) => (v ? dayjs(v).format('DD/MM/YYYY') : '-'),
          },
          { title: 'Số lượng tham gia', dataIndex: 'soLuongThamGia', width: 100, render: (v: number | null) => v ?? '-' },
          {
            title: 'Thao tác',
            width: 120,
            fixed: 'right' as const,
            render: (_, s) =>
              duocSua && (
                <Space>
                  <Button size="small" icon={<EditOutlined />} onClick={() => moFormSua(s)} />
                  <Popconfirm title="Xóa sự kiện này?" onConfirm={() => xoaMutation.mutate(s.id)}>
                    <Button size="small" danger icon={<DeleteOutlined />} />
                  </Popconfirm>
                </Space>
              ),
          },
        ]}
      />

      <Drawer title={dangSua ? 'Sửa sự kiện' : 'Thêm sự kiện'} open={moForm} onClose={dongForm} width={560}>
        <Tabs
          items={[
            {
              key: 'thong-tin',
              label: 'Thông tin',
              children: (
                <Form form={form} layout="vertical" onFinish={xuLySubmit}>
                  <Form.Item name="tenSuKien" label="Tên sự kiện" rules={[{ required: true, message: 'Bắt buộc' }]}>
                    <Input />
                  </Form.Item>
                  <Form.Item name="loaiSuKienTuDienId" label="Loại sự kiện">
                    <TuDienSelect loai="LOAI_SU_KIEN" placeholder="Chọn loại sự kiện" />
                  </Form.Item>
                  <Form.Item name="linhVucTuDienId" label="Lĩnh vực">
                    <TuDienSelect loai="LINH_VUC_HOAT_DONG" placeholder="Chọn lĩnh vực" />
                  </Form.Item>
                  <Space size={16}>
                    <Form.Item
                      name="thoiGianBatDau"
                      label="Thời gian bắt đầu"
                      rules={[{ required: true, message: 'Bắt buộc' }]}
                    >
                      <DatePicker format="DD/MM/YYYY" />
                    </Form.Item>
                    <Form.Item name="thoiGianKetThuc" label="Thời gian kết thúc">
                      <DatePicker format="DD/MM/YYYY" />
                    </Form.Item>
                  </Space>
                  <Form.Item name="diaDiem" label="Địa điểm">
                    <Input />
                  </Form.Item>
                  <Form.Item name="donViToChuc" label="Đơn vị tổ chức">
                    <Input />
                  </Form.Item>
                  <Form.Item name="soLuongThamGia" label="Số lượng tham gia">
                    <InputNumber style={{ width: '100%' }} min={0} />
                  </Form.Item>
                  <Form.Item name="noiDung" label="Nội dung">
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
                    children: <AttachmentPanel bang="su_kien" banGhiId={dangSua.id} choPhepSua={duocSua} />,
                  },
                ]
              : []),
          ]}
        />
      </Drawer>
    </div>
  )
}
