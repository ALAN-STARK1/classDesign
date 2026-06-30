const ENDPOINTS = require('../api/endpoints')
const { get, post, patch, del } = require('../utils/request')

function generateDayMealPlan(payload) {
  return post(ENDPOINTS.mealPlans.generateDay, {
    planDate: payload.planDate || payload.date,
    targetCalorie: payload.targetCalorie,
    goalCycleId: payload.goalCycleId,
  })
}

function fetchDayMealPlan(params) {
  return get(ENDPOINTS.mealPlans.day, { date: params.date || params.planDate })
}

function fetchPlanCompletion(planId) {
  return get(ENDPOINTS.mealPlans.completion(planId))
}

function normalizeMealRecord(row) {
  if (!row) return row
  return Object.assign({}, row, {
    foodName: row.foodName || row.remark || '未命名',
    amount: row.amount != null ? row.amount : (row.amountG != null ? row.amountG : 1),
    unit: row.unit || (row.amountG != null ? 'g' : '份'),
    calorie: row.calorie != null ? row.calorie : row.totalCalorie,
    protein: row.protein != null ? row.protein : row.totalProtein,
    fat: row.fat != null ? row.fat : row.totalFat,
    carbohydrate: row.carbohydrate != null ? row.carbohydrate : row.totalCarbohydrate,
  })
}

function createManualMealRecord(payload) {
  return post(ENDPOINTS.mealRecords.base, {
    recordDate: payload.recordDate || payload.date,
    mealType: payload.mealType,
    remark: payload.remark || payload.note,
    items: payload.items || [{
      foodName: payload.foodName,
      amountG: payload.amountG != null ? payload.amountG : payload.amount,
      calorie: payload.calorie,
      protein: payload.protein,
      fat: payload.fat,
      carbohydrate: payload.carbohydrate,
    }],
  })
}

function createMealRecordFromRecipe(recipeId, payload) {
  return post(ENDPOINTS.mealRecords.fromRecipe(recipeId), {
    recordDate: payload.recordDate || payload.date,
    mealType: payload.mealType,
    servingRatio: payload.servingRatio != null ? payload.servingRatio : (payload.amount || 1),
  })
}

function fetchDayMealRecords(params) {
  return get(ENDPOINTS.mealRecords.day, params).then((result) => {
    if (!result) return result
    return Object.assign({}, result, {
      records: (result.records || []).map(normalizeMealRecord),
    })
  })
}

function deleteMealRecord(id) {
  return del(ENDPOINTS.mealRecords.byId(id))
}

module.exports = {
  generateDayMealPlan,
  fetchDayMealPlan,
  fetchPlanCompletion,
  createManualMealRecord,
  createMealRecordFromRecipe,
  fetchDayMealRecords,
  deleteMealRecord,
}
