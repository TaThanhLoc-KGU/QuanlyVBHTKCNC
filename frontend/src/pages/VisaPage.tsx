import { useState } from 'react'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import {
  Button,
  DatePicker,
  Drawer,
  Form,
  Input,
  Popconfirm,
  Select,
  Space,
  Table,
  Tabs,
  Tag,
  Typography,
  message,
} from 'antd'
import { DeleteOutlined, EditOutlined, PlusOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import * as visaApi from '../api/visa'
import type { LoaiCapVisa, Visa, VisaRequest } from '../types'
import { useAuth } from '../auth/AuthContext'
import { thongBaoLoi } from '../api/client'
import { AttachmentPanel } from '../components/AttachmentPanel'
import { DoanVaoSelect } from '../components/DoanVaoSelect'
import { TuDienSelect } from '../components/TuDienSelect'

const NHAN_LOAI_CAP: Record<LoaiCapVisa, string> = { MOI: 'Cấp mới', GIA_HAN: 'Gia hạn' }
const MAU_LOAI_CAP: Record<LoaiCapVisa, string> = { MOI: 'blue', GIA_HAN: 'orange' }

export function VisaPage() {
  const { coTheSua } = useAuth()
  const duocSua = coTheSua('VISA')
  const queryClient = useQueryClient()

  const [trang, setTrang] = useState(1)
  const [tuKhoa, setTuKhoa] = useState('')
  const [loaiCapLoc, setLoaiCapLoc] = useState<LoaiCapVisa>()
  const [dangSua, setDangSua] = useState<Visa | null>(null)
  const [moForm, setMoForm] = useState(false)
  const [form] = Form.useForm()

  const { data, isLoading } = useQuery({
    queryKey: ['visa', trang, tuKhoa, loaiCapLoc],
    queryFn: () => visaApi.danhSach({ page: trang - 1, size: 20, tuKhoa: tuKhoa || undefined, loaiCap: loaiCapLoc }),
  })

  const luuMutation = useMutation({
    mutationFn: (body: VisaRequest) => (dangSua ? visaApi.sua(dangSua.id, body) : visaApi.tao(body)),
    onSuccess: () => {
      message.success(dangSua ? 'Đã cập nhật' : 'Đã tạo mới')
      queryClient.invalidateQueries({ queryKey: ['visa'] })
      dongForm()
    },
    onError: (err) => message.error(thongBaoLoi(err)),
  })

  const xoaMutation = useMutation({
    mutationFn: visaApi.xoa,
    onSuccess: () => {
      message.success('Đã xóa')
      queryClient.invalidateQueries({ queryKey: ['visa'] })
    },
    onError: (err) => message.error(thongBaoLoi(err)),
  })

  function moFormTao() {
    setDangSua(null)
    form.resetFields()
    form.setFieldsValue({ loaiCap: 'MOI' })
    setMoForm(true)
  }

  function moFormSua(v: Visa) {
    setDangSua(v)
    form.setFieldsValue({
      ...v,
      ngayCap: dayjs(v.ngayCap),
      ngayHetHan: v.ngayHetHan ? dayjs(v.ngayHetHan) : undefined,
      mucDichTuDienId: v.mucDichTuDienId ?? undefined,
      doanVaoId: v.doanVaoId ?? undefined,
    })
    setMoForm(true)
  }

  function dongForm() {
    setMoForm(false)
    setDangSua(null)
    form.resetFields()
  }

  function xuLySubmit(values: any) {
    const body: VisaRequest = {
      ...(values as VisaRequest),
      ngayCap: dayjs(values.ngayCap as dayjs.Dayjs).format('YYYY-MM-DD'),
      ngayHetHan: values.ngayHetHan ? dayjs(values.ngayHetHan as dayjs.Dayjs).format('YYYY-MM-DD') : null,
    }
    luuMutation.mutate(body)
  }

  return (
    <div>
      <Typography.Text type="secondary" style={{ display: 'block', marginBottom: 16 }}>
        Cấp mới / gia hạn visa cho khách quốc tế
      </Typography.Text>
      <Space style={{ marginBottom: 16 }} wrap>
        <Input.Search
          placeholder="Tìm theo họ tên, quốc tịch..."
          allowClear
          style={{ width: 260 }}
          onSearch={(v) => {
            setTuKhoa(v)
            setTrang(1)
          }}
        />
        <Select
          style={{ width: 160 }}
          allowClear
          placeholder="Loại cấp"
          value={loaiCapLoc}
          onChange={(v) => {
            setLoaiCapLoc(v)
            setTrang(1)
          }}
          options={Object.entries(NHAN_LOAI_CAP).map(([value, label]) => ({ value, label }))}
        />
        {duocSua && (
          <Button type="primary" icon={<PlusOutlined />} onClick={moFormTao}>
            Thêm visa
          </Button>
        )}
      </Space>

      <Table<Visa>
        rowKey="id"
        loading={isLoading}
        scroll={{ x: 'max-content' }}
        dataSource={data?.content}
        pagination={{
          current: trang,
          total: data?.totalElements,
          pageSize: 20,
          onChange: setTrang,
          showTotal: (t) => `Tổng ${t} visa`,
        }}
        columns={[
          { title: 'Họ tên', dataIndex: 'hoTen' },
          { title: 'Quốc tịch', dataIndex: 'quocTich', render: (v: string | null) => v ?? '-' },
          {
            title: 'Loại cấp',
            dataIndex: 'loaiCap',
            width: 110,
            render: (v: LoaiCapVisa) => <Tag color={MAU_LOAI_CAP[v]}>{NHAN_LOAI_CAP[v]}</Tag>,
          },
          { title: 'Ngày cấp', dataIndex: 'ngayCap', width: 110, render: (v: string) => dayjs(v).format('DD/MM/YYYY') },
          {
            title: 'Ngày hết hạn',
            dataIndex: 'ngayHetHan',
            width: 120,
            render: (v: string | null) => (v ? dayjs(v).format('DD/MM/YYYY') : '-'),
          },
          { title: 'Cơ quan cấp', dataIndex: 'coQuanCap', render: (v: string | null) => v ?? '-' },
          { title: 'Đoàn vào liên quan', dataIndex: 'doanVaoTen', render: (v: string | null) => v ?? '-' },
          {
            title: 'Thao tác',
            width: 120,
            fixed: 'right' as const,
            render: (_, v) =>
              duocSua && (
                <Space>
                  <Button size="small" icon={<EditOutlined />} onClick={() => moFormSua(v)} />
                  <Popconfirm title="Xóa visa này?" onConfirm={() => xoaMutation.mutate(v.id)}>
                    <Button size="small" danger icon={<DeleteOutlined />} />
                  </Popconfirm>
                </Space>
              ),
          },
        ]}
      />

      <Drawer title={dangSua ? 'Sửa visa' : 'Thêm visa'} open={moForm} onClose={dongForm} width={520}>
        <Tabs
          items={[
            {
              key: 'thong-tin',
              label: 'Thông tin',
              children: (
                <Form form={form} layout="vertical" onFinish={xuLySubmit}>
                  <Form.Item name="hoTen" label="Họ tên" rules={[{ required: true, message: 'Bắt buộc' }]}>
                    <Input />
                  </Form.Item>
                  <Form.Item name="quocTich" label="Quốc tịch">
                    <Input />
                  </Form.Item>
                  <Form.Item name="loaiCap" label="Loại cấp" rules={[{ required: true, message: 'Bắt buộc' }]}>
                    <Select options={Object.entries(NHAN_LOAI_CAP).map(([value, label]) => ({ value, label }))} />
                  </Form.Item>
                  <Space size={16}>
                    <Form.Item name="ngayCap" label="Ngày cấp" rules={[{ required: true, message: 'Bắt buộc' }]}>
                      <DatePicker format="DD/MM/YYYY" />
                    </Form.Item>
                    <Form.Item name="ngayHetHan" label="Ngày hết hạn">
                      <DatePicker format="DD/MM/YYYY" />
                    </Form.Item>
                  </Space>
                  <Form.Item name="coQuanCap" label="Cơ quan cấp">
                    <Input />
                  </Form.Item>
                  <Form.Item name="mucDichTuDienId" label="Mục đích">
                    <TuDienSelect loai="MUC_DICH_DEN" placeholder="Chọn mục đích" />
                  </Form.Item>
                  <Form.Item name="doanVaoId" label="Đoàn vào liên quan (nếu có)">
                    <DoanVaoSelect allowClear />
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
                    children: <AttachmentPanel bang="visa" banGhiId={dangSua.id} choPhepSua={duocSua} />,
                  },
                ]
              : []),
          ]}
        />
      </Drawer>
    </div>
  )
}
