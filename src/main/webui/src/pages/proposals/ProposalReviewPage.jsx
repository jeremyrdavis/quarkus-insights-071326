import { useState } from 'react'
import { useParams, Link, useNavigate } from 'react-router-dom'
import { useCfp } from '../../hooks/useCfp.js'
import { useSessionProposals } from '../../hooks/useSessionProposals.js'
import { sessionProposalApi } from '../../api/sessionProposalApi.js'
import LoadingSpinner from '../../components/ui/LoadingSpinner.jsx'
import ErrorAlert from '../../components/ui/ErrorAlert.jsx'
import StatusBadge from '../../components/ui/StatusBadge.jsx'
import { formatDuration } from '../../utils/duration.js'

function Meta({ label, value }) {
  return (
    <div className="flex items-center justify-between py-2.5 border-b border-white/[.06] last:border-0">
      <span className="font-mono text-[11px] uppercase tracking-[.06em] text-muted-500">{label}</span>
      <span className="text-[14px] text-white text-right">{value ?? '—'}</span>
    </div>
  )
}

function Section({ title, children }) {
  return (
    <div className="card">
      <div className="lbl mb-3">{title}</div>
      {children}
    </div>
  )
}

export default function ProposalReviewPage() {
  const { id, proposalId } = useParams()
  const navigate = useNavigate()
  const { cfp } = useCfp(id)
  const { proposals, loading, error } = useSessionProposals(id)
  const [reviewError, setReviewError] = useState(null)
  const [submitting, setSubmitting] = useState(false)

  const proposal = proposals.find(p => p.id === proposalId)

  async function review(status) {
    setReviewError(null)
    setSubmitting(true)
    try {
      await sessionProposalApi.review(proposalId, status)
      navigate(`/cfp/${id}`)
    } catch (e) {
      setReviewError(e.message)
      setSubmitting(false)
    }
  }

  if (loading) return <LoadingSpinner message="Loading proposal…" />
  if (error) return <ErrorAlert message={error} />
  if (!proposal) {
    return (
      <div>
        <ErrorAlert message="Proposal not found." />
        <Link to={`/cfp/${id}`} className="mt-4 inline-block text-brand-light">← Back to CFP</Link>
      </div>
    )
  }

  const presenter = proposal.presenter
  const presenterName = presenter ? `${presenter.firstName} ${presenter.lastName}` : null
  const presenterEmail = presenter?.emailAddress?.address
  const languages = proposal.programmingLanguagesUsed?.map(p => p.language).join(', ')
  const actions = [
    { status: 'ACCEPTED', label: 'Accept', cls: 'btn-primary w-full' },
    { status: 'WAITLISTED', label: 'Waitlist', cls: 'btn-ghost flex-1' },
    { status: 'DECLINED', label: 'Decline', cls: 'btn-danger flex-1' },
  ]

  return (
    <div>
      <Link to={`/cfp/${id}`} className="font-mono text-[12px] uppercase tracking-[.1em] text-brand-light">
        ← Back to CFP
      </Link>

      {/* Header */}
      <div className="mt-4 mb-8">
        <div className="flex items-center gap-3 mb-2">
          <h1 className="font-display font-extrabold text-[36px] leading-tight tracking-[-.02em] text-white">
            {proposal.title}
          </h1>
          <StatusBadge status={proposal.status} />
        </div>
        <p className="font-mono text-[13px] text-muted-400">
          {[presenterName, presenterEmail, cfp?.conferenceName].filter(Boolean).join(' · ')}
        </p>
      </div>

      <div className="grid gap-6 lg:grid-cols-[1fr_360px]">
        {/* Left: content */}
        <div className="space-y-6">
          <Section title="Abstract">
            <p className="text-[15px] leading-[1.65] text-muted-100 whitespace-pre-wrap">{proposal.description || '—'}</p>
          </Section>
          <Section title="Presentation Outline">
            <p className="text-[15px] leading-[1.65] text-muted-100 whitespace-pre-wrap">{proposal.presentationOutline || '—'}</p>
          </Section>
          <Section title="Prerequisite Knowledge">
            <p className="text-[15px] leading-[1.65] text-muted-100 whitespace-pre-wrap">{proposal.preRequisiteKnowledge || '—'}</p>
          </Section>
        </div>

        {/* Right: metadata + decision */}
        <div className="space-y-6">
          <div className="card">
            <div className="lbl mb-2">Details</div>
            <Meta label="Format" value={proposal.conferenceSessionFormat
              ? `${proposal.conferenceSessionFormat.title} (${formatDuration(proposal.conferenceSessionFormat.duration)})`
              : null} />
            <Meta label="Track" value={proposal.conferenceTrack?.title || proposal.conferenceTrack?.trackCode} />
            <Meta label="Level" value={proposal.level} />
            <Meta label="Language" value={proposal.language} />
            <Meta label="Prog. Languages" value={languages || null} />
          </div>

          <div className="card-accent">
            <div className="lbl mb-3">Review Decision</div>
            {reviewError && <div className="mb-3"><ErrorAlert message={reviewError} onDismiss={() => setReviewError(null)} /></div>}
            <textarea className="fld mb-4" rows={3} placeholder="Reviewer notes (optional)…" />
            <div className="flex flex-col gap-2.5">
              {actions.filter(a => a.status !== proposal.status).map(a => (
                a.status === 'ACCEPTED' ? (
                  <button key={a.status} className={a.cls} disabled={submitting} onClick={() => review(a.status)}>
                    {submitting ? 'Saving…' : a.label}
                  </button>
                ) : null
              ))}
              <div className="flex gap-2.5">
                {actions.filter(a => a.status !== proposal.status && a.status !== 'ACCEPTED').map(a => (
                  <button key={a.status} className={a.cls} disabled={submitting} onClick={() => review(a.status)}>
                    {a.label}
                  </button>
                ))}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}
