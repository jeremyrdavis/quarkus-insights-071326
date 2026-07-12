import { Link } from 'react-router-dom'

function isOpen(cfp) {
  const today = new Date().toISOString().slice(0, 10)
  return today >= cfp.cfpOpens && today <= cfp.cfpCloses
}

export default function CfpCard({ cfp }) {
  const open = isOpen(cfp)
  return (
    <Link
      to={`/cfp/${cfp.id}`}
      className="block bg-white rounded-lg border border-gray-200 p-5 hover:shadow-md transition-shadow"
    >
      <div className="flex items-start justify-between gap-2">
        <h2 className="text-base font-semibold text-gray-900">{cfp.conferenceName}</h2>
        <span className={`shrink-0 text-xs font-medium px-2 py-0.5 rounded-full ${
          open ? 'bg-green-100 text-green-800' : 'bg-gray-100 text-gray-600'
        }`}>
          {open ? 'Open' : 'Closed'}
        </span>
      </div>
      <p className="mt-1 text-sm text-gray-500">
        {cfp.cfpOpens} – {cfp.cfpCloses}
      </p>
      {cfp.conferenceDescription && (
        <p className="mt-2 text-sm text-gray-600 line-clamp-2">{cfp.conferenceDescription}</p>
      )}
      <p className="mt-3 text-xs text-gray-400">
        {cfp.conferenceSessionFormats?.length ?? 0} formats · {cfp.conferenceTracks?.length ?? 0} tracks
      </p>
    </Link>
  )
}
