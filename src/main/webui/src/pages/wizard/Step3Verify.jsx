import Button from '../../components/ui/Button.jsx'
import ErrorAlert from '../../components/ui/ErrorAlert.jsx'

function Row({ label, value }) {
  return (
    <div className="py-2 sm:grid sm:grid-cols-3 sm:gap-4 border-b border-gray-100 last:border-0">
      <dt className="text-sm font-medium text-gray-500">{label}</dt>
      <dd className="mt-1 text-sm text-gray-900 sm:col-span-2 sm:mt-0">{value ?? '—'}</dd>
    </div>
  )
}

export default function Step3Verify({ wizardData, onBack, onSubmit, submitting, submitError }) {
  const { presenter, session } = wizardData

  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-lg font-semibold text-gray-900">Step 3: Review Your Submission</h2>
        <p className="text-sm text-gray-500 mt-1">Check everything below, then click Submit to send your proposal.</p>
      </div>

      {submitError && <ErrorAlert message={submitError} />}

      <section>
        <h3 className="text-sm font-semibold text-indigo-700 uppercase tracking-wide mb-2">Presenter</h3>
        <dl className="bg-white rounded-md border border-gray-200 px-4 divide-y divide-gray-100">
          <Row label="Name" value={`${presenter.firstName} ${presenter.lastName}`} />
          <Row label="Email" value={presenter.email} />
        </dl>
      </section>

      <section>
        <h3 className="text-sm font-semibold text-indigo-700 uppercase tracking-wide mb-2">Session</h3>
        <dl className="bg-white rounded-md border border-gray-200 px-4 divide-y divide-gray-100">
          <Row label="Title" value={session.title} />
          <Row label="Abstract" value={session.description} />
          <Row label="Format" value={`${session.conferenceSessionFormat.title} (${session.conferenceSessionFormat.formatCode})`} />
          <Row label="Track" value={session.conferenceTrack.title || session.conferenceTrack.trackCode} />
          <Row label="Level" value={session.level} />
          <Row label="Language" value={session.language} />
          <Row label="Outline" value={session.presentationOutline} />
          <Row label="Programming Languages" value={session.programmingLanguagesUsed.map(p => p.language).join(', ')} />
          {session.preRequisiteKnowledge && <Row label="Prerequisites" value={session.preRequisiteKnowledge} />}
        </dl>
      </section>

      <div className="flex justify-between pt-2">
        <Button type="button" variant="secondary" onClick={onBack} disabled={submitting}>← Back</Button>
        <Button onClick={onSubmit} disabled={submitting}>
          {submitting ? 'Submitting…' : 'Submit Proposal ✓'}
        </Button>
      </div>
    </div>
  )
}
