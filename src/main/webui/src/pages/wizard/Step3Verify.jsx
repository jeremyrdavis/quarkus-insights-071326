import Button from '../../components/ui/Button.jsx'
import ErrorAlert from '../../components/ui/ErrorAlert.jsx'
import { formatDuration } from '../../utils/duration.js'

function Row({ label, value }) {
  return (
    <div className="py-2.5 sm:grid sm:grid-cols-3 sm:gap-4 border-b border-white/[.06] last:border-0">
      <dt className="text-sm font-medium text-muted-500">{label}</dt>
      <dd className="mt-1 text-sm text-muted-100 sm:col-span-2 sm:mt-0 whitespace-pre-wrap">{value ?? '—'}</dd>
    </div>
  )
}

export default function Step3Verify({ wizardData, onBack, onSubmit, submitting, submitError }) {
  const { presenter, session } = wizardData

  return (
    <div className="space-y-6">
      <div>
        <h2 className="font-display font-semibold text-[18px] text-white">Step 3: Review Your Submission</h2>
        <p className="text-sm text-muted-400 mt-1">Check everything below, then click Submit to send your proposal.</p>
      </div>

      {submitError && <ErrorAlert message={submitError} />}

      <section>
        <h3 className="font-mono text-[11px] font-semibold text-brand-light uppercase tracking-[.1em] mb-2">Presenter</h3>
        <dl className="bg-surface-2 rounded-[10px] border border-white/[.08] px-4">
          <Row label="Name" value={`${presenter.firstName} ${presenter.lastName}`} />
          <Row label="Email" value={presenter.email} />
        </dl>
      </section>

      <section>
        <h3 className="font-mono text-[11px] font-semibold text-brand-light uppercase tracking-[.1em] mb-2">Session</h3>
        <dl className="bg-surface-2 rounded-[10px] border border-white/[.08] px-4">
          <Row label="Title" value={session.title} />
          <Row label="Abstract" value={session.description} />
          <Row label="Format" value={`${session.conferenceSessionFormat.title} (${formatDuration(session.conferenceSessionFormat.duration)})`} />
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
