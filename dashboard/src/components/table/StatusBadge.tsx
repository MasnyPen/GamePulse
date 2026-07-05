interface StatusBadgeProps {
  online: boolean
}

export function StatusBadge({ online }: StatusBadgeProps) {
  return (
    <span
      className={`inline-flex items-center gap-2 rounded-full border px-2.5 py-1 text-xs font-semibold ${
        online
          ? 'border-emerald-400/40 bg-emerald-400/10 text-emerald-200'
          : 'border-rose-400/40 bg-rose-500/10 text-rose-200'
      }`}
    >
      <span className={`h-1.5 w-1.5 rounded-full ${online ? 'bg-emerald-300' : 'bg-rose-300'}`} />
      {online ? 'Online' : 'Offline'}
    </span>
  )
}
