import type { DashboardSummary } from '../../types/dashboard'
import { formatNumber, formatPercent } from '../../lib/formatters'

interface StatCardsProps {
  summary: DashboardSummary
}

export function StatCards({ summary }: StatCardsProps) {
  const cards = [
    {
      label: 'Registered agents',
      value: formatNumber(summary.totalAgents),
      detail: `${formatNumber(summary.onlineAgents)} online / ${formatNumber(summary.offlineAgents)} offline`,
    },
    {
      label: 'Latest snapshots',
      value: formatNumber(summary.latestSnapshots),
      detail: 'Agents with at least one telemetry snapshot',
    },
    {
      label: 'Average CPU usage',
      value: formatPercent(summary.averageCpuUsage),
      detail: 'Computed from latest available snapshots',
    },
    {
      label: 'Average memory usage',
      value: formatPercent(summary.averageMemoryUsage),
      detail: 'Computed from latest available snapshots',
    },
    {
      label: 'Max GPU temperature',
      value: formatNumber(summary.maxGpuTemperature, ' °C'),
      detail: 'Highest latest GPU temperature currently visible',
    },
  ]

  return (
    <section className="grid gap-4 sm:grid-cols-2 xl:grid-cols-5">
      {cards.map((card) => (
        <article key={card.label} className="rounded-2xl border border-slate-800 bg-slate-900/70 p-5 shadow-sm shadow-black/20">
          <p className="text-xs font-semibold uppercase tracking-[0.18em] text-slate-500">{card.label}</p>
          <p className="mt-3 text-2xl font-semibold text-white">{card.value}</p>
          <p className="mt-2 text-xs leading-5 text-slate-400">{card.detail}</p>
        </article>
      ))}
    </section>
  )
}
