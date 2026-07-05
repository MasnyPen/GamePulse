import { useEffect, type ReactNode } from 'react'
import type { DashboardAgent, DashboardSnapshot } from '../../types/dashboard'
import {
  formatBytes,
  formatDateTime,
  formatNumber,
  formatPercent,
  formatRelativeTime,
  percentOf,
} from '../../lib/formatters'
import { MetricsHistoryCharts } from '../charts/MetricsHistoryCharts'
import { UsageBar } from '../charts/UsageBar'
import { StatusBadge } from '../table/StatusBadge'
import { getAgentHostname } from '../../lib/agentDisplay'
import { CopyableIp } from '../ui/CopyableIp'

interface AgentDetailsModalProps {
  agent: DashboardAgent
  snapshot?: DashboardSnapshot
  history: DashboardSnapshot[]
  onClose: () => void
}

interface DetailItem {
  label: string
  value: ReactNode
}

export function AgentDetailsModal({ agent, snapshot, history, onClose }: AgentDetailsModalProps) {
  useEffect(() => {
    const handleKeyDown = (event: KeyboardEvent) => {
      if (event.key === 'Escape') {
        onClose()
      }
    }

    document.body.style.overflow = 'hidden'
    window.addEventListener('keydown', handleKeyDown)

    return () => {
      document.body.style.overflow = ''
      window.removeEventListener('keydown', handleKeyDown)
    }
  }, [onClose])

  const memoryUsage = percentOf(snapshot?.memory?.used, snapshot?.memory?.total)
  const vramUsage = percentOf(snapshot?.gpu?.usedVram, snapshot?.gpu?.totalVram)
  const displayHostname = getAgentHostname(agent, snapshot)

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-2 sm:p-4 lg:p-6" role="dialog" aria-modal="true">
      <button className="absolute inset-0 cursor-default bg-slate-950/80 backdrop-blur-sm animate-fade-in" type="button" aria-label="Close modal" onClick={onClose} />
      <div className="relative flex max-h-[94vh] w-full max-w-7xl min-w-0 flex-col overflow-hidden rounded-3xl border border-slate-700 bg-slate-950 shadow-2xl animate-scale-in">
        <div className="shrink-0 border-b border-slate-800 bg-slate-900/80 p-4 sm:p-5">
          <div className="flex min-w-0 flex-col gap-4 sm:flex-row sm:items-start sm:justify-between">
            <div className="min-w-0">
              <div className="mb-3 flex flex-wrap items-center gap-3">
                <StatusBadge online={agent.online} />
                <span className="rounded-full border border-slate-700 bg-slate-950 px-2.5 py-1 text-xs font-medium text-slate-400">
                  Snapshot {snapshot?.snapshotId ?? '-'}
                </span>
              </div>
              <h2 className="truncate text-2xl font-semibold text-white">{displayHostname}</h2>
              <p className="mt-2 break-all font-mono text-xs text-slate-500">{agent.id}</p>
            </div>
            <button
              className="shrink-0 rounded-xl border border-slate-700 bg-slate-950 px-4 py-2 text-sm font-medium text-slate-200 transition hover:border-rose-300/60 hover:text-white"
              type="button"
              onClick={onClose}
            >
              Close
            </button>
          </div>
        </div>

        <div className="min-h-0 overflow-y-auto overflow-x-hidden p-4 sm:p-5 lg:p-6">
          <div className="grid min-w-0 gap-4 sm:grid-cols-2 xl:grid-cols-4">
            <MetricTile label="CPU usage" value={formatPercent(snapshot?.cpu?.usedPercentage)} detail={snapshot?.cpu?.name ?? 'No CPU data'} />
            <MetricTile label="Memory usage" value={formatPercent(memoryUsage)} detail={`${formatBytes(snapshot?.memory?.used)} / ${formatBytes(snapshot?.memory?.total)}`} />
            <MetricTile label="GPU load" value={formatPercent(snapshot?.gpu?.loadPercentage)} detail={snapshot?.gpu?.model ?? 'No GPU data'} />
            <MetricTile label="History points" value={formatNumber(history.length)} detail="Client-side live points" />
          </div>

          <div className="mt-6 min-w-0 space-y-4">
            <div className="grid min-w-0 items-stretch gap-4 lg:grid-cols-3">
              <DetailSection
                title="Basic information"
                items={[
                  { label: 'Agent ID', value: agent.id },
                  { label: 'Hostname', value: displayHostname },
                  { label: 'Last IP', value: <CopyableIp value={agent.lastIp} /> },
                  { label: 'Snapshot ID', value: snapshot?.snapshotId?.toString() ?? '-' },
                ]}
              />

              <DetailSection
                title="Status and activity"
                items={[
                  { label: 'Connection status', value: agent.online ? 'Online' : 'Offline' },
                  { label: 'Last authentication', value: formatDateTime(agent.lastAuth) },
                  { label: 'Last authentication age', value: formatRelativeTime(agent.lastAuth) },
                  { label: 'Snapshot captured at', value: formatDateTime(snapshot?.capturedAt) },
                  { label: 'Snapshot age', value: formatRelativeTime(snapshot?.capturedAt) },
                ]}
              />

              <DetailSection
                title="System data"
                items={[
                  { label: 'System name', value: snapshot?.system?.name ?? '-' },
                  { label: 'Kernel', value: snapshot?.system?.kernel ?? '-' },
                  { label: 'OS version', value: snapshot?.system?.osVersion ?? '-' },
                  { label: 'Reported hostname', value: snapshot?.system?.hostname ?? '-' },
                ]}
              />
            </div>

            <div className="grid min-w-0 items-stretch gap-4 lg:grid-cols-2">
              <DetailSection
                title="CPU"
                items={[
                  { label: 'Name', value: snapshot?.cpu?.name ?? '-' },
                  { label: 'Threads', value: formatNumber(snapshot?.cpu?.threads) },
                  { label: 'Frequency', value: formatNumber(snapshot?.cpu?.frequency, ' MHz') },
                  { label: 'Temperature', value: formatNumber(snapshot?.cpu?.temperature, ' °C') },
                  { label: 'Used percentage', value: formatPercent(snapshot?.cpu?.usedPercentage) },
                ]}
              />

              <DetailSection
                title="GPU"
                items={[
                  { label: 'Vendor', value: snapshot?.gpu?.vendor ?? '-' },
                  { label: 'Model', value: snapshot?.gpu?.model ?? '-' },
                  { label: 'VRAM usage', value: formatPercent(vramUsage) },
                  { label: 'Load percentage', value: formatPercent(snapshot?.gpu?.loadPercentage) },
                  { label: 'Temperature', value: formatNumber(snapshot?.gpu?.temperature, ' °C') },
                ]}
              />
            </div>
          </div>

          <section className="mt-6 min-w-0 overflow-hidden rounded-2xl border border-slate-800 bg-slate-900/50 p-5">
            <h3 className="text-base font-semibold text-white">Resource usage</h3>
            <div className="mt-4 grid min-w-0 items-start gap-4 lg:grid-cols-3">
              <ResourceGroup title="CPU">
                <PercentUsageBar label="CPU usage" value={snapshot?.cpu?.usedPercentage} />
                <SmallMetric label="CPU temperature" value={formatNumber(snapshot?.cpu?.temperature, ' °C')} />
                <SmallMetric label="CPU frequency" value={formatNumber(snapshot?.cpu?.frequency, ' MHz')} />
              </ResourceGroup>

              <ResourceGroup title="Memory">
                <UsageBar label="RAM" used={snapshot?.memory?.used} total={snapshot?.memory?.total} />
                <UsageBar label="Swap" used={snapshot?.memory?.usedSwap} total={snapshot?.memory?.totalSwap} />
              </ResourceGroup>

              <ResourceGroup title="GPU">
                <PercentUsageBar label="GPU load" value={snapshot?.gpu?.loadPercentage} />
                <UsageBar label="GPU VRAM" used={snapshot?.gpu?.usedVram} total={snapshot?.gpu?.totalVram} />
                <SmallMetric label="GPU temperature" value={formatNumber(snapshot?.gpu?.temperature, ' °C')} />
              </ResourceGroup>

              <ResourceGroup title="Disks" className="lg:col-span-3">
                {snapshot?.disks && snapshot.disks.length > 0 ? (
                  <div className="grid min-w-0 gap-3 md:grid-cols-2 xl:grid-cols-3">
                    {snapshot.disks.map((disk) => (
                      <UsageBar
                        key={`${disk.device}-${disk.mountedOn}`}
                        label={`${disk.device ?? 'Disk'}${disk.mountedOn ? ` on ${disk.mountedOn}` : ''}`}
                        used={disk.usedSpace}
                        total={disk.totalSpace}
                      />
                    ))}
                  </div>
                ) : (
                  <p className="rounded-2xl border border-dashed border-slate-800 bg-slate-950/60 p-4 text-sm text-slate-500">No disk data available.</p>
                )}
              </ResourceGroup>
            </div>
          </section>

          <section className="mt-6 min-w-0 overflow-hidden rounded-2xl border border-slate-800 bg-slate-900/50 p-5">
            <h3 className="text-base font-semibold text-white">Live metric charts</h3>
            <div className="mt-4 min-w-0">
              <MetricsHistoryCharts history={history.length > 0 ? history : snapshot ? [snapshot] : []} />
            </div>
          </section>

          <section className="mt-6 min-w-0 overflow-hidden rounded-2xl border border-slate-800 bg-slate-900/50 p-5">
            <h3 className="text-base font-semibold text-white">Raw technical data</h3>
            <p className="mt-1 text-sm text-slate-400">Exact normalized data used by the UI, derived from backend REST and live payloads.</p>
            <pre className="mt-4 max-h-96 overflow-auto rounded-xl border border-slate-800 bg-slate-950 p-4 text-xs leading-5 text-slate-300">
              {JSON.stringify({ agent, latestSnapshot: snapshot ?? null, liveHistory: history }, null, 2)}
            </pre>
          </section>
        </div>
      </div>
    </div>
  )
}

