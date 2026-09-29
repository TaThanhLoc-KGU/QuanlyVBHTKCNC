import { useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import {
  Button,
  Card,
  DatePicker,
  Descriptions,
  Empty,
  Form,
  Input,
  InputNumber,
  Modal,
  Popconfirm,
  Rate,
  Select,
  Skeleton,
  Space,
  Switch,
  Table,
  Tabs,
  Tag,
  Typography,
  message,
} from 'antd'
import { ArrowLeftOutlined, DeleteOutlined, PlusOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import * as deTaiApi from '../api/deTai'
import * as hoiDongApi from '../api/hoiDong'
import * as taiChinhApi from '../api/deTaiTaiChinh'
import type {
  BaoCaoTienDoRequest,
  DeTaiThanhVienRequest,
  DuToanNamRequest,
  HoiDong,
  HoiDongRequest,
  HoiDongThanhVien,
  KetQuaHoiDong,
  LoaiHoiDong,
  QuyetToanRequest,
  TamUngRequest,
  ThanhToanRequest,
  TrangThaiDeTai,
  TrangThaiDuyet,
} from '../types'
import { useAuth } from '../auth/AuthContext'
import { thongBaoLoi } from '../api/client'
import { TuDienSelect } from '../components/TuDienSelect'
import { NguoiDungSelect } from '../components/NguoiDungSelect'
import { AttachmentPanel } from '../components/AttachmentPanel'
import { NHAN_TRANG_THAI_DE_TAI } from './DeTaiPage'

const NHAN_LOAI_HOI_DONG: Record<LoaiHoiDong, string> = {
  HOI_DONG_KHOA_VIEN: 'Hội đồng khoa/viện',
  TIEU_BAN_CHUYEN_MON: 'Tiểu ban chuyên môn',
  NGHIEM_THU_CO_SO: 'Nghiệm thu cơ sở',
  NGHIEM_THU_CHINH_THUC: 'Nghiệm thu chính thức',
}
const NHAN_KET_QUA_HOI_DONG: Record<KetQuaHoiDong, string> = {
  CHUA_CO_KET_QUA: 'Chưa có kết quả',
  DAT: 'Đạt',
  DAT_CO_SUA_CHUA: 'Đạt (có sửa chữa)',
  KHONG_DAT: 'Không đạt',
}
const NHAN_TRANG_THAI_DUYET: Record<TrangThaiDuyet, string> = {
  CHO_DUYET: 'Chờ duyệt',
  DA_DUYET: 'Đã duyệt',
  TU_CHOI: 'Từ chối',
}
const MAU_TRANG_THAI_DUYET: Record<TrangThaiDuyet, string> = {
  CHO_DUYET: 'default',
  DA_DUYET: 'success',
  TU_CHOI: 'error',
}

function tien(v: number | null) {
  if (v == null) return '-'
  return v.toLocaleString('vi-VN') + ' đ'
}

export function DeTaiChiTietPage() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const { coTheSua } = useAuth()
  const duocSua = coTheSua('DE_TAI_NCKH')
  const queryClient = useQueryClient()

  const { data: deTai, isLoading } = useQuery({
    queryKey: ['de-tai-chi-tiet', id],
    queryFn: () => deTaiApi.chiTiet(id!),
    enabled: !!id,
  })

  const trangThaiMutation = useMutation({
    mutationFn: (trangThai: TrangThaiDeTai) => deTaiApi.capNhatTrangThai(id!, { trangThai }),
    onSuccess: () => {
      message.success('Đã cập nhật trạng thái')
      queryClient.invalidateQueries({ queryKey: ['de-tai-chi-tiet', id] })
      queryClient.invalidateQueries({ queryKey: ['de-tai'] })
    },
    onError: (err) => message.error(thongBaoLoi(err)),
  })

  if (isLoading || !deTai) {
    return <Skeleton active />
  }

  return (
    <div>
      <Button icon={<ArrowLeftOutlined />} type="link" style={{ paddingLeft: 0 }} onClick={() => navigate('/de-tai')}>
        Quay lại danh sách
      </Button>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: 12 }}>
        <div>
          <Typography.Title level={4} style={{ marginBottom: 4 }}>
            {deTai.tenDeTai}
          </Typography.Title>
          {deTai.maDeTai && <Typography.Text type="secondary">Mã: {deTai.maDeTai}</Typography.Text>}
        </div>
        {duocSua ? (
          <Select
            value={deTai.trangThai}
            style={{ width: 220 }}
            onChange={(v: TrangThaiDeTai) => trangThaiMutation.mutate(v)}
            options={Object.entries(NHAN_TRANG_THAI_DE_TAI).map(([value, label]) => ({ value, label }))}
          />
        ) : (
          <Tag>{NHAN_TRANG_THAI_DE_TAI[deTai.trangThai]}</Tag>
        )}
      </div>

      <Card style={{ marginTop: 16, marginBottom: 16 }}>
        <Descriptions column={2} size="small">
          <Descriptions.Item label="Chủ nhiệm">{deTai.chuNhiemTen ?? deTai.chuNhiemNgoai ?? '-'}</Descriptions.Item>
          <Descriptions.Item label="Đơn vị thực hiện">{deTai.donViThucHien ?? '-'}</Descriptions.Item>
          <Descriptions.Item label="Phân loại">{deTai.phanLoaiTen ?? '-'}</Descriptions.Item>
          <Descriptions.Item label="Loại hình nghiên cứu">{deTai.loaiHinhTen ?? '-'}</Descriptions.Item>
          <Descriptions.Item label="Lĩnh vực">{deTai.linhVucTen ?? '-'}</Descriptions.Item>
          <Descriptions.Item label="Nguồn kinh phí">{deTai.nguonKinhPhiTen ?? '-'}</Descriptions.Item>
          <Descriptions.Item label="Thời gian">
            {deTai.thoiGianBatDau ? dayjs(deTai.thoiGianBatDau).format('DD/MM/YYYY') : '-'} —{' '}
            {deTai.thoiGianKetThuc ? dayjs(deTai.thoiGianKetThuc).format('DD/MM/YYYY') : '-'}
          </Descriptions.Item>
          <Descriptions.Item label="Kinh phí duyệt">{tien(deTai.kinhPhiDuyet)}</Descriptions.Item>
          <Descriptions.Item label="Mục tiêu" span={2}>
            {deTai.mucTieu ?? '-'}
          </Descriptions.Item>
          <Descriptions.Item label="Sản phẩm dự kiến" span={2}>
            {deTai.sanPhamDuKien ?? '-'}
          </Descriptions.Item>
        </Descriptions>
      </Card>

      <Tabs
        items={[
          { key: 'thanh-vien', label: 'Thành viên', children: <TabThanhVien deTaiId={deTai.id} duocSua={duocSua} /> },
          { key: 'hoi-dong', label: 'Hội đồng', children: <TabHoiDong deTaiId={deTai.id} duocSua={duocSua} /> },
          { key: 'du-toan', label: 'Dự toán năm', children: <TabDuToanNam deTaiId={deTai.id} duocSua={duocSua} /> },
          { key: 'tam-ung', label: 'Tạm ứng', children: <TabTamUng deTaiId={deTai.id} duocSua={duocSua} /> },
          { key: 'thanh-toan', label: 'Thanh toán', children: <TabThanhToan deTaiId={deTai.id} duocSua={duocSua} /> },
          { key: 'quyet-toan', label: 'Quyết toán', children: <TabQuyetToan deTaiId={deTai.id} duocSua={duocSua} /> },
          { key: 'tien-do', label: 'Báo cáo tiến độ', children: <TabBaoCaoTienDo deTaiId={deTai.id} duocSua={duocSua} /> },
          {
            key: 'dinh-kem',
            label: 'File đính kèm',
            children: <AttachmentPanel bang="de_tai" banGhiId={deTai.id} choPhepSua={duocSua} />,
          },
        ]}
      />
    </div>
  )
}

