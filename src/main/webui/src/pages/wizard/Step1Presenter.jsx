import { useState } from 'react'
import { useForm } from 'react-hook-form'
import { presenterApi } from '../../api/presenterApi.js'
import Button from '../../components/ui/Button.jsx'
import FormField, { inputClass } from '../../components/ui/FormField.jsx'
import ErrorAlert from '../../components/ui/ErrorAlert.jsx'

export default function Step1Presenter({ initial, onNext }) {
  const [mode, setMode] = useState(initial?.email ? 'existing' : 'lookup')
  const [found, setFound] = useState(initial ?? null)
  const [looking, setLooking] = useState(false)
  const [creating, setCreating] = useState(false)
  const [apiError, setApiError] = useState(null)

  const { register, handleSubmit, getValues, formState: { errors } } = useForm({
    defaultValues: { email: initial?.email ?? '', firstName: initial?.firstName ?? '', lastName: initial?.lastName ?? '' },
  })

  async function lookup() {
    const email = getValues('email')
    if (!email) return
    setLooking(true)
    setApiError(null)
    try {
      const presenter = await presenterApi.getByEmail(email)
      setFound(presenter)
      setMode('existing')
    } catch (e) {
      if (e.status === 404) {
        setMode('create')
      } else {
        setApiError(e.message)
      }
    } finally {
      setLooking(false)
    }
  }

  async function create(data) {
    setCreating(true)
    setApiError(null)
    try {
      const presenter = await presenterApi.create({ email: data.email, firstName: data.firstName, lastName: data.lastName })
      onNext({ id: presenter.id, email: presenter.emailAddress.address, firstName: presenter.firstName, lastName: presenter.lastName })
    } catch (e) {
      setApiError(e.message)
    } finally {
      setCreating(false)
    }
  }

  function confirmExisting() {
    onNext({
      id: found.id,
      email: found.emailAddress?.address ?? found.email,
      firstName: found.firstName,
      lastName: found.lastName,
    })
  }

  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-lg font-semibold text-gray-900">Step 1: Presenter</h2>
        <p className="text-sm text-gray-500 mt-1">Enter your email address to look up your profile or create a new one.</p>
      </div>

      {apiError && <ErrorAlert message={apiError} onDismiss={() => setApiError(null)} />}

      <div className="flex gap-3 items-end">
        <div className="flex-1">
          <FormField label="Email Address" error={errors.email?.message} required>
            <input
              type="email"
              className={inputClass(!!errors.email)}
              {...register('email', { required: 'Email is required' })}
              disabled={mode !== 'lookup'}
            />
          </FormField>
        </div>
        {mode === 'lookup' && (
          <Button type="button" variant="secondary" onClick={lookup} disabled={looking}>
            {looking ? 'Looking up…' : 'Look Up'}
          </Button>
        )}
        {(mode === 'existing' || mode === 'create') && (
          <Button type="button" variant="secondary" onClick={() => { setMode('lookup'); setFound(null); setApiError(null) }}>
            Change
          </Button>
        )}
      </div>

      {mode === 'existing' && found && (
        <div className="bg-green-50 border border-green-200 rounded-md p-4 space-y-3">
          <p className="text-sm font-medium text-green-800">Presenter found</p>
          <dl className="grid grid-cols-2 gap-2 text-sm">
            <div><dt className="text-gray-500">First Name</dt><dd className="font-medium">{found.firstName}</dd></div>
            <div><dt className="text-gray-500">Last Name</dt><dd className="font-medium">{found.lastName}</dd></div>
          </dl>
          <div className="flex justify-end">
            <Button onClick={confirmExisting}>Continue with this profile →</Button>
          </div>
        </div>
      )}

      {mode === 'create' && (
        <form onSubmit={handleSubmit(create)} className="space-y-4">
          <div className="bg-yellow-50 border border-yellow-200 rounded-md p-3">
            <p className="text-sm text-yellow-800">No profile found. Fill in your details to create one.</p>
          </div>
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
          <div className="flex justify-end">
            <Button type="submit" disabled={creating}>
              {creating ? 'Creating…' : 'Create Profile & Continue →'}
            </Button>
          </div>
        </form>
      )}
    </div>
  )
}
