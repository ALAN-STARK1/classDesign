import dayjs from 'dayjs'
import { ENDPOINTS } from '../../api/endpoints'
import { request } from '../request/http'
import { today as todayStr } from '../../utils/date'

function resolveRange(params = {}) {
  const endDate = params.endDate ?? params.date ?? todayStr()
  const days = Number(params.days ?? 14)
  const startDate = dayjs(endDate)
    .subtract(Math.max(days - 1, 0), 'day')
    .format('YYYY-MM-DD')
  return { startDate, endDate }
}

function normalizeSummary(data) {
  if (!data) return data
  const calorie = data.calorie ?? data.totalCalorie ?? 0
  return {
    ...data,
    calorie,
    targetCalorie: data.targetCalorie ?? 1600,
    score: data.score ?? data.caloriePercent ?? 0,
  }
}

function normalizeTrendPoint(point) {
  if (!point) return point
  return {
    ...point,
    date: point.date,
    calorie: point.calorie ?? point.totalCalorie ?? 0,
    protein: point.protein ?? 0,
    fat: point.fat ?? 0,
    carbohydrate: point.carbohydrate ?? 0,
    score: point.score ?? point.caloriePercent ?? 0,
  }
}

function normalizeGap(gap) {
  if (!gap) return []
  if (Array.isArray(gap)) return gap
  const build = (nutrient, label, value) => ({
    nutrient,
    label,
    gap: value ?? 0,
    status: value > 0 ? 'LOW' : value < 0 ? 'HIGH' : 'OK',
    suggestion:
      gap.suggestions?.[0] ||
      (value > 0 ? `建议增加${label}摄入` : value < 0 ? `注意控制${label}摄入` : `${label}摄入较均衡`),
  })
  return [
    build('calorie', '热量', gap.calorieGap),
    build('protein', '蛋白质', gap.proteinGap),
    build('fat', '脂肪', gap.fatGap),
    build('carbohydrate', '碳水', gap.carbohydrateGap),
  ]
}

function normalizeRisk(risk) {
  if (!risk) return risk
  return {
    ...risk,
    title: risk.title ?? risk.message ?? '营养风险',
    description: risk.description ?? risk.message ?? '',
    suggestion: risk.suggestion ?? '',
  }
}

export function fetchTodayNutrition(params = {}) {
  return request
    .get(ENDPOINTS.nutrition.today, { params: { date: params.date } })
    .then(normalizeSummary)
}

export function fetchNutritionRange(params = {}) {
  const { startDate, endDate } = resolveRange(params)
  return request
    .get(ENDPOINTS.nutrition.range, { params: { startDate, endDate } })
    .then((result) => {
      const days = result?.days ?? (Array.isArray(result) ? result : [])
      return days.map(normalizeTrendPoint)
    })
}

export function fetchNutritionGap(params = {}) {
  return request
    .get(ENDPOINTS.nutrition.gap, { params: { date: params.date } })
    .then(normalizeGap)
}

export function fetchNutritionRisks(params = {}) {
  const { startDate, endDate } = resolveRange(params)
  return request
    .get(ENDPOINTS.nutrition.risks, { params: { startDate, endDate } })
    .then((result) => (result || []).map(normalizeRisk))
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
  return request
    .post(ENDPOINTS.nutritionRiskRules.evaluate, {
      date: payload.date,
      sourceType: payload.sourceType,
      sourceId: payload.sourceId,
    })
    .then((result) => (result || []).map(normalizeRisk))
}

export function generateWeeklyNutritionReport(payload = {}) {
  const endDate = payload.endDate ?? payload.date ?? todayStr()
  const startDate =
    payload.startDate ??
    dayjs(endDate)
      .subtract(6, 'day')
      .format('YYYY-MM-DD')
  return request.post(ENDPOINTS.nutritionReports.weekly, { startDate, endDate })
}

export function generateMonthlyNutritionReport(payload = {}) {
  const endDate = payload.endDate ?? payload.date ?? todayStr()
  const month = payload.month ?? dayjs(endDate).format('YYYY-MM')
  return request.post(ENDPOINTS.nutritionReports.monthly, { month })
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
