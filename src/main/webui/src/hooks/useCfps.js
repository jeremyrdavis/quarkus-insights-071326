import { useState, useEffect, useCallback } from 'react'
import { cfpApi } from '../api/cfpApi.js'

export function useCfps() {
  const [cfps, setCfps] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  const load = useCallback(async () => {
    setLoading(true)
    setError(null)
    try {
      setCfps(await cfpApi.list())
    } catch (e) {
      setError(e.message)
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => { load() }, [load])

  return { cfps, loading, error, reload: load }
}
