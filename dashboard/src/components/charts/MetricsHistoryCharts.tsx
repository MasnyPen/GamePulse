import { useMemo } from 'react'
import type { DashboardSnapshot } from '../../types/dashboard'
import { formatShortTime, percentOf } from '../../lib/formatters'
import { MetricLineChart, type MetricChartPoint } from './MetricLineChart'

interface MetricsHistoryChartsProps {
  history: DashboardSnapshot[]
}

export function MetricsHistoryCharts({ history }: MetricsHistoryChartsProps) {
  const chartData = useMemo(() => buildChartData(history), [history])

  return (
    <section className="grid min-w-0 items-start gap-4 lg:grid-cols-2">
      <MetricLineChart title="CPU usage" unit="%" data={chartData.cpuUsage} />
      <MetricLineChart title="CPU temperature" unit="°C" data={chartData.cpuTemperature} />
      <MetricLineChart title="Memory usage" unit="%" data={chartData.memoryUsage} />
      <MetricLineChart title="GPU load" unit="%" data={chartData.gpuLoad} />
      <MetricLineChart className="lg:col-span-2" title="GPU temperature" unit="°C" data={chartData.gpuTemperature} />
    </section>
  )
}

interface ChartDataSet {
  cpuUsage: MetricChartPoint[]
  cpuTemperature: MetricChartPoint[]
  memoryUsage: MetricChartPoint[]
  gpuLoad: MetricChartPoint[]
  gpuTemperature: MetricChartPoint[]
}

function buildChartData(history: DashboardSnapshot[]): ChartDataSet {
  const sorted = [...history].sort((left, right) => toTimestamp(left.capturedAt) - toTimestamp(right.capturedAt))

  return {
    cpuUsage: sorted.map((snapshot) => ({ time: formatShortTime(snapshot.capturedAt), value: snapshot.cpu?.usedPercentage ?? null })),
    cpuTemperature: sorted.map((snapshot) => ({ time: formatShortTime(snapshot.capturedAt), value: snapshot.cpu?.temperature ?? null })),
    memoryUsage: sorted.map((snapshot) => ({ time: formatShortTime(snapshot.capturedAt), value: percentOf(snapshot.memory?.used, snapshot.memory?.total) })),
    gpuLoad: sorted.map((snapshot) => ({ time: formatShortTime(snapshot.capturedAt), value: snapshot.gpu?.loadPercentage ?? null })),
    gpuTemperature: sorted.map((snapshot) => ({ time: formatShortTime(snapshot.capturedAt), value: snapshot.gpu?.temperature ?? null })),
  }
}

function toTimestamp(value: string | null): number {
  if (!value) {
    return 0
  }
  const timestamp = new Date(value).getTime()
  return Number.isNaN(timestamp) ? 0 : timestamp
}
