import { ENDPOINTS } from '../../api/endpoints'
import { request } from '../request/http'

export function fetchTodayNutrition(params) {
  return request.get(ENDPOINTS.nutrition.today, { params })
}

export function fetchNutritionRange(params) {
  return request.get(ENDPOINTS.nutrition.range, { params })
}

export function fetchNutritionGap(params) {
  return request.get(ENDPOINTS.nutrition.gap, { params })
}

export function fetchNutritionRisks(params) {
  return request.get(ENDPOINTS.nutrition.risks, { params })
}

export function fetchRecipeNutrition(recipeId) {
  return request.get(ENDPOINTS.nutrition.recipe(recipeId))
}

export function fetchNutritionRiskRules(params) {
  return request.get(ENDPOINTS.nutritionRiskRules.base, { params })
}

export function createNutritionRiskRule(payload) {
  return request.post(ENDPOINTS.nutritionRiskRules.base, payload)
}

export function updateNutritionRiskRule(id, payload) {
  return request.put(ENDPOINTS.nutritionRiskRules.byId(id), payload)
}

export function updateNutritionRiskRuleStatus(id, payload) {
  return request.patch(ENDPOINTS.nutritionRiskRules.status(id), payload)
}

export function evaluateNutritionRiskRules(payload = {}) {
  return request.post(ENDPOINTS.nutritionRiskRules.evaluate, payload)
}

export function generateWeeklyNutritionReport(payload = {}) {
  return request.post(ENDPOINTS.nutritionReports.weekly, payload)
}

export function generateMonthlyNutritionReport(payload = {}) {
  return request.post(ENDPOINTS.nutritionReports.monthly, payload)
}

export function fetchNutritionReports(params) {
  return request.get(ENDPOINTS.nutritionReports.base, { params })
}

export function fetchNutritionReportDetail(id) {
  return request.get(ENDPOINTS.nutritionReports.byId(id))
}

export function deleteNutritionReport(id) {
  return request.delete(ENDPOINTS.nutritionReports.byId(id))
}
