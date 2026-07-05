import type {
  CpuData,
  DashboardAgent,
  DashboardSnapshot,
  DiskData,
  GpuData,
  MemoryData,
  SystemData,
} from '../types/dashboard'

type RawRecord = Record<string, unknown>

function isRecord(value: unknown): value is RawRecord {
  return typeof value === 'object' && value !== null && !Array.isArray(value)
}

function pickString(source: RawRecord, ...keys: string[]): string | null {
  for (const key of keys) {
    const value = source[key]
    if (typeof value === 'string') {
      return value
    }
    if (typeof value === 'number' || typeof value === 'boolean') {
      return String(value)
    }
  }
  return null
}

function pickNumber(source: RawRecord, ...keys: string[]): number | null {
  for (const key of keys) {
    const value = source[key]
    if (typeof value === 'number' && Number.isFinite(value)) {
      return value
    }
    if (typeof value === 'string' && value.trim() !== '') {
      const parsed = Number(value)
      if (Number.isFinite(parsed)) {
        return parsed
      }
    }
  }
  return null
}

function pickBoolean(source: RawRecord, ...keys: string[]): boolean {
  for (const key of keys) {
    const value = source[key]
    if (typeof value === 'boolean') {
      return value
    }
  }
  return false
}

function pickInstant(source: RawRecord, ...keys: string[]): string | null {
  for (const key of keys) {
    const value = source[key]

    if (typeof value === 'string' && value.trim() !== '') {
      return normalizeTemporalValue(value)
    }

    if (typeof value === 'number' && Number.isFinite(value)) {
      return normalizeTemporalNumber(value)
    }

    if (isRecord(value)) {
      const fromObject = normalizeTemporalObject(value)
      if (fromObject) {
        return fromObject
      }
    }
  }

  return null
}

function normalizeTemporalValue(value: string): string | null {
  const trimmed = value.trim()
  if (/^-?\d+(\.\d+)?$/.test(trimmed)) {
    const numeric = Number(trimmed)
    return Number.isFinite(numeric) ? normalizeTemporalNumber(numeric) : null
  }

  const timestamp = new Date(trimmed).getTime()
  return Number.isNaN(timestamp) ? trimmed : new Date(timestamp).toISOString()
}

function normalizeTemporalNumber(value: number): string {
  const milliseconds = Math.abs(value) < 10_000_000_000 ? value * 1000 : value
  return new Date(milliseconds).toISOString()
}

function normalizeTemporalObject(value: RawRecord): string | null {
  const epochMilliseconds = pickNumber(value, 'epochMilli', 'epoch_milli', 'milliseconds', 'millis')
  if (epochMilliseconds !== null) {
    return normalizeTemporalNumber(epochMilliseconds)
  }

  const epochSeconds = pickNumber(value, 'epochSecond', 'epoch_second', 'seconds')
  if (epochSeconds !== null) {
    const nanos = pickNumber(value, 'nano', 'nanos') ?? 0
    return new Date(epochSeconds * 1000 + nanos / 1_000_000).toISOString()
  }

  return null
}

function normalizeSystem(raw: unknown): SystemData | undefined {
  if (!isRecord(raw)) {
    return undefined
  }

  return {
    name: pickString(raw, 'name'),
    kernel: pickString(raw, 'kernel'),
    osVersion: pickString(raw, 'osVersion', 'os_version'),
    hostname: pickString(raw, 'hostname'),
  }
}

function normalizeCpu(raw: unknown): CpuData | undefined {
  if (!isRecord(raw)) {
    return undefined
  }

  return {
    name: pickString(raw, 'name'),
    threads: pickNumber(raw, 'threads'),
    temperature: pickNumber(raw, 'temperature'),
    frequency: pickNumber(raw, 'frequency'),
    usedPercentage: pickNumber(raw, 'usedPercentage', 'used_percentage'),
  }
}

function normalizeMemory(raw: unknown): MemoryData | undefined {
  if (!isRecord(raw)) {
    return undefined
  }

  return {
    total: pickNumber(raw, 'total'),
    used: pickNumber(raw, 'used'),
    totalSwap: pickNumber(raw, 'totalSwap', 'total_swap'),
    usedSwap: pickNumber(raw, 'usedSwap', 'used_swap'),
  }
}

function normalizeGpu(raw: unknown): GpuData | undefined {
  if (!isRecord(raw)) {
    return undefined
  }

  const rawTemperature = pickNumber(raw, 'temperature')

  return {
    vendor: pickString(raw, 'vendor'),
    model: pickString(raw, 'model'),
    totalVram: pickNumber(raw, 'totalVram', 'total_vram'),
    usedVram: pickNumber(raw, 'usedVram', 'used_vram'),
    loadPercentage: pickNumber(raw, 'loadPercentage', 'load_percentage'),
    temperature: rawTemperature === null ? null : rawTemperature / 1000,
  }
}

function normalizeDisk(raw: unknown): DiskData | null {
  if (!isRecord(raw)) {
    return null
  }

  return {
    device: pickString(raw, 'device'),
    filesystem: pickString(raw, 'filesystem'),
    totalSpace: pickNumber(raw, 'totalSpace', 'total_space'),
    usedSpace: pickNumber(raw, 'usedSpace', 'used_space'),
    mountedOn: pickString(raw, 'mountedOn', 'mounted_on'),
  }
}

export function normalizeAgent(raw: unknown): DashboardAgent {
  const source = isRecord(raw) ? raw : {}

  return {
    id: pickString(source, 'id') ?? '',
    hostname: pickString(source, 'hostname'),
    lastIp: pickString(source, 'lastIp'),
    lastAuth: pickNumber(source, 'lastAuth'),
    online: pickBoolean(source, 'online'),
  }
}

export function normalizeSnapshot(raw: unknown): DashboardSnapshot {
  const source = isRecord(raw) ? raw : {}
  const disksRaw = source.disks

  return {
    agentId: pickString(source, 'agentId', 'agent_id') ?? '',
    snapshotId: pickNumber(source, 'snapshotId', 'snapshot_id'),
    capturedAt: pickInstant(source, 'capturedAt', 'captured_at'),
    system: normalizeSystem(source.system),
    cpu: normalizeCpu(source.cpu),
    memory: normalizeMemory(source.memory),
    gpu: normalizeGpu(source.gpu),
    disks: Array.isArray(disksRaw) ? disksRaw.map(normalizeDisk).filter((disk): disk is DiskData => disk !== null) : undefined,
  }
}
