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
import { DeleteOutlined, EditOutlined, HistoryOutlined, PlusOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import * as mouApi from '../api/mou'
import type { Mou, MouRequest, TrangThaiMou } from '../types'
import { useAuth } from '../auth/AuthContext'
import { thongBaoLoi } from '../api/client'
import { AuditHistoryDrawer } from '../components/AuditHistoryDrawer'
import { AttachmentPanel } from '../components/AttachmentPanel'
import { DoiTacSelect } from '../components/DoiTacSelect'

const NHAN_TRANG_THAI: Record<TrangThaiMou, string> = {
  CON_HIEU_LUC: 'Còn hiệu lực',
  SAP_HET_HAN: 'Sắp hết hạn',
  DA_HET_HAN: 'Đã hết hạn',
}
const MAU_TRANG_THAI: Record<TrangThaiMou, string> = {
  CON_HIEU_LUC: 'green',
  SAP_HET_HAN: 'orange',
  DA_HET_HAN: 'red',
}

export function MouPage() {
  const { coTheSua } = useAuth()
  const duocSua = coTheSua('MOU')
  const queryClient = useQueryClient()

  const [trang, setTrang] = useState(1)
  const [tuKhoa, setTuKhoa] = useState('')
  const [trangThaiLoc, setTrangThaiLoc] = useState<TrangThaiMou>()
  const [dangSua, setDangSua] = useState<Mou | null>(null)
  const [moForm, setMoForm] = useState(false)
  const [lichSuId, setLichSuId] = useState<string | null>(null)
  const [form] = Form.useForm()

  const { data, isLoading } = useQuery({
    queryKey: ['mou', trang, tuKhoa, trangThaiLoc],
    queryFn: () =>
      mouApi.danhSachMou({ page: trang - 1, size: 20, tuKhoa: tuKhoa || undefined, trangThai: trangThaiLoc }),
  })

  const luuMutation = useMutation({
    mutationFn: (body: MouRequest) => (dangSua ? mouApi.suaMou(dangSua.id, body) : mouApi.taoMou(body)),
    onSuccess: () => {
      message.success(dangSua ? 'Đã cập nhật MoU' : 'Đã tạo MoU')
      queryClient.invalidateQueries({ queryKey: ['mou'] })
      dongForm()
    },
    onError: (err) => message.error(thongBaoLoi(err)),
  })

  const xoaMutation = useMutation({
    mutationFn: mouApi.xoaMou,
    onSuccess: () => {
      message.success('Đã xóa MoU')
      queryClient.invalidateQueries({ queryKey: ['mou'] })
    },
    onError: (err) => message.error(thongBaoLoi(err)),
  })

  function moFormTao() {
    setDangSua(null)
    form.resetFields()
    setMoForm(true)
  }

  function moFormSua(m: Mou) {
    setDangSua(m)
    form.setFieldsValue({
      ...m,
      ngayBanHanh: dayjs(m.ngayBanHanh),
      ngayHetHan: m.ngayHetHan ? dayjs(m.ngayHetHan) : undefined,
    })
    setMoForm(true)
  }

  function dongForm() {
    setMoForm(false)
    setDangSua(null)
    form.resetFields()
  }

  function xuLySubmit(values: Record<string, unknown>) {
    const body: MouRequest = {
      ...(values as MouRequest),
      ngayBanHanh: dayjs(values.ngayBanHanh as dayjs.Dayjs).format('YYYY-MM-DD'),
      ngayHetHan: values.ngayHetHan ? dayjs(values.ngayHetHan as dayjs.Dayjs).format('YYYY-MM-DD') : null,
    }
    luuMutation.mutate(body)
  }

  return (
    <div>
      <Typography.Text type="secondary" style={{ display: 'block', marginBottom: 16 }}>
        Thỏa thuận hợp tác (MoU) với các đối tác trong và ngoài nước
      </Typography.Text>
      <Space style={{ marginBottom: 16 }} wrap>
        <Input.Search
          placeholder="Tìm theo tên tài liệu, tên đối tác..."
          allowClear
          style={{ width: 280 }}
          onSearch={(v) => {
            setTuKhoa(v)
            setTrang(1)
          }}
        />
        <Select
          placeholder="Trạng thái"
          allowClear
          style={{ width: 180 }}
          options={Object.entries(NHAN_TRANG_THAI).map(([value, label]) => ({ value, label }))}
          onChange={(v) => {
            setTrangThaiLoc(v)
            setTrang(1)
          }}
        />
        {duocSua && (
          <Button type="primary" icon={<PlusOutlined />} onClick={moFormTao}>
            Thêm MoU
          </Button>
        )}
      </Space>

      <Table<Mou>
        rowKey="id"
        loading={isLoading}
        scroll={{ x: 'max-content' }}
        dataSource={data?.content}
        pagination={{
          current: trang,
          total: data?.totalElements,
          pageSize: 20,
          onChange: setTrang,
          showTotal: (t) => `Tổng ${t} MoU`,
        }}
        columns={[
          { title: 'Đối tác', dataIndex: 'tenDoiTac' },
          { title: 'Tên tài liệu', dataIndex: 'tenTaiLieu', ellipsis: true },
          {
            title: 'Ngày ký',
            dataIndex: 'ngayBanHanh',
            width: 110,
            render: (v: string) => dayjs(v).format('DD/MM/YYYY'),
          },
          {
            title: 'Ngày hết hạn',
            dataIndex: 'ngayHetHan',
            width: 110,
            render: (v: string | null) => (v ? dayjs(v).format('DD/MM/YYYY') : '-'),
          },
          {
            title: 'Còn lại',
            dataIndex: 'soNgayConLai',
            width: 90,
            render: (v: number | null) => (v != null ? `${v} ngày` : '-'),
          },
          {
            title: 'Trạng thái',
            dataIndex: 'trangThai',
            width: 130,
            render: (v: TrangThaiMou) => <Tag color={MAU_TRANG_THAI[v]}>{NHAN_TRANG_THAI[v]}</Tag>,
          },
          {
            title: 'Thao tác',
            width: 160,
            fixed: 'right' as const,
            render: (_, m) => (
              <Space>
                <Button size="small" icon={<HistoryOutlined />} onClick={() => setLichSuId(m.id)} />
                {duocSua && (
                  <>
                    <Button size="small" icon={<EditOutlined />} onClick={() => moFormSua(m)} />
                    <Popconfirm title="Xóa MoU này?" onConfirm={() => xoaMutation.mutate(m.id)}>
                      <Button size="small" danger icon={<DeleteOutlined />} />
                    </Popconfirm>
                  </>
                )}
              </Space>
            ),
          },
        ]}
      />

      <Drawer title={dangSua ? 'Sửa MoU' : 'Thêm MoU'} open={moForm} onClose={dongForm} width={600}>
        <Tabs
          items={[
            {
              key: 'thong-tin',
              label: 'Thông tin',
              children: (
                <Form form={form} layout="vertical" onFinish={xuLySubmit}>
                  <Form.Item name="doiTacId" label="Đối tác" rules={[{ required: true, message: 'Bắt buộc' }]}>
                    <DoiTacSelect />
                  </Form.Item>
                  <Form.Item name="tenTaiLieu" label="Tên tài liệu">
                    <Input />
                  </Form.Item>
                  <Space size={16}>
                    <Form.Item name="ngayBanHanh" label="Ngày ký" rules={[{ required: true, message: 'Bắt buộc' }]}>
                      <DatePicker format="DD/MM/YYYY" />
                    </Form.Item>
                    <Form.Item name="ngayHetHan" label="Ngày hết hạn">
                      <DatePicker format="DD/MM/YYYY" />
                    </Form.Item>
                  </Space>
                  <Form.Item name="caNhanDauMoi" label="Cá nhân/đơn vị đầu mối">
                    <Input />
                  </Form.Item>
                  <Form.Item name="donViThucHien" label="Đơn vị thực hiện">
                    <Input />
                  </Form.Item>
                  <Form.Item name="phamViHopTac" label="Phạm vi hợp tác">
                    <Select
                      allowClear
                      options={[
                        { value: 'TOAN_DIEN', label: 'Toàn diện (xã giao)' },
                        { value: 'THEO_LINH_VUC', label: 'Theo lĩnh vực cụ thể' },
                      ]}
                    />
                  </Form.Item>
                  <Form.Item name="linhVucHopTac" label="Lĩnh vực hợp tác">
                    <Input.TextArea rows={2} />
                  </Form.Item>
                  <Form.Item name="dauMoiGhiTrongMou" label="Đầu mối ghi trong MoU">
                    <Input />
                  </Form.Item>
                  <Form.Item name="daiDienKguKy" label="Đại diện KGU ký">
                    <Input />
                  </Form.Item>
                  <Form.Item name="thoiHanHieuLuc" label="Thời hạn hiệu lực (mô tả)">
                    <Input placeholder="VD: 5 năm, tự động gia hạn thêm 5 năm" />
                  </Form.Item>
                  <Form.Item name="soCongVan" label="Số công văn">
                    <Input />
                  </Form.Item>
                  <Form.Item>
                    <Button type="primary" htmlType="submit" loading={luuMutation.isPending} block>
                      Lưu
                    </Button>
                  </Form.Item>
                </Form>
              ),
            },
            ...(dangSua
              ? [
                  {
                    key: 'dinh-kem',
                    label: 'File đính kèm',
                    children: <AttachmentPanel bang="mou" banGhiId={dangSua.id} choPhepSua={duocSua} />,
                  },
                ]
              : []),
          ]}
        />
      </Drawer>

      <AuditHistoryDrawer open={!!lichSuId} onClose={() => setLichSuId(null)} bang="mou" banGhiId={lichSuId} />
    </div>
  )
}
