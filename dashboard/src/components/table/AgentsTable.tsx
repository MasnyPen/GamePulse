import { useMemo, useState } from 'react'
import type { AgentSortKey, AgentSortState, DashboardAgent, DashboardSnapshot } from '../../types/dashboard'
import { compactId, formatDateTime, formatPercent, formatRelativeTime, percentOf } from '../../lib/formatters'
import { StatusBadge } from './StatusBadge'
import { getAgentHostname } from '../../lib/agentDisplay'
import { CopyableIp } from '../ui/CopyableIp'

interface AgentsTableProps {
  agents: DashboardAgent[]
  snapshotsByAgent: Record<string, DashboardSnapshot>
  loading: boolean
  onSelectAgent: (agent: DashboardAgent) => void
}

type StatusFilter = 'all' | 'online' | 'offline'

const columns: Array<{ key: AgentSortKey; label: string; align?: 'right' }> = [
  { key: 'status', label: 'Status' },
  { key: 'hostname', label: 'Hostname' },
  { key: 'id', label: 'Agent ID' },
  { key: 'lastIp', label: 'Last IP' },
  { key: 'lastAuth', label: 'Last auth' },
  { key: 'capturedAt', label: 'Snapshot' },
  { key: 'snapshotId', label: 'Snapshot ID', align: 'right' },
  { key: 'system', label: 'System' },
  { key: 'cpuUsage', label: 'CPU', align: 'right' },
  { key: 'memoryUsage', label: 'Memory', align: 'right' },
  { key: 'gpuLoad', label: 'GPU', align: 'right' },
]

export function AgentsTable({ agents, snapshotsByAgent, loading, onSelectAgent }: AgentsTableProps) {
  const [query, setQuery] = useState('')
  const [statusFilter, setStatusFilter] = useState<StatusFilter>('all')
  const [sort, setSort] = useState<AgentSortState>({ key: 'hostname', direction: 'asc' })

  const visibleAgents = useMemo(() => {
    const normalizedQuery = query.trim().toLowerCase()

    return agents
      .filter((agent) => {
        if (statusFilter === 'online' && !agent.online) {
          return false
        }
        if (statusFilter === 'offline' && agent.online) {
          return false
        }
        if (!normalizedQuery) {
          return true
        }

        const snapshot = snapshotsByAgent[agent.id]
        const searchable = [
          agent.id,
          agent.hostname,
          agent.lastIp,
          snapshot?.system?.hostname,
          snapshot?.system?.name,
          snapshot?.system?.osVersion,
          snapshot?.cpu?.name,
          snapshot?.gpu?.model,
          snapshot?.gpu?.vendor,
        ]
          .filter(Boolean)
          .join(' ')
          .toLowerCase()

        return searchable.includes(normalizedQuery)
      })
      .sort((left, right) => compareAgents(left, right, snapshotsByAgent, sort))
  }, [agents, query, snapshotsByAgent, sort, statusFilter])

  function handleSort(key: AgentSortKey): void {
    setSort((current) => ({
      key,
      direction: current.key === key && current.direction === 'asc' ? 'desc' : 'asc',
    }))
  }

  return (
    <section className="rounded-3xl border border-slate-800 bg-slate-950/80 shadow-sm shadow-black/30">
      <div className="flex flex-col gap-4 border-b border-slate-800 p-4 sm:flex-row sm:items-center sm:justify-between sm:p-5">
        <div>
          <h2 className="text-lg font-semibold text-white">Agents</h2>
          <p className="mt-1 text-sm text-slate-400">Main entities returned by the dashboard API.</p>
        </div>
        <div className="flex flex-col gap-3 sm:flex-row sm:items-center">
          <input
            className="w-full rounded-xl border border-slate-700 bg-slate-900 px-4 py-2 text-sm text-slate-100 outline-none transition placeholder:text-slate-500 focus:border-cyan-400/70 sm:w-72"
            type="search"
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="Search ID, host, IP, system…"
          />
          <select
            className="rounded-xl border border-slate-700 bg-slate-900 px-4 py-2 text-sm text-slate-100 outline-none transition focus:border-cyan-400/70"
            value={statusFilter}
            onChange={(event) => setStatusFilter(event.target.value as StatusFilter)}
          >
            <option value="all">All statuses</option>
            <option value="online">Online only</option>
            <option value="offline">Offline only</option>
          </select>
        </div>
      </div>

      <div className="overflow-x-auto">
        <table className="min-w-full divide-y divide-slate-800 text-left text-sm">
          <thead className="bg-slate-900/80 text-xs uppercase tracking-[0.12em] text-slate-500">
            <tr>
              {columns.map((column) => (
                <th key={column.key} className={`whitespace-nowrap px-4 py-3 font-semibold ${column.align === 'right' ? 'text-right' : ''}`}>
                  <button
                    className="inline-flex items-center gap-1 transition hover:text-slate-200"
                    type="button"
                    onClick={() => handleSort(column.key)}
                  >
                    {column.label}
                    <span className="text-slate-600">{sort.key === column.key ? (sort.direction === 'asc' ? '▲' : '▼') : '↕'}</span>
                  </button>
                </th>
              ))}
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-800/80">
            {loading ? <SkeletonRows /> : null}
            {!loading && visibleAgents.length === 0 ? (
              <tr>
                <td className="px-4 py-12 text-center text-slate-400" colSpan={columns.length}>
                  No agents match the current filters.
                </td>
              </tr>
            ) : null}
            {!loading
              ? visibleAgents.map((agent) => {
                  const snapshot = snapshotsByAgent[agent.id]
                  const memoryUsage = percentOf(snapshot?.memory?.used, snapshot?.memory?.total)
                  const cpuUsageValue = agent.online ? formatPercent(snapshot?.cpu?.usedPercentage) : '-'
                  const memoryUsageValue = agent.online ? formatPercent(memoryUsage) : '-'
                  const gpuLoadValue = agent.online ? formatPercent(snapshot?.gpu?.loadPercentage) : '-'
                  return (
                    <tr
                      key={agent.id}
                      className="cursor-pointer bg-slate-950/30 transition hover:bg-cyan-400/[0.06]"
                      onClick={() => onSelectAgent(agent)}
                    >
                      <td className="whitespace-nowrap px-4 py-4"><StatusBadge online={agent.online} /></td>
                      <td className="whitespace-nowrap px-4 py-4 font-medium text-slate-100">{getAgentHostname(agent, snapshot)}</td>
                      <td className="whitespace-nowrap px-4 py-4 font-mono text-xs text-slate-400" title={agent.id}>{compactId(agent.id)}</td>
                      <td className="whitespace-nowrap px-4 py-4 text-xs text-slate-300"><CopyableIp value={agent.lastIp} stopPropagation /></td>
                      <td className="whitespace-nowrap px-4 py-4 text-slate-300" title={formatDateTime(agent.lastAuth)}>{formatRelativeTime(agent.lastAuth)}</td>
                      <td className="whitespace-nowrap px-4 py-4 text-slate-300" title={formatDateTime(snapshot?.capturedAt)}>{formatRelativeTime(snapshot?.capturedAt)}</td>
                      <td className="whitespace-nowrap px-4 py-4 text-right font-mono text-xs text-slate-400">{snapshot?.snapshotId ?? '-'}</td>
                      <td className="whitespace-nowrap px-4 py-4 text-slate-300">{snapshot?.system?.name ?? snapshot?.system?.osVersion ?? '-'}</td>
                      <td className="whitespace-nowrap px-4 py-4 text-right text-slate-300">{cpuUsageValue}</td>
                      <td className="whitespace-nowrap px-4 py-4 text-right text-slate-300">{memoryUsageValue}</td>
                      <td className="whitespace-nowrap px-4 py-4 text-right text-slate-300">{gpuLoadValue}</td>
                    </tr>
                  )
                })
              : null}
          </tbody>
        </table>
      </div>
    </section>
  )
}

