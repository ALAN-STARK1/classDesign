import Mock from 'mockjs'
import {
  createNutritionGap,
  createNutritionRange,
  createNutritionReport,
  createNutritionRisk,
  createNutritionSummary,
  createRiskRule,
} from '../factories/nutrition'
import { failure, page, parseBody, parseQuery, success } from '../helpers'
import { withScenario } from '../scenario-store'

const baseUrl = import.meta.env.VITE_API_BASE_URL || '/api/v1'

let rules = Array.from({ length: 8 }, (_, index) =>
  createRiskRule({
    id: 500 + index,
    status: index % 4 === 0 ? 'DISABLED' : 'ENABLED',
  }),
)

let reports = Array.from({ length: 9 }, (_, index) =>
  createNutritionReport({
    id: 7000 + index,
    type: index % 3 === 0 ? 'MONTHLY' : 'WEEKLY',
  }),
)

function idFrom(url, pattern) {
  return Number(url.match(pattern)?.[1])
}

function paginate(items, query) {
  const pageNo = Number(query.page || 1)
  const size = Number(query.size || 10)
  const type = query.type
  const status = query.status
  let filtered = items
  if (type) filtered = filtered.filter((item) => item.type === type)
  if (status) filtered = filtered.filter((item) => item.status === status)
  const start = (pageNo - 1) * size
  return page(filtered.slice(start, start + size), pageNo, size, filtered.length)
}

