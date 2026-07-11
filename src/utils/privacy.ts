const emptyLabels = ['未填写', '待补充', '-', '']

const shouldKeepValue = (value?: string | null) => {
  const normalizedValue = value?.trim() || ''
  return emptyLabels.includes(normalizedValue) || normalizedValue.includes('*')
}

export const maskPhone = (value?: string | null) => {
  const normalizedValue = value?.trim() || ''
  if (shouldKeepValue(normalizedValue)) return normalizedValue || '未填写'

  const digits = normalizedValue.replace(/\D/g, '')

  if (digits.length >= 11) {
    return `${digits.slice(0, 3)}****${digits.slice(-4)}`
  }

  if (digits.length >= 7) {
    return `${digits.slice(0, 3)}****${digits.slice(-2)}`
  }

  return normalizedValue.length > 2 ? `${normalizedValue.slice(0, 1)}****${normalizedValue.slice(-1)}` : '****'
}

export const maskIdCard = (value?: string | null) => {
  const normalizedValue = value?.trim() || ''
  if (shouldKeepValue(normalizedValue)) return normalizedValue || '未填写'

  if (normalizedValue.length >= 10) {
    return `${normalizedValue.slice(0, 6)}********${normalizedValue.slice(-4)}`
  }

  if (normalizedValue.length >= 4) {
    return `${normalizedValue.slice(0, 2)}****${normalizedValue.slice(-2)}`
  }

  return '****'
}
