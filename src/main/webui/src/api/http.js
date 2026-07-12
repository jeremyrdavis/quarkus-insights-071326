export async function request(path, options = {}) {
  const res = await fetch(path, {
    headers: { 'Content-Type': 'application/json', Accept: 'application/json' },
    ...options,
  })
  if (!res.ok) {
    const body = await res.text().catch(() => '')
    const err = new Error(`HTTP ${res.status}`)
    err.status = res.status
    err.body = body
    throw err
  }
  if (res.status === 204) return null
  const text = await res.text()
  try {
    return JSON.parse(text)
  } catch {
    const err = new Error(`Expected JSON but got: ${text.slice(0, 120)}`)
    err.status = res.status
    throw err
  }
}