// ---------- Thanh vien ----------

function TabThanhVien({ deTaiId, duocSua }: { deTaiId: string; duocSua: boolean }) {
  const queryClient = useQueryClient()
  const [moForm, setMoForm] = useState(false)
  const [form] = Form.useForm()

  const { data, isLoading } = useQuery({
    queryKey: ['de-tai-thanh-vien', deTaiId],
    queryFn: () => deTaiApi.danhSachThanhVien(deTaiId),
  })

  const themMutation = useMutation({
    mutationFn: (body: DeTaiThanhVienRequest) => deTaiApi.themThanhVien(deTaiId, body),
    onSuccess: () => {
      message.success('Đã thêm thành viên')
      queryClient.invalidateQueries({ queryKey: ['de-tai-thanh-vien', deTaiId] })
      setMoForm(false)
      form.resetFields()
    },
    onError: (err) => message.error(thongBaoLoi(err)),
  })
  const xoaMutation = useMutation({
    mutationFn: (tvId: string) => deTaiApi.xoaThanhVien(deTaiId, tvId),
    onSuccess: () => {
      message.success('Đã xóa')
      queryClient.invalidateQueries({ queryKey: ['de-tai-thanh-vien', deTaiId] })
    },
    onError: (err) => message.error(thongBaoLoi(err)),
  })

  return (
    <div>
      {duocSua && (
        <Button icon={<PlusOutlined />} style={{ marginBottom: 12 }} onClick={() => setMoForm(true)}>
          Thêm thành viên
        </Button>
      )}
      <Table
        rowKey="id"
        size="small"
        loading={isLoading}
        dataSource={data}
        pagination={false}
        locale={{ emptyText: <Empty description="Chưa có thành viên" /> }}
        columns={[
          { title: 'Họ tên', render: (_, tv) => tv.hoTen ?? tv.hoTenNgoai ?? '-' },
          { title: 'Vai trò', dataIndex: 'vaiTroTen', render: (v: string | null) => v ?? '-' },
          duocSua
            ? {
                title: '',
                width: 60,
                render: (_, tv) => (
                  <Popconfirm title="Xóa thành viên này?" onConfirm={() => xoaMutation.mutate(tv.id)}>
                    <Button size="small" danger icon={<DeleteOutlined />} />
                  </Popconfirm>
                ),
              }
            : {},
        ]}
      />
      <Modal
        title="Thêm thành viên"
        open={moForm}
        onCancel={() => setMoForm(false)}
        onOk={() => form.submit()}
        confirmLoading={themMutation.isPending}
      >
        <Form form={form} layout="vertical" onFinish={(v) => themMutation.mutate(v)}>
          <Form.Item name="nguoiDungId" label="Người dùng hệ thống">
            <NguoiDungSelect allowClear />
          </Form.Item>
          <Form.Item name="hoTenNgoai" label="Họ tên (nếu không có tài khoản)">
            <Input />
          </Form.Item>
          <Form.Item name="vaiTroTuDienId" label="Vai trò">
            <TuDienSelect loai="VAI_TRO_THANH_VIEN_DE_TAI" placeholder="Chọn vai trò" />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}

// ---------- Hoi dong ----------

function TabHoiDong({ deTaiId, duocSua }: { deTaiId: string; duocSua: boolean }) {
  const queryClient = useQueryClient()
  const [moForm, setMoForm] = useState(false)
  const [dangSua, setDangSua] = useState<HoiDong | null>(null)
  const [form] = Form.useForm()

  const { data, isLoading } = useQuery({
    queryKey: ['hoi-dong', deTaiId],
    queryFn: () => hoiDongApi.danhSach(deTaiId),
  })

  const luuMutation = useMutation({
    mutationFn: (body: HoiDongRequest) => (dangSua ? hoiDongApi.sua(dangSua.id, body) : hoiDongApi.tao(deTaiId, body)),
    onSuccess: () => {
      message.success('Đã lưu hội đồng')
      queryClient.invalidateQueries({ queryKey: ['hoi-dong', deTaiId] })
      setMoForm(false)
      setDangSua(null)
      form.resetFields()
    },
    onError: (err) => message.error(thongBaoLoi(err)),
  })
  const xoaMutation = useMutation({
    mutationFn: hoiDongApi.xoa,
    onSuccess: () => {
      message.success('Đã xóa')
      queryClient.invalidateQueries({ queryKey: ['hoi-dong', deTaiId] })
    },
    onError: (err) => message.error(thongBaoLoi(err)),
  })

  function moFormTao() {
    setDangSua(null)
    form.resetFields()
    setMoForm(true)
  }
  function moFormSua(h: HoiDong) {
    setDangSua(h)
    form.setFieldsValue({ ...h, ngayHop: h.ngayHop ? dayjs(h.ngayHop) : undefined })
    setMoForm(true)
  }

  return (
    <div>
      {duocSua && (
        <Button icon={<PlusOutlined />} style={{ marginBottom: 12 }} onClick={moFormTao}>
          Thêm hội đồng
        </Button>
      )}
      <Table<HoiDong>
        rowKey="id"
        size="small"
        loading={isLoading}
        dataSource={data}
        pagination={false}
        locale={{ emptyText: <Empty description="Chưa có hội đồng nào" /> }}
        expandable={{
          expandedRowRender: (h) => <ThanhVienHoiDong hoiDongId={h.id} duocSua={duocSua} />,
        }}
        columns={[
          { title: 'Loại hội đồng', dataIndex: 'loai', render: (v: LoaiHoiDong) => NHAN_LOAI_HOI_DONG[v] },
          {
            title: 'Ngày họp',
            dataIndex: 'ngayHop',
            width: 110,
            render: (v: string | null) => (v ? dayjs(v).format('DD/MM/YYYY') : '-'),
          },
          { title: 'Địa điểm', dataIndex: 'diaDiem', render: (v: string | null) => v ?? '-' },
          {
            title: 'Kết quả',
            dataIndex: 'ketQua',
            render: (v: KetQuaHoiDong) => NHAN_KET_QUA_HOI_DONG[v],
          },
          { title: 'Điểm TB', dataIndex: 'diemTrungBinh', width: 90, render: (v: number | null) => v ?? '-' },
          duocSua
            ? {
                title: '',
                width: 120,
                render: (_, h) => (
                  <Space>
                    <Button size="small" onClick={() => moFormSua(h)}>
                      Sửa
                    </Button>
                    <Popconfirm title="Xóa hội đồng này?" onConfirm={() => xoaMutation.mutate(h.id)}>
                      <Button size="small" danger icon={<DeleteOutlined />} />
                    </Popconfirm>
                  </Space>
                ),
              }
            : {},
        ]}
      />
      <Modal
        title={dangSua ? 'Sửa hội đồng' : 'Thêm hội đồng'}
        open={moForm}
        onCancel={() => setMoForm(false)}
        onOk={() => form.submit()}
        confirmLoading={luuMutation.isPending}
      >
        <Form
          form={form}
          layout="vertical"
          onFinish={(values) =>
            luuMutation.mutate({
              ...values,
              ngayHop: values.ngayHop ? dayjs(values.ngayHop).format('YYYY-MM-DD') : null,
            })
          }
        >
          <Form.Item name="loai" label="Loại hội đồng" rules={[{ required: true, message: 'Bắt buộc' }]}>
            <Select options={Object.entries(NHAN_LOAI_HOI_DONG).map(([value, label]) => ({ value, label }))} />
          </Form.Item>
          <Form.Item name="ngayHop" label="Ngày họp">
            <DatePicker format="DD/MM/YYYY" style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="diaDiem" label="Địa điểm">
            <Input />
          </Form.Item>
          <Form.Item name="ketQua" label="Kết quả tổng hợp">
            <Select options={Object.entries(NHAN_KET_QUA_HOI_DONG).map(([value, label]) => ({ value, label }))} />
          </Form.Item>
          <Form.Item name="diemTrungBinh" label="Điểm trung bình">
            <InputNumber min={0} max={10} step={0.1} style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="ghiChu" label="Ghi chú">
            <Input.TextArea rows={2} />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}

function ThanhVienHoiDong({ hoiDongId, duocSua }: { hoiDongId: string; duocSua: boolean }) {
  const queryClient = useQueryClient()
  const { nguoiDung } = useAuth()
  const [moThem, setMoThem] = useState(false)
  const [dangGhiYKien, setDangGhiYKien] = useState<HoiDongThanhVien | null>(null)
  const [formThem] = Form.useForm()
  const [formYKien] = Form.useForm()

  const { data, isLoading } = useQuery({
    queryKey: ['hoi-dong-thanh-vien', hoiDongId],
    queryFn: () => hoiDongApi.danhSachThanhVien(hoiDongId),
  })

  const themMutation = useMutation({
    mutationFn: (body: any) => hoiDongApi.themThanhVien(hoiDongId, body),
    onSuccess: () => {
      message.success('Đã thêm thành viên hội đồng')
      queryClient.invalidateQueries({ queryKey: ['hoi-dong-thanh-vien', hoiDongId] })
      setMoThem(false)
      formThem.resetFields()
    },
    onError: (err) => message.error(thongBaoLoi(err)),
  })
  const xoaMutation = useMutation({
    mutationFn: hoiDongApi.xoaThanhVien,
    onSuccess: () => {
      message.success('Đã xóa')
      queryClient.invalidateQueries({ queryKey: ['hoi-dong-thanh-vien', hoiDongId] })
    },
    onError: (err) => message.error(thongBaoLoi(err)),
  })
  const yKienMutation = useMutation({
    mutationFn: ({ id, body }: { id: string; body: any }) => hoiDongApi.ghiYKien(id, body),
    onSuccess: () => {
      message.success('Đã ghi ý kiến')
      queryClient.invalidateQueries({ queryKey: ['hoi-dong-thanh-vien', hoiDongId] })
      setDangGhiYKien(null)
    },
    onError: (err) => message.error(thongBaoLoi(err)),
  })

  function moGhiYKien(tv: HoiDongThanhVien) {
    setDangGhiYKien(tv)
    formYKien.setFieldsValue({ yKien: tv.yKien, diem: tv.diem, dongY: tv.dongY ?? undefined })
  }

  return (
    <div style={{ paddingLeft: 24 }}>
      {duocSua && (
        <Button size="small" icon={<PlusOutlined />} style={{ marginBottom: 8 }} onClick={() => setMoThem(true)}>
          Thêm thành viên hội đồng
        </Button>
      )}
      <Table
        rowKey="id"
        size="small"
        loading={isLoading}
        dataSource={data}
        pagination={false}
        locale={{ emptyText: <Empty description="Chưa có thành viên" /> }}
        columns={[
          { title: 'Họ tên', render: (_, tv) => tv.hoTen ?? tv.hoTenNgoai ?? '-' },
          { title: 'Vai trò', dataIndex: 'vaiTroTen', render: (v: string | null) => v ?? '-' },
          { title: 'Đồng ý', dataIndex: 'dongY', width: 80, render: (v: boolean | null) => (v == null ? '-' : v ? 'Có' : 'Không') },
          { title: 'Điểm', dataIndex: 'diem', width: 70, render: (v: number | null) => v ?? '-' },
          { title: 'Ý kiến', dataIndex: 'yKien', render: (v: string | null) => v ?? '-' },
          {
            title: '',
            width: 140,
            render: (_, tv) => {
              const laChinhChu = tv.nguoiDungId && tv.nguoiDungId === nguoiDung?.id
              return (
                <Space>
                  {(duocSua || laChinhChu) && (
                    <Button size="small" onClick={() => moGhiYKien(tv)}>
                      Ghi ý kiến
                    </Button>
                  )}
                  {duocSua && (
                    <Popconfirm title="Xóa thành viên này?" onConfirm={() => xoaMutation.mutate(tv.id)}>
                      <Button size="small" danger icon={<DeleteOutlined />} />
                    </Popconfirm>
                  )}
                </Space>
              )
            },
          },
        ]}
      />
      <Modal
        title="Thêm thành viên hội đồng"
        open={moThem}
        onCancel={() => setMoThem(false)}
        onOk={() => formThem.submit()}
        confirmLoading={themMutation.isPending}
      >
        <Form form={formThem} layout="vertical" onFinish={(v) => themMutation.mutate(v)}>
          <Form.Item name="nguoiDungId" label="Người dùng hệ thống">
            <NguoiDungSelect allowClear />
          </Form.Item>
          <Form.Item name="hoTenNgoai" label="Họ tên (nếu không có tài khoản)">
            <Input />
          </Form.Item>
          <Form.Item name="vaiTroTuDienId" label="Vai trò trong hội đồng">
            <TuDienSelect loai="VAI_TRO_HOI_DONG" placeholder="Chọn vai trò" />
          </Form.Item>
        </Form>
      </Modal>
      <Modal
        title="Ghi ý kiến thẩm định"
        open={!!dangGhiYKien}
        onCancel={() => setDangGhiYKien(null)}
        onOk={() => formYKien.submit()}
        confirmLoading={yKienMutation.isPending}
      >
        <Form
          form={formYKien}
          layout="vertical"
          onFinish={(v) => dangGhiYKien && yKienMutation.mutate({ id: dangGhiYKien.id, body: v })}
        >
          <Form.Item name="dongY" label="Đồng ý" valuePropName="checked">
            <Switch />
          </Form.Item>
          <Form.Item name="diem" label="Điểm">
            <Rate count={10} allowHalf />
          </Form.Item>
          <Form.Item name="yKien" label="Ý kiến nhận xét">
            <Input.TextArea rows={3} />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}

// ---------- Du toan nam ----------

function TabDuToanNam({ deTaiId, duocSua }: { deTaiId: string; duocSua: boolean }) {
  const queryClient = useQueryClient()
  const [moForm, setMoForm] = useState(false)
  const [form] = Form.useForm()

  const { data, isLoading } = useQuery({
    queryKey: ['du-toan-nam', deTaiId],
    queryFn: () => taiChinhApi.danhSachDuToanNam(deTaiId),
  })
  const themMutation = useMutation({
    mutationFn: (body: DuToanNamRequest) => taiChinhApi.taoDuToanNam(deTaiId, body),
    onSuccess: () => {
      message.success('Đã thêm dự toán')
      queryClient.invalidateQueries({ queryKey: ['du-toan-nam', deTaiId] })
      setMoForm(false)
      form.resetFields()
    },
    onError: (err) => message.error(thongBaoLoi(err)),
  })
  const duyetMutation = useMutation({
    mutationFn: ({ id, trangThai }: { id: string; trangThai: TrangThaiDuyet }) =>
      taiChinhApi.duyetDuToanNam(id, { trangThai }),
    onSuccess: () => {
      message.success('Đã cập nhật')
      queryClient.invalidateQueries({ queryKey: ['du-toan-nam', deTaiId] })
    },
    onError: (err) => message.error(thongBaoLoi(err)),
  })
  const xoaMutation = useMutation({
    mutationFn: taiChinhApi.xoaDuToanNam,
    onSuccess: () => {
      message.success('Đã xóa')
      queryClient.invalidateQueries({ queryKey: ['du-toan-nam', deTaiId] })
    },
    onError: (err) => message.error(thongBaoLoi(err)),
  })

  return (
    <div>
      {duocSua && (
        <Button icon={<PlusOutlined />} style={{ marginBottom: 12 }} onClick={() => setMoForm(true)}>
          Thêm dự toán năm
        </Button>
      )}
      <Table
        rowKey="id"
        size="small"
        loading={isLoading}
        dataSource={data}
        pagination={false}
        locale={{ emptyText: <Empty description="Chưa có dự toán năm nào" /> }}
        columns={[
          { title: 'Năm', dataIndex: 'nam', width: 80 },
          { title: 'KP đề xuất', dataIndex: 'kinhPhiDeXuat', render: tien },
          { title: 'KP duyệt', dataIndex: 'kinhPhiDuyet', render: tien },
          { title: 'Đã gửi Bộ', dataIndex: 'daGuiBo', width: 90, render: (v: boolean) => (v ? 'Có' : 'Không') },
          {
            title: 'Trạng thái',
            dataIndex: 'trangThai',
            width: 130,
            render: (v: TrangThaiDuyet) => <Tag color={MAU_TRANG_THAI_DUYET[v]}>{NHAN_TRANG_THAI_DUYET[v]}</Tag>,
          },
          duocSua
            ? {
                title: '',
                width: 180,
                render: (_, r) => (
                  <Space>
                    <Button size="small" onClick={() => duyetMutation.mutate({ id: r.id, trangThai: 'DA_DUYET' })}>
                      Duyệt
                    </Button>
                    <Button size="small" onClick={() => duyetMutation.mutate({ id: r.id, trangThai: 'TU_CHOI' })}>
                      Từ chối
                    </Button>
                    <Popconfirm title="Xóa dòng này?" onConfirm={() => xoaMutation.mutate(r.id)}>
                      <Button size="small" danger icon={<DeleteOutlined />} />
                    </Popconfirm>
                  </Space>
                ),
              }
            : {},
        ]}
      />
      <Modal
        title="Thêm dự toán năm"
        open={moForm}
        onCancel={() => setMoForm(false)}
        onOk={() => form.submit()}
        confirmLoading={themMutation.isPending}
      >
        <Form form={form} layout="vertical" onFinish={(v) => themMutation.mutate(v)}>
          <Form.Item name="nam" label="Năm" rules={[{ required: true, message: 'Bắt buộc' }]}>
            <InputNumber min={2000} max={2100} style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="kinhPhiDeXuat" label="Kinh phí đề xuất">
            <InputNumber min={0} step={1000000} style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="kinhPhiDuyet" label="Kinh phí duyệt">
            <InputNumber min={0} step={1000000} style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="daGuiBo" label="Đã gửi Bộ" valuePropName="checked">
            <Switch />
          </Form.Item>
          <Form.Item name="ghiChu" label="Ghi chú">
            <Input.TextArea rows={2} />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}

// ---------- Tam ung ----------

function TabTamUng({ deTaiId, duocSua }: { deTaiId: string; duocSua: boolean }) {
  const queryClient = useQueryClient()
  const [moForm, setMoForm] = useState(false)
  const [form] = Form.useForm()

  const { data, isLoading } = useQuery({
    queryKey: ['tam-ung', deTaiId],
    queryFn: () => taiChinhApi.danhSachTamUng(deTaiId),
  })
  const themMutation = useMutation({
    mutationFn: (body: TamUngRequest) => taiChinhApi.taoTamUng(deTaiId, body),
    onSuccess: () => {
      message.success('Đã đề nghị tạm ứng')
      queryClient.invalidateQueries({ queryKey: ['tam-ung', deTaiId] })
      setMoForm(false)
      form.resetFields()
    },
    onError: (err) => message.error(thongBaoLoi(err)),
  })
  const duyetMutation = useMutation({
    mutationFn: ({ id, trangThai }: { id: string; trangThai: TrangThaiDuyet }) => taiChinhApi.duyetTamUng(id, { trangThai }),
    onSuccess: () => {
      message.success('Đã cập nhật')
      queryClient.invalidateQueries({ queryKey: ['tam-ung', deTaiId] })
    },
    onError: (err) => message.error(thongBaoLoi(err)),
  })
  const xoaMutation = useMutation({
    mutationFn: taiChinhApi.xoaTamUng,
    onSuccess: () => {
      message.success('Đã xóa')
      queryClient.invalidateQueries({ queryKey: ['tam-ung', deTaiId] })
    },
    onError: (err) => message.error(thongBaoLoi(err)),
  })

  return (
    <div>
      {duocSua && (
        <Button icon={<PlusOutlined />} style={{ marginBottom: 12 }} onClick={() => setMoForm(true)}>
          Đề nghị tạm ứng
        </Button>
      )}
      <Table
        rowKey="id"
        size="small"
        loading={isLoading}
        dataSource={data}
        pagination={false}
        locale={{ emptyText: <Empty description="Chưa có đề nghị tạm ứng nào" /> }}
        columns={[
          { title: 'Năm', dataIndex: 'nam', width: 80, render: (v: number | null) => v ?? '-' },
          { title: 'Số tiền', dataIndex: 'soTien', render: tien },
          { title: 'Lý do', dataIndex: 'lyDo', render: (v: string | null) => v ?? '-' },
          {
            title: 'Ngày đề nghị',
            dataIndex: 'ngayDeNghi',
            width: 110,
            render: (v: string) => dayjs(v).format('DD/MM/YYYY'),
          },
          {
            title: 'Trạng thái',
            dataIndex: 'trangThai',
            width: 130,
            render: (v: TrangThaiDuyet) => <Tag color={MAU_TRANG_THAI_DUYET[v]}>{NHAN_TRANG_THAI_DUYET[v]}</Tag>,
          },
          duocSua
            ? {
                title: '',
                width: 180,
                render: (_, r) => (
                  <Space>
                    <Button size="small" onClick={() => duyetMutation.mutate({ id: r.id, trangThai: 'DA_DUYET' })}>
                      Duyệt
                    </Button>
                    <Button size="small" onClick={() => duyetMutation.mutate({ id: r.id, trangThai: 'TU_CHOI' })}>
                      Từ chối
                    </Button>
                    <Popconfirm title="Xóa dòng này?" onConfirm={() => xoaMutation.mutate(r.id)}>
                      <Button size="small" danger icon={<DeleteOutlined />} />
                    </Popconfirm>
                  </Space>
                ),
              }
            : {},
        ]}
      />
      <Modal
        title="Đề nghị tạm ứng"
        open={moForm}
        onCancel={() => setMoForm(false)}
        onOk={() => form.submit()}
        confirmLoading={themMutation.isPending}
      >
        <Form
          form={form}
          layout="vertical"
          onFinish={(values) =>
            themMutation.mutate({ ...values, ngayDeNghi: dayjs(values.ngayDeNghi).format('YYYY-MM-DD') })
          }
          initialValues={{ ngayDeNghi: dayjs() }}
        >
          <Form.Item name="nam" label="Năm">
            <InputNumber min={2000} max={2100} style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="soTien" label="Số tiền" rules={[{ required: true, message: 'Bắt buộc' }]}>
            <InputNumber min={0} step={1000000} style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="ngayDeNghi" label="Ngày đề nghị" rules={[{ required: true, message: 'Bắt buộc' }]}>
            <DatePicker format="DD/MM/YYYY" style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="lyDo" label="Lý do">
            <Input.TextArea rows={2} />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}

// ---------- Thanh toan ----------

function TabThanhToan({ deTaiId, duocSua }: { deTaiId: string; duocSua: boolean }) {
  const queryClient = useQueryClient()
  const [moForm, setMoForm] = useState(false)
  const [form] = Form.useForm()

  const { data, isLoading } = useQuery({
    queryKey: ['thanh-toan', deTaiId],
    queryFn: () => taiChinhApi.danhSachThanhToan(deTaiId),
  })
  const themMutation = useMutation({
    mutationFn: (body: ThanhToanRequest) => taiChinhApi.taoThanhToan(deTaiId, body),
    onSuccess: () => {
      message.success('Đã thêm')
      queryClient.invalidateQueries({ queryKey: ['thanh-toan', deTaiId] })
      setMoForm(false)
      form.resetFields()
    },
    onError: (err) => message.error(thongBaoLoi(err)),
  })
  const duyetMutation = useMutation({
    mutationFn: ({ id, trangThai }: { id: string; trangThai: TrangThaiDuyet }) =>
      taiChinhApi.duyetThanhToan(id, { trangThai }),
    onSuccess: () => {
      message.success('Đã cập nhật')
      queryClient.invalidateQueries({ queryKey: ['thanh-toan', deTaiId] })
    },
    onError: (err) => message.error(thongBaoLoi(err)),
  })
  const xoaMutation = useMutation({
    mutationFn: taiChinhApi.xoaThanhToan,
    onSuccess: () => {
      message.success('Đã xóa')
      queryClient.invalidateQueries({ queryKey: ['thanh-toan', deTaiId] })
    },
    onError: (err) => message.error(thongBaoLoi(err)),
  })

  return (
    <div>
      {duocSua && (
        <Button icon={<PlusOutlined />} style={{ marginBottom: 12 }} onClick={() => setMoForm(true)}>
          Thêm thanh toán
        </Button>
      )}
      <Table
        rowKey="id"
        size="small"
        loading={isLoading}
        dataSource={data}
        pagination={false}
        locale={{ emptyText: <Empty description="Chưa có ho so thanh toán nào" /> }}
        columns={[
          { title: 'Năm', dataIndex: 'nam', width: 80 },
          { title: 'Số tiền', dataIndex: 'soTien', render: tien },
          {
            title: 'Ngày thanh toán',
            dataIndex: 'ngayThanhToan',
            width: 130,
            render: (v: string | null) => (v ? dayjs(v).format('DD/MM/YYYY') : '-'),
          },
          {
            title: 'Trạng thái',
            dataIndex: 'trangThai',
            width: 130,
            render: (v: TrangThaiDuyet) => <Tag color={MAU_TRANG_THAI_DUYET[v]}>{NHAN_TRANG_THAI_DUYET[v]}</Tag>,
          },
          duocSua
            ? {
                title: '',
                width: 180,
                render: (_, r) => (
                  <Space>
                    <Button size="small" onClick={() => duyetMutation.mutate({ id: r.id, trangThai: 'DA_DUYET' })}>
                      Duyệt
                    </Button>
                    <Button size="small" onClick={() => duyetMutation.mutate({ id: r.id, trangThai: 'TU_CHOI' })}>
                      Từ chối
                    </Button>
                    <Popconfirm title="Xóa dòng này?" onConfirm={() => xoaMutation.mutate(r.id)}>
                      <Button size="small" danger icon={<DeleteOutlined />} />
                    </Popconfirm>
                  </Space>
                ),
              }
            : {},
        ]}
      />
      <Modal
        title="Thêm thanh toán"
        open={moForm}
        onCancel={() => setMoForm(false)}
        onOk={() => form.submit()}
        confirmLoading={themMutation.isPending}
      >
        <Form
          form={form}
          layout="vertical"
          onFinish={(values) =>
            themMutation.mutate({
              ...values,
              ngayThanhToan: values.ngayThanhToan ? dayjs(values.ngayThanhToan).format('YYYY-MM-DD') : null,
            })
          }
        >
          <Form.Item name="nam" label="Năm" rules={[{ required: true, message: 'Bắt buộc' }]}>
            <InputNumber min={2000} max={2100} style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="soTien" label="Số tiền" rules={[{ required: true, message: 'Bắt buộc' }]}>
            <InputNumber min={0} step={1000000} style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="ngayThanhToan" label="Ngày thanh toán">
            <DatePicker format="DD/MM/YYYY" style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="ghiChu" label="Ghi chú">
            <Input.TextArea rows={2} />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}

// ---------- Quyet toan ----------

function TabQuyetToan({ deTaiId, duocSua }: { deTaiId: string; duocSua: boolean }) {
  const queryClient = useQueryClient()
  const [form] = Form.useForm()

  const { data, isLoading } = useQuery({
    queryKey: ['quyet-toan', deTaiId],
    queryFn: () => taiChinhApi.layQuyetToan(deTaiId),
  })
  const luuMutation = useMutation({
    mutationFn: (body: QuyetToanRequest) => taiChinhApi.luuQuyetToan(deTaiId, body),
    onSuccess: () => {
      message.success('Đã lưu quyết toán')
      queryClient.invalidateQueries({ queryKey: ['quyet-toan', deTaiId] })
    },
    onError: (err) => message.error(thongBaoLoi(err)),
  })
  const duyetMutation = useMutation({
    mutationFn: (trangThai: TrangThaiDuyet) => taiChinhApi.duyetQuyetToan(deTaiId, { trangThai }),
    onSuccess: () => {
      message.success('Đã cập nhật')
      queryClient.invalidateQueries({ queryKey: ['quyet-toan', deTaiId] })
    },
    onError: (err) => message.error(thongBaoLoi(err)),
  })

  if (isLoading) return <Skeleton active />

  return (
    <Card style={{ maxWidth: 480 }}>
      {data && (
        <Tag color={MAU_TRANG_THAI_DUYET[data.trangThai]} style={{ marginBottom: 16 }}>
          {NHAN_TRANG_THAI_DUYET[data.trangThai]}
        </Tag>
      )}
      <Form
        form={form}
        layout="vertical"
        initialValues={{
          tongKinhPhiDaCap: data?.tongKinhPhiDaCap,
          tongKinhPhiDaSuDung: data?.tongKinhPhiDaSuDung,
          ngayQuyetToan: data?.ngayQuyetToan ? dayjs(data.ngayQuyetToan) : undefined,
          ghiChu: data?.ghiChu,
        }}
        onFinish={(values) =>
          luuMutation.mutate({
            ...values,
            ngayQuyetToan: values.ngayQuyetToan ? dayjs(values.ngayQuyetToan).format('YYYY-MM-DD') : null,
          })
        }
      >
        <Form.Item name="tongKinhPhiDaCap" label="Tổng kinh phí đã cấp">
          <InputNumber min={0} step={1000000} style={{ width: '100%' }} disabled={!duocSua} />
        </Form.Item>
        <Form.Item name="tongKinhPhiDaSuDung" label="Tổng kinh phí đã sử dụng">
          <InputNumber min={0} step={1000000} style={{ width: '100%' }} disabled={!duocSua} />
        </Form.Item>
        <Form.Item name="ngayQuyetToan" label="Ngày quyết toán">
          <DatePicker format="DD/MM/YYYY" style={{ width: '100%' }} disabled={!duocSua} />
        </Form.Item>
        <Form.Item name="ghiChu" label="Ghi chú">
          <Input.TextArea rows={2} disabled={!duocSua} />
        </Form.Item>
        {duocSua && (
          <Space>
            <Button type="primary" htmlType="submit" loading={luuMutation.isPending}>
              Lưu
            </Button>
            <Button onClick={() => duyetMutation.mutate('DA_DUYET')}>Duyệt</Button>
            <Button onClick={() => duyetMutation.mutate('TU_CHOI')}>Từ chối</Button>
          </Space>
        )}
      </Form>
    </Card>
  )
}

// ---------- Bao cao tien do ----------

function TabBaoCaoTienDo({ deTaiId, duocSua }: { deTaiId: string; duocSua: boolean }) {
  const queryClient = useQueryClient()
  const [moForm, setMoForm] = useState(false)
  const [form] = Form.useForm()

  const { data, isLoading } = useQuery({
    queryKey: ['bao-cao-tien-do', deTaiId],
    queryFn: () => taiChinhApi.danhSachBaoCaoTienDo(deTaiId),
  })
  const themMutation = useMutation({
    mutationFn: (body: BaoCaoTienDoRequest) => taiChinhApi.taoBaoCaoTienDo(deTaiId, body),
    onSuccess: () => {
      message.success('Đã nộp báo cáo')
      queryClient.invalidateQueries({ queryKey: ['bao-cao-tien-do', deTaiId] })
      setMoForm(false)
      form.resetFields()
    },
    onError: (err) => message.error(thongBaoLoi(err)),
  })
  const duyetMutation = useMutation({
    mutationFn: ({ id, trangThai }: { id: string; trangThai: TrangThaiDuyet }) =>
      taiChinhApi.duyetBaoCaoTienDo(id, { trangThai }),
    onSuccess: () => {
      message.success('Đã cập nhật')
      queryClient.invalidateQueries({ queryKey: ['bao-cao-tien-do', deTaiId] })
    },
    onError: (err) => message.error(thongBaoLoi(err)),
  })
  const xoaMutation = useMutation({
    mutationFn: taiChinhApi.xoaBaoCaoTienDo,
    onSuccess: () => {
      message.success('Đã xóa')
      queryClient.invalidateQueries({ queryKey: ['bao-cao-tien-do', deTaiId] })
    },
    onError: (err) => message.error(thongBaoLoi(err)),
  })

  return (
    <div>
      {duocSua && (
        <Button icon={<PlusOutlined />} style={{ marginBottom: 12 }} onClick={() => setMoForm(true)}>
          Thêm báo cáo tiến độ
        </Button>
      )}
      <Table
        rowKey="id"
        size="small"
        loading={isLoading}
        dataSource={data}
        pagination={false}
        locale={{ emptyText: <Empty description="Chưa có báo cáo tiến độ nào" /> }}
        columns={[
          { title: 'Kỳ báo cáo', dataIndex: 'kyBaoCao', render: (v: string | null) => v ?? '-' },
          {
            title: 'Hạn nộp',
            dataIndex: 'hanNop',
            width: 110,
            render: (v: string | null) => (v ? dayjs(v).format('DD/MM/YYYY') : '-'),
          },
          {
            title: 'Ngày nộp',
            dataIndex: 'ngayNop',
            width: 110,
            render: (v: string | null) => (v ? dayjs(v).format('DD/MM/YYYY') : '-'),
          },
          {
            title: 'Trạng thái',
            dataIndex: 'trangThai',
            width: 130,
            render: (v: TrangThaiDuyet) => <Tag color={MAU_TRANG_THAI_DUYET[v]}>{NHAN_TRANG_THAI_DUYET[v]}</Tag>,
          },
          duocSua
            ? {
                title: '',
                width: 180,
                render: (_, r) => (
                  <Space>
                    <Button size="small" onClick={() => duyetMutation.mutate({ id: r.id, trangThai: 'DA_DUYET' })}>
                      Duyệt
                    </Button>
                    <Button size="small" onClick={() => duyetMutation.mutate({ id: r.id, trangThai: 'TU_CHOI' })}>
                      Từ chối
                    </Button>
                    <Popconfirm title="Xóa dòng này?" onConfirm={() => xoaMutation.mutate(r.id)}>
                      <Button size="small" danger icon={<DeleteOutlined />} />
                    </Popconfirm>
                  </Space>
                ),
              }
            : {},
        ]}
      />
      <Modal
        title="Thêm báo cáo tiến độ"
        open={moForm}
        onCancel={() => setMoForm(false)}
        onOk={() => form.submit()}
        confirmLoading={themMutation.isPending}
      >
        <Form
          form={form}
          layout="vertical"
          onFinish={(values) =>
            themMutation.mutate({
              ...values,
              hanNop: values.hanNop ? dayjs(values.hanNop).format('YYYY-MM-DD') : null,
              ngayNop: values.ngayNop ? dayjs(values.ngayNop).format('YYYY-MM-DD') : null,
            })
          }
        >
          <Form.Item name="kyBaoCao" label="Kỳ báo cáo">
            <Input placeholder="VD: Quý 1/2026" />
          </Form.Item>
          <Form.Item name="hanNop" label="Hạn nộp">
            <DatePicker format="DD/MM/YYYY" style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="ngayNop" label="Ngày nộp">
            <DatePicker format="DD/MM/YYYY" style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="noiDung" label="Nội dung">
            <Input.TextArea rows={3} />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}
