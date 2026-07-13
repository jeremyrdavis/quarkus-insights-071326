import { Link } from 'react-router-dom'
import { useCfps } from '../../hooks/useCfps.js'
import LoadingSpinner from '../../components/ui/LoadingSpinner.jsx'
import ErrorAlert from '../../components/ui/ErrorAlert.jsx'

function isOpen(cfp) {
  const today = new Date().toISOString().slice(0, 10)
  return today >= cfp.cfpOpens && today <= cfp.cfpCloses
}

export default function ReviewsPage() {
  const { cfps, loading, error, reload } = useCfps()

  return (
    <div>
      <h1 className="text-2xl font-bold text-gray-900 mb-6">Review Proposals</h1>

      {loading && <LoadingSpinner />}
      {error && <ErrorAlert message={error} onDismiss={reload} />}

      {!loading && !error && cfps.length === 0 && (
        <p className="text-gray-400 text-center py-16">No CFPs found.</p>
      )}

      <div className="grid gap-4 sm:grid-cols-2">
        {cfps.map(cfp => {
          const open = isOpen(cfp)
          return (
            <div key={cfp.id} className="bg-white rounded-lg border border-gray-200 p-5">
              <div className="flex items-start justify-between gap-2 mb-1">
                <h2 className="text-base font-semibold text-gray-900">{cfp.conferenceName}</h2>
                <span className={`shrink-0 text-xs font-medium px-2 py-0.5 rounded-full ${
                  open ? 'bg-green-100 text-green-800' : 'bg-gray-100 text-gray-600'
                }`}>
                  {open ? 'Open' : 'Closed'}
                </span>
              </div>
              <p className="text-sm text-gray-500 mb-4">{cfp.cfpOpens} – {cfp.cfpCloses}</p>
              <Link
                to={`/cfp/${cfp.id}`}
                className="inline-block text-sm font-medium text-indigo-600 hover:text-indigo-800 hover:underline"
              >
                Review proposals →
              </Link>
            </div>
          )
        })}
      </div>
    </div>
  )
}
