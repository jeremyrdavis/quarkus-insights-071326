import { useForm } from 'react-hook-form'
import Button from '../ui/Button.jsx'
import FormField, { inputClass } from '../ui/FormField.jsx'
import FormatEditor from './FormatEditor.jsx'
import TrackEditor from './TrackEditor.jsx'

function toFormValues(cfp) {
  if (!cfp) return {}
  return {
    conferenceName: cfp.conferenceName ?? '',
    conferenceUrl: cfp.conferenceUrl ?? '',
    conferenceDescription: cfp.conferenceDescription ?? '',
    cfpOpens: cfp.cfpOpens ?? '',
    cfpCloses: cfp.cfpCloses ?? '',
    contactEmailAddress: cfp.contactEmailAddress?.address ?? '',
    formats: cfp.conferenceSessionFormats ?? [],
    conferenceTracks: cfp.conferenceTracks ?? [],
  }
}

export default function CfpForm({ initialValues, onSubmit, isSubmitting }) {
  const { register, control, handleSubmit, formState: { errors } } = useForm({
    defaultValues: toFormValues(initialValues),
  })

  async function submit(data) {
    await onSubmit({
      conferenceName: data.conferenceName,
      conferenceUrl: data.conferenceUrl,
      conferenceDescription: data.conferenceDescription,
      cfpOpens: data.cfpOpens,
      cfpCloses: data.cfpCloses,
      contactEmailAddress: data.contactEmailAddress,
      formats: data.formats,
      conferenceTracks: data.conferenceTracks,
    })
  }

  return (
    <form onSubmit={handleSubmit(submit)} className="space-y-6">
      <div className="grid grid-cols-2 gap-4">
        <div className="col-span-2">
          <FormField label="Conference Name" error={errors.conferenceName?.message} required>
            <input
              className={inputClass(!!errors.conferenceName)}
              {...register('conferenceName', { required: 'Conference name is required' })}
            />
          </FormField>
        </div>

        <FormField label="Conference URL" error={errors.conferenceUrl?.message} required>
          <input
            type="url"
            className={inputClass(!!errors.conferenceUrl)}
            {...register('conferenceUrl', { required: 'URL is required' })}
          />
        </FormField>

        <FormField label="Contact Email" error={errors.contactEmailAddress?.message} required>
          <input
            type="email"
            className={inputClass(!!errors.contactEmailAddress)}
            {...register('contactEmailAddress', { required: 'Email is required' })}
          />
        </FormField>

        <FormField label="CFP Opens" error={errors.cfpOpens?.message} required>
          <input
            type="date"
            className={inputClass(!!errors.cfpOpens)}
            {...register('cfpOpens', { required: 'Opening date is required' })}
          />
        </FormField>

        <FormField label="CFP Closes" error={errors.cfpCloses?.message} required>
          <input
            type="date"
            className={inputClass(!!errors.cfpCloses)}
            {...register('cfpCloses', { required: 'Closing date is required' })}
          />
        </FormField>

        <div className="col-span-2">
          <FormField label="Conference Description" error={errors.conferenceDescription?.message}>
            <textarea
              rows={3}
              className={inputClass(!!errors.conferenceDescription)}
              {...register('conferenceDescription')}
            />
          </FormField>
        </div>
      </div>

      <FormatEditor control={control} register={register} errors={errors} />
      <TrackEditor control={control} register={register} errors={errors} />

      <div className="flex justify-end">
        <Button type="submit" disabled={isSubmitting}>
          {isSubmitting ? 'Saving…' : 'Save CFP'}
        </Button>
      </div>
    </form>
  )
}
