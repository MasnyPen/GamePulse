import { formatBytes, formatPercent } from '../../lib/formatters'

interface UsageBarProps {
  label: string
  used: number | null | undefined
  total: number | null | undefined
}

export function UsageBar({ label, used, total }: UsageBarProps) {
  const percentage = used && total && total > 0 ? Math.min(100, Math.max(0, (used / total) * 100)) : null

  return (
    <div className="min-w-0 rounded-2xl border border-slate-800 bg-slate-950/60 p-4">
      <div className="mb-3 flex min-w-0 items-center justify-between gap-3 text-sm">
        <span className="min-w-0 truncate font-medium text-slate-200" title={label}>{label}</span>
        <span className="shrink-0 text-slate-400">{formatPercent(percentage)}</span>
      </div>
      <div className="h-2 overflow-hidden rounded-full bg-slate-800">
        <div className="h-full rounded-full bg-cyan-300 transition-all" style={{ width: `${percentage ?? 0}%` }} />
      </div>
      <div className="mt-2 flex min-w-0 items-center justify-between gap-3 text-xs text-slate-500">
        <span className="min-w-0 truncate">{formatBytes(used)} used</span>
        <span className="shrink-0">{formatBytes(total)} total</span>
      </div>
    </div>
  )
}
