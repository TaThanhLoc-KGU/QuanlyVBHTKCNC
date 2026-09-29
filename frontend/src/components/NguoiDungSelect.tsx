import { useQuery } from '@tanstack/react-query'
import { Select } from 'antd'
import * as nguoiDungApi from '../api/nguoiDung'

interface Props {
  value?: string | null
  onChange?: (value: string | undefined) => void
  placeholder?: string
  allowClear?: boolean
}

/** Select tim nguoi dung he thong (chu nhiem/thanh vien/hoi dong...) - so
 * luong tai khoan cua 1 truong dai hoc khong lon nen tai 1 lan (200 dong) va
 * loc phia client, khong can API tim kiem rieng. */
export function NguoiDungSelect({ value, onChange, placeholder = 'Chọn người dùng', allowClear = true }: Props) {
  const { data, isLoading } = useQuery({
    queryKey: ['nguoi-dung-select'],
    queryFn: () => nguoiDungApi.danhSachNguoiDung(0, 200),
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
      options={(data?.content ?? []).map((nd) => ({ value: nd.id, label: nd.hoTen }))}
    />
  )
}
