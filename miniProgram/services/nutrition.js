const ENDPOINTS = require('../api/endpoints')
const { get, post, del } = require('../utils/request')
const { today, subtractDays, monthOf } = require('../utils/date')

function resolveRange(params) {
  const endDate = params.endDate || params.date || today()
  const days = Number(params.days || 14)
  const startDate = subtractDays(endDate, Math.max(days - 1, 0))
  return { startDate, endDate }
}

function normalizeSummary(data) {
  if (!data) return data
  return Object.assign({}, data, {
    calorie: data.calorie != null ? data.calorie : (data.totalCalorie || 0),
    targetCalorie: data.targetCalorie || 1600,
    score: data.score != null ? data.score : (data.caloriePercent || 0),
  })
}

function normalizeGap(gap) {
  if (!gap) return []
  if (Array.isArray(gap)) return gap
  const build = (nutrient, label, value) => ({
    nutrient,
    label,
    gap: value || 0,
    status: value > 0 ? 'LOW' : (value < 0 ? 'HIGH' : 'OK'),
    suggestion: (gap.suggestions && gap.suggestions[0]) || `${label}摄入需关注`,
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
  return Object.assign({}, risk, {
    title: risk.title || risk.message || '营养风险',
    description: risk.description || risk.message || '',
  })
}

function fetchTodayNutrition(params) {
  return get(ENDPOINTS.nutrition.today, { date: params && params.date }).then(normalizeSummary)
}

function fetchNutritionGap(params) {
  return get(ENDPOINTS.nutrition.gap, { date: params && params.date }).then(normalizeGap)
}

function fetchNutritionRisks(params) {
  const range = resolveRange(params || {})
  return get(ENDPOINTS.nutrition.risks, range).then((result) => (result || []).map(normalizeRisk))
}

function fetchNutritionReports(params) {
  return get(ENDPOINTS.nutritionReports.base, params)
}

function fetchNutritionReportDetail(id) {
  return get(ENDPOINTS.nutritionReports.byId(id))
}

function generateWeeklyReport(payload) {
  const endDate = (payload && payload.endDate) || today()
  const startDate = (payload && payload.startDate) || subtractDays(endDate, 6)
  return post(ENDPOINTS.nutritionReports.weekly, { startDate, endDate })
}

function generateMonthlyReport(payload) {
  const endDate = (payload && payload.endDate) || today()
  const month = (payload && payload.month) || monthOf(endDate)
  return post(ENDPOINTS.nutritionReports.monthly, { month })
}

function deleteNutritionReport(id) {
  return del(ENDPOINTS.nutritionReports.byId(id))
}

module.exports = {
  fetchTodayNutrition,
  fetchNutritionGap,
  fetchNutritionRisks,
  fetchNutritionReports,
  fetchNutritionReportDetail,
  generateWeeklyReport,
  generateMonthlyReport,
  deleteNutritionReport,
}
