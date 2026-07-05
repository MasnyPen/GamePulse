const DEFAULT_API_BASE_URL = 'http://localhost:8080'

export const API_BASE_URL = normalizeBaseUrl(import.meta.env.VITE_API_BASE_URL ?? DEFAULT_API_BASE_URL)

function normalizeBaseUrl(value: string): string {
  return value.replace(/\/$/, '')
}

export function buildApiUrl(path: string, params?: URLSearchParams): string {
  const normalizedPath = path.startsWith('/') ? path : `/${path}`
  const query = params && params.size > 0 ? `?${params.toString()}` : ''
  return `${API_BASE_URL}${normalizedPath}${query}`
}

export async function apiGet<T>(path: string, params?: URLSearchParams): Promise<T> {
  const response = await fetch(buildApiUrl(path, params), {
    headers: {
      Accept: 'application/json',
    },
  })

  if (!response.ok) {
    const detail = await response.text().catch(() => '')
    throw new Error(`API request failed (${response.status})${detail ? `: ${detail}` : ''}`)
  }

  return response.json() as Promise<T>
}

export function buildWebSocketUrl(path: string): string {
  const normalizedPath = path.startsWith('/') ? path : `/${path}`
  const url = new URL(API_BASE_URL)
  url.protocol = url.protocol === 'https:' ? 'wss:' : 'ws:'
  url.pathname = normalizedPath
  url.search = ''
  return url.toString()
}
