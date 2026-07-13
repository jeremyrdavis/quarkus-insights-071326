import { useState } from 'react'
import { useParams, Link, useNavigate } from 'react-router-dom'
import { useCfp } from '../../hooks/useCfp.js'
import { useSessionProposals } from '../../hooks/useSessionProposals.js'
import { cfpApi } from '../../api/cfpApi.js'
import Button from '../../components/ui/Button.jsx'
import LoadingSpinner from '../../components/ui/LoadingSpinner.jsx'
import ErrorAlert from '../../components/ui/ErrorAlert.jsx'
import StatusBadge from '../../components/ui/StatusBadge.jsx'
import { formatDuration } from '../../utils/duration.js'
import { formatDate, isOpen } from '../../utils/date.js'

function ProposalCard({ proposal, to }) {
  const presenter = proposal.presenter
  const presenterName = presenter ? `${presenter.firstName} ${presenter.lastName}` : null
  return (
    <Link
      to={to}
      className="block rounded-2xl border border-white/[.08] bg-surface p-5 hover:border-brand/40 transition-colors"
    >
      <div className="flex items-start justify-between gap-4">
        <div className="min-w-0">
          <p className="font-display font-semibold text-[16px] text-white truncate">{proposal.title}</p>
          <p className="mt-1 font-mono text-[12px] text-muted-400">
            {[
              presenterName,
              proposal.conferenceTrack?.trackCode || proposal.conferenceTrack?.title,
              proposal.conferenceSessionFormat?.title,
            ].filter(Boolean).join(' · ')}
          </p>
        </div>
        <StatusBadge status={proposal.status} />
      </div>
      <div className="mt-4 pt-4 border-t border-white/[.08] text-right">
        <span className="font-display font-semibold text-[14px] text-brand-light">Review →</span>
      </div>
    </Link>
  )
}

export default function CfpDetailPage() {
  const { id } = useParams()
  const navigate = useNavigate()
  const { cfp, loading, error } = useCfp(id)
  const { proposals, loading: proposalsLoading, error: proposalsError } = useSessionProposals(id)
  const [deleting, setDeleting] = useState(false)
  const [deleteError, setDeleteError] = useState(null)
  const [confirmDelete, setConfirmDelete] = useState(false)

  async function handleDelete() {
    setDeleting(true)
    try {
      await cfpApi.delete(id)
      navigate('/cfp')
    } catch (e) {
      setDeleteError(e.message)
    } finally {
      setDeleting(false)
      setConfirmDelete(false)
    }
  }

  if (loading) return <LoadingSpinner />
  if (error) return <ErrorAlert message={error} />
  if (!cfp) return null

  const open = isOpen(cfp)

  return (
    <div>
      <Link to="/cfp" className="font-mono text-[12px] uppercase tracking-[.1em] text-brand-light">
        ← Back to CFPs
      </Link>

      {/* Header */}
      <div className="flex items-start justify-between gap-4 mt-4 mb-8">
        <div>
          <div className="flex items-center gap-3 mb-2">
            <h1 className="font-display font-extrabold text-[44px] leading-none tracking-[-.02em] text-white">
              {cfp.conferenceName}
            </h1>
            <StatusBadge status={open ? 'OPEN' : 'CLOSED'} />
          </div>
          <p className="font-mono text-[13px] text-muted-400">
            {formatDate(cfp.cfpOpens)} – {formatDate(cfp.cfpCloses)}
          </p>
        </div>
        <div className="flex gap-2 shrink-0">
          <Button variant="secondary" onClick={() => navigate(`/cfp/${id}/edit`)}>Edit</Button>
          <Button variant="danger" onClick={() => setConfirmDelete(true)}>Delete</Button>
        </div>
      </div>

      {deleteError && <div className="mb-4"><ErrorAlert message={deleteError} onDismiss={() => setDeleteError(null)} /></div>}

      {confirmDelete && (
        <div className="mb-6 card-accent !border-danger/40 flex items-center justify-between">
          <p className="text-sm text-danger-light">Delete <strong>{cfp.conferenceName}</strong>? This cannot be undone.</p>
          <div className="flex gap-2">
            <Button variant="secondary" onClick={() => setConfirmDelete(false)}>Cancel</Button>
            <Button variant="danger" onClick={handleDelete} disabled={deleting}>
              {deleting ? 'Deleting…' : 'Confirm Delete'}
            </Button>
          </div>
        </div>
      )}

      <div className="grid gap-6 lg:grid-cols-[340px_1fr]">
        {/* Left sidebar */}
        <div className="space-y-6">
          <div className="card">
            <div className="lbl mb-3">About</div>
            <p className="text-[14.5px] leading-[1.6] text-muted-200">{cfp.conferenceDescription || '—'}</p>
            <a
              href={cfp.conferenceUrl}
              target="_blank"
              rel="noreferrer"
              className="mt-4 inline-block font-mono text-[12.5px] text-brand-light break-all"
            >
              {cfp.conferenceUrl} →
            </a>
          </div>

          <div className="card">
            <div className="lbl mb-3">Session Formats</div>
            {cfp.conferenceSessionFormats?.length
              ? <div className="space-y-3">
                  {cfp.conferenceSessionFormats.map((f, i) => (
                    <div key={i}>
                      <div className="flex items-baseline justify-between gap-2">
                        <span className="font-display font-semibold text-[14.5px] text-white">{f.title}</span>
                        <span className="font-mono text-[12px] text-muted-400 shrink-0">{formatDuration(f.duration)}</span>
                      </div>
                      {f.description && <p className="text-[13px] text-muted-400 mt-0.5">{f.description}</p>}
                    </div>
                  ))}
                </div>
              : <p className="text-sm text-muted-500">None</p>}
          </div>

          <div className="card">
            <div className="lbl mb-3">Tracks</div>
            {cfp.conferenceTracks?.length
              ? <div className="flex flex-wrap gap-[7px]">
                  {cfp.conferenceTracks.map((t, i) => (
                    <span key={i} className="track-tag">{t.trackCode || t.title}</span>
                  ))}
                </div>
              : <p className="text-sm text-muted-500">None</p>}
          </div>
        </div>

        {/* Right column: proposals */}
        <div>
          <div className="flex items-center gap-2 mb-4">
            <h2 className="font-display font-bold text-[20px] text-white">Session Proposals</h2>
            {proposals.length > 0 && <span className="font-mono text-[13px] text-muted-400">({proposals.length})</span>}
          </div>

          {proposalsLoading && <LoadingSpinner message="Loading proposals…" />}
          {proposalsError && <ErrorAlert message={proposalsError} />}
          {!proposalsLoading && !proposalsError && proposals.length === 0 && (
            <div className="card text-muted-400 text-sm">No proposals submitted yet.</div>
          )}

          <div className="space-y-3">
            {proposals.map(p => (
              <ProposalCard key={p.id} proposal={p} to={`/cfp/${id}/proposals/${p.id}`} />
            ))}
          </div>
        </div>
      </div>
    </div>
  )
}