export function registerNutritionMocks() {
  Mock.mock(new RegExp(`${baseUrl}/nutrition/today(\\?.*)?$`), 'get', (options) =>
    withScenario('GET /nutrition/today', {
      success: () => {
        const query = parseQuery(options.url)
        return success(createNutritionSummary(query.date ? { date: query.date } : {}))
      },
      empty: () => success(null),
      error: () => failure(50000, '今日营养汇总加载失败'),
      timeout: () => failure(40800, '今日营养汇总加载超时'),
    }),
  )

  Mock.mock(new RegExp(`${baseUrl}/nutrition/range(\\?.*)?$`), 'get', (options) =>
    withScenario('GET /nutrition/range', {
      success: () => {
        const query = parseQuery(options.url)
        return success(createNutritionRange(Number(query.days || 14)))
      },
      empty: () => success([]),
      error: () => failure(50000, '营养趋势加载失败'),
      timeout: () => failure(40800, '营养趋势加载超时'),
    }),
  )

  Mock.mock(new RegExp(`${baseUrl}/nutrition/gap(\\?.*)?$`), 'get', () =>
    withScenario('GET /nutrition/gap', {
      success: () => success(createNutritionGap()),
      empty: () => success([]),
      error: () => failure(50000, '营养缺口加载失败'),
      timeout: () => failure(40800, '营养缺口加载超时'),
    }),
  )

  Mock.mock(new RegExp(`${baseUrl}/nutrition/risks(\\?.*)?$`), 'get', () =>
    withScenario('GET /nutrition/risks', {
      success: () => success(Array.from({ length: 5 }, () => createNutritionRisk())),
      empty: () => success([]),
      error: () => failure(50000, '营养风险加载失败'),
      timeout: () => failure(40800, '营养风险加载超时'),
    }),
  )

  Mock.mock(new RegExp(`${baseUrl}/nutrition/recipes/\\d+$`), 'get', (options) =>
    withScenario('GET /nutrition/recipes/{recipeId}', {
      success: () =>
        success({
          recipeId: idFrom(options.url, /recipes\/(\d+)/),
          ...createNutritionSummary(),
          suitability: Mock.Random.integer(62, 98),
        }),
      empty: () => success(null),
      error: () => failure(40400, '菜谱营养数据不存在'),
      timeout: () => failure(40800, '菜谱营养加载超时'),
    }),
  )

  Mock.mock(new RegExp(`${baseUrl}/nutrition-risk-rules(\\?.*)?$`), 'get', (options) =>
    withScenario('GET /nutrition-risk-rules', {
      success: () => success(paginate(rules, parseQuery(options.url))),
      empty: () => success(page([], 1, 10, 0)),
      error: () => failure(40300, '无权查看风险规则'),
      timeout: () => failure(40800, '风险规则加载超时'),
    }),
  )

  Mock.mock(`${baseUrl}/nutrition-risk-rules`, 'post', (options) =>
    withScenario('POST /nutrition-risk-rules', {
      success: () => {
        const rule = createRiskRule({ ...parseBody(options), id: Mock.Random.integer(900, 999), status: 'ENABLED' })
        rules = [rule, ...rules]
        return success(rule)
      },
      empty: () => success(null),
      error: () =>
        failure(40001, '风险规则校验失败', {
          fieldErrors: [{ field: 'name', message: '规则名称不能为空' }],
        }),
      timeout: () => failure(40800, '新增风险规则超时'),
    }),
  )

  Mock.mock(new RegExp(`${baseUrl}/nutrition-risk-rules/\\d+$`), 'put', (options) =>
    withScenario('PUT /nutrition-risk-rules/{id}', {
      success: () => {
        const id = idFrom(options.url, /nutrition-risk-rules\/(\d+)/)
        const next = { ...rules.find((item) => item.id === id), ...parseBody(options), id }
        rules = rules.map((item) => (item.id === id ? next : item))
        return success(next)
      },
      empty: () => success(null),
      error: () => failure(40001, '风险规则校验失败'),
      timeout: () => failure(40800, '更新风险规则超时'),
    }),
  )

  Mock.mock(new RegExp(`${baseUrl}/nutrition-risk-rules/\\d+/status$`), 'patch', (options) =>
    withScenario('PATCH /nutrition-risk-rules/{id}/status', {
      success: () => {
        const id = idFrom(options.url, /nutrition-risk-rules\/(\d+)/)
        const body = parseBody(options)
        rules = rules.map((item) => (item.id === id ? { ...item, status: body.status || (item.status === 'ENABLED' ? 'DISABLED' : 'ENABLED') } : item))
        return success(rules.find((item) => item.id === id))
      },
      empty: () => success(null),
      error: () => failure(40300, '无权修改风险规则状态'),
      timeout: () => failure(40800, '修改风险规则状态超时'),
    }),
  )

  Mock.mock(`${baseUrl}/nutrition-risk-rules/evaluate`, 'post', () =>
    withScenario('POST /nutrition-risk-rules/evaluate', {
      success: () => success(Array.from({ length: 4 }, () => createNutritionRisk({ source: 'MANUAL_EVALUATION' }))),
      empty: () => success([]),
      error: () => failure(50200, '风险规则执行失败'),
      timeout: () => failure(40800, '风险规则执行超时'),
    }),
  )

  Mock.mock(`${baseUrl}/nutrition-reports/weekly`, 'post', (options) =>
    withScenario('POST /nutrition-reports/weekly', {
      success: () => {
        const report = createNutritionReport({ ...parseBody(options), type: 'WEEKLY', status: 'READY' })
        reports = [report, ...reports]
        return success(report)
      },
      empty: () => success(null),
      error: () => failure(50200, '周报生成失败'),
      timeout: () => failure(40800, '周报生成超时'),
    }),
  )

  Mock.mock(`${baseUrl}/nutrition-reports/monthly`, 'post', (options) =>
    withScenario('POST /nutrition-reports/monthly', {
      success: () => {
        const report = createNutritionReport({ ...parseBody(options), type: 'MONTHLY', status: 'READY' })
        reports = [report, ...reports]
        return success(report)
      },
      empty: () => success(null),
      error: () => failure(50200, '月报生成失败'),
      timeout: () => failure(40800, '月报生成超时'),
    }),
  )

  Mock.mock(new RegExp(`${baseUrl}/nutrition-reports(\\?.*)?$`), 'get', (options) =>
    withScenario('GET /nutrition-reports', {
      success: () => success(paginate(reports, parseQuery(options.url))),
      empty: () => success(page([], 1, 10, 0)),
      error: () => failure(50000, '营养报告列表加载失败'),
      timeout: () => failure(40800, '营养报告列表加载超时'),
    }),
  )

  Mock.mock(new RegExp(`${baseUrl}/nutrition-reports/\\d+$`), 'get', (options) =>
    withScenario('GET /nutrition-reports/{id}', {
      success: () => success(reports.find((item) => item.id === idFrom(options.url, /nutrition-reports\/(\d+)/)) || null),
      empty: () => success(null),
      error: () => failure(40400, '营养报告不存在'),
      timeout: () => failure(40800, '营养报告详情加载超时'),
    }),
  )

  Mock.mock(new RegExp(`${baseUrl}/nutrition-reports/\\d+$`), 'delete', (options) =>
    withScenario('DELETE /nutrition-reports/{id}', {
      success: () => {
        const id = idFrom(options.url, /nutrition-reports\/(\d+)/)
        reports = reports.filter((item) => item.id !== id)
        return success(true)
      },
      empty: () => success(false),
      error: () => failure(40300, '无权删除营养报告'),
      timeout: () => failure(40800, '删除营养报告超时'),
    }),
  )
}
