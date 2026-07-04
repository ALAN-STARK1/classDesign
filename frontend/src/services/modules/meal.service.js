import { ENDPOINTS } from '../../api/endpoints'
import { request } from '../request/http'

function normalizeMealPlan(plan) {
  if (!plan) return null
  return {
    ...plan,
    status: plan.status || 'ACTIVE',
    summary: plan.summary || {
      calorie: plan.actualCalorie,
      protein: plan.protein || 0,
      fat: plan.fat || 0,
      carbohydrate: plan.carbohydrate || 0,
    },
    items: (plan.items || []).map((item) => ({
      ...item,
      status: item.status || 'PLANNED',
      description: item.description || item.recommendReason || '根据健康档案与热量目标推荐',
      protein: item.protein ?? '-',
      fat: item.fat ?? '-',
      carbohydrate: item.carbohydrate ?? '-',
    })),
  }
}

export function generateDayMealPlan(payload) {
  return request.post(ENDPOINTS.mealPlans.generateDay, {
    planDate: payload.planDate ?? payload.date,
    targetCalorie: payload.targetCalorie,
    goalCycleId: payload.goalCycleId,
  }).then(normalizeMealPlan)
}

export function generateWeekMealPlan(payload) {
  return request.post(ENDPOINTS.mealPlans.generateWeek, {
    planDate: payload.planDate ?? payload.date,
    targetCalorie: payload.targetCalorie,
    goalCycleId: payload.goalCycleId,
  }).then((plans) => (plans || []).map(normalizeMealPlan))
}

export function fetchDayMealPlan(params) {
  return request.get(ENDPOINTS.mealPlans.day, {
    params: { date: params.date ?? params.planDate },
  }).then(normalizeMealPlan).catch((error) => {
    if (error?.code === 40400 || error?.message?.includes('暂无膳食计划')) {
      return null
    }
    throw error
  })
}

export function fetchReplacementCandidates(planId, itemId, params) {
  return request.get(ENDPOINTS.mealPlans.replacementCandidates(planId, itemId), { params }).then((items) =>
    (items || []).map((item) => ({
      ...item,
      reason: item.reason || item.recommendReason || '热量接近当前餐项',
      protein: item.protein ?? '-',
    })),
  )
}

export function replaceMealPlanItem(planId, itemId, payload) {
  return request.patch(ENDPOINTS.mealPlans.replaceItem(planId, itemId), {
    newRecipeId: payload.newRecipeId ?? payload.recipeId,
    replaceReason: payload.replaceReason ?? payload.reason ?? payload.recommendReason ?? 'USER_SELECTED',
    remark: payload.remark,
  }).then(normalizeMealPlan)
}

export function fetchReplaceLogs(planId) {
  return request.get(ENDPOINTS.mealPlans.replaceLogs(planId))
}

export function submitPlanItemFeedback(planId, itemId, payload) {
  const feedbackStatus = payload.status || payload.level
  return request.post(ENDPOINTS.mealPlans.feedbackItem(planId, itemId), {
    status: feedbackStatus === 'DISLIKE' ? 'PARTIAL' : 'COMPLETED',
    actualRatio: payload.actualRatio ?? 1,
    reason: payload.reason,
    remark: payload.remark ?? payload.note,
    createMealRecord: payload.createMealRecord ?? false,
  })
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

export function fetchShoppingList(planId) {
  return request.get(ENDPOINTS.mealPlans.shoppingList(planId))
}

export function fetchWeekShoppingList(params) {
  return request.get(ENDPOINTS.mealPlans.weekShoppingList, { params })
}

export function fetchWeekPoster(params) {
  return request.get(ENDPOINTS.mealPlans.weekPoster, { params })
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
