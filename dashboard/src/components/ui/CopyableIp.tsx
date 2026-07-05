import { useRef, useState, type MouseEvent } from 'react'
import { copyTextToClipboard } from '../../lib/clipboard'

interface CopyableIpProps {
  value: string | null | undefined
  className?: string
  stopPropagation?: boolean
}

type CopyState = 'idle' | 'copied' | 'failed'

export function CopyableIp({ value, className = '', stopPropagation = false }: CopyableIpProps) {
  const [copyState, setCopyState] = useState<CopyState>('idle')
  const resetTimer = useRef<number | null>(null)
  const ipAddress = value?.trim()

  if (!ipAddress) {
    return <span className={className}>-</span>
  }

  async function handleCopy(event: MouseEvent<HTMLButtonElement>): Promise<void> {
    if (stopPropagation) {
      event.stopPropagation()
    }

    if (!ipAddress) {
      return
    }

    const copied = await copyTextToClipboard(ipAddress)
    setCopyState(copied ? 'copied' : 'failed')

    if (resetTimer.current !== null) {
      window.clearTimeout(resetTimer.current)
    }

    resetTimer.current = window.setTimeout(() => {
      setCopyState('idle')
      resetTimer.current = null
    }, 1400)
  }

  const label = copyState === 'copied' ? 'Copied' : copyState === 'failed' ? 'Copy failed' : 'Copy'

  return (
    <button
      className={`group inline-flex max-w-full items-center gap-2 rounded-lg border border-transparent px-1.5 py-1 text-left font-mono text-cyan-200 transition hover:border-cyan-400/40 hover:bg-cyan-400/10 hover:text-cyan-50 focus:outline-none focus:ring-2 focus:ring-cyan-300/50 ${className}`}
      type="button"
      title={copyState === 'copied' ? `Copied ${ipAddress}` : `Copy IP ${ipAddress}`}
      onClick={handleCopy}
    >
      <span className="min-w-0 truncate">{ipAddress}</span>
      <span
        className={`shrink-0 rounded-full border px-2 py-0.5 text-[10px] font-semibold uppercase tracking-[0.12em] transition ${
          copyState === 'copied'
            ? 'border-emerald-400/40 bg-emerald-400/10 text-emerald-200 opacity-100'
            : copyState === 'failed'
              ? 'border-rose-400/40 bg-rose-400/10 text-rose-200 opacity-100'
              : 'border-slate-700 bg-slate-900 text-slate-400 opacity-0 group-hover:opacity-100 group-focus:opacity-100'
        }`}
      >
        {label}
      </span>
    </button>
  )
}
