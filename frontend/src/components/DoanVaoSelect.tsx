import { useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import { Select, Spin } from 'antd'
import dayjs from 'dayjs'
import * as doanApi from '../api/doan'

interface Props {
  value?: string | null
  onChange?: (value: string | undefined) => void
  placeholder?: string
  allowClear?: boolean
}

export function DoanVaoSelect({ value, onChange, placeholder = 'Chọn đoàn vào', allowClear = true }: Props) {
  const [tuKhoa, setTuKhoa] = useState('')

  const { data, isFetching } = useQuery({
    queryKey: ['doan-vao-select', tuKhoa],
    queryFn: () => doanApi.danhSachDoanVao({ tuKhoa: tuKhoa || undefined, size: 30 }),
  })

  return (
    <Select
      value={value ?? undefined}
      onChange={onChange}
      showSearch
      allowClear={allowClear}
      placeholder={placeholder}
      filterOption={false}
      onSearch={setTuKhoa}
      notFoundContent={isFetching ? <Spin size="small" /> : 'Không tìm thấy'}
      options={(data?.content ?? []).map((d) => ({
        value: d.id,
        label: `${d.tenDoan} (${dayjs(d.thoiGianDen).format('DD/MM/YYYY')})`,
      }))}
    />
  )
}
