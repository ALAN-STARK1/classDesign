import Mock from 'mockjs'
import dayjs from 'dayjs'
import { ReportStatus, ReportType, RiskRuleStatus, RiskSeverity } from '../../constants/enums'
import { today } from '../../utils/date'
import { pick } from '../helpers'

const nutrientNames = ['calorie', 'protein', 'fat', 'carbohydrate', 'fiber', 'sodium']
const nutrientLabel = {
  calorie: '热量',
  protein: '蛋白质',
  fat: '脂肪',
  carbohydrate: '碳水',
  fiber: '膳食纤维',
  sodium: '钠',
}

export function createNutritionSummary(overrides = {}) {
  const targetCalorie = Mock.Random.integer(1550, 1900)
  const calorie = Mock.Random.integer(1180, 2150)
  return {
    date: today(),
    calorie,
    targetCalorie,
    protein: Mock.Random.float(58, 112, 1, 1),
    targetProtein: 90,
    fat: Mock.Random.float(38, 76, 1, 1),
    targetFat: 60,
    carbohydrate: Mock.Random.float(145, 260, 1, 1),
    targetCarbohydrate: 210,
    fiber: Mock.Random.float(12, 32, 1, 1),
    targetFiber: 25,
    sodium: Mock.Random.integer(1400, 3100),
    targetSodium: 2000,
    score: Mock.Random.integer(68, 96),
    mealCount: Mock.Random.integer(2, 5),
    ...overrides,
  }
}

export function createNutritionRange(days = 14) {
  return Array.from({ length: days }, (_, index) => {
    const date = dayjs().subtract(days - index - 1, 'day').format('YYYY-MM-DD')
    return createNutritionSummary({
      date,
      calorie: Mock.Random.integer(1250, 2150),
      protein: Mock.Random.float(52, 118, 1, 1),
      fat: Mock.Random.float(30, 82, 1, 1),
      carbohydrate: Mock.Random.float(135, 278, 1, 1),
      score: Mock.Random.integer(62, 98),
    })
  })
}

export function createNutritionGap(overrides = {}) {
  return nutrientNames.map((name) => {
    const target = name === 'calorie' ? 1700 : name === 'sodium' ? 2000 : Mock.Random.integer(25, 120)
    const actual = Math.round(target * Mock.Random.float(0.62, 1.28, 2, 2))
    const gap = target - actual
    return {
      nutrient: name,
      label: nutrientLabel[name],
      actual,
      target,
      gap,
      status: gap > target * 0.12 ? 'LOW' : gap < -target * 0.12 ? 'HIGH' : 'OK',
      suggestion: gap > 0 ? `建议增加${nutrientLabel[name]}来源` : `注意控制${nutrientLabel[name]}摄入`,
      ...overrides[name],
    }
  })
}

export function createNutritionRisk(overrides = {}) {
  const severity = pick(RiskSeverity)
  const nutrient = pick(nutrientNames)
  return {
    id: Mock.Random.integer(8000, 9999),
    severity,
    nutrient,
    title: pick(['钠摄入偏高', '热量摄入不足', '蛋白质不足', '脂肪比例偏高', '碳水波动偏大']),
    description: pick(['连续两天超过建议范围', '今日记录低于目标下限', '餐次结构不均衡', '晚餐摄入占比偏高']),
    suggestion: pick(['减少高盐调味品', '补充优质蛋白', '增加蔬菜和全谷物', '晚餐控制油脂和主食份量']),
    triggeredAt: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss'),
    source: 'DAILY_EVALUATION',
    ...overrides,
  }
}

export function createRiskRule(overrides = {}) {
  return {
    id: Mock.Random.integer(300, 999),
    name: pick(['钠摄入上限', '蛋白质下限', '热量缺口', '脂肪占比', '碳水波动']),
    nutrient: pick(nutrientNames),
    operator: pick(['GT', 'LT', 'BETWEEN']),
    thresholdMin: Mock.Random.integer(20, 80),
    thresholdMax: Mock.Random.integer(90, 2400),
    severity: pick(RiskSeverity),
    status: pick(RiskRuleStatus),
    message: '触发后提示用户调整餐次结构',
    updatedAt: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss'),
    ...overrides,
  }
}

export function createNutritionReport(overrides = {}) {
  const type = overrides.type || pick(ReportType)
  const startDate = dayjs().subtract(type === 'WEEKLY' ? 7 : 30, 'day').format('YYYY-MM-DD')
  const endDate = today()
  return {
    id: Mock.Random.integer(4000, 9999),
    type,
    title: type === 'WEEKLY' ? '本周营养报告' : '本月营养报告',
    startDate,
    endDate,
    status: pick(ReportStatus.filter((item) => item !== 'GENERATING')),
    score: Mock.Random.integer(66, 96),
    averageCalorie: Mock.Random.integer(1450, 1950),
    averageProtein: Mock.Random.float(62, 104, 1, 1),
    riskCount: Mock.Random.integer(0, 5),
    summary: '整体摄入结构稳定，蛋白质供给较好，需继续关注钠摄入和晚餐热量占比。',
    recommendations: ['保持早餐蛋白质摄入', '每日至少两份蔬菜', '减少高盐调味品', '晚餐主食控制在目标范围内'],
    createdAt: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss'),
    ...overrides,
  }
}
