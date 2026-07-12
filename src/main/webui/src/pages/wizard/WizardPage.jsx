import { useState, useEffect } from 'react'
import { cfpApi } from '../../api/cfpApi.js'
import { sessionProposalApi } from '../../api/sessionProposalApi.js'
import { inputClass } from '../../components/ui/FormField.jsx'
import LoadingSpinner from '../../components/ui/LoadingSpinner.jsx'
import ErrorAlert from '../../components/ui/ErrorAlert.jsx'
import Step1Presenter from './Step1Presenter.jsx'
import Step2Session from './Step2Session.jsx'
import Step3Verify from './Step3Verify.jsx'
import Step4Confirmation from './Step4Confirmation.jsx'

const STEP_LABELS = ['Presenter', 'Session', 'Review', 'Done']

function StepIndicator({ step }) {
  return (
    <nav aria-label="Progress" className="mb-8">
      <ol className="flex items-center gap-2">
        {STEP_LABELS.map((label, i) => {
          const n = i + 1
          const done = step > n
          const active = step === n
          return (
            <li key={label} className="flex items-center gap-2">
              {i > 0 && <div className="h-px w-8 bg-gray-300" />}
              <span className={[
                'inline-flex items-center gap-1.5 text-sm font-medium',
                done ? 'text-indigo-600' : active ? 'text-indigo-700' : 'text-gray-400',
              ].join(' ')}>
                <span className={[
                  'inline-flex h-6 w-6 items-center justify-center rounded-full text-xs font-bold',
                  done ? 'bg-indigo-600 text-white' : active ? 'ring-2 ring-indigo-600 text-indigo-700' : 'bg-gray-200 text-gray-500',
                ].join(' ')}>
                  {done ? '✓' : n}
                </span>
                {label}
              </span>
            </li>
          )
        })}
      </ol>
    </nav>
  )
}

export default function WizardPage() {
  const [cfps, setCfps] = useState([])
  const [cfpsLoading, setCfpsLoading] = useState(true)
  const [cfpsError, setCfpsError] = useState(null)

  const [cfpId, setCfpId] = useState('')
  const [step, setStep] = useState(0)
  const [wizardData, setWizardData] = useState({ presenter: null, session: null })
  const [submitting, setSubmitting] = useState(false)
  const [submitError, setSubmitError] = useState(null)

  useEffect(() => {
    cfpApi.list()
      .then(data => {
        setCfps(data ?? [])
        if (data?.length === 1) setCfpId(data[0].id)
      })
      .catch(e => setCfpsError(e.message))
      .finally(() => setCfpsLoading(false))
  }, [])

  function handleCfpSelect(e) {
    setCfpId(e.target.value)
  }

  function startWizard() {
    if (!cfpId) return
    setStep(1)
  }

  function handlePresenterNext(presenter) {
    setWizardData(prev => ({ ...prev, presenter }))
    setStep(2)
  }

  function handleSessionNext(session) {
    setWizardData(prev => ({ ...prev, session }))
    setStep(3)
  }

  async function handleSubmit() {
    const { presenter, session } = wizardData
    setSubmitting(true)
    setSubmitError(null)
    try {
      await sessionProposalApi.create({
        cfpId,
        title: session.title,
        description: session.description,
        conferenceSessionFormat: session.conferenceSessionFormat,
        conferenceTrack: session.conferenceTrack,
        level: session.level,
        language: session.language,
        presenterEmail: presenter.email,
        presentationOutline: session.presentationOutline,
        programmingLanguagesUsed: session.programmingLanguagesUsed,
        preRequisiteKnowledge: session.preRequisiteKnowledge,
      })
      setStep(4)
    } catch (e) {
      setSubmitError(e.message)
    } finally {
      setSubmitting(false)
    }
  }

  function handleReset() {
    setWizardData({ presenter: null, session: null })
    setSubmitError(null)
    setStep(1)
  }

  if (cfpsLoading) return <LoadingSpinner message="Loading CFPs…" />
  if (cfpsError) return <ErrorAlert message={cfpsError} />

  return (
    <div className="max-w-2xl mx-auto">
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-gray-900">Submit a Session Proposal</h1>
      </div>

      {step === 0 && (
        <div className="bg-white rounded-lg border border-gray-200 p-6 space-y-6">
          <div>
            <h2 className="text-lg font-semibold text-gray-900">Select a CFP</h2>
            <p className="text-sm text-gray-500 mt-1">Choose the conference you are submitting to.</p>
          </div>

          {cfps.length === 0 ? (
            <p className="text-sm text-gray-500">No open CFPs found.</p>
          ) : (
            <div className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Conference <span className="text-red-500">*</span>
                </label>
                <select
                  value={cfpId}
                  onChange={handleCfpSelect}
                  className={inputClass(!cfpId)}
                >
                  <option value="">— Select a CFP —</option>
                  {cfps.map(c => (
                    <option key={c.id} value={c.id}>
                      {c.conferenceName} (CFP closes {c.cfpCloses})
                    </option>
                  ))}
                </select>
              </div>
              <div className="flex justify-end">
                <button
                  onClick={startWizard}
                  disabled={!cfpId}
                  className="inline-flex items-center px-4 py-2 text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  Start Submission →
                </button>
              </div>
            </div>
          )}
        </div>
      )}

      {step >= 1 && step <= 3 && (
        <div className="bg-white rounded-lg border border-gray-200 p-6">
          <StepIndicator step={step} />

          {step === 1 && (
            <Step1Presenter
              initial={wizardData.presenter}
              onNext={handlePresenterNext}
            />
          )}

          {step === 2 && (
            <Step2Session
              cfpId={cfpId}
              initial={wizardData.session}
              onNext={handleSessionNext}
              onBack={() => setStep(1)}
            />
          )}

          {step === 3 && (
            <Step3Verify
              wizardData={wizardData}
              onBack={() => setStep(2)}
              onSubmit={handleSubmit}
              submitting={submitting}
              submitError={submitError}
            />
          )}
        </div>
      )}

      {step === 4 && (
        <div className="bg-white rounded-lg border border-gray-200 p-6">
          <Step4Confirmation wizardData={wizardData} onReset={handleReset} />
        </div>
      )}
    </div>
  )
}
