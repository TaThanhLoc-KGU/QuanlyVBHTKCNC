import { useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import {
  Button,
  Card,
  Col,
  DatePicker,
  InputNumber,
  Row,
  Space,
  Statistic,
  Table,
  Tabs,
  Tag,
  Typography,
  message,
} from 'antd'
import { FileExcelOutlined, FilePdfOutlined } from '@ant-design/icons'
import dayjs, { type Dayjs } from 'dayjs'
import * as baoCaoApi from '../api/baoCao'
import type { LoaiDoiTac, TrangThaiMou } from '../types'

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
const NHAN_LOAI_DOI_TAC: Record<LoaiDoiTac, string> = { TRONG_NUOC: 'Trong nước', NGOAI_NUOC: 'Ngoài nước' }

function BaoCaoMouTrongNamTab() {
  const [nam, setNam] = useState(dayjs().year())
  const { data, isLoading } = useQuery({
    queryKey: ['bc-mou-trong-nam', nam],
    queryFn: () => baoCaoApi.baoCaoMouTrongNam(nam),
  })

  async function xuat(dinhDang: 'EXCEL' | 'PDF') {
    try {
      await baoCaoApi.xuatBaoCao('mou-trong-nam', dinhDang, { nam }, `bao-cao-mou-nam-${nam}`)
    } catch {
      message.error('Xuất báo cáo không thành công')
    }
  }

  return (
    <div>
      <Space style={{ marginBottom: 16 }}>
        <Typography.Text>Năm:</Typography.Text>
        <InputNumber value={nam} onChange={(v) => setNam(v ?? dayjs().year())} />
        <Button icon={<FileExcelOutlined />} onClick={() => xuat('EXCEL')}>
          Xuất Excel
        </Button>
        <Button icon={<FilePdfOutlined />} onClick={() => xuat('PDF')}>
          Xuất PDF
        </Button>
      </Space>

      {data && (
        <>
          <Row gutter={16} style={{ marginBottom: 16 }}>
            <Col span={8}>
              <Card>
                <Statistic title="Tổng số MoU mới ký" value={data.tomTat.tongSoMouMoiKy} />
              </Card>
            </Col>
            <Col span={8}>
              <Card>
                <Statistic title="Cùng kỳ năm trước" value={data.tomTat.tongSoMouCungKyNamTruoc} />
              </Card>
            </Col>
          </Row>
          <Table
            loading={isLoading}
            rowKey={(_, i) => String(i)}
            scroll={{ x: 'max-content' }}
            dataSource={data.chiTiet}
            pagination={{ pageSize: 10 }}
            columns={[
              { title: 'Đối tác', dataIndex: 'tenDoiTac' },
              {
                title: 'Loại đối tác',
                dataIndex: 'loaiDoiTac',
                render: (v: LoaiDoiTac) => NHAN_LOAI_DOI_TAC[v] ?? v,
              },
              { title: 'Lĩnh vực hợp tác', dataIndex: 'linhVucHopTac' },
              { title: 'Ngày ký', dataIndex: 'ngayKy', render: (v: string) => dayjs(v).format('DD/MM/YYYY') },
              {
                title: 'Ngày hết hạn',
                dataIndex: 'ngayHetHan',
                render: (v: string | null) => (v ? dayjs(v).format('DD/MM/YYYY') : '-'),
              },
              { title: 'Đầu mối', dataIndex: 'donViDauMoi' },
              {
                title: 'Trạng thái',
                dataIndex: 'trangThai',
                render: (v: TrangThaiMou) => <Tag color={MAU_TRANG_THAI[v]}>{NHAN_TRANG_THAI[v]}</Tag>,
              },
            ]}
          />
        </>
      )}
    </div>
  )
}

function BaoCaoDoanRaVaoTab() {
  const [khoang, setKhoang] = useState<[Dayjs, Dayjs] | null>(null)
  const tu = khoang?.[0]?.format('YYYY-MM-DD')
  const den = khoang?.[1]?.format('YYYY-MM-DD')

  const { data } = useQuery({
    queryKey: ['bc-doan-ra-vao', tu, den],
    queryFn: () => baoCaoApi.baoCaoDoanRaVao(tu, den),
  })

  async function xuat(dinhDang: 'EXCEL' | 'PDF') {
    try {
      await baoCaoApi.xuatBaoCao('doan-ra-vao', dinhDang, { tu, den }, 'bao-cao-doan-ra-vao')
    } catch {
      message.error('Xuất báo cáo không thành công')
    }
  }

  return (
    <div>
      <Space style={{ marginBottom: 16 }}>
        <DatePicker.RangePicker
          format="DD/MM/YYYY"
          onChange={(v) => setKhoang(v && v[0] && v[1] ? [v[0], v[1]] : null)}
        />
        <Button icon={<FileExcelOutlined />} onClick={() => xuat('EXCEL')}>
          Xuất Excel
        </Button>
        <Button icon={<FilePdfOutlined />} onClick={() => xuat('PDF')}>
          Xuất PDF
        </Button>
      </Space>

      {data && (
        <>
          <Row gutter={16} style={{ marginBottom: 16 }}>
            <Col span={6}>
              <Card>
                <Statistic title="Tổng đoàn vào" value={data.tomTat.tongSoDoanVao} />
              </Card>
            </Col>
            <Col span={6}>
              <Card>
                <Statistic title="Khách nước ngoài" value={data.tomTat.tongKhachNuocNgoaiDaDen} />
              </Card>
            </Col>
            <Col span={6}>
              <Card>
                <Statistic title="Tổng đoàn ra" value={data.tomTat.tongSoDoanRa} />
              </Card>
            </Col>
            <Col span={6}>
              <Card>
                <Statistic title="Lượt cán bộ đi công tác" value={data.tomTat.tongLuotCanBoDiCongTac} />
              </Card>
            </Col>
          </Row>
          <Typography.Title level={5}>Đoàn vào</Typography.Title>
          <Table
            size="small"
            rowKey="id"
            scroll={{ x: 'max-content' }}
            dataSource={data.doanVao}
            pagination={{ pageSize: 10 }}
            columns={[
              { title: 'Tên đoàn', dataIndex: 'tenDoan' },
              { title: 'Đối tác', dataIndex: 'tenDoiTac' },
              { title: 'Đến', dataIndex: 'thoiGianDen', render: (v: string) => dayjs(v).format('DD/MM/YYYY') },
              { title: 'Đi', dataIndex: 'thoiGianDi', render: (v: string) => dayjs(v).format('DD/MM/YYYY') },
              { title: 'Quốc tịch', dataIndex: 'quocTich', render: (v: string[]) => v.join(', ') },
            ]}
          />
          <Typography.Title level={5} style={{ marginTop: 16 }}>
            Đoàn ra
          </Typography.Title>
          <Table
            size="small"
            rowKey="id"
            scroll={{ x: 'max-content' }}
            dataSource={data.doanRa}
            pagination={{ pageSize: 10 }}
            columns={[
              { title: 'Đơn vị làm việc', dataIndex: 'tenDoiTac' },
              { title: 'Quốc gia', dataIndex: 'quocGiaLamViec' },
              { title: 'Đi', dataIndex: 'thoiGianDi', render: (v: string) => dayjs(v).format('DD/MM/YYYY') },
              { title: 'Về', dataIndex: 'thoiGianVe', render: (v: string) => dayjs(v).format('DD/MM/YYYY') },
            ]}
          />
        </>
      )}
    </div>
  )
}

function BaoCaoThoiHanMouTab() {
  const [thangToi, setThangToi] = useState<number | null>(6)
  const { data, isLoading } = useQuery({
    queryKey: ['bc-thoi-han-mou', thangToi],
    queryFn: () => baoCaoApi.baoCaoThoiHanMou(thangToi ?? undefined),
  })

  async function xuat(dinhDang: 'EXCEL' | 'PDF') {
    try {
      await baoCaoApi.xuatBaoCao('thoi-han-mou', dinhDang, { thangToi: thangToi ?? undefined }, 'bao-cao-thoi-han-mou')
    } catch {
      message.error('Xuất báo cáo không thành công')
    }
  }

  return (
    <div>
      <Space style={{ marginBottom: 16 }}>
        <Typography.Text>Hết hạn trong (tháng tới, để trống = tất cả còn hiệu lực):</Typography.Text>
        <InputNumber value={thangToi} onChange={setThangToi} min={1} />
        <Button icon={<FileExcelOutlined />} onClick={() => xuat('EXCEL')}>
          Xuất Excel
        </Button>
        <Button icon={<FilePdfOutlined />} onClick={() => xuat('PDF')}>
          Xuất PDF
        </Button>
      </Space>
      <Table
        loading={isLoading}
        rowKey="id"
        scroll={{ x: 'max-content' }}
        dataSource={data}
        pagination={{ pageSize: 15 }}
        columns={[
          { title: 'Đối tác', dataIndex: 'tenDoiTac' },
          { title: 'Ngày ký', dataIndex: 'ngayBanHanh', render: (v: string) => dayjs(v).format('DD/MM/YYYY') },
          {
            title: 'Ngày hết hạn',
            dataIndex: 'ngayHetHan',
            render: (v: string | null) => (v ? dayjs(v).format('DD/MM/YYYY') : '-'),
          },
          { title: 'Còn lại', dataIndex: 'soNgayConLai', render: (v: number | null) => (v != null ? `${v} ngày` : '-') },
          {
            title: 'Trạng thái',
            dataIndex: 'trangThai',
            render: (v: TrangThaiMou) => <Tag color={MAU_TRANG_THAI[v]}>{NHAN_TRANG_THAI[v]}</Tag>,
          },
        ]}
      />
    </div>
  )
}

export function BaoCaoPage() {
  return (
    <div>
      <Tabs
        items={[
          { key: 'mou-trong-nam', label: 'MoU hợp tác trong năm', children: <BaoCaoMouTrongNamTab /> },
          { key: 'doan-ra-vao', label: 'Đoàn ra và đoàn khách vào', children: <BaoCaoDoanRaVaoTab /> },
          { key: 'thoi-han-mou', label: 'Thời hạn MoU theo đối tác', children: <BaoCaoThoiHanMouTab /> },
        ]}
      />
    </div>
  )
}
