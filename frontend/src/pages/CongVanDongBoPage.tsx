import { useState } from 'react'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import {
  Alert,
  Button,
  DatePicker,
  Drawer,
  Form,
  Input,
  Popconfirm,
  Select,
  Space,
  Table,
  Tag,
  Tooltip,
  Typography,
  message,
} from 'antd'
import { CheckOutlined, CloudSyncOutlined, FileTextOutlined, PaperClipOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import * as congVanApi from '../api/congVan'
import type { UngVienDhkg } from '../api/congVan'
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

const NHAN_TINH_TRANG: Record<TinhTrangHieuLuc, string> = {
  CON_HIEU_LUC: 'Còn hiệu lực',
  HET_HIEU_LUC_TOAN_BO: 'Hết hiệu lực toàn bộ',
  HET_HIEU_LUC_MOT_PHAN: 'Hết hiệu lực một phần',
  BI_THAY_THE: 'Bị thay thế',
  DA_BI_BAI_BO: 'Đã bị bãi bỏ',
}

export function CongVanDongBoPage() {
  const { laAdmin } = useAuth()
  const queryClient = useQueryClient()
  const [trang, setTrang] = useState(1)
  const [locTrangThai, setLocTrangThai] = useState<congVanApi.UngVienDhkg['trangThai'] | undefined>('CHUA_XU_LY')
  const [dangNhan, setDangNhan] = useState<UngVienDhkg | null>(null)
  const [form] = Form.useForm<VanBanRequest>()

  const { data, isLoading } = useQuery({
    queryKey: ['congvan-ung-vien', locTrangThai, trang],
    queryFn: () => congVanApi.danhSachUngVien(locTrangThai, trang - 1),
  })

  const { data: danhMucLoai } = useQuery({
    queryKey: ['danh-muc-loai-van-ban', 'DHKG'],
    queryFn: () => danhMucApi.danhSachLoaiVanBan('DHKG'),
  })

  const dongBoMutation = useMutation({
    mutationFn: congVanApi.dongBoNgay,
    onSuccess: (kq) => {
      message.success(`Đã đồng bộ ${kq.tongSoTuCongVan} văn bản từ CongVan, ${kq.soUngVienMoi} ứng viên mới`)
      queryClient.invalidateQueries({ queryKey: ['congvan-ung-vien'] })
    },
    onError: (err) => message.error(thongBaoLoi(err)),
  })

  const nhanMutation = useMutation({
    mutationFn: ({ id, body }: { id: string; body: VanBanRequest }) => congVanApi.nhanUngVien(id, body),
    onSuccess: () => {
      message.success('Đã nhận vào Văn bản ĐHKG')
      queryClient.invalidateQueries({ queryKey: ['congvan-ung-vien'] })
      dongForm()
    },
    onError: (err) => message.error(thongBaoLoi(err)),
  })

  const boQuaMutation = useMutation({
    mutationFn: congVanApi.boQuaUngVien,
    onSuccess: () => {
      message.success('Đã bỏ qua')
      queryClient.invalidateQueries({ queryKey: ['congvan-ung-vien'] })
    },
  })

  function moFormNhan(uv: UngVienDhkg) {
    setDangNhan(uv)
    form.setFieldsValue({
      soHieu: uv.soHieu,
      tenVanBan: uv.tieuDe,
      ngayBanHanh: uv.ngayBanHanh ? (dayjs(uv.ngayBanHanh) as never) : undefined,
      coQuanBanHanh: 'Trường Đại học Kiên Giang',
      tinhTrangHieuLuc: 'CON_HIEU_LUC',
      noiDungChinh: uv.noiDung ?? undefined,
      ghiChu: uv.nguoiKy ? `Người ký (CongVan): ${uv.nguoiKy}` : undefined,
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
      <Typography.Paragraph type="secondary">
        Đồng bộ một chiều (chỉ kéo về) văn bản nội bộ đơn vị từ hệ thống CongVan của trường
        (qlvb.vnkgu.edu.vn) vào module Văn bản ĐHKG. Dữ liệu tạo/sửa trong CongVan không bị ảnh hưởng.
      </Typography.Paragraph>

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
            loading={dongBoMutation.isPending}
            onClick={() => dongBoMutation.mutate()}
          >
            Đồng bộ ngay
          </Button>
        )}
      </Space>

      <Table<UngVienDhkg>
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
          { title: 'Số hiệu', dataIndex: 'soHieu', width: 160 },
          { title: 'Tiêu đề', dataIndex: 'tieuDe', ellipsis: true },
          {
            title: 'Ngày ban hành',
            dataIndex: 'ngayBanHanh',
            width: 120,
            render: (v: string | null) => (v ? dayjs(v).format('DD/MM/YYYY') : '-'),
          },
          { title: 'Người ký', dataIndex: 'nguoiKy', width: 160, render: (v: string | null) => v ?? '-' },
          {
            title: 'File',
            dataIndex: 'soFile',
            width: 70,
            render: (v: number) =>
              v > 0 ? (
                <Tooltip title={`${v} file đính kèm`}>
                  <Tag icon={<PaperClipOutlined />}>{v}</Tag>
                </Tooltip>
              ) : (
                '-'
              ),
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

      <Drawer title="Nhận vào Văn bản ĐHKG" open={!!dangNhan} onClose={dongForm} width={520}>
        {dangNhan && dangNhan.soFile > 0 && (
          <Alert
            style={{ marginBottom: 16 }}
            type="info"
            showIcon
            icon={<FileTextOutlined />}
            message={`${dangNhan.soFile} file đính kèm sẽ được tự động tải về từ CongVan khi lưu.`}
          />
        )}
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
          <Form.Item name="noiDungChinh" label="Nội dung chính">
            <Input.TextArea rows={3} />
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit" loading={nhanMutation.isPending} block>
              Lưu vào Văn bản ĐHKG
            </Button>
          </Form.Item>
        </Form>
      </Drawer>
    </div>
  )
}
