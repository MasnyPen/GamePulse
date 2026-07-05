import { apiGet, buildWebSocketUrl } from './client'
import { normalizeAgent, normalizeSnapshot } from '../lib/normalizers'
import type { DashboardAgent, DashboardSnapshot, MetricSection, WebSocketState } from '../types/dashboard'

interface DashboardLiveConnectionOptions {
  metrics: MetricSection[]
  onSnapshot: (snapshot: DashboardSnapshot) => void
  onStatusChange: (status: WebSocketState) => void
  onError: (message: string) => void
}

export interface DashboardLiveConnection {
  close: () => void
  setFilter: (agentIds: string[], metrics: MetricSection[]) => void
}

export async function fetchDashboardAgents(): Promise<DashboardAgent[]> {
  const rawAgents = await apiGet<unknown[]>('/api/dashboard/agents')
  return rawAgents.map(normalizeAgent).filter((agent) => agent.id !== '')
}

export async function fetchLatestSnapshots(
  agentIds: string[] = [],
  metrics: MetricSection[] = [],
): Promise<DashboardSnapshot[]> {
  const params = new URLSearchParams()

  if (agentIds.length > 0) {
    params.set('agentIds', agentIds.join(','))
  }
  if (metrics.length > 0) {
    params.set('metrics', metrics.join(','))
  }

  const rawSnapshots = await apiGet<unknown[]>('/api/dashboard/snapshots/latest', params)
  return rawSnapshots.map(normalizeSnapshot).filter((snapshot) => snapshot.agentId !== '')
}

export function connectDashboardLive(options: DashboardLiveConnectionOptions): DashboardLiveConnection {
  const socket = new WebSocket(buildWebSocketUrl('/dashboard/live'))

  options.onStatusChange('connecting')

  socket.addEventListener('open', () => {
    options.onStatusChange('connected')
    sendFilter(socket, [], options.metrics)
  })

  socket.addEventListener('message', (event) => {
    try {
      const message = JSON.parse(String(event.data)) as Record<string, unknown>
      const type = typeof message.type === 'string' ? message.type : 'unknown'

      if (type === 'live_update' && message.payload) {
        const snapshot = normalizeSnapshot(message.payload)
        if (snapshot.agentId) {
          options.onSnapshot(snapshot)
        }
        return
      }

      if (type === 'error') {
        const error = typeof message.error === 'string' ? message.error : 'unknown_dashboard_live_error'
        options.onError(error)
      }
    } catch (error) {
      options.onError(error instanceof Error ? error.message : 'Unable to parse live dashboard message')
    }
  })

  socket.addEventListener('close', () => {
    options.onStatusChange('disconnected')
  })

  socket.addEventListener('error', () => {
    options.onStatusChange('error')
    options.onError('Dashboard live WebSocket connection failed')
  })

  return {
    close: () => socket.close(),
    setFilter: (agentIds, metrics) => sendFilter(socket, agentIds, metrics),
  }
}

function sendFilter(socket: WebSocket, agentIds: string[], metrics: MetricSection[]): void {
  if (socket.readyState !== WebSocket.OPEN) {
    return
  }

  socket.send(
    JSON.stringify({
      type: 'set_filter',
      agent_ids: agentIds,
      metrics,
    }),
  )
}
