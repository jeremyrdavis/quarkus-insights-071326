const MONTHS = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec']

// "2026-09-30" -> "Sep 30, 2026". Falls back to the raw value if unparseable.
export function formatDate(iso) {
  if (!iso) return '—'
  const m = /^(\d{4})-(\d{2})-(\d{2})/.exec(iso)
  if (!m) return iso
  const [, y, mo, d] = m
  return `${MONTHS[parseInt(mo, 10) - 1]} ${parseInt(d, 10)}, ${y}`
}

// "2026-01-01" + "2026-03-31" -> "Jan 1 – Mar 31, 2026"
export function formatDateRange(fromIso, toIso) {
  if (!fromIso || !toIso) return `${formatDate(fromIso)} – ${formatDate(toIso)}`
  return `${formatDate(fromIso)} – ${formatDate(toIso)}`
}

export function isOpen(cfp) {
  if (!cfp) return false
  const today = new Date().toISOString().slice(0, 10)
  return today >= cfp.cfpOpens && today <= cfp.cfpCloses
}
