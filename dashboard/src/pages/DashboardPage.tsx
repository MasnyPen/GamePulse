import { useMemo, useState } from 'react'
import { AgentDetailsModal } from '../components/modal/AgentDetailsModal'
import { StatCards } from '../components/stats/StatCards'
import { AgentsTable } from '../components/table/AgentsTable'
import { useDashboardData } from '../hooks/useDashboardData'
import { buildDashboardSummary } from '../lib/metrics'
import type { DashboardAgent } from '../types/dashboard'

export function DashboardPage() {
  const {
    agents,
    snapshotsByAgent,
    historyByAgent,
    loading,
    error,
  } = useDashboardData()
  const [selectedAgent, setSelectedAgent] = useState<DashboardAgent | null>(null)

  const summary = useMemo(() => buildDashboardSummary(agents, snapshotsByAgent), [agents, snapshotsByAgent])

  return (
    <main className="min-h-screen bg-slate-950 px-3 py-4 text-slate-200 sm:px-5 lg:px-8 lg:py-8">
      <div className="mx-auto flex max-w-[1800px] flex-col gap-6">
        {error ? (
          <div className="rounded-2xl border border-rose-400/30 bg-rose-400/10 px-4 py-3 text-sm text-rose-100">
            <strong className="font-semibold">API error:</strong> {error}
          </div>
        ) : null}

        <StatCards summary={summary} />

        <AgentsTable
          agents={agents}
          snapshotsByAgent={snapshotsByAgent}
          loading={loading}
          onSelectAgent={setSelectedAgent}
        />
      </div>

      {selectedAgent ? (
        <AgentDetailsModal
          agent={selectedAgent}
          snapshot={snapshotsByAgent[selectedAgent.id]}
          history={historyByAgent[selectedAgent.id] ?? []}
          onClose={() => setSelectedAgent(null)}
        />
      ) : null}
    </main>
  )
}
