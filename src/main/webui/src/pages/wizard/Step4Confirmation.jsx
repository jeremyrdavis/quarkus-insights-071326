import { Link } from 'react-router-dom'
import Button from '../../components/ui/Button.jsx'

export default function Step4Confirmation({ wizardData, onReset }) {
  const { session, presenter } = wizardData

  return (
    <div className="text-center space-y-6 py-8">
      <div className="inline-flex items-center justify-center w-16 h-16 rounded-full bg-success/15">
        <svg className="w-8 h-8 text-success" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
        </svg>
      </div>

      <div>
        <h2 className="font-display font-extrabold text-[26px] text-white">Proposal Submitted!</h2>
        <p className="text-muted-300 mt-2">
          <strong className="text-muted-100">{session.title}</strong> has been submitted for {presenter.firstName} {presenter.lastName}.
        </p>
      </div>

      <div className="bg-surface-2 rounded-[10px] border border-white/[.08] p-4 text-sm text-left max-w-md mx-auto">
        <dl className="space-y-2">
          <div className="flex justify-between">
            <dt className="text-muted-500">Format</dt>
            <dd className="font-medium text-white">{session.conferenceSessionFormat.title}</dd>
          </div>
          <div className="flex justify-between">
            <dt className="text-muted-500">Track</dt>
            <dd className="font-medium text-white">{session.conferenceTrack.title || session.conferenceTrack.trackCode}</dd>
          </div>
          <div className="flex justify-between">
            <dt className="text-muted-500">Level</dt>
            <dd className="font-medium text-white">{session.level}</dd>
          </div>
        </dl>
      </div>

      <div className="flex flex-col sm:flex-row items-center justify-center gap-3 pt-2">
        <Button onClick={onReset}>Submit Another Proposal</Button>
        <Link to="/cfp">
          <Button variant="secondary">View All CFPs</Button>
        </Link>
      </div>
    </div>
  )
}
