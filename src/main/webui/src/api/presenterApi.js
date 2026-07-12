import { request } from './http.js'

const BASE = '/presenters'

export const presenterApi = {
  getByEmail: (email)          => request(`${BASE}/${encodeURIComponent(email)}`),
  create:     (payload)        => request(BASE,                                       { method: 'POST',   body: JSON.stringify(payload) }),
  update:     (email, payload) => request(`${BASE}/${encodeURIComponent(email)}`,     { method: 'PUT',    body: JSON.stringify(payload) }),
  delete:     (email)          => request(`${BASE}/${encodeURIComponent(email)}`,     { method: 'DELETE' }),
}
