import { useQuery } from '@tanstack/react-query'
import { Select } from 'antd'
import * as tuDienApi from '../api/tuDien'
import type { LoaiTuDien } from '../types'

interface Props {
  loai: LoaiTuDien
  value?: string | null
  onChange?: (value: string | undefined) => void
  placeholder?: string
  allowClear?: boolean
}

/** Select dung chung cho moi truong tham chieu toi 1 gia tri trong Tu dien -
 * chi hien gia tri dang "hoat dong" (admin co the tat gia tri cu ma khong
 * xoa han, tranh vo du lieu da dung gia tri do). */
export function TuDienSelect({ loai, value, onChange, placeholder = 'Chọn...', allowClear = true }: Props) {
  const { data, isLoading } = useQuery({
    queryKey: ['tu-dien-select', loai],
    queryFn: () => tuDienApi.danhSach(loai),
  })

  return (
    <Select
      value={value ?? undefined}
      onChange={onChange}
      loading={isLoading}
      showSearch
      allowClear={allowClear}
      placeholder={placeholder}
      optionFilterProp="label"
      options={(data ?? []).filter((td) => td.hoatDong).map((td) => ({ value: td.id, label: td.ten }))}
    />
  )
}
