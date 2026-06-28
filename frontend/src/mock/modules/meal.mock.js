import Mock from 'mockjs'
import {
  createMealFeedback,
  createMealPlan,
  createMealRecord,
  createReplacementCandidate,
  summarizeItems,
} from '../factories/meal'
import { failure, parseBody, parseQuery, success } from '../helpers'
import { withScenario } from '../scenario-store'

const baseUrl = import.meta.env.VITE_API_BASE_URL || '/api/v1'

let currentPlan = createMealPlan({ status: 'GENERATED' })
let replaceLogs = []
let feedbackList = currentPlan.items.slice(0, 1).map((item) => createMealFeedback(item, { level: 'LIKE' }))
let mealRecords = [
  createMealRecord({ id: 6001, mealType: 'BREAKFAST', sourceType: 'MANUAL', foodName: '无糖酸奶燕麦杯', calorie: 360 }),
  createMealRecord({ id: 6002, mealType: 'LUNCH', sourceType: 'RECIPE', foodName: '鸡胸肉能量碗', calorie: 520 }),
]

function idFrom(url, pattern) {
  return Number(url.match(pattern)?.[1])
}

function recalculatePlan(plan) {
  const summary = summarizeItems(plan.items)
  return {
    ...plan,
    summary: {
      calorie: Math.round(summary.calorie),
      protein: Number(summary.protein.toFixed(1)),
      fat: Number(summary.fat.toFixed(1)),
      carbohydrate: Number(summary.carbohydrate.toFixed(1)),
    },
  }
}

function completionFor(plan) {
  const total = plan?.items?.length || 0
  const completed = plan?.items?.filter((item) => item.completed || item.status === 'COMPLETED').length || 0
  return {
    planId: plan?.id,
    total,
    completed,
    skipped: plan?.items?.filter((item) => item.status === 'SKIPPED').length || 0,
    percent: total ? Math.round((completed / total) * 100) : 0,
  }
}

function toRecordFromItem(plan, item) {
  return createMealRecord({
    date: plan.date,
    mealType: item.mealType,
    sourceType: 'PLAN',
    foodName: item.recipeName,
    recipeId: item.recipeId,
    calorie: item.calorie,
    protein: item.protein,
    fat: item.fat,
    carbohydrate: item.carbohydrate,
    amount: item.servings || 1,
    unit: '份',
  })
}

