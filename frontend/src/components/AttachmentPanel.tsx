import { useQuery, useQueryClient } from '@tanstack/react-query'
import { Button, List, Popconfirm, Typography, Upload, message } from 'antd'
import { DeleteOutlined, DownloadOutlined, UploadOutlined } from '@ant-design/icons'
import * as taiLieuApi from '../api/taiLieu'
import type { BangDinhKem } from '../api/taiLieu'
import { thongBaoLoi } from '../api/client'

interface Props {
  bang: BangDinhKem
  banGhiId: string
  choPhepSua: boolean
}

function dinhDangKichThuoc(byte: number | null) {
  if (byte == null) return ''
  if (byte < 1024) return `${byte} B`
  if (byte < 1024 * 1024) return `${(byte / 1024).toFixed(1)} KB`
  return `${(byte / 1024 / 1024).toFixed(1)} MB`
}

export function AttachmentPanel({ bang, banGhiId, choPhepSua }: Props) {
  const queryClient = useQueryClient()
  const queryKey = ['tai-lieu-dinh-kem', bang, banGhiId]

  const { data: danhSach, isLoading } = useQuery({
    queryKey,
    queryFn: () => taiLieuApi.danhSachDinhKem(bang, banGhiId),
  })

  async function xuLyUpload(file: File) {
    try {
      await taiLieuApi.taiLenDinhKem(bang, banGhiId, file)
      message.success('Đã tải lên file đính kèm')
      queryClient.invalidateQueries({ queryKey })
    } catch (err) {
      message.error(thongBaoLoi(err, 'Tải lên không thành công'))
    }
    return false
  }

  async function xuLyXoa(id: string) {
    try {
      await taiLieuApi.xoaDinhKem(id)
      message.success('Đã xóa file đính kèm')
      queryClient.invalidateQueries({ queryKey })
    } catch (err) {
      message.error(thongBaoLoi(err, 'Xóa không thành công'))
    }
  }

  async function xuLyTaiXuong(id: string, tenFile: string) {
    try {
      await taiLieuApi.taiXuongDinhKem(id, tenFile)
    } catch {
      message.error('Tải xuống không thành công')
    }
  }

  return (
    <div>
      {choPhepSua && (
        <Upload beforeUpload={xuLyUpload} showUploadList={false} multiple>
          <Button icon={<UploadOutlined />} style={{ marginBottom: 12 }}>
            Tải lên file đính kèm
          </Button>
        </Upload>
      )}
      <List
        loading={isLoading}
        size="small"
        bordered
        dataSource={danhSach ?? []}
        locale={{ emptyText: 'Chưa có file đính kèm' }}
        renderItem={(tl) => (
          <List.Item
            actions={[
              <Button
                key="tai-xuong"
                type="link"
                icon={<DownloadOutlined />}
                onClick={() => xuLyTaiXuong(tl.id, tl.tenFile)}
              />,
              ...(choPhepSua
                ? [
                    <Popconfirm key="xoa" title="Xóa file này?" onConfirm={() => xuLyXoa(tl.id)}>
                      <Button type="link" danger icon={<DeleteOutlined />} />
                    </Popconfirm>,
                  ]
                : []),
            ]}
          >
            <Typography.Text>{tl.tenFile}</Typography.Text>
            <Typography.Text type="secondary" style={{ marginLeft: 8, fontSize: 12 }}>
              {dinhDangKichThuoc(tl.kichThuocByte)}
            </Typography.Text>
          </List.Item>
        )}
      />
    </div>
  )
}