function SkeletonRows() {
  return (
    <>
      {Array.from({ length: 6 }).map((_, index) => (
        <tr key={index} className="animate-pulse">
          {columns.map((column) => (
            <td key={column.key} className="px-4 py-4">
              <div className="h-4 rounded bg-slate-800" />
            </td>
          ))}
        </tr>
      ))}
    </>
  )
}

function compareAgents(
  left: DashboardAgent,
  right: DashboardAgent,
  snapshotsByAgent: Record<string, DashboardSnapshot>,
  sort: AgentSortState,
): number {
  const direction = sort.direction === 'asc' ? 1 : -1
  const leftValue = getSortValue(left, snapshotsByAgent[left.id], sort.key)
  const rightValue = getSortValue(right, snapshotsByAgent[right.id], sort.key)

  if (typeof leftValue === 'number' && typeof rightValue === 'number') {
    return (leftValue - rightValue) * direction
  }

  return String(leftValue).localeCompare(String(rightValue)) * direction
}

function getSortValue(agent: DashboardAgent, snapshot: DashboardSnapshot | undefined, key: AgentSortKey): string | number {
  switch (key) {
    case 'status':
      return agent.online ? 1 : 0
    case 'hostname':
      return getAgentHostname(agent, snapshot)
    case 'id':
      return agent.id
    case 'lastIp':
      return agent.lastIp ?? ''
    case 'lastAuth':
      return agent.lastAuth ?? 0
    case 'capturedAt':
      return snapshot?.capturedAt ? new Date(snapshot.capturedAt).getTime() : 0
    case 'snapshotId':
      return snapshot?.snapshotId ?? 0
    case 'system':
      return snapshot?.system?.name ?? snapshot?.system?.osVersion ?? ''
    case 'cpuUsage':
      return agent.online ? snapshot?.cpu?.usedPercentage ?? -1 : -1
    case 'memoryUsage':
      return agent.online ? percentOf(snapshot?.memory?.used, snapshot?.memory?.total) ?? -1 : -1
    case 'gpuLoad':
      return agent.online ? snapshot?.gpu?.loadPercentage ?? -1 : -1
  }
}
