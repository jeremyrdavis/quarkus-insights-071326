import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { cfpApi } from '../../api/cfpApi.js'
import CfpForm from '../../components/cfp/CfpForm.jsx'
import ErrorAlert from '../../components/ui/ErrorAlert.jsx'

export default function CfpCreatePage() {
  const navigate = useNavigate()
  const [submitting, setSubmitting] = useState(false)
  const [error, setError] = useState(null)

  async function handleSubmit(data) {
    setSubmitting(true)
    setError(null)
    try {
      const created = await cfpApi.create(data)
      navigate(`/cfp/${created.id}`)
    } catch (e) {
      setError(e.message)
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div>
      <div className="mb-6">
        <Link to="/cfp" className="font-mono text-[12px] uppercase tracking-[.1em] text-brand-light">← Back to CFPs</Link>
        <h1 className="font-display font-extrabold text-[32px] tracking-[-.02em] text-white mt-3">Create CFP</h1>
      </div>
      {error && <div className="mb-4"><ErrorAlert message={error} onDismiss={() => setError(null)} /></div>}
      <div className="card">
        <CfpForm onSubmit={handleSubmit} isSubmitting={submitting} />
      </div>
    </div>
  )
}
