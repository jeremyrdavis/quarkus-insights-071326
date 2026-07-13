import { useState } from 'react'
import Button from '../ui/Button.jsx'
import ErrorAlert from '../ui/ErrorAlert.jsx'
import { formatDuration } from '../../utils/duration.js'

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

function Field({ label, value }) {
  return (
    <div className="py-3 sm:grid sm:grid-cols-3 sm:gap-4 border-b border-gray-100 last:border-0">
      <dt className="text-sm font-medium text-gray-500">{label}</dt>
      <dd className="mt-1 text-sm text-gray-900 sm:col-span-2 sm:mt-0 whitespace-pre-wrap">{value ?? '—'}</dd>
    </div>
  )
}

export default function ProposalReviewModal({ proposal, onClose, onReview, reviewError, submitting }) {
  const [confirming, setConfirming] = useState(null)

  const presenter = proposal.presenter
  const presenterName = presenter
    ? `${presenter.firstName} ${presenter.lastName}`
    : null
  const presenterEmail = presenter?.emailAddress?.address
  const languages = proposal.programmingLanguagesUsed?.map(p => p.language).join(', ')

  return (
    <div
      className="fixed inset-0 z-50 flex items-start justify-center overflow-y-auto bg-black/40 p-4 sm:p-8"
      onClick={onClose}
    >
      <div
        className="w-full max-w-2xl bg-white rounded-lg shadow-xl my-auto"
        onClick={e => e.stopPropagation()}
      >
        <div className="flex items-start justify-between gap-4 border-b border-gray-200 p-5">
          <div className="min-w-0">
            <h2 className="text-lg font-semibold text-gray-900">{proposal.title}</h2>
            <span className={`inline-block mt-1 text-xs font-medium px-2 py-0.5 rounded-full ${STATUS_STYLES[proposal.status] ?? STATUS_STYLES.SUBMITTED}`}>
              {proposal.status}
            </span>
          </div>
          <button
            onClick={onClose}
            className="shrink-0 text-gray-400 hover:text-gray-600 text-xl leading-none"
            aria-label="Close"
          >
            ✕
          </button>
        </div>

        <div className="p-5">
          {reviewError && <div className="mb-3"><ErrorAlert message={reviewError} /></div>}

          <dl>
            <Field label="Description" value={proposal.description} />
            <Field label="Format" value={proposal.conferenceSessionFormat
              ? `${proposal.conferenceSessionFormat.title} (${formatDuration(proposal.conferenceSessionFormat.duration)})`
              : null} />
            <Field label="Duration" value={formatDuration(proposal.conferenceSessionFormat?.duration)} />
            <Field label="Track" value={proposal.conferenceTrack?.title || proposal.conferenceTrack?.trackCode} />
            <Field label="Level" value={proposal.level} />
            <Field label="Language" value={proposal.language} />
            <Field label="Presenter" value={presenterName
              ? (presenterEmail ? `${presenterName} · ${presenterEmail}` : presenterName)
              : presenterEmail} />
            <Field label="Programming Languages" value={languages || null} />
            <Field label="Prerequisite Knowledge" value={proposal.preRequisiteKnowledge} />
            <Field label="Outline" value={proposal.presentationOutline} />
          </dl>
        </div>

        <div className="border-t border-gray-200 p-5">
          {confirming ? (
            <div className="flex items-center justify-between bg-gray-50 border border-gray-200 rounded px-3 py-2 text-sm">
              <span className="text-gray-700">Mark as <strong>{confirming}</strong>?</span>
              <div className="flex gap-2">
                <Button variant="secondary" onClick={() => setConfirming(null)} disabled={submitting}>Cancel</Button>
                <Button
                  variant={confirming === 'DECLINED' ? 'danger' : 'primary'}
                  onClick={() => onReview(proposal.id, confirming)}
                  disabled={submitting}
                >
                  {submitting ? 'Saving…' : 'Confirm'}
                </Button>
              </div>
            </div>
          ) : (
            <div className="flex justify-end gap-2">
              {REVIEW_ACTIONS.filter(a => a.status !== proposal.status).map(({ status, label, variant }) => (
                <Button key={status} variant={variant} onClick={() => setConfirming(status)}>
                  {label}
                </Button>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  )
}
