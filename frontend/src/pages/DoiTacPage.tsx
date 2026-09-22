import { useState } from 'react'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import {
  Alert,
  Button,
  Drawer,
  Form,
  Input,
  Popconfirm,
  Select,
  Space,
  Table,
  Tag,
  Typography,
  message,
} from 'antd'
import { DeleteOutlined, EditOutlined, HistoryOutlined, PlusOutlined } from '@ant-design/icons'
import * as doiTacApi from '../api/doiTac'
import type { DoiTac, DoiTacRequest, LoaiDoiTac } from '../types'
import { useAuth } from '../auth/AuthContext'
import { thongBaoLoi } from '../api/client'
import { AuditHistoryDrawer } from '../components/AuditHistoryDrawer'

const NHAN_LOAI: Record<LoaiDoiTac, string> = { TRONG_NUOC: 'Trong nước', NGOAI_NUOC: 'Ngoài nước' }

export function DoiTacPage() {
  const { coTheSua } = useAuth()
  const duocSua = coTheSua('DOI_TAC')
  const queryClient = useQueryClient()

  const [trang, setTrang] = useState(1)
  const [tuKhoa, setTuKhoa] = useState('')
  const [loaiLoc, setLoaiLoc] = useState<LoaiDoiTac | undefined>()
  const [dangSua, setDangSua] = useState<DoiTac | null>(null)
  const [moForm, setMoForm] = useState(false)
  const [lichSuId, setLichSuId] = useState<string | null>(null)
  const [goiYTrung, setGoiYTrung] = useState<DoiTac[]>([])
  const [form] = Form.useForm<DoiTacRequest>()

  const { data, isLoading } = useQuery({
    queryKey: ['doi-tac', trang, tuKhoa, loaiLoc],
    queryFn: () => doiTacApi.danhSachDoiTac({ page: trang - 1, size: 20, tuKhoa: tuKhoa || undefined, loaiDoiTac: loaiLoc }),
  })

  const luuMutation = useMutation({
    mutationFn: (body: DoiTacRequest) =>
      dangSua ? doiTacApi.suaDoiTac(dangSua.id, body) : doiTacApi.taoDoiTac(body),
    onSuccess: () => {
      message.success(dangSua ? 'Đã cập nhật đối tác' : 'Đã tạo đối tác')
      queryClient.invalidateQueries({ queryKey: ['doi-tac'] })
      dongForm()
    },
    onError: (err) => message.error(thongBaoLoi(err)),
  })

  const xoaMutation = useMutation({
    mutationFn: doiTacApi.xoaDoiTac,
    onSuccess: () => {
      message.success('Đã xóa đối tác')
      queryClient.invalidateQueries({ queryKey: ['doi-tac'] })
    },
    onError: (err) => message.error(thongBaoLoi(err)),
  })

  function moFormTao() {
    setDangSua(null)
    form.resetFields()
    setGoiYTrung([])
    setMoForm(true)
  }

  function moFormSua(dt: DoiTac) {
    setDangSua(dt)
    form.setFieldsValue(dt)
    setGoiYTrung([])
    setMoForm(true)
  }

  function dongForm() {
    setMoForm(false)
    setDangSua(null)
    form.resetFields()
  }

  async function xuLyDoiTen(ten: string) {
    if (!ten || ten.length < 3 || dangSua) return
    try {
      const ds = await doiTacApi.goiYTrungTenDoiTac(ten)
      setGoiYTrung(ds)
    } catch {
      // bo qua loi goi y, khong quan trong (chi la goi y UX, khong chan luong nhap lieu)
    }
  }

  return (
    <div>
      <Space style={{ marginBottom: 16 }} wrap>
        <Input.Search
          placeholder="Tìm theo tên, địa chỉ, ghi chú..."
          allowClear
          style={{ width: 280 }}
          onSearch={(v) => {
            setTuKhoa(v)
            setTrang(1)
          }}
        />
        <Select
          placeholder="Loại đối tác"
          allowClear
          style={{ width: 160 }}
          options={[
            { value: 'TRONG_NUOC', label: 'Trong nước' },
            { value: 'NGOAI_NUOC', label: 'Ngoài nước' },
          ]}
          onChange={(v) => {
            setLoaiLoc(v)
            setTrang(1)
          }}
        />
        {duocSua && (
          <Button type="primary" icon={<PlusOutlined />} onClick={moFormTao}>
            Thêm đối tác
          </Button>
        )}
      </Space>

      <Table<DoiTac>
        rowKey="id"
        loading={isLoading}
        scroll={{ x: 'max-content' }}
        dataSource={data?.content}
        pagination={{
          current: trang,
          total: data?.totalElements,
          pageSize: 20,
          onChange: setTrang,
          showTotal: (t) => `Tổng ${t} đối tác`,
        }}
        columns={[
          { title: 'Tên đối tác', dataIndex: 'tenDoiTac' },
          {
            title: 'Loại',
            dataIndex: 'loaiDoiTac',
            width: 120,
            render: (v: LoaiDoiTac) => <Tag color={v === 'NGOAI_NUOC' ? 'blue' : 'default'}>{NHAN_LOAI[v]}</Tag>,
          },
          { title: 'Quốc gia', dataIndex: 'quocGia', width: 140 },
          { title: 'Địa chỉ', dataIndex: 'diaChi', ellipsis: true },
          {
            title: 'Thao tác',
            width: 160,
            fixed: 'right' as const,
            render: (_, dt) => (
              <Space>
                <Button size="small" icon={<HistoryOutlined />} onClick={() => setLichSuId(dt.id)} />
                {duocSua && (
                  <>
                    <Button size="small" icon={<EditOutlined />} onClick={() => moFormSua(dt)} />
                    <Popconfirm title="Xóa đối tác này?" onConfirm={() => xoaMutation.mutate(dt.id)}>
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
        title={dangSua ? 'Sửa đối tác' : 'Thêm đối tác'}
        open={moForm}
        onClose={dongForm}
        width={480}
        styles={{ body: { paddingBottom: 24 } }}
      >
        <Form form={form} layout="vertical" onFinish={(v) => luuMutation.mutate(v)}>
          <Form.Item
            name="tenDoiTac"
            label="Tên đối tác"
            rules={[{ required: true, message: 'Bắt buộc' }]}
          >
            <Input onBlur={(e) => xuLyDoiTen(e.target.value)} />
          </Form.Item>
          {goiYTrung.length > 0 && (
            <Alert
              style={{ marginBottom: 16 }}
              type="warning"
              showIcon
              message="Có thể trùng với đối tác đã có"
              description={goiYTrung.map((g) => g.tenDoiTac).join(', ')}
            />
          )}
          <Form.Item name="loaiDoiTac" label="Loại đối tác" rules={[{ required: true, message: 'Bắt buộc' }]}>
            <Select
              options={[
                { value: 'TRONG_NUOC', label: 'Trong nước' },
                { value: 'NGOAI_NUOC', label: 'Ngoài nước' },
              ]}
            />
          </Form.Item>
          <Form.Item
            name="quocGia"
            label="Quốc gia"
            dependencies={['loaiDoiTac']}
            rules={[
              ({ getFieldValue }) => ({
                required: getFieldValue('loaiDoiTac') === 'NGOAI_NUOC',
                message: 'Bắt buộc khi Loại đối tác = Ngoài nước',
              }),
            ]}
          >
            <Input />
          </Form.Item>
          <Form.Item name="diaChi" label="Địa chỉ">
            <Input.TextArea rows={2} />
          </Form.Item>
          <Form.Item name="thongTinLienHe" label="Thông tin liên hệ">
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
        </Form>
      </Drawer>

      <AuditHistoryDrawer open={!!lichSuId} onClose={() => setLichSuId(null)} bang="doi_tac" banGhiId={lichSuId} />
    </div>
  )
}
