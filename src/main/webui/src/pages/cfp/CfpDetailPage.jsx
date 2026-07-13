import { useState } from 'react'
import { useParams, Link, useNavigate } from 'react-router-dom'
import { useCfp } from '../../hooks/useCfp.js'
import { useSessionProposals } from '../../hooks/useSessionProposals.js'
import { cfpApi } from '../../api/cfpApi.js'
import { sessionProposalApi } from '../../api/sessionProposalApi.js'
import Button from '../../components/ui/Button.jsx'
import LoadingSpinner from '../../components/ui/LoadingSpinner.jsx'
import ErrorAlert from '../../components/ui/ErrorAlert.jsx'

const STATUS_STYLES = {
  SUBMITTED:  'bg-gray-100 text-gray-700',
  APPROVED:   'bg-green-100 text-green-800',
  DECLINED:   'bg-red-100 text-red-700',
  WAITLISTED: 'bg-yellow-100 text-yellow-800',
}

const REVIEW_ACTIONS = [
  { status: 'APPROVED',   label: 'Approve',   variant: 'primary' },
  { status: 'WAITLISTED', label: 'Waitlist',  variant: 'secondary' },
  { status: 'DECLINED',   label: 'Decline',   variant: 'danger' },
]

function Row({ label, value }) {
  return (
    <div className="py-3 sm:grid sm:grid-cols-3 sm:gap-4 border-b border-gray-100 last:border-0">
      <dt className="text-sm font-medium text-gray-500">{label}</dt>
      <dd className="mt-1 text-sm text-gray-900 sm:col-span-2 sm:mt-0">{value ?? '—'}</dd>
    </div>
  )
}

function isOpen(cfp) {
  const today = new Date().toISOString().slice(0, 10)
  return today >= cfp.cfpOpens && today <= cfp.cfpCloses
}

function ProposalRow({ proposal, onReview }) {
  const [confirming, setConfirming] = useState(null)

  return (
    <div className="border border-gray-200 rounded-md p-4 space-y-3">
      <div className="flex items-start justify-between gap-4">
        <div className="min-w-0">
          <p className="text-sm font-medium text-gray-900 truncate">{proposal.title}</p>
          <p className="text-xs text-gray-500 mt-0.5">
            {proposal.presenter
              ? `${proposal.presenter.firstName} ${proposal.presenter.lastName}`
              : proposal.presenterEmail}
            {' · '}{proposal.conferenceTrack?.title || proposal.conferenceTrack?.trackCode}
            {' · '}{proposal.conferenceSessionFormat?.title}
          </p>
        </div>
        <span className={`shrink-0 text-xs font-medium px-2 py-0.5 rounded-full ${STATUS_STYLES[proposal.status] ?? STATUS_STYLES.SUBMITTED}`}>
          {proposal.status}
        </span>
      </div>

      {confirming ? (
        <div className="flex items-center justify-between bg-gray-50 border border-gray-200 rounded px-3 py-2 text-sm">
          <span className="text-gray-700">
            Mark as <strong>{confirming}</strong>?
          </span>
          <div className="flex gap-2">
            <Button variant="secondary" onClick={() => setConfirming(null)}>Cancel</Button>
            <Button
              variant={confirming === 'DECLINED' ? 'danger' : 'primary'}
              onClick={() => { onReview(proposal.id, confirming); setConfirming(null) }}
            >
              Confirm
            </Button>
          </div>
        </div>
      ) : (
        <div className="flex gap-2">
          {REVIEW_ACTIONS.filter(a => a.status !== proposal.status).map(({ status, label, variant }) => (
            <Button key={status} variant={variant} onClick={() => setConfirming(status)}>
              {label}
            </Button>
          ))}
        </div>
      )}
    </div>
  )
}

