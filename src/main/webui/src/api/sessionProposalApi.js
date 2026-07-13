import { request } from './http.js'

export const sessionProposalApi = {
  create: (payload) => request('/session-proposals', { method: 'POST', body: JSON.stringify(payload) }),
  list:   (cfpId)  => request(`/session-proposals?cfpId=${cfpId}`),
  review: (id, status) => request(`/session-proposals/${id}/status`, { method: 'PUT', body: JSON.stringify({ status }) }),
}
