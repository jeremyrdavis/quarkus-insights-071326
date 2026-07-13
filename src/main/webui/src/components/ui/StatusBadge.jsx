const CLASS_BY_STATUS = {
  OPEN:       'badge-open',
  CLOSED:     'badge-closed',
  SUBMITTED:  'badge-submitted',
  ACCEPTED:   'badge-accepted',
  DECLINED:   'badge-declined',
  WAITLISTED: 'badge-waitlisted',
}

// Renders a proposal status (SUBMITTED/ACCEPTED/DECLINED/WAITLISTED) or a
// CFP open/closed state as a themed pill. Open gets a pulsing dot.
export default function StatusBadge({ status, label }) {
  const key = String(status ?? '').toUpperCase()
  const cls = CLASS_BY_STATUS[key] ?? 'badge-submitted'
  const text = label ?? (key ? key.charAt(0) + key.slice(1).toLowerCase() : '')
  return (
    <span className={cls}>
      {key === 'OPEN' && (
        <span
          className="w-[7px] h-[7px] rounded-full bg-success"
          style={{ boxShadow: '0 0 8px #3fe08f' }}
        />
      )}
      {text}
    </span>
  )
}
