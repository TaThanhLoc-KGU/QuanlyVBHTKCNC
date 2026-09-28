import { useState } from 'react'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import {
  Button,
  Drawer,
  Form,
  Input,
  InputNumber,
  Popconfirm,
  Select,
  Space,
  Switch,
  Table,
  Tag,
  Typography,
  message,
} from 'antd'
import { EditOutlined, PlusOutlined } from '@ant-design/icons'
import * as tuDienApi from '../api/tuDien'
import type { TuDienRequest } from '../api/tuDien'
import type { LoaiTuDien, TuDien } from '../types'
import { useAuth } from '../auth/AuthContext'
import { thongBaoLoi } from '../api/client'

const NHOM_LOAI_TU_DIEN: { nhom: string; muc: { value: LoaiTuDien; label: string }[] }[] = [
  {
    nhom: 'Hợp tác quốc tế',
    muc: [
      { value: 'MUC_TIEU_DOAN_RA', label: 'Mục tiêu đoàn ra' },
      { value: 'MAU_CONG_VAN_QUYET_DINH', label: 'Mẫu công văn, quyết định' },
      { value: 'MUC_DICH_DEN', label: 'Mục đích đến' },
      { value: 'NGUON_KINH_PHI', label: 'Nguồn kinh phí' },
      { value: 'NOI_GUI_CONG_VAN_DEN', label: 'Nơi gửi công văn đến' },
      { value: 'NOI_GUI_CONG_VAN_DEN_BO_SUNG', label: 'Nơi gửi công văn đến (bổ sung)' },
      { value: 'QUOC_GIA', label: 'Quốc gia' },
      { value: 'TIEN_TE', label: 'Tiền tệ' },
      { value: 'HOAT_DONG_KY_KET', label: 'Hoạt động ký kết' },
      { value: 'NGON_NGU_KY_KET', label: 'Ngôn ngữ ký kết' },
      { value: 'VAI_TRO_THANH_VIEN', label: 'Vai trò thành viên' },
    ],
  },
  {
    nhom: 'Đối tác',
    muc: [
      { value: 'LOAI_DOI_TAC', label: 'Loại đối tác' },
      { value: 'LOAI_SU_KIEN', label: 'Loại sự kiện' },
      { value: 'LINH_VUC_HOAT_DONG', label: 'Lĩnh vực hoạt động' },
    ],
  },
]

const NHAN_LOAI_TU_DIEN: Record<LoaiTuDien, string> = Object.fromEntries(
  NHOM_LOAI_TU_DIEN.flatMap((n) => n.muc).map((m) => [m.value, m.label]),
) as Record<LoaiTuDien, string>

