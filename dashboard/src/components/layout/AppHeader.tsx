import { METRIC_SECTIONS, type MetricSection, type WebSocketState } from '../../types/dashboard'
import { formatDateTime } from '../../lib/formatters'

interface AppHeaderProps {
  wsStatus: WebSocketState
  lastUpdatedAt: number | null
  selectedMetrics: MetricSection[]
  refreshing: boolean
  onRefresh: () => void
  onToggleMetric: (metric: MetricSection) => void
}

export function AppHeader({
  wsStatus,
  lastUpdatedAt,
  selectedMetrics,
  refreshing,
  onRefresh,
  onToggleMetric,
}: AppHeaderProps) {
  return (
    <header className="relative overflow-hidden rounded-3xl border border-slate-800/80 bg-slate-950/80 px-5 py-6 shadow-glow backdrop-blur sm:px-7 lg:px-8">
      <div className="absolute inset-0 -z-10 bg-[radial-gradient(circle_at_top_left,rgba(14,165,233,0.22),transparent_32%),radial-gradient(circle_at_top_right,rgba(168,85,247,0.18),transparent_28%)]" />
      <div className="flex flex-col gap-6 lg:flex-row lg:items-end lg:justify-between">
        <div className="max-w-3xl">
          <div className="mb-4 inline-flex items-center gap-2 rounded-full border border-cyan-400/20 bg-cyan-400/10 px-3 py-1 text-xs font-medium uppercase tracking-[0.25em] text-cyan-200">
            <span className="h-2 w-2 rounded-full bg-cyan-300 shadow-[0_0_18px_rgba(103,232,249,0.8)]" />
            GamePulse monitor
          </div>
          <h1 className="text-3xl font-semibold tracking-tight text-white sm:text-4xl lg:text-5xl">
            Hardware telemetry dashboard
          </h1>
          <p className="mt-3 max-w-2xl text-sm leading-6 text-slate-400 sm:text-base">
            Live overview of registered agents, latest system snapshots and streaming CPU, memory, GPU and disk metrics exposed by the backend API.
          </p>
        </div>

        <div className="flex flex-col gap-3 rounded-2xl border border-slate-800 bg-slate-900/70 p-4 text-sm text-slate-300 sm:min-w-80">
          <div className="flex items-center justify-between gap-4">
            <span className="text-slate-500">Live socket</span>
            <StatusPill status={wsStatus} />
          </div>
          <div className="flex items-center justify-between gap-4">
            <span className="text-slate-500">Last refresh</span>
            <span className="font-medium text-slate-200">{formatDateTime(lastUpdatedAt)}</span>
          </div>
          <button
            className="mt-1 rounded-xl border border-slate-700 bg-slate-800 px-4 py-2 font-medium text-slate-100 transition hover:border-cyan-400/60 hover:bg-slate-700 disabled:cursor-not-allowed disabled:opacity-60"
            type="button"
            onClick={onRefresh}
            disabled={refreshing}
          >
            {refreshing ? 'Refreshing…' : 'Refresh snapshot'}
          </button>
        </div>
      </div>

      <div className="mt-6 border-t border-slate-800 pt-5">
        <div className="mb-3 text-xs font-semibold uppercase tracking-[0.2em] text-slate-500">Metric sections requested from API</div>
        <div className="flex flex-wrap gap-2">
          {METRIC_SECTIONS.map((metric) => {
            const active = selectedMetrics.includes(metric)
            return (
              <button
                key={metric}
                className={`rounded-full border px-3 py-1.5 text-xs font-semibold transition ${
                  active
                    ? 'border-cyan-300/50 bg-cyan-300/10 text-cyan-100'
                    : 'border-slate-700 bg-slate-900 text-slate-400 hover:border-slate-500 hover:text-slate-200'
                }`}
                type="button"
                onClick={() => onToggleMetric(metric)}
                aria-pressed={active}
              >
                {metric}
              </button>
            )
          })}
        </div>
      </div>
    </header>
  )
}

function StatusPill({ status }: { status: WebSocketState }) {
  const label = status === 'connected' ? 'Connected' : status === 'connecting' ? 'Connecting' : status === 'error' ? 'Error' : 'Disconnected'
  const tone = status === 'connected'
    ? 'border-emerald-400/30 bg-emerald-400/10 text-emerald-200'
    : status === 'connecting'
      ? 'border-amber-400/30 bg-amber-400/10 text-amber-200'
      : 'border-rose-400/30 bg-rose-400/10 text-rose-200'

  return <span className={`rounded-full border px-2.5 py-1 text-xs font-semibold ${tone}`}>{label}</span>
}
