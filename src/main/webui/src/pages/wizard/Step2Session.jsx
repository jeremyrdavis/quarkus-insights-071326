import { useForm } from 'react-hook-form'
import { useCfp } from '../../hooks/useCfp.js'
import Button from '../../components/ui/Button.jsx'
import FormField, { inputClass } from '../../components/ui/FormField.jsx'
import LoadingSpinner from '../../components/ui/LoadingSpinner.jsx'
import { formatDuration } from '../../utils/duration.js'

const LEVELS = ['BEGINNER', 'INTERMEDIATE', 'ADVANCED']
const LANGUAGES = ['ENGLISH', 'DUTCH']

export default function Step2Session({ cfpId, initial, onNext, onBack }) {
  const { cfp, loading } = useCfp(cfpId)

  const { register, handleSubmit, formState: { errors } } = useForm({
    defaultValues: {
      title: initial?.title ?? '',
      description: initial?.description ?? '',
      formatIndex: initial ? '0' : '0',
      trackIndex: initial ? '0' : '0',
      level: initial?.level ?? 'BEGINNER',
      language: initial?.language ?? 'ENGLISH',
      presentationOutline: initial?.presentationOutline ?? '',
      programmingLanguages: initial?.programmingLanguagesUsed?.map(p => p.language).join(', ') ?? '',
      preRequisiteKnowledge: initial?.preRequisiteKnowledge ?? '',
    },
  })

  function submit(data) {
    const format = cfp.conferenceSessionFormats[parseInt(data.formatIndex)]
    const track  = cfp.conferenceTracks[parseInt(data.trackIndex)]
    onNext({
      title: data.title,
      description: data.description,
      conferenceSessionFormat: format,
      conferenceTrack: track,
      level: data.level,
      language: data.language,
      presentationOutline: data.presentationOutline,
      programmingLanguagesUsed: data.programmingLanguages
        .split(',')
        .map(l => ({ language: l.trim() }))
        .filter(l => l.language),
      preRequisiteKnowledge: data.preRequisiteKnowledge,
    })
  }

  if (loading) return <LoadingSpinner message="Loading CFP…" />

  return (
    <form onSubmit={handleSubmit(submit)} className="space-y-6">
      <div>
        <h2 className="font-display font-semibold text-[18px] text-white">Step 2: Session Details</h2>
        {cfp && (
          <p className="text-sm text-muted-400 mt-1">
            Submitting to <strong className="text-muted-100">{cfp.conferenceName}</strong> · CFP closes {cfp.cfpCloses}
          </p>
        )}
      </div>

      <FormField label="Session Title" error={errors.title?.message} required>
        <input
          className={inputClass(!!errors.title)}
          {...register('title', { required: 'Title is required' })}
        />
      </FormField>

      <FormField label="Abstract / Description" error={errors.description?.message} required>
        <textarea
          rows={4}
          className={inputClass(!!errors.description)}
          {...register('description', { required: 'Description is required' })}
        />
      </FormField>

      <div className="grid grid-cols-2 gap-4">
        <FormField label="Session Format" error={errors.formatIndex?.message} required>
          <select
            className={inputClass(!!errors.formatIndex)}
            {...register('formatIndex', { required: true })}
          >
            {cfp?.conferenceSessionFormats?.map((f, i) => (
              <option key={i} value={i}>{f.title} ({formatDuration(f.duration)})</option>
            ))}
          </select>
        </FormField>

        <FormField label="Track" error={errors.trackIndex?.message} required>
          <select
            className={inputClass(!!errors.trackIndex)}
            {...register('trackIndex', { required: true })}
          >
            {cfp?.conferenceTracks?.map((t, i) => (
              <option key={i} value={i}>{t.title || t.trackCode}</option>
            ))}
          </select>
        </FormField>

        <FormField label="Level" error={errors.level?.message} required>
          <select className={inputClass(!!errors.level)} {...register('level', { required: true })}>
            {LEVELS.map(l => <option key={l} value={l}>{l}</option>)}
          </select>
        </FormField>

        <FormField label="Language" error={errors.language?.message} required>
          <select className={inputClass(!!errors.language)} {...register('language', { required: true })}>
            {LANGUAGES.map(l => <option key={l} value={l}>{l}</option>)}
          </select>
        </FormField>
      </div>

      <FormField label="Presentation Outline" error={errors.presentationOutline?.message} required>
        <textarea
          rows={3}
          placeholder="1. Introduction  2. Demo  3. Q&A"
          className={inputClass(!!errors.presentationOutline)}
          {...register('presentationOutline', { required: 'Outline is required' })}
        />
      </FormField>

      <FormField
        label="Programming Languages"
        error={errors.programmingLanguages?.message}
        required
      >
        <input
          placeholder="Java, Kotlin, Python (comma-separated)"
          className={inputClass(!!errors.programmingLanguages)}
          {...register('programmingLanguages', {
            required: 'At least one language is required',
            validate: v => v.split(',').some(l => l.trim()) || 'At least one language is required',
          })}
        />
      </FormField>

      <FormField label="Prerequisite Knowledge">
        <input className={inputClass(false)} {...register('preRequisiteKnowledge')} />
      </FormField>

      <div className="flex justify-between pt-2">
        <Button type="button" variant="secondary" onClick={onBack}>← Back</Button>
        <Button type="submit">Review Submission →</Button>
      </div>
    </form>
  )
}
