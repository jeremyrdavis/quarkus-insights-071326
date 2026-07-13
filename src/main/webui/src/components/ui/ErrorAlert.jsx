export default function ErrorAlert({ message, onDismiss }) {
  return (
    <div className="rounded-[10px] bg-danger/10 border border-danger/40 p-4 flex items-start gap-3">
      <svg className="h-5 w-5 text-danger-light mt-0.5 shrink-0" viewBox="0 0 20 20" fill="currentColor">
        <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm-1-9a1 1 0 012 0v4a1 1 0 11-2 0V9zm1-4a1 1 0 100 2 1 1 0 000-2z" clipRule="evenodd" />
      </svg>
      <p className="text-sm text-danger-light flex-1">{message}</p>
      {onDismiss && (
        <button onClick={onDismiss} className="text-danger-light hover:text-white text-sm">✕</button>
      )}
    </div>
  )
}
