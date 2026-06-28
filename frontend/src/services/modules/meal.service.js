import { ENDPOINTS } from '../../api/endpoints'
import { request } from '../request/http'

export function generateDayMealPlan(payload) {
  return request.post(ENDPOINTS.mealPlans.generateDay, payload)
}

export function fetchDayMealPlan(params) {
  return request.get(ENDPOINTS.mealPlans.day, { params })
}

export function fetchReplacementCandidates(planId, itemId, params) {
  return request.get(ENDPOINTS.mealPlans.replacementCandidates(planId, itemId), { params })
}

export function replaceMealPlanItem(planId, itemId, payload) {
  return request.patch(ENDPOINTS.mealPlans.replaceItem(planId, itemId), payload)
}

export function fetchReplaceLogs(planId) {
  return request.get(ENDPOINTS.mealPlans.replaceLogs(planId))
}

export function submitPlanItemFeedback(planId, itemId, payload) {
  return request.post(ENDPOINTS.mealPlans.feedbackItem(planId, itemId), payload)
}

export function fetchPlanFeedback(planId) {
  return request.get(ENDPOINTS.mealPlans.feedback(planId))
}

export function fetchPlanCompletion(planId) {
  return request.get(ENDPOINTS.mealPlans.completion(planId))
}

export function convertPlanToRecords(planId, payload = {}) {
  return request.post(ENDPOINTS.mealPlans.toRecords(planId), payload)
}

export function createManualMealRecord(payload) {
  return request.post(ENDPOINTS.mealRecords.base, payload)
}

export function createMealRecordFromRecipe(recipeId, payload = {}) {
  return request.post(ENDPOINTS.mealRecords.fromRecipe(recipeId), payload)
}

export function createMealRecordFromAiRecipe(aiRecipeId, payload = {}) {
  return request.post(ENDPOINTS.mealRecords.fromAiRecipe(aiRecipeId), payload)
}

export function fetchDayMealRecords(params) {
  return request.get(ENDPOINTS.mealRecords.day, { params })
}

export function deleteMealRecord(id) {
  return request.delete(ENDPOINTS.mealRecords.byId(id))
}
