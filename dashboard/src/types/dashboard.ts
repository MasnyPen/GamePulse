export const METRIC_SECTIONS = ['SYSTEM', 'CPU', 'MEMORY', 'GPU', 'DISKS'] as const

export type MetricSection = (typeof METRIC_SECTIONS)[number]

export type WebSocketState = 'connecting' | 'connected' | 'disconnected' | 'error'

export interface DashboardAgent {
  id: string
  hostname: string | null
  lastIp: string | null
  lastAuth: number | null
  online: boolean
}

export interface SystemData {
  name: string | null
  kernel: string | null
  osVersion: string | null
  hostname: string | null
}

export interface CpuData {
  name: string | null
  threads: number | null
  temperature: number | null
  frequency: number | null
  usedPercentage: number | null
}

export interface MemoryData {
  total: number | null
  used: number | null
  totalSwap: number | null
  usedSwap: number | null
}

export interface GpuData {
  vendor: string | null
  model: string | null
  totalVram: number | null
  usedVram: number | null
  loadPercentage: number | null
  temperature: number | null
}

export interface DiskData {
  device: string | null
  filesystem: string | null
  totalSpace: number | null
  usedSpace: number | null
  mountedOn: string | null
}

export interface DashboardSnapshot {
  agentId: string
  snapshotId: number | null
  capturedAt: string | null
  system?: SystemData
  cpu?: CpuData
  memory?: MemoryData
  gpu?: GpuData
  disks?: DiskData[]
}

export interface DashboardLiveMessage {
  type: 'ok' | 'error' | 'live_update'
  event?: string
  error?: string
  payload?: DashboardSnapshot
}

export interface DashboardSummary {
  totalAgents: number
  onlineAgents: number
  offlineAgents: number
  latestSnapshots: number
  averageCpuUsage: number | null
  averageMemoryUsage: number | null
  maxGpuTemperature: number | null
}

export type AgentSortKey =
  | 'status'
  | 'hostname'
  | 'id'
  | 'lastIp'
  | 'lastAuth'
  | 'capturedAt'
  | 'snapshotId'
  | 'system'
  | 'cpuUsage'
  | 'memoryUsage'
  | 'gpuLoad'

export interface AgentSortState {
  key: AgentSortKey
  direction: 'asc' | 'desc'
}
