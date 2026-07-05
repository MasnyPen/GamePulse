export function formatDateTime(value: string | number | null | undefined): string {
  if (value === null || value === undefined || value === '') {
    return '-'
  }

  const date = typeof value === 'number' ? new Date(value) : new Date(value)
  if (Number.isNaN(date.getTime())) {
    return '-'
  }

  return new Intl.DateTimeFormat(undefined, {
    year: 'numeric',
    month: 'short',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
  }).format(date)
}

export function formatShortTime(value: string | number | null | undefined): string {
  if (value === null || value === undefined || value === '') {
    return '-'
  }

  const date = typeof value === 'number' ? new Date(value) : new Date(value)
  if (Number.isNaN(date.getTime())) {
    return '-'
  }

  return new Intl.DateTimeFormat(undefined, {
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
  }).format(date)
}

export function formatRelativeTime(value: string | number | null | undefined): string {
  if (value === null || value === undefined || value === '') {
    return '-'
  }

  const date = typeof value === 'number' ? new Date(value) : new Date(value)
  if (Number.isNaN(date.getTime())) {
    return '-'
  }

  const seconds = Math.round((date.getTime() - Date.now()) / 1000)
  const absoluteSeconds = Math.abs(seconds)
  const rtf = new Intl.RelativeTimeFormat(undefined, { numeric: 'auto' })

  if (absoluteSeconds < 60) {
    return rtf.format(seconds, 'second')
  }
  if (absoluteSeconds < 3600) {
    return rtf.format(Math.round(seconds / 60), 'minute')
  }
  if (absoluteSeconds < 86400) {
    return rtf.format(Math.round(seconds / 3600), 'hour')
  }
  return rtf.format(Math.round(seconds / 86400), 'day')
}

export function formatBytes(value: number | null | undefined): string {
  if (value === null || value === undefined || !Number.isFinite(value)) {
    return '-'
  }

  const units = ['B', 'KB', 'MB', 'GB', 'TB', 'PB']
  let amount = Math.abs(value)
  let index = 0

  while (amount >= 1024 && index < units.length - 1) {
    amount /= 1024
    index += 1
  }

  const signedAmount = value < 0 ? -amount : amount
  return `${signedAmount.toFixed(index === 0 ? 0 : 1)} ${units[index]}`
}

export function formatPercent(value: number | null | undefined): string {
  if (value === null || value === undefined || !Number.isFinite(value)) {
    return '-'
  }
  return `${value.toFixed(value >= 10 ? 0 : 1)}%`
}

export function formatNumber(value: number | null | undefined, suffix = ''): string {
  if (value === null || value === undefined || !Number.isFinite(value)) {
    return '-'
  }

  return `${new Intl.NumberFormat(undefined, { maximumFractionDigits: 1 }).format(value)}${suffix}`
}

export function percentOf(used: number | null | undefined, total: number | null | undefined): number | null {
  if (!used || !total || total <= 0) {
    return null
  }
  return Math.min(100, Math.max(0, (used / total) * 100))
}

export function compactId(id: string): string {
  if (id.length <= 12) {
    return id
  }
  return `${id.slice(0, 8)}…${id.slice(-4)}`
}
