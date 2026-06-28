import Mock from 'mockjs'
import { page, success } from '../helpers'
import { withScenario } from '../scenario-store'
import { updateCommunityPostReview } from './community.mock'
import {
  createAdminReviewRecipe,
  createAdminUser,
  createAiCallLog,
  createDemoDataResult,
  createNutritionRiskResult,
  createReportStatistics,
} from '../factories/admin'

const USERS = Array.from({ length: 24 }, (_, i) => createAdminUser({ id: 2000 + i }))
const REVIEW_RECIPES = Array.from({ length: 16 }, (_, i) => createAdminReviewRecipe({ id: 6000 + i }))
const AI_CALL_LOGS = Array.from({ length: 40 }, () => createAiCallLog())
const RISK_RESULTS = Array.from({ length: 30 }, () => createNutritionRiskResult())

export function registerAdminMocks() {
  const BASE = import.meta.env.VITE_API_BASE_URL || ''

  Mock.mock(new RegExp(`${BASE}/admin/users`), 'get', (options) => {
    const key = 'GET /admin/users'
    return withScenario(key, {
      success: () => {
        const url = new URL(options.url, 'http://localhost')
        const p = parseInt(url.searchParams.get('page') || '1')
        const s = parseInt(url.searchParams.get('size') || '10')
        const keyword = url.searchParams.get('keyword') || ''
        let list = [...USERS]
        if (keyword) list = list.filter((u) => u.username.includes(keyword) || u.email.includes(keyword))
        return success(page(list.slice((p - 1) * s, p * s), p, s, list.length))
      },
      empty: () => success(page([], 1, 10, 0)),
      error: () => ({
        code: 40300,
        message: '权限不足，仅管理员可访问',
        data: null,
        timestamp: new Date().toISOString(),
      }),
      timeout: () => new Promise(() => {}),
    })
  })

  Mock.mock(new RegExp(`${BASE}/admin/users/\\d+/disable`), 'patch', (options) => {
    const key = 'PATCH /admin/users/{id}/disable'
    return withScenario(key, {
      success: () => {
        const body = JSON.parse(options.body || '{}')
        const idMatch = options.url.match(/\/admin\/users\/(\d+)\/disable/)
        const id = parseInt(idMatch?.[1] || '0')
        const user = USERS.find((u) => u.id === id)
        if (user) user.status = body.status || 'DISABLED'
        return success(user)
      },
      error: () => ({
        code: 40300,
        message: '权限不足，仅管理员可操作',
        data: null,
        timestamp: new Date().toISOString(),
      }),
      timeout: () => new Promise(() => {}),
    })
  })

  Mock.mock(new RegExp(`${BASE}/admin/recipes/\\d+/review`), 'patch', (options) => {
    const key = 'PATCH /admin/recipes/{id}/review'
    return withScenario(key, {
      success: () => {
        const body = JSON.parse(options.body || '{}')
        const idMatch = options.url.match(/\/admin\/recipes\/(\d+)\/review/)
        const id = parseInt(idMatch?.[1] || '0')
        const recipe = REVIEW_RECIPES.find((r) => r.id === id)
        if (recipe) recipe.status = body.status || 'ONLINE'
        return success(recipe)
      },
      error: () => ({
        code: 40300,
        message: '权限不足，仅管理员可操作',
        data: null,
        timestamp: new Date().toISOString(),
      }),
      timeout: () => new Promise(() => {}),
    })
  })

  Mock.mock(new RegExp(`${BASE}/admin/community/posts/\\d+/review`), 'patch', (options) => {
    const key = 'PATCH /admin/community/posts/{id}/review'
    return withScenario(key, {
      success: () => {
        const body = JSON.parse(options.body || '{}')
        const idMatch = options.url.match(/\/admin\/community\/posts\/(\d+)\/review/)
        const id = parseInt(idMatch?.[1] || '0')
        const post = updateCommunityPostReview(id, body.status || 'PUBLISHED')
        return success(post)
      },
      error: () => ({
        code: 40300,
        message: '权限不足，仅管理员可操作',
        data: null,
        timestamp: new Date().toISOString(),
      }),
      timeout: () => new Promise(() => {}),
    })
  })

  Mock.mock(new RegExp(`${BASE}/admin/ai-call-logs`), 'get', (options) => {
    const key = 'GET /admin/ai-call-logs'
    return withScenario(key, {
      success: () => {
        const url = new URL(options.url, 'http://localhost')
        const p = parseInt(url.searchParams.get('page') || '1')
        const s = parseInt(url.searchParams.get('size') || '10')
        const model = url.searchParams.get('model') || ''
        const status = url.searchParams.get('status') || ''
        let list = [...AI_CALL_LOGS]
        if (model) list = list.filter((l) => l.model === model)
        if (status) list = list.filter((l) => l.status === status)
        return success(page(list.slice((p - 1) * s, p * s), p, s, list.length))
      },
      empty: () => success(page([], 1, 10, 0)),
      error: () => ({
        code: 40300,
        message: '权限不足，仅管理员可访问',
        data: null,
        timestamp: new Date().toISOString(),
      }),
      timeout: () => new Promise(() => {}),
    })
  })

  Mock.mock(new RegExp(`${BASE}/admin/nutrition-risk-results`), 'get', (options) => {
    const key = 'GET /admin/nutrition-risk-results'
    return withScenario(key, {
      success: () => {
        const url = new URL(options.url, 'http://localhost')
        const p = parseInt(url.searchParams.get('page') || '1')
        const s = parseInt(url.searchParams.get('size') || '10')
        return success(page(RISK_RESULTS.slice((p - 1) * s, p * s), p, s, RISK_RESULTS.length))
      },
      empty: () => success(page([], 1, 10, 0)),
      error: () => ({
        code: 40300,
        message: '权限不足，仅管理员可访问',
        data: null,
        timestamp: new Date().toISOString(),
      }),
      timeout: () => new Promise(() => {}),
    })
  })

  Mock.mock(new RegExp(`${BASE}/admin/nutrition-reports/statistics`), 'get', () => {
    const key = 'GET /admin/nutrition-reports/statistics'
    return withScenario(key, {
      success: () => success(createReportStatistics()),
      empty: () => success(createReportStatistics({ totalReports: 0, weeklyCount: 0, monthlyCount: 0 })),
      error: () => ({
        code: 40300,
        message: '权限不足，仅管理员可访问',
        data: null,
        timestamp: new Date().toISOString(),
      }),
      timeout: () => new Promise(() => {}),
    })
  })

  Mock.mock(new RegExp(`${BASE}/admin/demo-data/reset`), 'post', () => {
    const key = 'POST /admin/demo-data/reset'
    return withScenario(key, {
      success: () => success(createDemoDataResult()),
      error: () => ({
        code: 40001,
        message: '重置失败，演示数据服务不可用',
        data: null,
        timestamp: new Date().toISOString(),
      }),
      timeout: () => new Promise(() => {}),
    })
  })
}
