import { useQuery } from '@tanstack/react-query'
import { Drawer, Empty, Skeleton, Tag, Timeline, Typography } from 'antd'
import dayjs from 'dayjs'
import * as lichSuApi from '../api/lichSu'

interface Props {
  open: boolean
  onClose: () => void
  bang: string
  banGhiId: string | null
}

const MAU_HANH_DONG: Record<string, string> = {
  INSERT: 'green',
  UPDATE: 'blue',
  DELETE: 'red',
}
const NHAN_HANH_DONG: Record<string, string> = {
  INSERT: 'Tạo mới',
  UPDATE: 'Cập nhật',
  DELETE: 'Xóa',
}

export function AuditHistoryDrawer({ open, onClose, bang, banGhiId }: Props) {
  const { data, isLoading } = useQuery({
    queryKey: ['lich-su', bang, banGhiId],
    queryFn: () => lichSuApi.layLichSuThayDoi(bang, banGhiId!),
    enabled: open && !!banGhiId,
  })

  return (
    <Drawer title="Lịch sử chỉnh sửa" open={open} onClose={onClose} width={480}>
      {isLoading && <Skeleton active />}
      {!isLoading && (!data || data.length === 0) && <Empty description="Chưa có lịch sử" />}
      {!isLoading && data && data.length > 0 && (
        <Timeline
          items={data.map((ls) => ({
            color: MAU_HANH_DONG[ls.hanhDong],
            children: (
              <div key={ls.id}>
                <Tag color={MAU_HANH_DONG[ls.hanhDong]}>{NHAN_HANH_DONG[ls.hanhDong]}</Tag>
                <Typography.Text type="secondary" style={{ fontSize: 12 }}>
                  {dayjs(ls.thoiDiem).format('DD/MM/YYYY HH:mm:ss')}
                </Typography.Text>
              </div>
            ),
          }))}
        />
      )}
    </Drawer>
  )
}
