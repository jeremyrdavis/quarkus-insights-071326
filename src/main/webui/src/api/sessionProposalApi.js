import { request } from './http.js'

export const sessionProposalApi = {
  create: (payload) => request('/session-proposals', { method: 'POST', body: JSON.stringify(payload) }),
}
