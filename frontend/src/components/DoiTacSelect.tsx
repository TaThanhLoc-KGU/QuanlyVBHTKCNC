import { useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import { Select, Spin } from 'antd'
import * as doiTacApi from '../api/doiTac'

interface Props {
  value?: string
  onChange?: (value: string | undefined) => void
  placeholder?: string
  allowClear?: boolean
}

export function DoiTacSelect({ value, onChange, placeholder = 'Chọn đối tác', allowClear }: Props) {
  const [tuKhoa, setTuKhoa] = useState('')

  const { data, isFetching } = useQuery({
    queryKey: ['doi-tac-select', tuKhoa],
    queryFn: () => doiTacApi.danhSachDoiTac({ tuKhoa: tuKhoa || undefined, size: 30 }),
  })

  return (
    <Select
      value={value}
      onChange={onChange}
      showSearch
      allowClear={allowClear}
      placeholder={placeholder}
      filterOption={false}
      onSearch={setTuKhoa}
      notFoundContent={isFetching ? <Spin size="small" /> : 'Không tìm thấy'}
      options={(data?.content ?? []).map((dt) => ({
        value: dt.id,
        label: `${dt.tenDoiTac}${dt.quocGia ? ' (' + dt.quocGia + ')' : ''}`,
      }))}
    />
  )
}
