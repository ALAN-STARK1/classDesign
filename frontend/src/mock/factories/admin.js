import Mock from 'mockjs'
import dayjs from 'dayjs'
import { AiRecipeSourceType, AiRecipeStatus, CommunityPostStatus, RecipeDifficulty, RecipeStatus, ReportType, RiskSeverity, UserRole, UserStatus } from '../../constants/enums'
import { today } from '../../utils/date'
import { pick } from '../helpers'

export function createAdminUser(overrides = {}) {
  return {
    id: Mock.Random.integer(1001, 9999),
    username: Mock.Random.word(4, 8) + Mock.Random.integer(0, 99),
    email: Mock.Random.email(),
    role: pick(UserRole),
    status: pick(UserStatus),
    createdAt: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss'),
    lastLoginAt: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss'),
    healthProfileExists: Mock.Random.boolean(),
    goalCycleCount: Mock.Random.integer(0, 5),
    recipeCount: Mock.Random.integer(0, 12),
    ...overrides,
  }
}

export function createAdminReviewRecipe(overrides = {}) {
  return {
    id: Mock.Random.integer(1001, 9999),
    name: `${pick(['鸡胸肉', '番茄', '西兰花', '牛肉', '豆腐'])}${pick(['蔬菜碗', '炒蛋', '沙拉', '汤'])}`,
    author: Mock.Random.cname(),
    authorId: Mock.Random.integer(1001, 9999),
    category: pick(['BREAKFAST', 'LUNCH', 'DINNER', 'SNACK']),
    difficulty: pick(RecipeDifficulty),
    status: pick(RecipeStatus.filter((s) => s !== 'ONLINE')),
    submittedAt: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss'),
    description: pick(['高蛋白低脂餐', '家常快手菜', '适合控糖人群', '轻负担晚餐']),
    ...overrides,
  }
}

export function createAdminReviewPost(overrides = {}) {
  return {
    id: Mock.Random.integer(2000, 9999),
    title: pick(['今日健康午餐分享', '低卡路里食谱推荐', '一周膳食小结', '高蛋白餐打卡']),
    author: Mock.Random.cname(),
    authorId: Mock.Random.integer(1001, 9999),
    status: pick(CommunityPostStatus.filter((s) => s !== 'PUBLISHED')),
    submittedAt: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss'),
    content: Mock.Random.paragraph(1, 3),
    ...overrides,
  }
}

export function createAiCallLog(overrides = {}) {
  const model = pick(['fable-5', 'opus-4-8', 'sonnet-4-6', 'haiku-4-5'])
  const endpoint = pick([
    '/ai-recipes/parse',
    '/ai-recipes/parse-image',
    '/meal-plans/generate/day',
    '/nutrition-risk-rules/evaluate',
  ])
  return {
    id: Mock.Random.guid(),
    model,
    endpoint,
    callerId: Mock.Random.integer(1001, 9999),
    callerName: Mock.Random.cname(),
    latencyMs: Mock.Random.integer(800, 14000),
    inputTokens: Mock.Random.integer(120, 4800),
    outputTokens: Mock.Random.integer(60, 3200),
    status: pick(['SUCCESS', 'SUCCESS', 'SUCCESS', 'ERROR', 'TIMEOUT']),
    errorMessage: '',
    createdAt: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss'),
    ...overrides,
  }
}

export function createNutritionRiskResult(overrides = {}) {
  return {
    id: Mock.Random.integer(8000, 9999),
    userId: Mock.Random.integer(1001, 9999),
    username: Mock.Random.cname(),
    severity: pick(RiskSeverity),
    ruleName: pick(['钠摄入上限', '蛋白质下限', '热量缺口', '脂肪占比', '碳水波动']),
    value: Mock.Random.float(0, 3500, 1, 1),
    threshold: Mock.Random.float(20, 2400, 1, 1),
    triggeredAt: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss'),
    date: dayjs().subtract(Mock.Random.integer(0, 6), 'day').format('YYYY-MM-DD'),
    ...overrides,
  }
}

export function createReportStatistics(overrides = {}) {
  return {
    totalReports: Mock.Random.integer(45, 280),
    weeklyCount: Mock.Random.integer(12, 80),
    monthlyCount: Mock.Random.integer(6, 36),
    averageScore: Mock.Random.integer(68, 92),
    userReportRate: Mock.Random.float(0.22, 0.78, 2, 2),
    riskTriggerRate: Mock.Random.float(0.08, 0.42, 2, 2),
    topRisks: [
      { ruleName: '钠摄入上限', count: Mock.Random.integer(30, 120) },
      { ruleName: '热量缺口', count: Mock.Random.integer(20, 90) },
      { ruleName: '蛋白质下限', count: Mock.Random.integer(10, 60) },
    ],
    dailyReportTrend: Array.from({ length: 7 }, (_, i) => ({
      date: dayjs().subtract(6 - i, 'day').format('YYYY-MM-DD'),
      count: Mock.Random.integer(3, 22),
    })),
    ...overrides,
  }
}

export function createDemoDataResult(overrides = {}) {
  return {
    success: true,
    message: '演示数据已重置',
    usersCreated: Mock.Random.integer(8, 24),
    recipesCreated: Mock.Random.integer(20, 60),
    recordsCreated: Mock.Random.integer(60, 200),
    timestamp: new Date().toISOString(),
    ...overrides,
  }
}