export function registerMealMocks() {
  Mock.mock(`${baseUrl}/meal-plans/generate/day`, 'post', (options) =>
    withScenario('POST /meal-plans/generate/day', {
      success: () => {
        const body = parseBody(options)
        currentPlan = createMealPlan({
          date: body.date || currentPlan.date,
          targetCalorie: body.targetCalorie || currentPlan.targetCalorie,
          status: 'GENERATED',
        })
        replaceLogs = []
        feedbackList = []
        return success(currentPlan)
      },
      empty: () => success(null),
      error: () => failure(42200, '无法生成计划，请先完善健康档案'),
      timeout: () => failure(40800, '膳食计划生成超时'),
    }),
  )

  Mock.mock(new RegExp(`${baseUrl}/meal-plans/day(\\?.*)?$`), 'get', (options) =>
    withScenario('GET /meal-plans/day', {
      success: () => {
        const query = parseQuery(options.url)
        return success({ ...currentPlan, date: query.date || currentPlan.date })
      },
      empty: () => success(null),
      error: () => failure(40400, '指定日期暂无膳食计划'),
      timeout: () => failure(40800, '膳食计划加载超时'),
    }),
  )

  Mock.mock(new RegExp(`${baseUrl}/meal-plans/\\d+/items/\\d+/replacement-candidates(\\?.*)?$`), 'get', (options) =>
    withScenario('GET /meal-plans/{planId}/items/{itemId}/replacement-candidates', {
      success: () => {
        const itemId = idFrom(options.url, /items\/(\d+)/)
        const item = currentPlan.items.find((entry) => entry.id === itemId)
        return success(Array.from({ length: 4 }, () => createReplacementCandidate(item?.mealType || 'LUNCH')))
      },
      empty: () => success([]),
      error: () => failure(40400, '暂无可替换菜谱'),
      timeout: () => failure(40800, '替换候选加载超时'),
    }),
  )

  Mock.mock(new RegExp(`${baseUrl}/meal-plans/\\d+/items/\\d+/replace$`), 'patch', (options) =>
    withScenario('PATCH /meal-plans/{planId}/items/{itemId}/replace', {
      success: () => {
        const itemId = idFrom(options.url, /items\/(\d+)/)
        const body = parseBody(options)
        const original = currentPlan.items.find((entry) => entry.id === itemId)
        const replacement = {
          ...original,
          recipeId: body.recipeId || Mock.Random.integer(2000, 9999),
          recipeName: body.recipeName || '替换菜谱',
          calorie: body.calorie || original.calorie,
          protein: body.protein || original.protein,
          fat: body.fat || original.fat,
          carbohydrate: body.carbohydrate || original.carbohydrate,
          suitabilityScore: body.suitabilityScore || original.suitabilityScore,
          status: 'REPLACED',
        }
        currentPlan.items = currentPlan.items.map((entry) => (entry.id === itemId ? replacement : entry))
        currentPlan = recalculatePlan(currentPlan)
        replaceLogs = [
          {
            id: Mock.Random.integer(7000, 9999),
            itemId,
            mealType: original.mealType,
            fromRecipeName: original.recipeName,
            toRecipeName: replacement.recipeName,
            reason: body.reason || '用户选择替换',
            createdAt: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss'),
          },
          ...replaceLogs,
        ]
        return success(currentPlan)
      },
      empty: () => success(currentPlan),
      error: () => failure(42200, '替换餐项失败'),
      timeout: () => failure(40800, '替换餐项超时'),
    }),
  )

  Mock.mock(new RegExp(`${baseUrl}/meal-plans/\\d+/replace-logs$`), 'get', () =>
    withScenario('GET /meal-plans/{planId}/replace-logs', {
      success: () => success(replaceLogs),
      empty: () => success([]),
      error: () => failure(50000, '替换历史加载失败'),
      timeout: () => failure(40800, '替换历史加载超时'),
    }),
  )

  Mock.mock(new RegExp(`${baseUrl}/meal-plans/\\d+/items/\\d+/feedback$`), 'post', (options) =>
    withScenario('POST /meal-plans/{planId}/items/{itemId}/feedback', {
      success: () => {
        const itemId = idFrom(options.url, /items\/(\d+)/)
        const item = currentPlan.items.find((entry) => entry.id === itemId)
        const body = parseBody(options)
        const feedback = createMealFeedback(item, { level: body.level || 'NORMAL', note: body.note || '' })
        feedbackList = [feedback, ...feedbackList.filter((entry) => entry.itemId !== itemId)]
        currentPlan.items = currentPlan.items.map((entry) =>
          entry.id === itemId ? { ...entry, feedbackLevel: feedback.level, note: feedback.note } : entry,
        )
        return success(feedback)
      },
      empty: () => success(null),
      error: () => failure(40001, '反馈内容校验失败'),
      timeout: () => failure(40800, '提交反馈超时'),
    }),
  )

  Mock.mock(new RegExp(`${baseUrl}/meal-plans/\\d+/feedback$`), 'get', () =>
    withScenario('GET /meal-plans/{planId}/feedback', {
      success: () => success(feedbackList),
      empty: () => success([]),
      error: () => failure(50000, '反馈加载失败'),
      timeout: () => failure(40800, '反馈加载超时'),
    }),
  )

  Mock.mock(new RegExp(`${baseUrl}/meal-plans/\\d+/completion$`), 'get', () =>
    withScenario('GET /meal-plans/{planId}/completion', {
      success: () => success(completionFor(currentPlan)),
      empty: () => success({ total: 0, completed: 0, skipped: 0, percent: 0 }),
      error: () => failure(50000, '完成度加载失败'),
      timeout: () => failure(40800, '完成度加载超时'),
    }),
  )

  Mock.mock(new RegExp(`${baseUrl}/meal-plans/\\d+/to-records$`), 'post', () =>
    withScenario('POST /meal-plans/{planId}/to-records', {
      success: () => {
        const created = currentPlan.items.map((item) => toRecordFromItem(currentPlan, item))
        mealRecords = [...created, ...mealRecords]
        currentPlan.items = currentPlan.items.map((item) => ({ ...item, completed: true, status: 'COMPLETED' }))
        currentPlan.status = 'COMPLETED'
        return success(created)
      },
      empty: () => success([]),
      error: () => failure(42200, '计划无法转换为膳食记录'),
      timeout: () => failure(40800, '转换膳食记录超时'),
    }),
  )

  Mock.mock(`${baseUrl}/meal-records`, 'post', (options) =>
    withScenario('POST /meal-records', {
      success: () => {
        const record = createMealRecord({ ...parseBody(options), id: Mock.Random.integer(90000, 99999), sourceType: 'MANUAL' })
        mealRecords = [record, ...mealRecords]
        return success(record)
      },
      empty: () => success(null),
      error: () =>
        failure(40001, '记录参数校验失败', {
          fieldErrors: [{ field: 'foodName', message: '食物名称不能为空' }],
        }),
      timeout: () => failure(40800, '新增膳食记录超时'),
    }),
  )

  Mock.mock(new RegExp(`${baseUrl}/meal-records/from-recipe/\\d+$`), 'post', (options) =>
    withScenario('POST /meal-records/from-recipe/{recipeId}', {
      success: () => {
        const recipeId = idFrom(options.url, /from-recipe\/(\d+)/)
        const body = parseBody(options)
        const record = createMealRecord({ ...body, recipeId, sourceType: 'RECIPE', foodName: body.foodName || '正式菜谱记录' })
        mealRecords = [record, ...mealRecords]
        return success(record)
      },
      empty: () => success(null),
      error: () => failure(40400, '菜谱不存在，无法生成记录'),
      timeout: () => failure(40800, '从菜谱生成记录超时'),
    }),
  )

  Mock.mock(new RegExp(`${baseUrl}/meal-records/from-ai-recipe/\\d+$`), 'post', (options) =>
    withScenario('POST /meal-records/from-ai-recipe/{aiRecipeId}', {
      success: () => {
        const aiRecipeId = idFrom(options.url, /from-ai-recipe\/(\d+)/)
        const body = parseBody(options)
        const record = createMealRecord({ ...body, aiRecipeId, sourceType: 'AI_RECIPE', foodName: body.foodName || 'AI 菜谱记录' })
        mealRecords = [record, ...mealRecords]
        return success(record)
      },
      empty: () => success(null),
      error: () => failure(40400, 'AI 菜谱不存在，无法生成记录'),
      timeout: () => failure(40800, '从 AI 菜谱生成记录超时'),
    }),
  )

  Mock.mock(new RegExp(`${baseUrl}/meal-records/day(\\?.*)?$`), 'get', (options) =>
    withScenario('GET /meal-records/day', {
      success: () => {
        const query = parseQuery(options.url)
        const date = query.date || currentPlan.date
        const records = mealRecords.filter((record) => record.date === date)
        const summary = summarizeItems(records)
        return success({
          date,
          records,
          summary: {
            calorie: Math.round(summary.calorie),
            protein: Number(summary.protein.toFixed(1)),
            fat: Number(summary.fat.toFixed(1)),
            carbohydrate: Number(summary.carbohydrate.toFixed(1)),
          },
        })
      },
      empty: () => success({ date: parseQuery(options.url).date || currentPlan.date, records: [], summary: { calorie: 0, protein: 0, fat: 0, carbohydrate: 0 } }),
      error: () => failure(50000, '膳食记录加载失败'),
      timeout: () => failure(40800, '膳食记录加载超时'),
    }),
  )

  Mock.mock(new RegExp(`${baseUrl}/meal-records/\\d+$`), 'delete', (options) =>
    withScenario('DELETE /meal-records/{id}', {
      success: () => {
        const id = idFrom(options.url, /meal-records\/(\d+)/)
        mealRecords = mealRecords.filter((record) => record.id !== id)
        return success(true)
      },
      empty: () => success(false),
      error: () => failure(40300, '无权删除该膳食记录'),
      timeout: () => failure(40800, '删除膳食记录超时'),
    }),
  )
}
