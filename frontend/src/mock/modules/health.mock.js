import Mock from 'mockjs'
import { createGoalCycle, createGoalProgress, createHealthProfile, createHealthSummary } from '../factories/health'
import { failure, page, parseBody, parseQuery, success } from '../helpers'
import { withScenario } from '../scenario-store'

const baseUrl = import.meta.env.VITE_API_BASE_URL || '/api/v1'

let profile = createHealthProfile({
  gender: 'FEMALE',
  activityLevel: 'LIGHT',
  healthGoal: 'FAT_LOSS',
})
let currentCycle = createGoalCycle({
  startWeightKg: profile.weightKg,
  targetWeightKg: profile.targetWeightKg,
  targetCalorie: profile.dailyCalorieTarget,
})
let cycles = [
  currentCycle,
  createGoalCycle({ id: 9, status: 'COMPLETED', progressPercent: 92 }),
  createGoalCycle({ id: 8, status: 'CANCELLED', progressPercent: 36 }),
]

export function registerHealthMocks() {
  Mock.mock(`${baseUrl}/health-profile/me`, 'get', () =>
    withScenario('GET /health-profile/me', {
      success: () => success(profile),
      empty: () => success(null),
      error: () => failure(40001, '健康档案暂时不可用'),
      timeout: () => failure(40800, '查询健康档案超时'),
    }),
  )

  Mock.mock(`${baseUrl}/health-profile/me`, 'put', (options) =>
    withScenario('PUT /health-profile/me', {
      success: () => {
        profile = createHealthProfile({ ...profile, ...parseBody(options) })
        return success(profile)
      },
      empty: () => success(null),
      error: () =>
        failure(40001, '参数校验失败', {
          fieldErrors: [{ field: 'heightCm', message: '身高必须大于 0' }],
        }),
      timeout: () => failure(40800, '保存健康档案超时'),
    }),
  )

  Mock.mock(`${baseUrl}/health-profile/me/summary`, 'get', () =>
    withScenario('GET /health-profile/me/summary', {
      success: () => success(createHealthSummary(profile)),
      empty: () => success(null),
      error: () => failure(42200, '缺少身高或体重，无法计算摘要'),
      timeout: () => failure(40800, '查询摘要超时'),
    }),
  )

  Mock.mock(`${baseUrl}/health-profile/me/allergens`, 'put', (options) =>
    withScenario('PUT /health-profile/me/allergens', {
      success: () => {
        profile.allergens = parseBody(options).allergens || []
        return success(profile.allergens)
      },
      empty: () => success([]),
      error: () => failure(40001, '过敏源格式不正确'),
      timeout: () => failure(40800, '保存过敏源超时'),
    }),
  )

  Mock.mock(`${baseUrl}/health-profile/me/restrictions`, 'put', (options) =>
    withScenario('PUT /health-profile/me/restrictions', {
      success: () => {
        profile.restrictions = parseBody(options).restrictions || []
        return success(profile.restrictions)
      },
      empty: () => success([]),
      error: () => failure(40001, '饮食禁忌格式不正确'),
      timeout: () => failure(40800, '保存饮食禁忌超时'),
    }),
  )

  Mock.mock(`${baseUrl}/health-goal-cycles`, 'post', (options) =>
    withScenario('POST /health-goal-cycles', {
      success: () => {
        currentCycle = createGoalCycle({ ...parseBody(options), id: Mock.Random.integer(100, 999), progressPercent: 0, status: 'ACTIVE' })
        cycles = [currentCycle, ...cycles]
        return success(currentCycle)
      },
      empty: () => success(null),
      error: () => failure(42200, '当前已有进行中的目标周期'),
      timeout: () => failure(40800, '创建目标周期超时'),
    }),
  )

  Mock.mock(`${baseUrl}/health-goal-cycles/current`, 'get', () =>
    withScenario('GET /health-goal-cycles/current', {
      success: () => success(currentCycle),
      empty: () => success(null),
      error: () => failure(40400, '暂无当前目标周期'),
      timeout: () => failure(40800, '查询当前目标周期超时'),
    }),
  )

  Mock.mock(new RegExp(`${baseUrl}/health-goal-cycles\\?.*`), 'get', (options) =>
    withScenario('GET /health-goal-cycles', {
      success: () => {
        const query = parseQuery(options.url)
        return success(page(cycles, Number(query.page || 1), Number(query.size || 10), cycles.length))
      },
      empty: () => success(page([], 1, 10, 0)),
      error: () => failure(50000, '目标周期列表加载失败'),
      timeout: () => failure(40800, '目标周期列表超时'),
    }),
  )

  Mock.mock(new RegExp(`${baseUrl}/health-goal-cycles/\\d+$`), 'put', (options) =>
    withScenario('PUT /health-goal-cycles/{id}', {
      success: () => {
        const id = Number(options.url.match(/health-goal-cycles\/(\d+)/)?.[1])
        const next = { ...cycles.find((item) => item.id === id), ...parseBody(options), id }
        cycles = cycles.map((item) => (item.id === id ? next : item))
        if (currentCycle?.id === id) currentCycle = next
        return success(next)
      },
      empty: () => success(null),
      error: () => failure(40001, '目标周期参数校验失败'),
      timeout: () => failure(40800, '更新目标周期超时'),
    }),
  )

  Mock.mock(new RegExp(`${baseUrl}/health-goal-cycles/\\d+/complete$`), 'patch', (options) =>
    withScenario('PATCH /health-goal-cycles/{id}/complete', {
      success: () => {
        const id = Number(options.url.match(/health-goal-cycles\/(\d+)/)?.[1])
        cycles = cycles.map((item) => (item.id === id ? { ...item, status: 'COMPLETED', progressPercent: 100 } : item))
        if (currentCycle?.id === id) currentCycle = null
        return success({ id, status: 'COMPLETED', summary: '本周期完成度 92%，体重趋势基本符合预期。' })
      },
      empty: () => success(null),
      error: () => failure(42200, '当前周期暂不能完成'),
      timeout: () => failure(40800, '完成目标周期超时'),
    }),
  )

  Mock.mock(new RegExp(`${baseUrl}/health-goal-cycles/\\d+/cancel$`), 'patch', (options) =>
    withScenario('PATCH /health-goal-cycles/{id}/cancel', {
      success: () => {
        const id = Number(options.url.match(/health-goal-cycles\/(\d+)/)?.[1])
        cycles = cycles.map((item) => (item.id === id ? { ...item, status: 'CANCELLED' } : item))
        if (currentCycle?.id === id) currentCycle = null
        return success({ id, status: 'CANCELLED' })
      },
      empty: () => success(null),
      error: () => failure(42200, '当前周期暂不能取消'),
      timeout: () => failure(40800, '取消目标周期超时'),
    }),
  )

  Mock.mock(new RegExp(`${baseUrl}/health-goal-cycles/\\d+/progress$`), 'get', (options) =>
    withScenario('GET /health-goal-cycles/{id}/progress', {
      success: () => {
        const id = Number(options.url.match(/health-goal-cycles\/(\d+)/)?.[1])
        const cycle = cycles.find((item) => item.id === id) || currentCycle
        return success(createGoalProgress(cycle))
      },
      empty: () => success(null),
      error: () => failure(42200, '目标进度异常偏离'),
      timeout: () => failure(40800, '查询目标进度超时'),
    }),
  )
}
