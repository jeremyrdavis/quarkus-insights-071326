import { useState } from 'react'
import { useParams, Link, useNavigate } from 'react-router-dom'
import { useCfp } from '../../hooks/useCfp.js'
import { cfpApi } from '../../api/cfpApi.js'
import CfpForm from '../../components/cfp/CfpForm.jsx'
import LoadingSpinner from '../../components/ui/LoadingSpinner.jsx'
import ErrorAlert from '../../components/ui/ErrorAlert.jsx'

export default function CfpEditPage() {
  const { id } = useParams()
  const navigate = useNavigate()
  const { cfp, loading, error: loadError } = useCfp(id)
  const [submitting, setSubmitting] = useState(false)
  const [submitError, setSubmitError] = useState(null)

  async function handleSubmit(data) {
    setSubmitting(true)
    setSubmitError(null)
    try {
      await cfpApi.update(id, data)
      navigate(`/cfp/${id}`)
    } catch (e) {
      setSubmitError(e.message)
    } finally {
      setSubmitting(false)
    }
  }

  if (loading) return <LoadingSpinner />
  if (loadError) return <ErrorAlert message={loadError} />

  return (
    <div>
      <div className="mb-6">
        <Link to={`/cfp/${id}`} className="font-mono text-[12px] uppercase tracking-[.1em] text-brand-light">← Back to CFP</Link>
        <h1 className="font-display font-extrabold text-[32px] tracking-[-.02em] text-white mt-3">Edit CFP</h1>
        {cfp && <p className="text-muted-400 text-sm mt-1">{cfp.conferenceName}</p>}
      </div>
      {submitError && <div className="mb-4"><ErrorAlert message={submitError} onDismiss={() => setSubmitError(null)} /></div>}
      <div className="card">
        {cfp && <CfpForm initialValues={cfp} onSubmit={handleSubmit} isSubmitting={submitting} />}
      </div>
    </div>
  )
}
