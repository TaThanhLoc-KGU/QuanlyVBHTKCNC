import { useState } from 'react'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import {
  Button,
  DatePicker,
  Drawer,
  Empty,
  Form,
  Input,
  Popconfirm,
  Select,
  Space,
  Switch,
  Table,
  Tabs,
  Tag,
  Tooltip,
  Typography,
  message,
} from 'antd'
import { CheckOutlined, CloudSyncOutlined, DeleteOutlined, LinkOutlined, PlusOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import * as crawlApi from '../api/crawlPhapLuat'
import type { TrangThaiUngVien, UngVienPhapLuat } from '../api/crawlPhapLuat'
import * as danhMucApi from '../api/danhMuc'
import type { TinhTrangHieuLuc, VanBanRequest } from '../types'
import { useAuth } from '../auth/AuthContext'
import { thongBaoLoi } from '../api/client'

function boDauTiengViet(chuoi: string): string {
  return chuoi
    .normalize('NFD')
    .replace(/[̀-ͯ]/g, '')
    .replace(/đ/g, 'd')
    .replace(/Đ/g, 'D')
    .toLowerCase()
}

const NHAN_NGUON: Record<UngVienPhapLuat['nguon'], string> = {
  CONG_BAO_CHINH_PHU: 'Công báo Chính phủ',
  VBPL_VN_PORTAL: 'vbpl.vn',
}

const NHAN_TINH_TRANG: Record<TinhTrangHieuLuc, string> = {
  CON_HIEU_LUC: 'Còn hiệu lực',
  HET_HIEU_LUC_TOAN_BO: 'Hết hiệu lực toàn bộ',
  HET_HIEU_LUC_MOT_PHAN: 'Hết hiệu lực một phần',
  BI_THAY_THE: 'Bị thay thế',
  DA_BI_BAI_BO: 'Đã bị bãi bỏ',
}

function TabUngVien() {
  const { laAdmin } = useAuth()
  const queryClient = useQueryClient()
  const [trang, setTrang] = useState(1)
  const [locTrangThai, setLocTrangThai] = useState<TrangThaiUngVien | undefined>('CHUA_XU_LY')
  const [dangNhan, setDangNhan] = useState<UngVienPhapLuat | null>(null)
  const [form] = Form.useForm<VanBanRequest>()

  const { data, isLoading } = useQuery({
    queryKey: ['crawl-ung-vien', locTrangThai, trang],
    queryFn: () => crawlApi.danhSachUngVien(locTrangThai, trang - 1),
  })

  const { data: danhMucLoai } = useQuery({
    queryKey: ['danh-muc-loai-van-ban', 'VBPL'],
    queryFn: () => danhMucApi.danhSachLoaiVanBan('VBPL'),
  })

  const crawlNgayMutation = useMutation({
    mutationFn: crawlApi.chayCrawlNgay,
    onSuccess: (kq) => {
      message.success(`Đã quét ${kq.soTuKhoaDaQuet} từ khóa, tìm thấy ${kq.soUngVienMoi} ứng viên mới`)
      queryClient.invalidateQueries({ queryKey: ['crawl-ung-vien'] })
    },
    onError: (err) => message.error(thongBaoLoi(err)),
  })

  const nhanMutation = useMutation({
    mutationFn: ({ id, body }: { id: string; body: VanBanRequest }) => crawlApi.nhanUngVien(id, body),
    onSuccess: () => {
      message.success('Đã nhận vào VBPL VN')
      queryClient.invalidateQueries({ queryKey: ['crawl-ung-vien'] })
      dongForm()
    },
    onError: (err) => message.error(thongBaoLoi(err)),
  })

  const boQuaMutation = useMutation({
    mutationFn: crawlApi.boQuaUngVien,
    onSuccess: () => {
      message.success('Đã bỏ qua')
      queryClient.invalidateQueries({ queryKey: ['crawl-ung-vien'] })
    },
    onError: (err) => message.error(thongBaoLoi(err)),
  })

  function moFormNhan(uv: UngVienPhapLuat) {
    setDangNhan(uv)
    form.setFieldsValue({
      soHieu: uv.soHieu,
      tenVanBan: uv.tenVanBan,
      ngayBanHanh: uv.ngayBanHanh ? (dayjs(uv.ngayBanHanh) as never) : undefined,
      coQuanBanHanh: uv.coQuanBanHanh ?? '',
      tinhTrangHieuLuc: 'CON_HIEU_LUC',
    })
  }

  function dongForm() {
    setDangNhan(null)
    form.resetFields()
  }

  function xuLySubmit(values: Record<string, unknown>) {
    if (!dangNhan) return
    const body: VanBanRequest = {
      ...(values as VanBanRequest),
      ngayBanHanh: dayjs(values.ngayBanHanh as dayjs.Dayjs).format('YYYY-MM-DD'),
      ngayHieuLuc: values.ngayHieuLuc ? dayjs(values.ngayHieuLuc as dayjs.Dayjs).format('YYYY-MM-DD') : null,
    }
    nhanMutation.mutate({ id: dangNhan.id, body })
  }

  return (
    <div>
      <Space style={{ marginBottom: 16 }} wrap>
        <Select
          style={{ width: 200 }}
          value={locTrangThai}
          onChange={(v) => {
            setLocTrangThai(v)
            setTrang(1)
          }}
          options={[
            { value: 'CHUA_XU_LY', label: 'Chưa xử lý' },
            { value: 'DA_NHAN', label: 'Đã nhận' },
            { value: 'DA_BO_QUA', label: 'Đã bỏ qua' },
            { value: undefined, label: 'Tất cả' },
          ]}
        />
        {laAdmin && (
          <Button
            type="primary"
            icon={<CloudSyncOutlined />}
            loading={crawlNgayMutation.isPending}
            onClick={() => crawlNgayMutation.mutate()}
          >
            Crawl ngay
          </Button>
        )}
      </Space>

      <Table<UngVienPhapLuat>
        rowKey="id"
        loading={isLoading}
        dataSource={data?.content}
        scroll={{ x: 'max-content' }}
        pagination={{
          current: trang,
          total: data?.totalElements,
          pageSize: 20,
          onChange: setTrang,
          showTotal: (t) => `Tổng ${t} ứng viên`,
        }}
        columns={[
          {
            title: 'Nguồn',
            dataIndex: 'nguon',
            width: 150,
            render: (v: UngVienPhapLuat['nguon']) => <Tag>{NHAN_NGUON[v]}</Tag>,
          },
          { title: 'Số hiệu', dataIndex: 'soHieu', width: 180 },
          {
            title: 'Tên văn bản',
            dataIndex: 'tenVanBan',
            ellipsis: true,
            render: (v: string, uv) => (
              <Tooltip title="Mở nguồn gốc">
                <a href={uv.urlNguon} target="_blank" rel="noreferrer">
                  {v} <LinkOutlined style={{ fontSize: 11 }} />
                </a>
              </Tooltip>
            ),
          },
          { title: 'Loại', dataIndex: 'loaiVanBanText', width: 140, render: (v: string | null) => v ?? '-' },
          {
            title: 'Ngày ban hành',
            dataIndex: 'ngayBanHanh',
            width: 120,
            render: (v: string | null) => (v ? dayjs(v).format('DD/MM/YYYY') : '-'),
          },
          {
            title: 'Từ khóa khớp',
            dataIndex: 'tuKhoaKhop',
            width: 160,
            render: (v: string | null) => (v ? <Tag color="blue">{v}</Tag> : '-'),
          },
          {
            title: 'Thao tác',
            width: 160,
            fixed: 'right' as const,
            render: (_, uv) =>
              uv.trangThai === 'CHUA_XU_LY' ? (
                <Space>
                  <Button size="small" type="primary" icon={<CheckOutlined />} onClick={() => moFormNhan(uv)}>
                    Nhận
                  </Button>
                  <Popconfirm title="Bỏ qua ứng viên này?" onConfirm={() => boQuaMutation.mutate(uv.id)}>
                    <Button size="small" danger>
                      Bỏ qua
                    </Button>
                  </Popconfirm>
                </Space>
              ) : (
                <Tag color={uv.trangThai === 'DA_NHAN' ? 'green' : 'default'}>
                  {uv.trangThai === 'DA_NHAN' ? 'Đã nhận' : 'Đã bỏ qua'}
                </Tag>
              ),
          },
        ]}
      />

      <Drawer title="Nhận vào VBPL VN" open={!!dangNhan} onClose={dongForm} width={520}>
        <Typography.Paragraph type="secondary">
          Dữ liệu lấy tự động từ nguồn crawl - vui lòng đối chiếu với liên kết gốc trước khi lưu.
        </Typography.Paragraph>
        <Form form={form} layout="vertical" onFinish={xuLySubmit}>
          <Form.Item name="soHieu" label="Số hiệu" rules={[{ required: true, message: 'Bắt buộc' }]}>
            <Input />
          </Form.Item>
          <Form.Item name="tenVanBan" label="Tên văn bản" rules={[{ required: true, message: 'Bắt buộc' }]}>
            <Input.TextArea rows={2} />
          </Form.Item>
          <Form.Item name="loaiVanBanId" label="Loại văn bản" rules={[{ required: true, message: 'Bắt buộc' }]}>
            <Select
              showSearch
              placeholder="Tìm và chọn loại văn bản..."
              optionFilterProp="label"
              filterOption={(input, option) =>
                boDauTiengViet(option?.label ?? '').includes(boDauTiengViet(input))
              }
              options={(danhMucLoai ?? []).map((l) => ({ value: l.id, label: l.ten }))}
            />
          </Form.Item>
          <Space size={16}>
            <Form.Item name="ngayBanHanh" label="Ngày ban hành" rules={[{ required: true, message: 'Bắt buộc' }]}>
              <DatePicker format="DD/MM/YYYY" />
            </Form.Item>
            <Form.Item name="ngayHieuLuc" label="Ngày có hiệu lực">
              <DatePicker format="DD/MM/YYYY" />
            </Form.Item>
          </Space>
          <Form.Item name="tinhTrangHieuLuc" label="Tình trạng hiệu lực" rules={[{ required: true, message: 'Bắt buộc' }]}>
            <Select options={Object.entries(NHAN_TINH_TRANG).map(([value, label]) => ({ value, label }))} />
          </Form.Item>
          <Form.Item name="coQuanBanHanh" label="Cơ quan ban hành" rules={[{ required: true, message: 'Bắt buộc' }]}>
            <Input />
          </Form.Item>
          <Form.Item name="ghiChu" label="Ghi chú">
            <Input.TextArea rows={2} />
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit" loading={nhanMutation.isPending} block>
              Lưu vào VBPL VN
            </Button>
          </Form.Item>
        </Form>
      </Drawer>
    </div>
  )
}

function TabTuKhoa() {
  const queryClient = useQueryClient()
  const [tuKhoaMoi, setTuKhoaMoi] = useState('')

  const { data, isLoading } = useQuery({ queryKey: ['crawl-tu-khoa'], queryFn: crawlApi.danhSachTuKhoa })

  const themMutation = useMutation({
    mutationFn: crawlApi.themTuKhoa,
    onSuccess: () => {
      setTuKhoaMoi('')
      queryClient.invalidateQueries({ queryKey: ['crawl-tu-khoa'] })
    },
    onError: (err) => message.error(thongBaoLoi(err)),
  })

  const doiTrangThaiMutation = useMutation({
    mutationFn: ({ id, hoatDong }: { id: string; hoatDong: boolean }) => crawlApi.doiTrangThaiTuKhoa(id, hoatDong),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['crawl-tu-khoa'] }),
  })

  const xoaMutation = useMutation({
    mutationFn: crawlApi.xoaTuKhoa,
    onSuccess: () => {
      message.success('Đã xóa từ khóa')
      queryClient.invalidateQueries({ queryKey: ['crawl-tu-khoa'] })
    },
  })

  return (
    <div>
      <Typography.Paragraph type="secondary">
        Văn bản chứa các từ khóa dưới đây (từ khóa đang bật) sẽ được đưa vào danh sách ứng viên khi crawl chạy. Phạm vi
        hợp tác KHCN &amp; QHQT khá rộng nên có thể thêm/bớt dần theo thực tế.
      </Typography.Paragraph>
      <Space style={{ marginBottom: 16 }}>
        <Input
          style={{ width: 280 }}
          placeholder="Thêm từ khóa mới..."
          value={tuKhoaMoi}
          onChange={(e) => setTuKhoaMoi(e.target.value)}
          onPressEnter={() => tuKhoaMoi.trim() && themMutation.mutate(tuKhoaMoi.trim())}
        />
        <Button
          type="primary"
          icon={<PlusOutlined />}
          loading={themMutation.isPending}
          disabled={!tuKhoaMoi.trim()}
          onClick={() => themMutation.mutate(tuKhoaMoi.trim())}
        >
          Thêm
        </Button>
      </Space>

      <Table
        rowKey="id"
        loading={isLoading}
        dataSource={data}
        pagination={false}
        locale={{ emptyText: <Empty description="Chưa có từ khóa nào" /> }}
        columns={[
          { title: 'Từ khóa', dataIndex: 'tuKhoa' },
          {
            title: 'Đang bật',
            dataIndex: 'hoatDong',
            width: 100,
            render: (hoatDong: boolean, tk) => (
              <Switch
                checked={hoatDong}
                onChange={(checked) => doiTrangThaiMutation.mutate({ id: tk.id, hoatDong: checked })}
              />
            ),
          },
          {
            title: 'Thao tác',
            width: 80,
            render: (_, tk) => (
              <Popconfirm title="Xóa từ khóa này?" onConfirm={() => xoaMutation.mutate(tk.id)}>
                <Button size="small" danger icon={<DeleteOutlined />} />
              </Popconfirm>
            ),
          },
        ]}
      />
    </div>
  )
}

export function CrawlPhapLuatPage() {
  const { laAdmin } = useAuth()
  return (
    <div>
      <Typography.Paragraph type="secondary">
        Tự động quét văn bản pháp luật mới từ Công báo Chính phủ theo từ khóa, đưa vào danh sách ứng viên để xét duyệt
        trước khi nhận vào VBPL VN. Chạy tự động mỗi ngày, hoặc bấm "Crawl ngay" để chạy thủ công.
      </Typography.Paragraph>
      <Tabs
        items={[
          { key: 'ung-vien', label: 'Ứng viên', children: <TabUngVien /> },
          ...(laAdmin ? [{ key: 'tu-khoa', label: 'Từ khóa', children: <TabTuKhoa /> }] : []),
        ]}
      />
    </div>
  )
}
