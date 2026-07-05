import {
  CartesianGrid,
  Line,
  LineChart,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from 'recharts'

export interface MetricChartPoint {
  time: string
  value: number | null
}

interface MetricLineChartProps {
  title: string
  unit: string
  data: MetricChartPoint[]
  className?: string
}

export function MetricLineChart({ title, unit, data, className = '' }: MetricLineChartProps) {
  const hasData = data.some((point) => typeof point.value === 'number')

  return (
    <article className={`min-w-0 rounded-2xl border border-slate-800 bg-slate-950/70 p-4 ${className}`}>
      <div className="mb-4">
        <h3 className="text-sm font-semibold text-slate-100">{title}</h3>
      </div>
      <div className="h-56 min-w-0">
        {hasData ? (
          <ResponsiveContainer width="100%" height="100%">
            <LineChart data={data} margin={{ top: 8, right: 12, left: -8, bottom: 0 }}>
              <CartesianGrid strokeDasharray="4 4" stroke="rgba(51,65,85,0.75)" />
              <XAxis dataKey="time" tick={{ fill: '#94a3b8', fontSize: 11 }} tickLine={false} axisLine={{ stroke: '#334155' }} />
              <YAxis tick={{ fill: '#94a3b8', fontSize: 11 }} tickLine={false} axisLine={{ stroke: '#334155' }} width={42} />
              <Tooltip content={<MetricTooltip unit={unit} />} />
              <Line
                type="monotone"
                dataKey="value"
                stroke="#67e8f9"
                strokeWidth={2}
                dot={false}
                activeDot={{ r: 4, fill: '#67e8f9', stroke: '#0f172a', strokeWidth: 2 }}
                connectNulls
              />
            </LineChart>
          </ResponsiveContainer>
        ) : (
          <div className="flex h-full items-center justify-center rounded-xl border border-dashed border-slate-800 text-sm text-slate-500">
            No metric points available yet
          </div>
        )}
      </div>
    </article>
  )
}

interface MetricTooltipProps {
  active?: boolean
  payload?: Array<{ value?: number | string | null }>
  label?: string | number
  unit: string
}

function MetricTooltip({ active, payload, label, unit }: MetricTooltipProps) {
  if (!active || !payload?.length) {
    return null
  }

  const value = payload[0]?.value
  return (
    <div className="rounded-xl border border-slate-700 bg-slate-950 px-3 py-2 text-xs shadow-xl">
      <div className="font-medium text-slate-200">{label}</div>
      <div className="mt-1 text-cyan-200">{typeof value === 'number' ? `${value.toFixed(1)} ${unit}` : '-'}</div>
    </div>
  )
}
