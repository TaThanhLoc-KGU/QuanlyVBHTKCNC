import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
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
  Tag,
  Typography,
  message,
} from 'antd'
import { DeleteOutlined, EditOutlined, EyeOutlined, PlusOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import * as deTaiApi from '../api/deTai'
import * as linhVucApi from '../api/linhVucNghienCuu'
import type { DeTai, DeTaiRequest, TrangThaiDeTai } from '../types'
import { useAuth } from '../auth/AuthContext'
import { thongBaoLoi } from '../api/client'
import { TuDienSelect } from '../components/TuDienSelect'
import { NguoiDungSelect } from '../components/NguoiDungSelect'

export const NHAN_TRANG_THAI_DE_TAI: Record<TrangThaiDeTai, string> = {
  DE_XUAT: 'Đề xuất',
  DANG_THAM_DINH_KHOA: 'Đang thẩm định khoa/viện',
  DANG_THAM_DINH_CHUYEN_MON: 'Đang thẩm định chuyên môn',
  DA_TRUNG_TUYEN: 'Đã trúng tuyển',
  KHONG_TRUNG_TUYEN: 'Không trúng tuyển',
  DA_KY_HOP_DONG: 'Đã ký hợp đồng',
  DANG_THUC_HIEN: 'Đang thực hiện',
  CHO_NGHIEM_THU_CO_SO: 'Chờ nghiệm thu cơ sở',
  CHO_NGHIEM_THU_CHINH_THUC: 'Chờ nghiệm thu chính thức',
  DA_NGHIEM_THU: 'Đã nghiệm thu',
  DA_THANH_LY: 'Đã thanh lý',
  BI_HUY: 'Bị hủy',
}
const MAU_TRANG_THAI_DE_TAI: Record<TrangThaiDeTai, string> = {
  DE_XUAT: 'default',
  DANG_THAM_DINH_KHOA: 'blue',
  DANG_THAM_DINH_CHUYEN_MON: 'blue',
  DA_TRUNG_TUYEN: 'cyan',
  KHONG_TRUNG_TUYEN: 'red',
  DA_KY_HOP_DONG: 'geekblue',
  DANG_THUC_HIEN: 'processing',
  CHO_NGHIEM_THU_CO_SO: 'orange',
  CHO_NGHIEM_THU_CHINH_THUC: 'orange',
  DA_NGHIEM_THU: 'success',
  DA_THANH_LY: 'default',
  BI_HUY: 'error',
}

export function DeTaiPage() {
  const { coTheSua } = useAuth()
  const duocSua = coTheSua('DE_TAI_NCKH')
  const queryClient = useQueryClient()
  const navigate = useNavigate()

  const [trang, setTrang] = useState(1)
  const [tuKhoa, setTuKhoa] = useState('')
  const [trangThaiLoc, setTrangThaiLoc] = useState<TrangThaiDeTai>()
  const [dangSua, setDangSua] = useState<DeTai | null>(null)
  const [moForm, setMoForm] = useState(false)
  const [form] = Form.useForm()

  const { data, isLoading } = useQuery({
    queryKey: ['de-tai', trang, tuKhoa, trangThaiLoc],
    queryFn: () => deTaiApi.danhSach({ page: trang - 1, size: 20, tuKhoa: tuKhoa || undefined, trangThai: trangThaiLoc }),
  })
  const { data: linhVucList } = useQuery({ queryKey: ['linh-vuc-nghien-cuu'], queryFn: linhVucApi.danhSach })

  const luuMutation = useMutation({
    mutationFn: (body: DeTaiRequest) => (dangSua ? deTaiApi.sua(dangSua.id, body) : deTaiApi.tao(body)),
    onSuccess: (res) => {
      message.success(dangSua ? 'Đã cập nhật' : 'Đã tạo mới')
      queryClient.invalidateQueries({ queryKey: ['de-tai'] })
      dongForm()
      if (!dangSua) {
        navigate(`/de-tai/${res.id}`)
      }
    },
    onError: (err) => message.error(thongBaoLoi(err)),
  })

  const xoaMutation = useMutation({
    mutationFn: deTaiApi.xoa,
    onSuccess: () => {
      message.success('Đã xóa')
      queryClient.invalidateQueries({ queryKey: ['de-tai'] })
    },
    onError: (err) => message.error(thongBaoLoi(err)),
  })

  function moFormTao() {
    setDangSua(null)
    form.resetFields()
    setMoForm(true)
  }

  function moFormSua(d: DeTai) {
    setDangSua(d)
    form.setFieldsValue({
      ...d,
      thoiGianBatDau: d.thoiGianBatDau ? dayjs(d.thoiGianBatDau) : undefined,
      thoiGianKetThuc: d.thoiGianKetThuc ? dayjs(d.thoiGianKetThuc) : undefined,
      chuNhiemId: d.chuNhiemId ?? undefined,
      phanLoaiTuDienId: d.phanLoaiTuDienId ?? undefined,
      loaiHinhTuDienId: d.loaiHinhTuDienId ?? undefined,
      nguonKinhPhiTuDienId: d.nguonKinhPhiTuDienId ?? undefined,
      linhVucId: d.linhVucId ?? undefined,
    })
    setMoForm(true)
  }

  function dongForm() {
    setMoForm(false)
    setDangSua(null)
    form.resetFields()
  }

  function xuLySubmit(values: any) {
    const body: DeTaiRequest = {
      ...(values as DeTaiRequest),
      thoiGianBatDau: values.thoiGianBatDau ? dayjs(values.thoiGianBatDau as dayjs.Dayjs).format('YYYY-MM-DD') : null,
      thoiGianKetThuc: values.thoiGianKetThuc ? dayjs(values.thoiGianKetThuc as dayjs.Dayjs).format('YYYY-MM-DD') : null,
    }
    luuMutation.mutate(body)
  }

  return (
    <div>
      <Typography.Text type="secondary" style={{ display: 'block', marginBottom: 16 }}>
        Quản lý đề tài, dự án nghiên cứu khoa học - từ đề xuất, thẩm định, hợp đồng, tài chính đến nghiệm thu
      </Typography.Text>
      <Space style={{ marginBottom: 16 }} wrap>
        <Input.Search
          placeholder="Tìm theo tên, mã đề tài..."
          allowClear
          style={{ width: 260 }}
          onSearch={(v) => {
            setTuKhoa(v)
            setTrang(1)
          }}
        />
        <Select
          style={{ width: 220 }}
          allowClear
          placeholder="Trạng thái"
          value={trangThaiLoc}
          onChange={(v) => {
            setTrangThaiLoc(v)
            setTrang(1)
          }}
          options={Object.entries(NHAN_TRANG_THAI_DE_TAI).map(([value, label]) => ({ value, label }))}
        />
        {duocSua && (
          <Button type="primary" icon={<PlusOutlined />} onClick={moFormTao}>
            Thêm đề tài
          </Button>
        )}
      </Space>

      <Table<DeTai>
        rowKey="id"
        loading={isLoading}
        scroll={{ x: 'max-content' }}
        dataSource={data?.content}
        pagination={{
          current: trang,
          total: data?.totalElements,
          pageSize: 20,
          onChange: setTrang,
          showTotal: (t) => `Tổng ${t} đề tài`,
        }}
        columns={[
          { title: 'Mã đề tài', dataIndex: 'maDeTai', width: 120, render: (v: string | null) => v ?? '-' },
          { title: 'Tên đề tài', dataIndex: 'tenDeTai' },
          { title: 'Chủ nhiệm', render: (_, d) => d.chuNhiemTen ?? d.chuNhiemNgoai ?? '-' },
          { title: 'Lĩnh vực', dataIndex: 'linhVucTen', render: (v: string | null) => v ?? '-' },
          { title: 'Năm đề xuất', dataIndex: 'namDeXuat', width: 110, render: (v: number | null) => v ?? '-' },
          {
            title: 'Trạng thái',
            dataIndex: 'trangThai',
            width: 190,
            render: (v: TrangThaiDeTai) => <Tag color={MAU_TRANG_THAI_DE_TAI[v]}>{NHAN_TRANG_THAI_DE_TAI[v]}</Tag>,
          },
          {
            title: 'Thao tác',
            width: 140,
            fixed: 'right' as const,
            render: (_, d) => (
              <Space>
                <Button size="small" icon={<EyeOutlined />} onClick={() => navigate(`/de-tai/${d.id}`)} />
                {duocSua && (
                  <>
                    <Button size="small" icon={<EditOutlined />} onClick={() => moFormSua(d)} />
                    <Popconfirm title="Xóa đề tài này?" onConfirm={() => xoaMutation.mutate(d.id)}>
                      <Button size="small" danger icon={<DeleteOutlined />} />
                    </Popconfirm>
                  </>
                )}
              </Space>
            ),
          },
        ]}
      />

      <Drawer title={dangSua ? 'Sửa đề tài' : 'Thêm đề tài'} open={moForm} onClose={dongForm} width={560}>
        <Form form={form} layout="vertical" onFinish={xuLySubmit}>
          <Form.Item name="maDeTai" label="Mã đề tài">
            <Input />
          </Form.Item>
          <Form.Item name="tenDeTai" label="Tên đề tài" rules={[{ required: true, message: 'Bắt buộc' }]}>
            <Input.TextArea rows={2} />
          </Form.Item>
          <Form.Item name="chuNhiemId" label="Chủ nhiệm (người dùng hệ thống)">
            <NguoiDungSelect allowClear />
          </Form.Item>
          <Form.Item name="chuNhiemNgoai" label="Chủ nhiệm (nếu không có tài khoản)">
            <Input placeholder="Họ tên chủ nhiệm ngoài hệ thống" />
          </Form.Item>
          <Form.Item name="donViThucHien" label="Đơn vị thực hiện">
            <Input />
          </Form.Item>
          <Form.Item name="donViChuQuan" label="Đơn vị chủ quản">
            <Input />
          </Form.Item>
          <Form.Item name="phanLoaiTuDienId" label="Phân loại đề tài">
            <TuDienSelect loai="PHAN_LOAI_DE_TAI" placeholder="Chọn phân loại" />
          </Form.Item>
          <Form.Item name="loaiHinhTuDienId" label="Loại hình nghiên cứu">
            <TuDienSelect loai="LOAI_HINH_NGHIEN_CUU" placeholder="Chọn loại hình" />
          </Form.Item>
          <Form.Item name="nguonKinhPhiTuDienId" label="Nguồn kinh phí">
            <TuDienSelect loai="NGUON_KINH_PHI" placeholder="Chọn nguồn kinh phí" />
          </Form.Item>
          <Form.Item name="linhVucId" label="Lĩnh vực nghiên cứu">
            <Select
              allowClear
              showSearch
              optionFilterProp="label"
              placeholder="Chọn lĩnh vực"
              options={(linhVucList ?? []).map((l) => ({
                value: l.id,
                label: `${'　'.repeat(l.cap - 1)}${l.ten}`,
              }))}
            />
          </Form.Item>
          <Space size={16}>
            <Form.Item name="namDeXuat" label="Năm đề xuất">
              <InputNumber min={2000} max={2100} style={{ width: 140 }} />
            </Form.Item>
            <Form.Item name="thoiGianBatDau" label="Bắt đầu">
              <DatePicker format="DD/MM/YYYY" />
            </Form.Item>
            <Form.Item name="thoiGianKetThuc" label="Kết thúc">
              <DatePicker format="DD/MM/YYYY" />
            </Form.Item>
          </Space>
          <Space size={16}>
            <Form.Item name="kinhPhiDeXuat" label="Kinh phí đề xuất">
              <InputNumber style={{ width: 200 }} min={0} step={1000000} />
            </Form.Item>
            <Form.Item name="kinhPhiDuyet" label="Kinh phí duyệt">
              <InputNumber style={{ width: 200 }} min={0} step={1000000} />
            </Form.Item>
          </Space>
          <Form.Item name="mucTieu" label="Mục tiêu">
            <Input.TextArea rows={2} />
          </Form.Item>
          <Form.Item name="noiDung" label="Nội dung">
            <Input.TextArea rows={2} />
          </Form.Item>
          <Form.Item name="sanPhamDuKien" label="Sản phẩm dự kiến">
            <Input.TextArea rows={2} />
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
              Sau khi lưu, hệ thống mở trang chi tiết để quản lý thành viên, hội đồng, tài chính, tiến độ và file đính
              kèm.
            </Typography.Text>
          )}
        </Form>
      </Drawer>
    </div>
  )
}
