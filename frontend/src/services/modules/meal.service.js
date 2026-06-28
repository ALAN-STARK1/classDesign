import { ENDPOINTS } from '../../api/endpoints'
import { request } from '../request/http'

export function generateDayMealPlan(payload) {
  return request.post(ENDPOINTS.mealPlans.generateDay, {
    planDate: payload.planDate ?? payload.date,
    targetCalorie: payload.targetCalorie,
    goalCycleId: payload.goalCycleId,
  })
}

export function fetchDayMealPlan(params) {
  return request.get(ENDPOINTS.mealPlans.day, {
    params: { date: params.date ?? params.planDate },
  })
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

function normalizeMealRecord(row) {
  if (!row) return row
  return {
    ...row,
    foodName: row.foodName ?? row.remark ?? '未命名',
    amount: row.amount ?? row.amountG ?? 1,
    unit: row.unit ?? (row.amountG != null ? 'g' : '份'),
    calorie: row.calorie ?? row.totalCalorie,
    protein: row.protein ?? row.totalProtein,
    fat: row.fat ?? row.totalFat,
    carbohydrate: row.carbohydrate ?? row.totalCarbohydrate,
  }
}

export function createManualMealRecord(payload) {
  const body = {
    recordDate: payload.recordDate ?? payload.date,
    mealType: payload.mealType,
    remark: payload.remark ?? payload.note,
    items: payload.items ?? [
      {
        foodName: payload.foodName,
        amountG: payload.amountG ?? payload.amount,
        calorie: payload.calorie,
        protein: payload.protein,
        fat: payload.fat,
        carbohydrate: payload.carbohydrate,
      },
    ],
  }
  return request.post(ENDPOINTS.mealRecords.base, body)
}

export function createMealRecordFromRecipe(recipeId, payload = {}) {
  return request.post(ENDPOINTS.mealRecords.fromRecipe(recipeId), {
    recordDate: payload.recordDate ?? payload.date,
    mealType: payload.mealType,
    servingRatio: payload.servingRatio ?? payload.amount ?? 1,
  })
}

export function createMealRecordFromAiRecipe(aiRecipeId, payload = {}) {
  return request.post(ENDPOINTS.mealRecords.fromAiRecipe(aiRecipeId), {
    recordDate: payload.recordDate ?? payload.date,
    mealType: payload.mealType,
    servingRatio: payload.servingRatio ?? payload.amount ?? 1,
  })
}

export function fetchDayMealRecords(params) {
  return request.get(ENDPOINTS.mealRecords.day, { params }).then((result) => {
    if (!result) return result
    return {
      ...result,
      records: (result.records || []).map(normalizeMealRecord),
    }
  })
}

export function deleteMealRecord(id) {
  return request.delete(ENDPOINTS.mealRecords.byId(id))
}