function MetricTile({ label, value, detail }: { label: string; value: string; detail: string }) {
  return (
    <article className="min-w-0 rounded-2xl border border-slate-800 bg-slate-900/70 p-4">
      <p className="truncate text-xs font-semibold uppercase tracking-[0.16em] text-slate-500">{label}</p>
      <p className="mt-3 text-2xl font-semibold text-white">{value}</p>
      <p className="mt-1 truncate text-xs text-slate-400" title={detail}>{detail}</p>
    </article>
  )
}

function DetailSection({ title, items }: { title: string; items: DetailItem[] }) {
  return (
    <section className="flex h-full min-w-0 flex-col overflow-hidden rounded-2xl border border-slate-800 bg-slate-900/50 p-5">
      <h3 className="text-base font-semibold text-white">{title}</h3>
      <dl className="mt-4 flex-1 space-y-3">
        {items.map((item) => (
          <div key={`${title}-${item.label}`} className="grid min-w-0 gap-1 border-b border-slate-800/70 pb-3 last:border-0 last:pb-0 sm:grid-cols-[150px_minmax(0,1fr)]">
            <dt className="text-xs uppercase tracking-[0.16em] text-slate-500">{item.label}</dt>
            <dd className="min-w-0 break-words text-sm text-slate-200">{item.value}</dd>
          </div>
        ))}
      </dl>
    </section>
  )
}

