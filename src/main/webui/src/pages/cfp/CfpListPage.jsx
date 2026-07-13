import { Link } from 'react-router-dom'
import { useCfps } from '../../hooks/useCfps.js'
import ConferenceCard from '../../components/cfp/ConferenceCard.jsx'
import LoadingSpinner from '../../components/ui/LoadingSpinner.jsx'
import ErrorAlert from '../../components/ui/ErrorAlert.jsx'

export default function CfpListPage() {
  const { cfps, loading, error, reload } = useCfps()

  return (
    <div>
      <div className="flex items-end justify-between mb-7">
        <div>
          <div className="kicker mb-2.5">Conferences</div>
          <h1 className="font-display font-extrabold text-[34px] tracking-[-.02em] text-white">
            Call for Papers
          </h1>
        </div>
        <Link to="/cfp/new" className="btn-primary">+ Create CFP</Link>
      </div>

      {loading && <LoadingSpinner />}
      {error && <ErrorAlert message={error} onDismiss={reload} />}

      {!loading && !error && cfps.length === 0 && (
        <div className="text-center py-16 text-muted-400">
          <p className="text-lg">No CFPs yet.</p>
          <Link to="/cfp/new" className="mt-2 inline-block text-brand-light hover:underline">
            Create the first one
          </Link>
        </div>
      )}

      <div className="grid gap-[22px] sm:grid-cols-2">
        {cfps.map(cfp => <ConferenceCard key={cfp.id} cfp={cfp} />)}
      </div>
    </div>
  )
}
