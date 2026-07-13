import { useState, useEffect } from 'react'
import { sessionProposalApi } from '../api/sessionProposalApi.js'

// Given a list of CFPs, fetches each CFP's proposals and returns a map
// { [cfpId]: { total, pending, approved } }. Pending = SUBMITTED, Approved = APPROVED.
export function useReviewStats(cfps) {
  const [statsById, setStatsById] = useState({})
  const [loading, setLoading] = useState(false)

  const ids = cfps.map(c => c.id).join(',')

  useEffect(() => {
    if (cfps.length === 0) {
      setStatsById({})
      return
    }
    let cancelled = false
    setLoading(true)
    Promise.all(
      cfps.map(async cfp => {
        try {
          const proposals = (await sessionProposalApi.list(cfp.id)) ?? []
          return [cfp.id, {
            total: proposals.length,
            pending: proposals.filter(p => p.status === 'SUBMITTED').length,
            approved: proposals.filter(p => p.status === 'APPROVED').length,
          }]
        } catch {
          return [cfp.id, { total: 0, pending: 0, approved: 0 }]
        }
      })
    ).then(entries => {
      if (!cancelled) setStatsById(Object.fromEntries(entries))
    }).finally(() => {
      if (!cancelled) setLoading(false)
    })

    return () => { cancelled = true }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [ids])

  return { statsById, loading }
}
