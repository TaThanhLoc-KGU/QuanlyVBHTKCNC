import type { ReactNode } from 'react'
import { useQuery } from '@tanstack/react-query'
import { Card, Col, Empty, List, Progress, Row, Skeleton, Statistic, Tag, Typography } from 'antd'
import {
  BankOutlined,
  FileTextOutlined,
  GlobalOutlined,
  SendOutlined,
  SolutionOutlined,
  TeamOutlined,
  UserSwitchOutlined,
} from '@ant-design/icons'
import { Bar, BarChart, CartesianGrid, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts'
import dayjs from 'dayjs'
import * as dashboardApi from '../api/dashboard'
import type { TrangThaiMou } from '../types'

const MAU_TRANG_THAI: Record<TrangThaiMou, string> = {
  CON_HIEU_LUC: '#16794F',
  SAP_HET_HAN: '#B45309',
  DA_HET_HAN: '#C0362C',
}
const NHAN_TRANG_THAI: Record<TrangThaiMou, string> = {
  CON_HIEU_LUC: 'Còn hiệu lực',
  SAP_HET_HAN: 'Sắp hết hạn',
  DA_HET_HAN: 'Đã hết hạn',
}

function TheThongKe({
  tieuDe,
  giaTri,
  icon,
  mau,
}: {
  tieuDe: string
  giaTri: number
  icon: ReactNode
  mau: string
}) {
  return (
    <Card styles={{ body: { padding: '18px 20px' } }}>
      <div style={{ display: 'flex', alignItems: 'flex-start', gap: 14 }}>
        <div
          style={{
            width: 40,
            height: 40,
            borderRadius: 10,
            background: `${mau}15`,
            color: mau,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            fontSize: 18,
            flexShrink: 0,
          }}
        >
          {icon}
        </div>
        <div style={{ minWidth: 0 }}>
          <div style={{ fontSize: 12.5, color: '#667085', marginBottom: 2 }}>{tieuDe}</div>
          <div style={{ fontSize: 24, fontWeight: 700, color: '#101828', lineHeight: 1.2 }}>{giaTri}</div>
        </div>
      </div>
    </Card>
  )
}

function NhanNhomThongKe({ children }: { children: ReactNode }) {
  return (
    <Typography.Text
      style={{
        fontSize: 12,
        fontWeight: 600,
        color: '#98A2B3',
        textTransform: 'uppercase',
        letterSpacing: 0.4,
        display: 'block',
        marginBottom: 10,
      }}
    >
      {children}
    </Typography.Text>
  )
}

export function DashboardPage() {
  const { data, isLoading } = useQuery({ queryKey: ['dashboard'], queryFn: dashboardApi.layDashboard })

  if (isLoading || !data) {
    return <Skeleton active />
  }

  const duLieuBieuDo = data.mouDenHanTheoThang.map((d) => ({
    thang: dayjs(d.thang + '-01').format('MM/YYYY'),
    soLuong: d.soLuong,
  }))

  return (
    <div>
      {data.lamMoiLuc && (
        <Typography.Text type="secondary" style={{ fontSize: 13 }}>
          Số liệu tổng hợp làm mới lúc: {dayjs(data.lamMoiLuc).format('DD/MM/YYYY HH:mm')}
        </Typography.Text>
      )}

      <div style={{ marginTop: 20 }}>
        <NhanNhomThongKe>Văn bản &amp; MoU</NhanNhomThongKe>
        <Row gutter={[16, 16]}>
          <Col xs={12} lg={6}>
            <TheThongKe
              tieuDe="Văn bản ĐHKG còn hiệu lực"
              giaTri={data.tongVanBanDhkgHieuLuc}
              icon={<FileTextOutlined />}
              mau="#155E75"
            />
          </Col>
          <Col xs={12} lg={6}>
            <TheThongKe
              tieuDe="VBPL VN còn hiệu lực"
              giaTri={data.tongVbplVnHieuLuc}
              icon={<BankOutlined />}
              mau="#155E75"
            />
          </Col>
          <Col xs={12} lg={6}>
            <TheThongKe
              tieuDe="MoU còn hiệu lực"
              giaTri={data.tongMouConHieuLuc}
              icon={<SolutionOutlined />}
              mau="#16794F"
            />
          </Col>
          <Col xs={12} lg={6}>
            <TheThongKe
              tieuDe="MoU sắp hết hạn"
              giaTri={data.tongMouSapHetHan}
              icon={<SolutionOutlined />}
              mau="#B45309"
            />
          </Col>
        </Row>
      </div>

      <div style={{ marginTop: 24 }}>
        <NhanNhomThongKe>Hoạt động đối ngoại (năm nay)</NhanNhomThongKe>
        <Row gutter={[16, 16]}>
          <Col xs={12} lg={6}>
            <TheThongKe tieuDe="Đoàn vào" giaTri={data.tongDoanVaoNamHienTai} icon={<GlobalOutlined />} mau="#155E75" />
          </Col>
          <Col xs={12} lg={6}>
            <TheThongKe
              tieuDe="Khách nước ngoài đã đến"
              giaTri={data.tongKhachNuocNgoaiNamHienTai}
              icon={<TeamOutlined />}
              mau="#155E75"
            />
          </Col>
          <Col xs={12} lg={6}>
            <TheThongKe tieuDe="Đoàn ra" giaTri={data.tongDoanRaNamHienTai} icon={<SendOutlined />} mau="#155E75" />
          </Col>
          <Col xs={12} lg={6}>
            <TheThongKe
              tieuDe="Lượt cán bộ đi công tác"
              giaTri={data.tongLuotCanBoDiCongTacNamHienTai}
              icon={<UserSwitchOutlined />}
              mau="#155E75"
            />
          </Col>
        </Row>
      </div>

      <Row gutter={[16, 16]} style={{ marginTop: 24 }}>
        <Col xs={24} lg={14}>
          <Card title="Thời hạn hiệu lực MoU (sắp xếp theo số ngày còn lại)" style={{ height: 460 }} styles={{ body: { height: 396, overflowY: 'auto' } }}>
            {data.widgetMouSapHetHan.length === 0 ? (
              <Empty description="Không có MoU nào sắp hết hạn" />
            ) : (
              <List
                dataSource={data.widgetMouSapHetHan}
                renderItem={(m) => (
                  <List.Item key={m.id}>
                    <div style={{ width: '100%' }}>
                      <div style={{ display: 'flex', justifyContent: 'space-between', gap: 8 }}>
                        <Typography.Text strong>{m.tenDoiTac}</Typography.Text>
                        <Tag color={MAU_TRANG_THAI[m.trangThai]} style={{ flexShrink: 0 }}>
                          {NHAN_TRANG_THAI[m.trangThai]}
                        </Tag>
                      </div>
                      <Typography.Text type="secondary" style={{ fontSize: 12 }}>
                        Hết hạn: {dayjs(m.ngayHetHan).format('DD/MM/YYYY')} - Còn lại {m.soNgayConLai} ngày
                      </Typography.Text>
                      <Progress
                        percent={m.phanTramThoiGianDaQua ?? 0}
                        size="small"
                        strokeColor={MAU_TRANG_THAI[m.trangThai]}
                        showInfo={false}
                      />
                    </div>
                  </List.Item>
                )}
              />
            )}
          </Card>
        </Col>
        <Col xs={24} lg={10}>
          <Card title="Số lượng MoU đến hạn theo tháng (12 tháng tới)" style={{ height: 460 }}>
            <ResponsiveContainer width="100%" height={380}>
              <BarChart data={duLieuBieuDo}>
                <CartesianGrid strokeDasharray="3 3" stroke="#EAECEF" />
                <XAxis dataKey="thang" fontSize={11} stroke="#98A2B3" />
                <YAxis allowDecimals={false} fontSize={11} stroke="#98A2B3" />
                <Tooltip />
                <Bar dataKey="soLuong" fill="#155E75" name="Số lượng MoU" radius={[4, 4, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </Card>
        </Col>
      </Row>
    </div>
  )
}
