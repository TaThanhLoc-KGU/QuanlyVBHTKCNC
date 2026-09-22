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
import {
  DeleteOutlined,
  EditOutlined,
  HistoryOutlined,
  LinkOutlined,
  PlusOutlined,
} from '@ant-design/icons'
import dayjs from 'dayjs'
import * as vanBanApi from '../api/vanBan'
import type { DuongDanVanBan } from '../api/vanBan'
import * as danhMucApi from '../api/danhMuc'
import type { PhamViVanBan, TinhTrangHieuLuc, VanBanDhkg, VanBanRequest, VbplVn } from '../types'
import { useAuth } from '../auth/AuthContext'
import { thongBaoLoi } from '../api/client'
import { AuditHistoryDrawer } from '../components/AuditHistoryDrawer'
import { AttachmentPanel } from '../components/AttachmentPanel'

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
const MAU_TINH_TRANG: Record<TinhTrangHieuLuc, string> = {
  CON_HIEU_LUC: 'green',
  HET_HIEU_LUC_TOAN_BO: 'red',
  HET_HIEU_LUC_MOT_PHAN: 'orange',
  BI_THAY_THE: 'purple',
  DA_BI_BAI_BO: 'default',
}

interface Props {
  duongDan: DuongDanVanBan
}

export function VanBanPage({ duongDan }: Props) {
  const laVbpl = duongDan === 'vbpl-vn'
  const moduleKey = laVbpl ? 'VBPL_VN' : 'VAN_BAN_DHKG'
  const phamVi: PhamViVanBan = laVbpl ? 'VBPL' : 'DHKG'

  const { coTheSua } = useAuth()
  const duocSua = coTheSua(moduleKey)
  const queryClient = useQueryClient()

  const [trang, setTrang] = useState(1)
  const [tuKhoa, setTuKhoa] = useState('')
  const [tinhTrangLoc, setTinhTrangLoc] = useState<TinhTrangHieuLuc>()
  const [dangSua, setDangSua] = useState<VanBanDhkg | VbplVn | null>(null)
  const [moForm, setMoForm] = useState(false)
  const [lichSuId, setLichSuId] = useState<string | null>(null)
  const [form] = Form.useForm()

  const queryKey = [duongDan, trang, tuKhoa, tinhTrangLoc]
  const { data, isLoading } = useQuery({
    queryKey,
    queryFn: () =>
      vanBanApi.danhSachVanBan(duongDan, {
        page: trang - 1,
        size: 20,
        tuKhoa: tuKhoa || undefined,
        tinhTrang: tinhTrangLoc,
      }),
  })

  const { data: danhMucLoai } = useQuery({
    queryKey: ['danh-muc-loai-van-ban', phamVi],
    queryFn: () => danhMucApi.danhSachLoaiVanBan(phamVi),
  })

  const luuMutation = useMutation({
    mutationFn: (body: VanBanRequest) =>
      dangSua ? vanBanApi.suaVanBan(duongDan, dangSua.id, body) : vanBanApi.taoVanBan(duongDan, body),
    onSuccess: () => {
      message.success(dangSua ? 'Đã cập nhật' : 'Đã tạo mới')
      queryClient.invalidateQueries({ queryKey: [duongDan] })
      dongForm()
    },
    onError: (err) => message.error(thongBaoLoi(err)),
  })

  const xoaMutation = useMutation({
    mutationFn: (id: string) => vanBanApi.xoaVanBan(duongDan, id),
    onSuccess: () => {
      message.success('Đã xóa')
      queryClient.invalidateQueries({ queryKey: [duongDan] })
    },
    onError: (err) => message.error(thongBaoLoi(err)),
  })

  const xacNhanDoiChieuMutation = useMutation({
    mutationFn: vanBanApi.xacNhanDoiChieuVbpl,
    onSuccess: () => {
      message.success('Đã xác nhận đối chiếu')
      queryClient.invalidateQueries({ queryKey: [duongDan] })
    },
  })

  function moFormTao() {
    setDangSua(null)
    form.resetFields()
    setMoForm(true)
  }

  function moFormSua(vb: VanBanDhkg | VbplVn) {
    setDangSua(vb)
    form.setFieldsValue({
      ...vb,
      ngayBanHanh: dayjs(vb.ngayBanHanh),
      ngayHieuLuc: vb.ngayHieuLuc ? dayjs(vb.ngayHieuLuc) : undefined,
    })
    setMoForm(true)
  }

  function dongForm() {
    setMoForm(false)
    setDangSua(null)
    form.resetFields()
  }

  function xuLySubmit(values: Record<string, unknown>) {
    const body: VanBanRequest = {
      ...(values as VanBanRequest),
      ngayBanHanh: dayjs(values.ngayBanHanh as dayjs.Dayjs).format('YYYY-MM-DD'),
      ngayHieuLuc: values.ngayHieuLuc ? dayjs(values.ngayHieuLuc as dayjs.Dayjs).format('YYYY-MM-DD') : null,
    }
    luuMutation.mutate(body)
  }

  return (
    <div>
      <Space style={{ marginBottom: 16 }} wrap>
        <Input.Search
          placeholder="Tìm theo số hiệu, tên văn bản..."
          allowClear
          style={{ width: 280 }}
          onSearch={(v) => {
            setTuKhoa(v)
            setTrang(1)
          }}
        />
        <Select
          placeholder="Tình trạng hiệu lực"
          allowClear
          style={{ width: 200 }}
          options={Object.entries(NHAN_TINH_TRANG).map(([value, label]) => ({ value, label }))}
          onChange={(v) => {
            setTinhTrangLoc(v)
            setTrang(1)
          }}
        />
        {duocSua && (
          <Button type="primary" icon={<PlusOutlined />} onClick={moFormTao}>
            Thêm văn bản
          </Button>
        )}
      </Space>

      <Table<VanBanDhkg | VbplVn>
        rowKey="id"
        loading={isLoading}
        scroll={{ x: 'max-content' }}
        dataSource={data?.content}
        pagination={{
          current: trang,
          total: data?.totalElements,
          pageSize: 20,
          onChange: setTrang,
          showTotal: (t) => `Tổng ${t} văn bản`,
        }}
        columns={[
          { title: 'Số hiệu', dataIndex: 'soHieu', width: 160 },
          { title: 'Tên văn bản', dataIndex: 'tenVanBan', ellipsis: true },
          { title: 'Loại', dataIndex: 'tenLoaiVanBan', width: 140 },
          {
            title: 'Ngày ban hành',
            dataIndex: 'ngayBanHanh',
            width: 120,
            render: (v: string) => dayjs(v).format('DD/MM/YYYY'),
          },
          {
            title: 'Tình trạng',
            dataIndex: 'tinhTrangHieuLuc',
            width: 160,
            render: (v: TinhTrangHieuLuc) => <Tag color={MAU_TINH_TRANG[v]}>{NHAN_TINH_TRANG[v]}</Tag>,
          },
          ...(laVbpl
            ? [
                {
                  title: 'Đối chiếu gần nhất',
                  dataIndex: 'ngayDoiChieuGanNhat',
                  width: 130,
                  render: (v: string | null) => (v ? dayjs(v).format('DD/MM/YYYY') : <Tag>Chưa đối chiếu</Tag>),
                },
              ]
            : []),
          {
            title: 'Thao tác',
            width: laVbpl ? 220 : 160,
            fixed: 'right' as const,
            render: (_, vb) => (
              <Space>
                {laVbpl && (
                  <Button
                    size="small"
                    icon={<LinkOutlined />}
                    onClick={() =>
                      window.open(`https://vbpl.vn/pages/portal.aspx?keyword=${encodeURIComponent(vb.soHieu)}`, '_blank')
                    }
                    title="Tra cứu trên vbpl.vn"
                  />
                )}
                {laVbpl && duocSua && (
                  <Button
                    size="small"
                    onClick={() => xacNhanDoiChieuMutation.mutate(vb.id)}
                    loading={xacNhanDoiChieuMutation.isPending}
                  >
                    Đối chiếu
                  </Button>
                )}
                <Button size="small" icon={<HistoryOutlined />} onClick={() => setLichSuId(vb.id)} />
                {duocSua && (
                  <>
                    <Button size="small" icon={<EditOutlined />} onClick={() => moFormSua(vb)} />
                    <Popconfirm title="Xóa văn bản này?" onConfirm={() => xoaMutation.mutate(vb.id)}>
                      <Button size="small" danger icon={<DeleteOutlined />} />
                    </Popconfirm>
                  </>
                )}
              </Space>
            ),
          },
        ]}
      />

      <Drawer title={dangSua ? 'Sửa văn bản' : 'Thêm văn bản'} open={moForm} onClose={dongForm} width={600}>
        <Tabs
          items={[
            {
              key: 'thong-tin',
              label: 'Thông tin',
              children: (
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
                  <Form.Item
                    name="tinhTrangHieuLuc"
                    label="Tình trạng hiệu lực"
                    rules={[{ required: true, message: 'Bắt buộc' }]}
                  >
                    <Select options={Object.entries(NHAN_TINH_TRANG).map(([value, label]) => ({ value, label }))} />
                  </Form.Item>
                  <Form.Item name="coQuanBanHanh" label="Cơ quan ban hành" rules={[{ required: true, message: 'Bắt buộc' }]}>
                    <Input />
                  </Form.Item>
                  <Form.Item name="ghiChu" label="Ghi chú">
                    <Input.TextArea rows={2} />
                  </Form.Item>
                  <Form.Item name="noiDungChinh" label="Nội dung chính">
                    <Input.TextArea rows={4} />
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
                    children: (
                      <AttachmentPanel
                        bang={laVbpl ? 'vbpl_vn' : 'van_ban_dhkg'}
                        banGhiId={dangSua.id}
                        choPhepSua={duocSua}
                      />
                    ),
                  },
                ]
              : []),
          ]}
        />
      </Drawer>

      <AuditHistoryDrawer
        open={!!lichSuId}
        onClose={() => setLichSuId(null)}
        bang={laVbpl ? 'vbpl_vn' : 'van_ban_dhkg'}
        banGhiId={lichSuId}
      />
    </div>
  )
}