function ResourceGroup({ title, className = '', children }: { title: string; className?: string; children: ReactNode }) {
  return (
    <div className={`h-full min-w-0 rounded-2xl border border-slate-800/80 bg-slate-950/40 p-4 ${className}`}>
      <h4 className="mb-3 text-sm font-semibold text-slate-100">{title}</h4>
      <div className="min-w-0 space-y-3">{children}</div>
    </div>
  )
}

function PercentUsageBar({ label, value }: { label: string; value: number | null | undefined }) {
  const percentage = typeof value === 'number' && Number.isFinite(value) ? Math.min(100, Math.max(0, value)) : null

  return (
    <div className="min-w-0 rounded-2xl border border-slate-800 bg-slate-950/60 p-4">
      <div className="mb-3 flex min-w-0 items-center justify-between gap-3 text-sm">
        <span className="min-w-0 truncate font-medium text-slate-200">{label}</span>
        <span className="shrink-0 text-slate-400">{formatPercent(percentage)}</span>
      </div>
      <div className="h-2 overflow-hidden rounded-full bg-slate-800">
        <div className="h-full rounded-full bg-cyan-300 transition-all" style={{ width: `${percentage ?? 0}%` }} />
      </div>
    </div>
  )
}

function SmallMetric({ label, value }: { label: string; value: string }) {
  return (
    <div className="min-w-0 rounded-2xl border border-slate-800 bg-slate-950/60 p-4">
      <p className="truncate text-xs uppercase tracking-[0.16em] text-slate-500">{label}</p>
      <p className="mt-2 truncate text-sm font-medium text-slate-200" title={value}>{value}</p>
    </div>
  )
}
