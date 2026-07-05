import type { DashboardAgent, DashboardSnapshot } from '../types/dashboard'
import { compactId } from './formatters'

export function getAgentHostname(agent: DashboardAgent, snapshot?: DashboardSnapshot): string {
  return snapshot?.system?.hostname ?? agent.hostname ?? compactId(agent.id)
}