export function TuDienPage() {
  const { coTheSua } = useAuth()
  const choPhepSua = coTheSua('TU_DIEN')
  const queryClient = useQueryClient()
  const [loai, setLoai] = useState<LoaiTuDien>('QUOC_GIA')
  const [dangSua, setDangSua] = useState<TuDien | null>(null)
  const [moForm, setMoForm] = useState(false)
  const [form] = Form.useForm<TuDienRequest>()

  const { data, isLoading } = useQuery({
    queryKey: ['tu-dien', loai],
    queryFn: () => tuDienApi.danhSach(loai),
  })

  const taoMutation = useMutation({
    mutationFn: tuDienApi.tao,
    onSuccess: () => {
      message.success('Đã thêm giá trị')
      queryClient.invalidateQueries({ queryKey: ['tu-dien', loai] })
      dongForm()
    },
    onError: (err) => message.error(thongBaoLoi(err)),
  })

  const suaMutation = useMutation({
    mutationFn: ({ id, body }: { id: string; body: TuDienRequest }) => tuDienApi.sua(id, body),
    onSuccess: () => {
      message.success('Đã lưu thay đổi')
      queryClient.invalidateQueries({ queryKey: ['tu-dien', loai] })
      dongForm()
    },
    onError: (err) => message.error(thongBaoLoi(err)),
  })

  const xoaMutation = useMutation({
    mutationFn: tuDienApi.xoa,
    onSuccess: () => {
      message.success('Đã xóa')
      queryClient.invalidateQueries({ queryKey: ['tu-dien', loai] })
    },
    onError: (err) => message.error(thongBaoLoi(err)),
  })

  function moThem() {
    setDangSua(null)
    form.resetFields()
    form.setFieldsValue({ loai, hoatDong: true, thuTu: (data?.length ?? 0) + 1 })
    setMoForm(true)
  }

  function moSua(td: TuDien) {
    setDangSua(td)
    form.setFieldsValue({
      loai: td.loai,
      ma: td.ma,
      ten: td.ten,
      moTa: td.moTa,
      thuTu: td.thuTu,
      hoatDong: td.hoatDong,
    })
    setMoForm(true)
  }

  function dongForm() {
    setMoForm(false)
    setDangSua(null)
    form.resetFields()
  }

  function xuLySubmit(values: TuDienRequest) {
    if (dangSua) {
      suaMutation.mutate({ id: dangSua.id, body: values })
    } else {
      taoMutation.mutate(values)
    }
  }

  return (
    <div>
      <Typography.Paragraph type="secondary">
        Các danh mục giá trị dùng chung trong nghiệp vụ Hợp tác quốc tế và Đối tác (quốc gia, tiền tệ, nguồn kinh
        phí...). Có thể bổ sung/sửa/tắt từng giá trị mà không ảnh hưởng dữ liệu đã dùng giá trị đó.
      </Typography.Paragraph>

      <Space style={{ marginBottom: 16 }} wrap>
        <Select
          style={{ width: 280 }}
          value={loai}
          onChange={setLoai}
          options={NHOM_LOAI_TU_DIEN.map((n) => ({
            label: n.nhom,
            options: n.muc.map((m) => ({ value: m.value, label: m.label })),
          }))}
        />
        {choPhepSua && (
          <Button type="primary" icon={<PlusOutlined />} onClick={moThem}>
            Thêm giá trị
          </Button>
        )}
      </Space>

      <Table<TuDien>
        rowKey="id"
        loading={isLoading}
        dataSource={data}
        pagination={false}
        scroll={{ x: 'max-content' }}
        columns={[
          { title: 'Mã', dataIndex: 'ma', width: 140, render: (v: string | null) => (v ? <Tag>{v}</Tag> : '-') },
          { title: 'Tên', dataIndex: 'ten' },
          { title: 'Mô tả', dataIndex: 'moTa', render: (v: string | null) => v ?? '-' },
          { title: 'Thứ tự', dataIndex: 'thuTu', width: 90 },
          {
            title: 'Hoạt động',
            dataIndex: 'hoatDong',
            width: 110,
            render: (v: boolean) => <Tag color={v ? 'green' : 'default'}>{v ? 'Đang dùng' : 'Đã tắt'}</Tag>,
          },
          ...(choPhepSua
            ? [
                {
                  title: 'Thao tác',
                  width: 120,
                  fixed: 'right' as const,
                  render: (_: unknown, td: TuDien) => (
                    <Space>
                      <Button size="small" icon={<EditOutlined />} onClick={() => moSua(td)} />
                      <Popconfirm title="Xóa giá trị này khỏi tự điển?" onConfirm={() => xoaMutation.mutate(td.id)}>
                        <Button size="small" danger>
                          Xóa
                        </Button>
                      </Popconfirm>
                    </Space>
                  ),
                },
              ]
            : []),
        ]}
      />

      <Drawer
        title={dangSua ? 'Sửa giá trị tự điển' : 'Thêm giá trị tự điển'}
        open={moForm}
        onClose={dongForm}
        width={420}
      >
        <Form form={form} layout="vertical" onFinish={xuLySubmit}>
          <Form.Item label="Loại tự điển">
            <Input value={NHAN_LOAI_TU_DIEN[loai]} disabled />
          </Form.Item>
          <Form.Item name="loai" hidden>
            <Input />
          </Form.Item>
          <Form.Item name="ten" label="Tên" rules={[{ required: true, message: 'Bắt buộc' }]}>
            <Input />
          </Form.Item>
          <Form.Item name="ma" label="Mã (tùy chọn)">
            <Input />
          </Form.Item>
          <Form.Item name="moTa" label="Mô tả">
            <Input.TextArea rows={2} />
          </Form.Item>
          <Form.Item name="thuTu" label="Thứ tự hiển thị">
            <InputNumber style={{ width: '100%' }} min={0} />
          </Form.Item>
          <Form.Item name="hoatDong" label="Hoạt động" valuePropName="checked">
            <Switch />
          </Form.Item>
          <Form.Item>
            <Button
              type="primary"
              htmlType="submit"
              loading={taoMutation.isPending || suaMutation.isPending}
              block
            >
              {dangSua ? 'Lưu thay đổi' : 'Thêm giá trị'}
            </Button>
          </Form.Item>
        </Form>
      </Drawer>
    </div>
  )
}
