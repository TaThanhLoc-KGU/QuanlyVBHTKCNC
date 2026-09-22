import { useState } from 'react'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { Button, Descriptions, Drawer, Input, InputNumber, Select, Space, Table, Tag, Typography, message } from 'antd'
import { CloudSyncOutlined, PaperClipOutlined, SearchOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import * as congVanDenApi from '../api/congVanDen'
import type { CongVanDen } from '../api/congVanDen'
import { AttachmentPanel } from '../components/AttachmentPanel'
import { useAuth } from '../auth/AuthContext'
import { thongBaoLoi } from '../api/client'

const NHAN_TRANG_THAI: Record<string, string> = {
  moi: 'Mới',
  datiepnhan: 'Đã tiếp nhận',
  dangxuly: 'Đang xử lý',
  hoanthanh: 'Hoàn thành',
  quahan: 'Quá hạn',
}
const MAU_TRANG_THAI: Record<string, string> = {
  moi: 'blue',
  datiepnhan: 'cyan',
  dangxuly: 'orange',
  hoanthanh: 'green',
  quahan: 'red',
}

function ngay(v: string | null) {
  return v ? dayjs(v).format('DD/MM/YYYY') : '-'
}

export function CongVanDenPage() {
  const { coTheSua } = useAuth()
  const queryClient = useQueryClient()
  const [trang, setTrang] = useState(1)
  const [trangThai, setTrangThai] = useState<string | undefined>(undefined)
  const [nam, setNam] = useState<number | undefined>(undefined)
  const [tuKhoa, setTuKhoa] = useState<string>('')
  const [dangXem, setDangXem] = useState<CongVanDen | null>(null)

  const { data, isLoading } = useQuery({
    queryKey: ['cong-van-den', trangThai, nam, tuKhoa, trang],
    queryFn: () => congVanDenApi.danhSach({ trangThai, nam, tuKhoa: tuKhoa || undefined, page: trang - 1 }),
  })

  const dongBoMutation = useMutation({
    mutationFn: congVanDenApi.dongBoNgay,
    onSuccess: (kq) => {
      message.success(`Đã đồng bộ ${kq.tongSoTuCongVan} công văn đến, ${kq.soMoi} mới, ${kq.soCapNhat} cập nhật`)
      queryClient.invalidateQueries({ queryKey: ['cong-van-den'] })
    },
    onError: (err) => message.error(thongBaoLoi(err)),
  })

  return (
    <div>
      <Typography.Paragraph type="secondary">
        Đồng bộ một chiều (chỉ kéo về) công văn đến của đơn vị từ hệ thống CongVan của trường
        (qlvb.vnkgu.edu.vn). Dữ liệu bên CongVan không bị ảnh hưởng.
      </Typography.Paragraph>

      <Space style={{ marginBottom: 16 }} wrap>
        <Select
          style={{ width: 180 }}
          allowClear
          placeholder="Trạng thái xử lý"
          value={trangThai}
          onChange={(v) => {
            setTrangThai(v)
            setTrang(1)
          }}
          options={Object.entries(NHAN_TRANG_THAI).map(([value, label]) => ({ value, label }))}
        />
        <InputNumber
          style={{ width: 120 }}
          placeholder="Năm đến"
          value={nam}
          onChange={(v) => {
            setNam(v ?? undefined)
            setTrang(1)
          }}
        />
        <Input.Search
          style={{ width: 260 }}
          allowClear
          placeholder="Tìm số văn bản, trích yếu, cơ quan..."
          prefix={<SearchOutlined />}
          onSearch={(v) => {
            setTuKhoa(v)
            setTrang(1)
          }}
        />
        {coTheSua('CONG_VAN_DEN') && (
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

      <Table<CongVanDen>
        rowKey="id"
        loading={isLoading}
        dataSource={data?.content}
        scroll={{ x: 'max-content' }}
        pagination={{
          current: trang,
          total: data?.totalElements,
          pageSize: 20,
          onChange: setTrang,
          showTotal: (t) => `Tổng ${t} công văn`,
        }}
        onRow={(cv) => ({ onClick: () => setDangXem(cv), style: { cursor: 'pointer' } })}
        columns={[
          { title: 'Số văn bản', dataIndex: 'soVanBan', width: 150, render: (v: string | null) => v ?? '-' },
          { title: 'Trích yếu', dataIndex: 'trichYeu', ellipsis: true },
          { title: 'Cơ quan ban hành', dataIndex: 'coQuanBanHanh', width: 220, ellipsis: true },
          { title: 'Ngày đến', dataIndex: 'ngayDen', width: 110, render: ngay },
          { title: 'Hạn xử lý', dataIndex: 'hanXuLy', width: 110, render: ngay },
          {
            title: 'Trạng thái',
            dataIndex: 'trangThai',
            width: 130,
            render: (v: string | null) =>
              v ? <Tag color={MAU_TRANG_THAI[v] ?? 'default'}>{NHAN_TRANG_THAI[v] ?? v}</Tag> : '-',
          },
          {
            title: 'File',
            dataIndex: 'coFile',
            width: 60,
            render: (v: boolean) => (v ? <PaperClipOutlined /> : null),
          },
        ]}
      />

      <Drawer title="Chi tiết công văn đến" open={!!dangXem} onClose={() => setDangXem(null)} width={560}>
        {dangXem && (
          <>
            <Descriptions column={1} size="small" bordered>
              <Descriptions.Item label="Số văn bản">{dangXem.soVanBan ?? '-'}</Descriptions.Item>
              <Descriptions.Item label="Số đến">{dangXem.soDen ?? '-'}</Descriptions.Item>
              <Descriptions.Item label="Trích yếu">{dangXem.trichYeu}</Descriptions.Item>
              <Descriptions.Item label="Cơ quan ban hành">{dangXem.coQuanBanHanh ?? '-'}</Descriptions.Item>
              <Descriptions.Item label="Loại văn bản">{dangXem.loaiVanBan ?? '-'}</Descriptions.Item>
              <Descriptions.Item label="Người ký">{dangXem.nguoiKy ?? '-'}</Descriptions.Item>
              <Descriptions.Item label="Ngày ban hành">{ngay(dangXem.ngayBanHanh)}</Descriptions.Item>
              <Descriptions.Item label="Ngày đến">{ngay(dangXem.ngayDen)}</Descriptions.Item>
              <Descriptions.Item label="Đơn vị xử lý chính">{dangXem.donViXuLyChinh ?? '-'}</Descriptions.Item>
              <Descriptions.Item label="Hạn xử lý">{ngay(dangXem.hanXuLy)}</Descriptions.Item>
              <Descriptions.Item label="Ngày hoàn thành">{ngay(dangXem.ngayHoanThanh)}</Descriptions.Item>
              <Descriptions.Item label="Trạng thái xử lý">
                {dangXem.trangThai ? (
                  <Tag color={MAU_TRANG_THAI[dangXem.trangThai] ?? 'default'}>
                    {dangXem.trangThaiText ?? NHAN_TRANG_THAI[dangXem.trangThai] ?? dangXem.trangThai}
                  </Tag>
                ) : (
                  '-'
                )}
              </Descriptions.Item>
              {dangXem.ghiChu && <Descriptions.Item label="Ghi chú">{dangXem.ghiChu}</Descriptions.Item>}
            </Descriptions>

            <Typography.Title level={5} style={{ marginTop: 20 }}>
              File đính kèm
            </Typography.Title>
            <AttachmentPanel bang="cong_van_den" banGhiId={dangXem.id} choPhepSua={false} />
          </>
        )}
      </Drawer>
    </div>
  )
}
