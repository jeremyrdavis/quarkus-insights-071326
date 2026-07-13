import { Link } from 'react-router-dom'
import { useCfps } from '../../hooks/useCfps.js'
import LoadingSpinner from '../../components/ui/LoadingSpinner.jsx'
import ErrorAlert from '../../components/ui/ErrorAlert.jsx'

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
        {cfps.map(cfp => (
          <Link
            key={cfp.id}
            to={`/cfp/${cfp.id}`}
            className="block bg-white rounded-lg border border-gray-200 p-5 hover:shadow-md transition-shadow"
          >
            <h2 className="text-base font-semibold text-gray-900">{cfp.conferenceName}</h2>
          </Link>
        ))}
      </div>
    </div>
  )
}
