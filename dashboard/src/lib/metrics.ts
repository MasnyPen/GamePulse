import type { DashboardAgent, DashboardSnapshot, DashboardSummary } from '../types/dashboard'
import { percentOf } from './formatters'

export function buildDashboardSummary(
  agents: DashboardAgent[],
  snapshotsByAgent: Record<string, DashboardSnapshot>,
): DashboardSummary {
  const snapshots = Object.values(snapshotsByAgent)
  const cpuValues = snapshots
    .map((snapshot) => snapshot.cpu?.usedPercentage)
    .filter((value): value is number => typeof value === 'number' && Number.isFinite(value))
  const memoryValues = snapshots
    .map((snapshot) => percentOf(snapshot.memory?.used, snapshot.memory?.total))
    .filter((value): value is number => typeof value === 'number' && Number.isFinite(value))
  const gpuTemperatures = snapshots
    .map((snapshot) => snapshot.gpu?.temperature)
    .filter((value): value is number => typeof value === 'number' && Number.isFinite(value))

  return {
    totalAgents: agents.length,
    onlineAgents: agents.filter((agent) => agent.online).length,
    offlineAgents: agents.filter((agent) => !agent.online).length,
    latestSnapshots: snapshots.length,
    averageCpuUsage: average(cpuValues),
    averageMemoryUsage: average(memoryValues),
    maxGpuTemperature: gpuTemperatures.length > 0 ? Math.max(...gpuTemperatures) : null,
  }
}

function average(values: number[]): number | null {
  if (values.length === 0) {
    return null
  }
  return values.reduce((sum, value) => sum + value, 0) / values.length
}
