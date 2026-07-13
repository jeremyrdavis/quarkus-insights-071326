import { useState, useEffect, useCallback } from 'react'
import { sessionProposalApi } from '../api/sessionProposalApi.js'

export function useSessionProposals(cfpId) {
  const [proposals, setProposals] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

  const load = useCallback(async () => {
    if (!cfpId) return
    setLoading(true)
    setError(null)
    try {
      setProposals(await sessionProposalApi.list(cfpId) ?? [])
    } catch (e) {
      setError(e.message)
    } finally {
      setLoading(false)
    }
  }, [cfpId])

  useEffect(() => { load() }, [load])

  return { proposals, loading, error, reload: load }
}
