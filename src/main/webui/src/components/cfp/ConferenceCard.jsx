import { Link } from 'react-router-dom'
import StatusBadge from '../ui/StatusBadge.jsx'
import { formatDate, isOpen } from '../../utils/date.js'

export default function ConferenceCard({ cfp }) {
  const open = isOpen(cfp)
  const tracks = cfp.conferenceTracks ?? []
  const formats = cfp.conferenceSessionFormats ?? []
  const shownTracks = tracks.slice(0, 3)
  const extra = tracks.length - shownTracks.length

  return (
    <Link
      to={`/cfp/${cfp.id}`}
      className={`block rounded-2xl p-[30px] transition-shadow hover:shadow-card ${
        open
          ? 'bg-surface border border-brand/40 shadow-card'
          : 'bg-surface-closed border border-white/[.07]'
      }`}
    >
      <div className="flex items-center justify-between mb-[18px]">
        <StatusBadge status={open ? 'OPEN' : 'CLOSED'} />
        <span className="font-mono text-[12px] text-muted-400">
          {open ? `closes ${formatDate(cfp.cfpCloses)}` : formatDate(cfp.cfpCloses)}
        </span>
      </div>

      <h3
        className={`font-display font-bold text-[26px] tracking-[-.01em] mb-2.5 ${
          open ? 'text-white' : 'text-muted-100'
        }`}
      >
        {cfp.conferenceName}
      </h3>

      {cfp.conferenceDescription && (
        <p className={`text-[14.5px] leading-[1.55] mb-[22px] ${open ? 'text-muted-200' : 'text-muted-300'}`}>
          {cfp.conferenceDescription}
        </p>
      )}

      {shownTracks.length > 0 && (
        <div className="flex flex-wrap gap-[7px] mb-6">
          {shownTracks.map((t, i) => (
            <span key={i} className="track-tag">{t.trackCode || t.title}</span>
          ))}
          {extra > 0 && (
            <span className="inline-flex items-center rounded-md bg-white/[.05] px-2 py-1 font-mono text-[11px] text-muted-500">
              +{extra}
            </span>
          )}
        </div>
      )}

      <div className="flex items-center justify-between pt-[18px] border-t border-white/[.08]">
        <span className="font-mono text-[12px] text-muted-400">
          {formats.length} formats · {tracks.length} tracks
        </span>
        <span className={`font-display font-semibold text-[14px] ${open ? 'text-brand-light' : 'text-muted-300'}`}>
          View CFP →
        </span>
      </div>
    </Link>
  )
}
