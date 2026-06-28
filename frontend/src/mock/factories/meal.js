import Mock from 'mockjs'
import { MealFeedbackLevel, MealItemStatus, MealPlanStatus, MealType } from '../../constants/enums'
import { today } from '../../utils/date'
import { pick } from '../helpers'

const recipeNames = ['鸡胸肉能量碗', '番茄豆腐汤', '三文鱼糙米饭', '西兰花虾仁', '燕麦酸奶杯', '菠菜鸡蛋卷', '鹰嘴豆沙拉', '低脂牛奶麦片']

function macroFromCalorie(calorie) {
  return {
    calorie,
    protein: Mock.Random.float(12, 42, 1, 1),
    fat: Mock.Random.float(4, 24, 1, 1),
    carbohydrate: Mock.Random.float(16, 86, 1, 1),
  }
}

export function createMealRecipe(overrides = {}) {
  const calorie = Mock.Random.integer(220, 680)
  return {
    recipeId: Mock.Random.integer(2000, 9999),
    recipeName: pick(recipeNames),
    description: pick(['高蛋白低负担', '控糖友好组合', '饱腹感稳定', '适合工作日快速准备']),
    cookMinutes: Mock.Random.integer(8, 45),
    suitabilityScore: Mock.Random.integer(68, 98),
    ...macroFromCalorie(calorie),
    ...overrides,
  }
}

export function createMealPlanItem(type, overrides = {}) {
  const recipe = createMealRecipe()
  return {
    id: Mock.Random.integer(10000, 99999),
    mealType: type,
    status: pick(MealItemStatus),
    servings: Mock.Random.float(0.8, 1.5, 1, 1),
    completed: false,
    feedbackLevel: '',
    note: '',
    ...recipe,
    ...overrides,
  }
}

export function summarizeItems(items = []) {
  return items.reduce(
    (total, item) => ({
      calorie: total.calorie + Number(item.calorie || 0),
      protein: total.protein + Number(item.protein || 0),
      fat: total.fat + Number(item.fat || 0),
      carbohydrate: total.carbohydrate + Number(item.carbohydrate || 0),
    }),
    { calorie: 0, protein: 0, fat: 0, carbohydrate: 0 },
  )
}

export function createMealPlan(overrides = {}) {
  const items = MealType.map((type) => createMealPlanItem(type, { status: 'PLANNED' }))
  const summary = summarizeItems(items)
  return {
    id: Mock.Random.integer(3000, 9999),
    date: today(),
    status: pick(MealPlanStatus),
    targetCalorie: 1650,
    items,
    summary: {
      calorie: Math.round(summary.calorie),
      protein: Number(summary.protein.toFixed(1)),
      fat: Number(summary.fat.toFixed(1)),
      carbohydrate: Number(summary.carbohydrate.toFixed(1)),
    },
    createdAt: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss'),
    ...overrides,
  }
}

export function createReplacementCandidate(baseType = 'LUNCH', overrides = {}) {
  return {
    ...createMealRecipe(),
    mealType: baseType,
    reason: pick(['热量更接近目标', '蛋白质更高', '过敏风险更低', '烹饪时间更短']),
    ...overrides,
  }
}

export function createMealFeedback(item, overrides = {}) {
  return {
    itemId: item.id,
    mealType: item.mealType,
    recipeName: item.recipeName,
    level: pick(MealFeedbackLevel),
    note: pick(['口味合适', '份量偏多', '下次减少油脂', '希望替换主食']),
    createdAt: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss'),
    ...overrides,
  }
}

export function createMealRecord(overrides = {}) {
  const recipe = createMealRecipe()
  return {
    id: Mock.Random.integer(5000, 99999),
    date: today(),
    mealType: pick(MealType),
    sourceType: 'MANUAL',
    foodName: recipe.recipeName,
    amount: Mock.Random.float(0.5, 1.5, 1, 1),
    unit: '份',
    note: '',
    ...recipe,
    ...overrides,
  }
}
