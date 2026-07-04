import { ENDPOINTS } from '../../api/endpoints'
import { request } from '../request/http'

// User management
export function fetchAdminUsers(params) {
  return request.get(ENDPOINTS.admin.users, { params })
}

export function disableUser(id, payload) {
  return request.patch(ENDPOINTS.admin.disableUser(id), payload)
}

// Recipe review
export function reviewRecipe(id, payload) {
  const approved = payload.approved ?? payload.status === 'ONLINE'
  return request.patch(ENDPOINTS.admin.reviewRecipe(id), {
    approved,
    reason: payload.reason,
  })
}

// Post review
export function reviewPost(id, payload) {
  const approved = payload.approved ?? (payload.status === 'PUBLISHED' || payload.status === 'ONLINE')
  return request.patch(ENDPOINTS.admin.reviewPost(id), {
    approved,
    reason: payload.reason,
  })
}

export function deletePost(id) {
  return request.delete(ENDPOINTS.admin.deletePost(id))
}

// AI call logs
export function fetchAiCallLogs(params) {
  return request.get(ENDPOINTS.admin.aiCallLogs, { params })
}

// Nutrition risk results
export function fetchAdminRiskResults(params) {
  return request.get(ENDPOINTS.admin.nutritionRiskResults, { params })
}

// Report statistics
export function fetchReportStatistics(params) {
  return request.get(ENDPOINTS.admin.nutritionReportStatistics, { params })
}

// Demo data reset
export function resetDemoData() {
  return request.post(ENDPOINTS.admin.demoDataReset)
}
