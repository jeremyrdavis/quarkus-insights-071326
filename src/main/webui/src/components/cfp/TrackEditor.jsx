import { useFieldArray } from 'react-hook-form'
import Button from '../ui/Button.jsx'
import FormField, { inputClass } from '../ui/FormField.jsx'

export default function TrackEditor({ control, register, errors }) {
  const { fields, append, remove } = useFieldArray({ control, name: 'conferenceTracks' })

  return (
    <div className="space-y-3">
      <div className="flex items-center justify-between">
        <h3 className="text-sm font-medium text-gray-700">Tracks</h3>
        <Button
          type="button"
          variant="secondary"
          onClick={() => append({ trackCode: '', title: '', description: '' })}
        >
          + Add Track
        </Button>
      </div>
      {fields.length === 0 && (
        <p className="text-sm text-gray-400 italic">No tracks added yet.</p>
      )}
      {fields.map((field, i) => (
        <div key={field.id} className="grid grid-cols-12 gap-2 p-3 bg-gray-50 rounded-md border border-gray-200">
          <div className="col-span-4">
            <FormField label="Track Code" error={errors?.conferenceTracks?.[i]?.trackCode?.message}>
              <input
                placeholder="JAVA_LANGUAGE"
                className={inputClass(!!errors?.conferenceTracks?.[i]?.trackCode)}
                {...register(`conferenceTracks.${i}.trackCode`, { required: 'Required' })}
              />
            </FormField>
          </div>
          <div className="col-span-4">
            <FormField label="Title" error={errors?.conferenceTracks?.[i]?.title?.message}>
              <input
                className={inputClass(!!errors?.conferenceTracks?.[i]?.title)}
                {...register(`conferenceTracks.${i}.title`, { required: 'Required' })}
              />
            </FormField>
          </div>
          <div className="col-span-3">
            <FormField label="Description">
              <input className={inputClass(false)} {...register(`conferenceTracks.${i}.description`)} />
            </FormField>
          </div>
          <div className="col-span-1 flex items-end pb-0.5">
            <Button type="button" variant="danger" onClick={() => remove(i)} className="w-full justify-center">✕</Button>
          </div>
        </div>
      ))}
    </div>
  )
}