export default function CfpDetailPage() {
  const { id } = useParams()
  const navigate = useNavigate()
  const { cfp, loading, error } = useCfp(id)
  const { proposals, loading: proposalsLoading, error: proposalsError, reload: reloadProposals } = useSessionProposals(id)
  const [deleting, setDeleting] = useState(false)
  const [deleteError, setDeleteError] = useState(null)
  const [confirmDelete, setConfirmDelete] = useState(false)
  const [reviewError, setReviewError] = useState(null)

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

  async function handleReview(proposalId, status) {
    setReviewError(null)
    try {
      await sessionProposalApi.review(proposalId, status)
      reloadProposals()
    } catch (e) {
      setReviewError(e.message)
    }
  }

  if (loading) return <LoadingSpinner />
  if (error) return <ErrorAlert message={error} />
  if (!cfp) return null

  const open = isOpen(cfp)

  return (
    <div>
      <div className="flex items-start justify-between mb-6">
        <div>
          <Link to="/cfp" className="text-sm text-indigo-600 hover:underline">← Back to CFPs</Link>
          <div className="flex items-center gap-3 mt-2">
            <h1 className="text-2xl font-bold text-gray-900">{cfp.conferenceName}</h1>
            <span className={`text-xs font-medium px-2 py-0.5 rounded-full ${
              open ? 'bg-green-100 text-green-800' : 'bg-gray-100 text-gray-600'
            }`}>{open ? 'Open' : 'Closed'}</span>
          </div>
        </div>
        <div className="flex gap-2">
          <Button variant="secondary" onClick={() => navigate(`/cfp/${id}/edit`)}>Edit</Button>
          <Button variant="danger" onClick={() => setConfirmDelete(true)}>Delete</Button>
        </div>
      </div>

      {deleteError && <div className="mb-4"><ErrorAlert message={deleteError} onDismiss={() => setDeleteError(null)} /></div>}

      {confirmDelete && (
        <div className="mb-4 p-4 bg-red-50 border border-red-200 rounded-md flex items-center justify-between">
          <p className="text-sm text-red-700">Delete <strong>{cfp.conferenceName}</strong>? This cannot be undone.</p>
          <div className="flex gap-2">
            <Button variant="secondary" onClick={() => setConfirmDelete(false)}>Cancel</Button>
            <Button variant="danger" onClick={handleDelete} disabled={deleting}>
              {deleting ? 'Deleting…' : 'Confirm Delete'}
            </Button>
          </div>
        </div>
      )}

      <div className="bg-white rounded-lg border border-gray-200 divide-y divide-gray-100">
        <dl className="p-6 space-y-0">
          <Row label="Conference URL" value={<a href={cfp.conferenceUrl} target="_blank" rel="noreferrer" className="text-indigo-600 hover:underline">{cfp.conferenceUrl}</a>} />
          <Row label="Contact Email" value={cfp.contactEmailAddress?.address} />
          <Row label="CFP Opens" value={cfp.cfpOpens} />
          <Row label="CFP Closes" value={cfp.cfpCloses} />
          <Row label="Description" value={cfp.conferenceDescription} />
        </dl>

        <div className="p-6">
          <h2 className="text-sm font-semibold text-gray-700 mb-3">Session Formats</h2>
          {cfp.conferenceSessionFormats?.length === 0
            ? <p className="text-sm text-gray-400">None</p>
            : <div className="space-y-2">
                {cfp.conferenceSessionFormats?.map((f, i) => (
                  <div key={i} className="flex items-center gap-3 text-sm">
                    <span className="font-mono text-xs bg-gray-100 px-2 py-0.5 rounded">{f.formatCode}</span>
                    <span className="font-medium">{f.title}</span>
                    <span className="text-gray-400">{f.duration}</span>
                    {f.description && <span className="text-gray-500">— {f.description}</span>}
                  </div>
                ))}
              </div>
          }
        </div>

        <div className="p-6">
          <h2 className="text-sm font-semibold text-gray-700 mb-3">Tracks</h2>
          {cfp.conferenceTracks?.length === 0
            ? <p className="text-sm text-gray-400">None</p>
            : <div className="flex flex-wrap gap-2">
                {cfp.conferenceTracks?.map((t, i) => (
                  <span key={i} className="text-xs bg-indigo-50 text-indigo-700 border border-indigo-100 rounded px-2 py-1">
                    {t.title || t.trackCode}
                  </span>
                ))}
              </div>
          }
        </div>

        <div className="p-6">
          <h2 className="text-sm font-semibold text-gray-700 mb-3">
            Session Proposals
            {proposals.length > 0 && <span className="ml-2 text-xs font-normal text-gray-400">({proposals.length})</span>}
          </h2>
          {reviewError && <div className="mb-3"><ErrorAlert message={reviewError} onDismiss={() => setReviewError(null)} /></div>}
          {proposalsLoading && <LoadingSpinner message="Loading proposals…" />}
          {proposalsError && <ErrorAlert message={proposalsError} />}
          {!proposalsLoading && !proposalsError && proposals.length === 0 && (
            <p className="text-sm text-gray-400">No proposals submitted yet.</p>
          )}
          {proposals.length > 0 && (
            <div className="space-y-3">
              {proposals.map(p => (
                <ProposalRow key={p.id} proposal={p} onReview={handleReview} />
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  )
}
