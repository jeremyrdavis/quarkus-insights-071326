export default function FormField({ label, error, children, required }) {
  return (
    <div>
      <label className="lbl">
        {label}{required && <span className="text-danger-light ml-1">*</span>}
      </label>
      {children}
      {error && <p className="mt-1.5 text-[13px] text-danger-light">{error}</p>}
    </div>
  )
}

export function inputClass(hasError) {
  return `fld ${hasError ? 'fld-error' : ''}`
}
