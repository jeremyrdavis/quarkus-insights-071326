export default function FormField({ label, error, children, required }) {
  return (
    <div>
      <label className="block text-sm font-medium text-gray-700 mb-1">
        {label}{required && <span className="text-red-500 ml-1">*</span>}
      </label>
      {children}
      {error && <p className="mt-1 text-sm text-red-600">{error}</p>}
    </div>
  )
}

export function inputClass(hasError) {
  return `block w-full rounded-md border px-3 py-2 text-sm shadow-sm
    focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:border-indigo-500
    ${hasError ? 'border-red-300 text-red-900' : 'border-gray-300'}`
}
