export default function ErrorAlert({ message, onDismiss }) {
  return (
    <div className="rounded-md bg-red-50 border border-red-200 p-4 flex items-start gap-3">
      <svg className="h-5 w-5 text-red-400 mt-0.5 shrink-0" viewBox="0 0 20 20" fill="currentColor">
        <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm-1-9a1 1 0 012 0v4a1 1 0 11-2 0V9zm1-4a1 1 0 100 2 1 1 0 000-2z" clipRule="evenodd" />
      </svg>
      <p className="text-sm text-red-700 flex-1">{message}</p>
      {onDismiss && (
        <button onClick={onDismiss} className="text-red-400 hover:text-red-600 text-sm">✕</button>
      )}
    </div>
  )
}
