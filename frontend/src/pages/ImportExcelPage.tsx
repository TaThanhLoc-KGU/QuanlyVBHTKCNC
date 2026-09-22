import { useState } from 'react'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import {
  Alert,
  Button,
  Card,
  Popconfirm,
  Select,
  Space,
  Table,
  Tag,
  Typography,
  Upload,
  message,
} from 'antd'
import { InboxOutlined, UndoOutlined, UploadOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import * as excelImportApi from '../api/excelImport'
import * as phienImportApi from '../api/phienImport'
import type { ImportPreviewResponse, ModuleKey } from '../types'
import { thongBaoLoi } from '../api/client'
import { useAuth } from '../auth/AuthContext'

const TUY_CHON_MODULE: { value: ModuleKey; label: string }[] = [
  { value: 'DOI_TAC', label: 'Đối tác' },
  { value: 'VAN_BAN_DHKG', label: 'Văn bản ĐHKG' },
  { value: 'VBPL_VN', label: 'VBPL VN' },
  { value: 'MOU', label: 'MoU' },
  { value: 'DOAN_VAO', label: 'Đoàn vào' },
  { value: 'DOAN_RA', label: 'Đoàn ra' },
]

const NHAN_MODULE: Record<ModuleKey, string> = Object.fromEntries(
  TUY_CHON_MODULE.map((m) => [m.value, m.label]),
) as Record<ModuleKey, string>

export function ImportExcelPage() {
  const { laAdmin } = useAuth()
  const queryClient = useQueryClient()
  const [moDun, setMoDun] = useState<ModuleKey>('DOI_TAC')
  const [file, setFile] = useState<File | null>(null)
  const [ketQuaXemTruoc, setKetQuaXemTruoc] = useState<ImportPreviewResponse | null>(null)

  const { data: lichSuPhien } = useQuery({
    queryKey: ['phien-import'],
    queryFn: () => phienImportApi.danhSachPhienImport(0, 10),
  })

  const xemTruocMutation = useMutation({
    mutationFn: () => excelImportApi.xemTruocImport(moDun, file!),
    onSuccess: setKetQuaXemTruoc,
    onError: (err) => message.error(thongBaoLoi(err, 'Không đọc được file')),
  })

  const xacNhanMutation = useMutation({
    mutationFn: () => excelImportApi.xacNhanImport(moDun, file!),
    onSuccess: (res) => {
      message.success(`Đã nhập thành công ${res.soDongThanhCong}/${res.tongSoDong} dòng`)
      setKetQuaXemTruoc(null)
      setFile(null)
      queryClient.invalidateQueries({ queryKey: ['phien-import'] })
    },
    onError: (err) => message.error(thongBaoLoi(err, 'Import không thành công')),
  })

  const rollbackMutation = useMutation({
    mutationFn: phienImportApi.rollbackPhienImport,
    onSuccess: (res) => {
      message.success(`Đã rollback, xóa ${res.soBanGhiDaXoa} bản ghi`)
      queryClient.invalidateQueries({ queryKey: ['phien-import'] })
    },
    onError: (err) => message.error(thongBaoLoi(err)),
  })

  return (
    <div>
      <Card style={{ marginBottom: 24 }}>
        <Space direction="vertical" style={{ width: '100%' }} size={16}>
          <Space>
            <Typography.Text>Module:</Typography.Text>
            <Select
              value={moDun}
              style={{ width: 200 }}
              options={TUY_CHON_MODULE}
              onChange={(v) => {
                setMoDun(v)
                setKetQuaXemTruoc(null)
                setFile(null)
              }}
            />
          </Space>

          <Upload.Dragger
            multiple={false}
            accept=".xlsx"
            fileList={file ? [{ uid: '1', name: file.name } as never] : []}
            beforeUpload={(f) => {
              setFile(f)
              setKetQuaXemTruoc(null)
              return false
            }}
            onRemove={() => {
              setFile(null)
              setKetQuaXemTruoc(null)
            }}
          >
            <p className="ant-upload-drag-icon">
              <InboxOutlined />
            </p>
            <p>Kéo thả hoặc chọn file .xlsx</p>
          </Upload.Dragger>

          <Space>
            <Button
              icon={<UploadOutlined />}
              disabled={!file}
              loading={xemTruocMutation.isPending}
              onClick={() => xemTruocMutation.mutate()}
            >
              Xem trước
            </Button>
            <Button
              type="primary"
              disabled={!ketQuaXemTruoc || ketQuaXemTruoc.soDongHopLe === 0}
              loading={xacNhanMutation.isPending}
              onClick={() => xacNhanMutation.mutate()}
            >
              Xác nhận nhập ({ketQuaXemTruoc?.soDongHopLe ?? 0} dòng hợp lệ)
            </Button>
          </Space>

          {ketQuaXemTruoc && (
            <>
              <Alert
                type={ketQuaXemTruoc.soDongLoi > 0 ? 'warning' : 'success'}
                showIcon
                message={`Tổng ${ketQuaXemTruoc.tongSoDong} dòng - Hợp lệ: ${ketQuaXemTruoc.soDongHopLe} - Lỗi: ${ketQuaXemTruoc.soDongLoi}`}
              />
              <Table
                size="small"
                rowKey="soDong"
                scroll={{ x: 'max-content' }}
                dataSource={ketQuaXemTruoc.dong}
                pagination={{ pageSize: 10 }}
                columns={[
                  { title: 'Dòng', dataIndex: 'soDong', width: 70 },
                  {
                    title: 'Dữ liệu',
                    dataIndex: 'duLieu',
                    render: (v: Record<string, unknown>) => (
                      <Typography.Text style={{ fontSize: 12 }}>
                        {Object.entries(v)
                          .map(([k, val]) => `${k}: ${val ?? ''}`)
                          .join(' | ')}
                      </Typography.Text>
                    ),
                  },
                  {
                    title: 'Trạng thái',
                    width: 260,
                    render: (_, dong) =>
                      dong.loi.length > 0 ? (
                        <Tag color="red">{dong.loi.join('; ')}</Tag>
                      ) : dong.ghiChuGoiYDoiTac ? (
                        <Tag color="blue">{dong.ghiChuGoiYDoiTac}</Tag>
                      ) : (
                        <Tag color="green">Hợp lệ</Tag>
                      ),
                  },
                ]}
              />
            </>
          )}
        </Space>
      </Card>

      <Typography.Title level={5}>Lịch sử phiên import</Typography.Title>
      <Table
        rowKey="id"
        size="small"
        scroll={{ x: 'max-content' }}
        dataSource={lichSuPhien?.content}
        pagination={false}
        columns={[
          { title: 'Thời điểm', dataIndex: 'thoiDiem', render: (v: string) => dayjs(v).format('DD/MM/YYYY HH:mm') },
          { title: 'Module', dataIndex: 'moDun', render: (v: ModuleKey) => NHAN_MODULE[v] ?? v },
          { title: 'File', dataIndex: 'tenFile' },
          { title: 'Tổng dòng', dataIndex: 'tongSoDong' },
          { title: 'Thành công', dataIndex: 'soDongThanhCong' },
          { title: 'Lỗi', dataIndex: 'soDongLoi' },
          {
            title: 'Thao tác',
            render: (_, phien) =>
              laAdmin && phien.coTheRollback && !phien.daRollback ? (
                <Popconfirm title="Rollback phiên này? Các bản ghi đã tạo sẽ bị xóa mềm." onConfirm={() => rollbackMutation.mutate(phien.id)}>
                  <Button size="small" danger icon={<UndoOutlined />}>
                    Rollback
                  </Button>
                </Popconfirm>
              ) : phien.daRollback ? (
                <Tag>Đã rollback</Tag>
              ) : null,
          },
        ]}
      />
    </div>
  )
}
