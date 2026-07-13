import { useFieldArray } from 'react-hook-form'
import Button from '../ui/Button.jsx'
import FormField, { inputClass } from '../ui/FormField.jsx'

export default function FormatEditor({ control, register, errors }) {
  const { fields, append, remove } = useFieldArray({ control, name: 'formats' })

  return (
    <div className="space-y-3">
      <div className="flex items-center justify-between">
        <h3 className="font-display font-semibold text-[15px] text-white">Session Formats</h3>
        <Button
          type="button"
          variant="secondary"
          onClick={() => append({ formatCode: 'TECHNICAL_SESSION', title: '', description: '', duration: 'PT45M' })}
        >
          + Add Format
        </Button>
      </div>
      {fields.length === 0 && (
        <p className="text-sm text-muted-500 italic">No formats added yet.</p>
      )}
      {fields.map((field, i) => (
        <div key={field.id} className="grid grid-cols-12 gap-2 p-3 bg-surface-2 rounded-[10px] border border-white/[.08]">
          {/* formatCode is preserved but not user-editable */}
          <input type="hidden" {...register(`formats.${i}.formatCode`)} />
          <div className="col-span-4">
            <FormField label="Title" error={errors?.formats?.[i]?.title?.message}>
              <input
                className={inputClass(!!errors?.formats?.[i]?.title)}
                {...register(`formats.${i}.title`, { required: 'Required' })}
              />
            </FormField>
          </div>
          <div className="col-span-3">
            <FormField label="Duration (ISO)" error={errors?.formats?.[i]?.duration?.message}>
              <input
                placeholder="PT45M"
                className={inputClass(!!errors?.formats?.[i]?.duration)}
                {...register(`formats.${i}.duration`, { required: 'Required' })}
              />
            </FormField>
          </div>
          <div className="col-span-4">
            <FormField label="Description">
              <input className={inputClass(false)} {...register(`formats.${i}.description`)} />
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
