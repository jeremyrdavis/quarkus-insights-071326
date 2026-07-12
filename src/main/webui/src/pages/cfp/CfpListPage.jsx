import { Link } from 'react-router-dom'
import { useCfps } from '../../hooks/useCfps.js'
import CfpCard from '../../components/cfp/CfpCard.jsx'
import Button from '../../components/ui/Button.jsx'
import LoadingSpinner from '../../components/ui/LoadingSpinner.jsx'
import ErrorAlert from '../../components/ui/ErrorAlert.jsx'

export default function CfpListPage() {
  const { cfps, loading, error, reload } = useCfps()

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold text-gray-900">Call for Papers</h1>
        <Button as={Link} to="/cfp/new">
          <Link to="/cfp/new" className="text-white no-underline">+ Create CFP</Link>
        </Button>
      </div>

      {loading && <LoadingSpinner />}
      {error && <ErrorAlert message={error} onDismiss={reload} />}

      {!loading && !error && cfps.length === 0 && (
        <div className="text-center py-16 text-gray-400">
          <p className="text-lg">No CFPs yet.</p>
          <Link to="/cfp/new" className="mt-2 inline-block text-indigo-600 hover:underline">
            Create the first one
          </Link>
        </div>
      )}

      <div className="grid gap-4 sm:grid-cols-2">
        {cfps.map(cfp => <CfpCard key={cfp.id} cfp={cfp} />)}
      </div>
    </div>
  )
}
