import { ENDPOINTS } from '../../api/endpoints'
import { request } from '../request/http'

export function fetchHealthProfile() {
  return request.get(ENDPOINTS.healthProfile.me)
}

export function saveHealthProfile(payload) {
  return request.put(ENDPOINTS.healthProfile.me, payload)
}

export function fetchHealthSummary() {
  return request.get(ENDPOINTS.healthProfile.summary)
}

export function updateAllergens(allergens) {
  return request.put(ENDPOINTS.healthProfile.allergens, { allergens })
}

export function updateRestrictions(restrictions) {
  return request.put(ENDPOINTS.healthProfile.restrictions, { restrictions })
}

export function createGoalCycle(payload) {
  return request.post(ENDPOINTS.healthGoalCycles.base, payload)
}

export function fetchCurrentGoalCycle() {
  return request.get(ENDPOINTS.healthGoalCycles.current)
}

export function fetchGoalCycles(params) {
  return request.get(ENDPOINTS.healthGoalCycles.base, { params })
}

export function updateGoalCycle(id, payload) {
  return request.put(ENDPOINTS.healthGoalCycles.byId(id), payload)
}

export function completeGoalCycle(id) {
  return request.patch(ENDPOINTS.healthGoalCycles.complete(id))
}

export function cancelGoalCycle(id) {
  return request.patch(ENDPOINTS.healthGoalCycles.cancel(id))
}

export function fetchGoalCycleProgress(id) {
  return request.get(ENDPOINTS.healthGoalCycles.progress(id))
}
