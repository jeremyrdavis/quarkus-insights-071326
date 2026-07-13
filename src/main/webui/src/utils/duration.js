// The backend models format duration as a java.time.Duration. Depending on the
// Jackson config it can arrive as an ISO-8601 string ("PT45M", "PT1H30M") or as
// a numeric number of seconds. Handle both so the UI can render minutes.
export function durationToMinutes(d) {
  if (d == null) return null
  if (typeof d === 'number') return Math.round(d / 60)
  if (typeof d === 'string') {
    const m = /^PT(?:(\d+)H)?(?:(\d+)M)?(?:(\d+)S)?$/i.exec(d.trim())
    if (m && (m[1] || m[2] || m[3])) {
      const hours = parseInt(m[1] || '0', 10)
      const mins = parseInt(m[2] || '0', 10)
      const secs = parseInt(m[3] || '0', 10)
      return hours * 60 + mins + Math.round(secs / 60)
    }
    const n = Number(d)
    if (!Number.isNaN(n)) return Math.round(n / 60)
  }
  return null
}

export function formatDuration(d) {
  const mins = durationToMinutes(d)
  return mins == null ? '—' : `${mins} min`
}

// Convert a whole number of minutes to the ISO-8601 duration the backend expects.
export function minutesToDuration(mins) {
  const n = parseInt(mins, 10)
  if (Number.isNaN(n) || n <= 0) return ''
  return `PT${n}M`
}
