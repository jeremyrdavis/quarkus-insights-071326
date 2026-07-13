import { useState, useEffect } from 'react'
import { useForm } from 'react-hook-form'
import { presenterApi } from '../../api/presenterApi.js'
import { sessionProposalApi } from '../../api/sessionProposalApi.js'
import Button from '../../components/ui/Button.jsx'
import FormField, { inputClass } from '../../components/ui/FormField.jsx'
import ErrorAlert from '../../components/ui/ErrorAlert.jsx'

const EMAIL_RE = /^[^\s@]+@[^\s@]+\.[^\s@]+$/

export default function Step1Presenter({ cfpId, initial, onNext }) {
  const { register, handleSubmit, watch, setValue, getValues, formState: { errors } } = useForm({
    defaultValues: {
      firstName: initial?.firstName ?? '',
      lastName: initial?.lastName ?? '',
      email: initial?.email ?? '',
    },
  })

  const [apiError, setApiError] = useState(null)
  const [alreadySubmitted, setAlreadySubmitted] = useState(false)
  const [foundId, setFoundId] = useState(initial?.id ?? null)
  const [submitting, setSubmitting] = useState(false)

  const email = watch('email')

  // Validate the email in the background: look up the presenter (to silently
  // prefill their name) and check whether they have already submitted a talk
  // for this conference. Only the prior-submission notice is surfaced.
  useEffect(() => {
    if (!EMAIL_RE.test(email)) {
      setAlreadySubmitted(false)
      setFoundId(null)
      return
    }
    let cancelled = false
    const timer = setTimeout(async () => {
      try {
        const presenter = await presenterApi.getByEmail(email)
        if (!cancelled && presenter) {
          setFoundId(presenter.id ?? null)
          if (!getValues('firstName') && presenter.firstName) setValue('firstName', presenter.firstName)
          if (!getValues('lastName') && presenter.lastName) setValue('lastName', presenter.lastName)
        }
      } catch (e) {
        if (!cancelled && e.status === 404) setFoundId(null)
        // any other background lookup error is ignored — validation is silent
      }

      if (cfpId) {
        try {
          const proposals = await sessionProposalApi.list(cfpId)
          if (!cancelled) {
            const submitted = (proposals ?? []).some(p => {
              const addr = p.presenter?.emailAddress?.address
              return addr && addr.toLowerCase() === email.toLowerCase()
            })
            setAlreadySubmitted(submitted)
          }
        } catch {
          // ignore — the notice is best-effort
        }
      }
    }, 400)

    return () => { cancelled = true; clearTimeout(timer) }
  }, [email, cfpId, getValues, setValue])

  async function proceed(data) {
    setSubmitting(true)
    setApiError(null)
    try {
      let id = foundId
      if (!id) {
        try {
          const created = await presenterApi.create({
            email: data.email,
            firstName: data.firstName,
            lastName: data.lastName,
          })
          id = created.id
        } catch (e) {
          // Presenter may already exist — fall back to a lookup.
          const existing = await presenterApi.getByEmail(data.email).catch(() => null)
          id = existing?.id ?? null
          if (!id) throw e
        }
      }
      onNext({ id, email: data.email, firstName: data.firstName, lastName: data.lastName })
    } catch (e) {
      setApiError(e.message)
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <form onSubmit={handleSubmit(proceed)} className="space-y-6">
      <div>
        <h2 className="text-lg font-semibold text-gray-900">Step 1: Presenter</h2>
        <p className="text-sm text-gray-500 mt-1">Tell us who you are.</p>
      </div>

      {apiError && <ErrorAlert message={apiError} onDismiss={() => setApiError(null)} />}

      <div className="grid grid-cols-2 gap-4">
        <FormField label="First Name" error={errors.firstName?.message} required>
          <input
            className={inputClass(!!errors.firstName)}
            {...register('firstName', { required: 'Required' })}
          />
        </FormField>
        <FormField label="Last Name" error={errors.lastName?.message} required>
          <input
            className={inputClass(!!errors.lastName)}
            {...register('lastName', { required: 'Required' })}
          />
        </FormField>
      </div>

      <FormField label="Email Address" error={errors.email?.message} required>
        <input
          type="email"
          className={inputClass(!!errors.email)}
          {...register('email', {
            required: 'Email is required',
            pattern: { value: EMAIL_RE, message: 'Enter a valid email address' },
          })}
        />
      </FormField>

      {alreadySubmitted && (
        <div className="bg-yellow-50 border border-yellow-200 rounded-md p-3">
          <p className="text-sm text-yellow-800">
            You have already submitted a talk for this conference. You can still submit another.
          </p>
        </div>
      )}

      <div className="flex justify-end">
        <Button type="submit" disabled={submitting}>
          {submitting ? 'Please wait…' : 'Continue →'}
        </Button>
      </div>
    </form>
  )
}
