import { Link } from 'react-router-dom'
import { useCfps } from '../../hooks/useCfps.js'
import { useReviewStats } from '../../hooks/useReviewStats.js'
import LoadingSpinner from '../../components/ui/LoadingSpinner.jsx'
import ErrorAlert from '../../components/ui/ErrorAlert.jsx'
import StatusBadge from '../../components/ui/StatusBadge.jsx'
import { formatDate, isOpen } from '../../utils/date.js'

function StatBox({ value, label, accent }) {
  const color = accent === 'pending' ? 'text-brand-light' : accent === 'approved' ? 'text-success' : 'text-white'
  return (
    <div className={`stat-box ${accent === 'pending' ? '!border-brand/30' : ''}`}>
      <div className={`font-display font-extrabold text-[24px] ${color}`}>{value}</div>
      <div className="mt-1 font-mono text-[10.5px] uppercase tracking-[.1em] text-muted-400">{label}</div>
    </div>
  )
}

export default function ReviewsPage() {
  const { cfps, loading, error, reload } = useCfps()
  const { statsById } = useReviewStats(cfps)

  return (
    <div>
      <div className="mb-7">
        <div className="kicker mb-2">Program Committee</div>
        <h1 className="font-display font-extrabold text-[34px] tracking-[-.02em] text-white">Review Proposals</h1>
      </div>

      {loading && <LoadingSpinner />}
      {error && <ErrorAlert message={error} onDismiss={reload} />}
      {!loading && !error && cfps.length === 0 && (
        <p className="text-muted-400 text-center py-16">No CFPs found.</p>
      )}

      <div className="grid gap-[22px] sm:grid-cols-2">
        {cfps.map(cfp => {
          const open = isOpen(cfp)
          const stats = statsById[cfp.id] ?? { total: 0, pending: 0, approved: 0 }
          return (
            <Link
              key={cfp.id}
              to={`/cfp/${cfp.id}`}
              className={`block rounded-2xl p-7 transition-shadow hover:shadow-card ${
                open ? 'bg-surface border border-brand/40 shadow-card' : 'bg-surface-closed border border-white/[.07]'
              }`}
            >
              <div className="flex items-start justify-between gap-3 mb-2">
                <h2 className={`font-display font-bold text-[23px] tracking-[-.01em] ${open ? 'text-white' : 'text-muted-100'}`}>
                  {cfp.conferenceName}
                </h2>
                <StatusBadge status={open ? 'OPEN' : 'CLOSED'} />
              </div>
              <p className="font-mono text-[12px] text-muted-400 mb-5">
                {formatDate(cfp.cfpOpens)} – {formatDate(cfp.cfpCloses)}
              </p>

              <div className="flex gap-2.5">
                <StatBox value={stats.total} label="Proposals" />
                <StatBox value={stats.pending} label="Pending" accent="pending" />
                <StatBox value={stats.approved} label="Accepted" accent="approved" />
              </div>

              <div className="mt-5 text-right">
                <span className={`font-display font-semibold text-[14px] ${open ? 'text-brand-light' : 'text-muted-300'}`}>
                  Review proposals →
                </span>
              </div>
            </Link>
          )
        })}
      </div>
    </div>
  )
}
