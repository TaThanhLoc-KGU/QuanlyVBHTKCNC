import { useState } from 'react'
import { useQuery, useQueryClient } from '@tanstack/react-query'
import { Badge, Button, Empty, List, Popover, Tag, Typography } from 'antd'
import { BellOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import * as thongBaoApi from '../api/thongBao'
import type { MucDoCanhBao } from '../types'

const MAU_MUC_DO: Record<MucDoCanhBao, string> = {
  INFO: 'blue',
  WARNING: 'orange',
  CRITICAL: 'red',
}
const NHAN_MUC_DO: Record<MucDoCanhBao, string> = {
  INFO: 'Thông tin',
  WARNING: 'Cảnh báo',
  CRITICAL: 'Nghiêm trọng',
}

export function NotificationBell() {
  const [mo, setMo] = useState(false)
  const queryClient = useQueryClient()

  const { data: soLuong } = useQuery({
    queryKey: ['thong-bao-so-luong'],
    queryFn: thongBaoApi.soLuongChuaDoc,
    refetchInterval: 60_000,
  })

  const { data: trangDanhSach } = useQuery({
    queryKey: ['thong-bao-danh-sach'],
    queryFn: () => thongBaoApi.danhSachThongBao(undefined, 0, 10),
    enabled: mo,
  })

  async function danhDauDaDoc(id: string) {
    await thongBaoApi.danhDauDaDoc(id)
    queryClient.invalidateQueries({ queryKey: ['thong-bao-so-luong'] })
    queryClient.invalidateQueries({ queryKey: ['thong-bao-danh-sach'] })
  }

  async function danhDauTatCa() {
    await thongBaoApi.danhDauTatCaDaDoc()
    queryClient.invalidateQueries({ queryKey: ['thong-bao-so-luong'] })
    queryClient.invalidateQueries({ queryKey: ['thong-bao-danh-sach'] })
  }

  const noiDung = (
    <div style={{ width: 380, maxHeight: 480, overflowY: 'auto' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 8 }}>
        <Typography.Text strong>Thông báo</Typography.Text>
        <Button size="small" type="link" onClick={danhDauTatCa}>
          Đánh dấu tất cả đã đọc
        </Button>
      </div>
      {trangDanhSach && trangDanhSach.content.length > 0 ? (
        <List
          size="small"
          dataSource={trangDanhSach.content}
          renderItem={(tb) => (
            <List.Item
              style={{ cursor: tb.daDocWeb ? 'default' : 'pointer', opacity: tb.daDocWeb ? 0.55 : 1 }}
              onClick={() => !tb.daDocWeb && danhDauDaDoc(tb.id)}
            >
              <List.Item.Meta
                title={
                  <>
                    <Tag color={MAU_MUC_DO[tb.mucDo]}>{NHAN_MUC_DO[tb.mucDo]}</Tag>
                    {tb.tieuDe}
                  </>
                }
                description={
                  <>
                    <div>{tb.noiDung}</div>
                    <Typography.Text type="secondary" style={{ fontSize: 12 }}>
                      {dayjs(tb.ngayTao).format('DD/MM/YYYY HH:mm')}
                    </Typography.Text>
                  </>
                }
              />
            </List.Item>
          )}
        />
      ) : (
        <Empty description="Không có thông báo" />
      )}
    </div>
  )

  return (
    <Popover content={noiDung} trigger="click" open={mo} onOpenChange={setMo} placement="bottomRight">
      <Badge count={soLuong ?? 0} size="small">
        <Button type="text" icon={<BellOutlined style={{ fontSize: 18 }} />} />
      </Badge>
    </Popover>
  )
}
