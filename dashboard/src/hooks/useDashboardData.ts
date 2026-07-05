import { useCallback, useEffect, useMemo, useRef, useState } from 'react'
import {
  connectDashboardLive,
  fetchDashboardAgents,
  fetchLatestSnapshots,
  type DashboardLiveConnection,
} from '../api/dashboard'
import { METRIC_SECTIONS, type DashboardAgent, type DashboardSnapshot, type MetricSection, type WebSocketState } from '../types/dashboard'

const MAX_HISTORY_POINTS = 80

interface DashboardDataState {
  agents: DashboardAgent[]
  snapshotsByAgent: Record<string, DashboardSnapshot>
  historyByAgent: Record<string, DashboardSnapshot[]>
  selectedMetrics: MetricSection[]
  loading: boolean
  refreshing: boolean
  error: string | null
  wsStatus: WebSocketState
  lastUpdatedAt: number | null
  refresh: () => Promise<void>
  toggleMetric: (metric: MetricSection) => void
}

export function useDashboardData(): DashboardDataState {
  const [agents, setAgents] = useState<DashboardAgent[]>([])
  const [snapshotsByAgent, setSnapshotsByAgent] = useState<Record<string, DashboardSnapshot>>({})
  const [historyByAgent, setHistoryByAgent] = useState<Record<string, DashboardSnapshot[]>>({})
  const [selectedMetrics, setSelectedMetrics] = useState<MetricSection[]>([...METRIC_SECTIONS])
  const [loading, setLoading] = useState(true)
  const [refreshing, setRefreshing] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [wsStatus, setWsStatus] = useState<WebSocketState>('disconnected')
  const [lastUpdatedAt, setLastUpdatedAt] = useState<number | null>(null)
  const connectionRef = useRef<DashboardLiveConnection | null>(null)
  const selectedMetricsKey = useMemo(() => selectedMetrics.join(','), [selectedMetrics])

  const mergeSnapshots = useCallback((snapshots: DashboardSnapshot[]) => {
    setSnapshotsByAgent((current) => {
      const next = { ...current }
      snapshots.forEach((snapshot) => {
        next[snapshot.agentId] = snapshot
      })
      return next
    })

    setHistoryByAgent((current) => {
      const next = { ...current }
      snapshots.forEach((snapshot) => {
        next[snapshot.agentId] = appendSnapshot(current[snapshot.agentId] ?? [], snapshot)
      })
      return next
    })
  }, [])

  const refresh = useCallback(async () => {
    setRefreshing(true)
    setError(null)

    try {
      const [nextAgents, latestSnapshots] = await Promise.all([
        fetchDashboardAgents(),
        fetchLatestSnapshots([], selectedMetrics),
      ])

      setAgents(nextAgents)
      mergeSnapshots(latestSnapshots)
      setLastUpdatedAt(Date.now())
    } catch (nextError) {
      setError(nextError instanceof Error ? nextError.message : 'Unable to load dashboard data')
    } finally {
      setLoading(false)
      setRefreshing(false)
    }
  }, [mergeSnapshots, selectedMetrics])

  useEffect(() => {
    const timeoutId = window.setTimeout(() => {
      void refresh()
    }, 0)

    return () => window.clearTimeout(timeoutId)
  }, [refresh])

  useEffect(() => {
    const intervalId = window.setInterval(() => {
      fetchDashboardAgents()
        .then(setAgents)
        .catch((nextError: unknown) => {
          setError(nextError instanceof Error ? nextError.message : 'Unable to refresh agents')
        })
    }, 15_000)

    return () => window.clearInterval(intervalId)
  }, [])

  useEffect(() => {
    connectionRef.current?.close()
    const connection = connectDashboardLive({
      metrics: selectedMetrics,
      onSnapshot: (snapshot) => {
        mergeSnapshots([snapshot])
        setAgents((current) =>
          current.map((agent) =>
            agent.id === snapshot.agentId
              ? {
                  ...agent,
                  online: true,
                }
              : agent,
          ),
        )
        setLastUpdatedAt(Date.now())
      },
      onStatusChange: setWsStatus,
      onError: setError,
    })

    connectionRef.current = connection

    return () => {
      connection.close()
      if (connectionRef.current === connection) {
        connectionRef.current = null
      }
    }
  }, [mergeSnapshots, selectedMetrics, selectedMetricsKey])

  const toggleMetric = useCallback((metric: MetricSection) => {
    setSelectedMetrics((current) => {
      if (current.includes(metric)) {
        return current.length === 1 ? current : current.filter((item) => item !== metric)
      }
      return [...current, metric].sort((a, b) => METRIC_SECTIONS.indexOf(a) - METRIC_SECTIONS.indexOf(b))
    })
  }, [])

  return {
    agents,
    snapshotsByAgent,
    historyByAgent,
    selectedMetrics,
    loading,
    refreshing,
    error,
    wsStatus,
    lastUpdatedAt,
    refresh,
    toggleMetric,
  }
}

function appendSnapshot(history: DashboardSnapshot[], snapshot: DashboardSnapshot): DashboardSnapshot[] {
  const withoutDuplicate = snapshot.snapshotId === null
    ? history
    : history.filter((item) => item.snapshotId !== snapshot.snapshotId)

  return [...withoutDuplicate, snapshot]
    .sort((a, b) => toTimestamp(a.capturedAt) - toTimestamp(b.capturedAt))
    .slice(-MAX_HISTORY_POINTS)
}

function toTimestamp(value: string | null): number {
  if (!value) {
    return 0
  }
  const timestamp = new Date(value).getTime()
  return Number.isNaN(timestamp) ? 0 : timestamp
}
