import { request } from './http.js'

const BASE = '/cfp'

export const cfpApi = {
  list:   ()            => request(BASE),
  get:    (id)          => request(`${BASE}/${id}`),
  create: (payload)     => request(BASE,            { method: 'POST',   body: JSON.stringify(payload) }),
  update: (id, payload) => request(`${BASE}/${id}`, { method: 'PUT',    body: JSON.stringify(payload) }),
  delete: (id)          => request(`${BASE}/${id}`, { method: 'DELETE' }),
}
