import { useState, useEffect } from 'react'
import { cfpApi } from '../api/cfpApi.js'

export function useCfp(id) {
  const [cfp, setCfp] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    if (!id) return
    let cancelled = false
    setLoading(true)
    setError(null)
    cfpApi.get(id)
      .then(data => { if (!cancelled) setCfp(data) })
      .catch(e  => { if (!cancelled) setError(e.message) })
      .finally(() => { if (!cancelled) setLoading(false) })
    return () => { cancelled = true }
  }, [id])

  return { cfp, loading, error }
}
