import Mock from 'mockjs'
import dayjs from 'dayjs'
import { ActivityLevel, Gender, HealthGoal } from '../../constants/enums'
import { addDays, today } from '../../utils/date'
import { pick } from '../helpers'

export function createHealthProfile(overrides = {}) {
  const weightKg = Mock.Random.float(55, 86, 1, 1)
  const heightCm = Mock.Random.integer(158, 180)
  const bmi = Number((weightKg / ((heightCm / 100) ** 2)).toFixed(2))
  return {
    id: 1,
    gender: pick(Gender),
    birthday: '2000-01-01',
    heightCm,
    weightKg,
    targetWeightKg: Number((weightKg - Mock.Random.float(3, 8, 1, 1)).toFixed(1)),
    activityLevel: pick(ActivityLevel),
    healthGoal: pick(HealthGoal),
    bmi,
    dailyCalorieTarget: Mock.Random.integer(1450, 2100),
    allergens: ['花生', '虾'],
    restrictions: ['少盐', '少糖'],
    ...overrides,
  }
}

export function createHealthSummary(profile = createHealthProfile()) {
  return {
    bmi: profile.bmi,
    bmiStatus: profile.bmi < 18.5 ? '偏低' : profile.bmi > 24 ? '偏高' : '正常',
    bmr: Mock.Random.integer(1250, 1650),
    tdee: Mock.Random.integer(1750, 2350),
    dailyCalorieTarget: profile.dailyCalorieTarget,
  }
}

export function createGoalCycle(overrides = {}) {
  const startWeightKg = Mock.Random.float(60, 75, 1, 1)
  return {
    id: Mock.Random.integer(10, 999),
    goalType: 'FAT_LOSS',
    startDate: today(),
    endDate: addDays(today(), 56),
    startWeightKg,
    targetWeightKg: Number((startWeightKg - 5.5).toFixed(1)),
    targetCalorie: 1600,
    weeklyTargetDeltaKg: -0.75,
    progressPercent: Mock.Random.integer(18, 72),
    status: 'ACTIVE',
    ...overrides,
  }
}

export function createGoalProgress(cycle = createGoalCycle()) {
  const trend = Array.from({ length: 8 }, (_, index) => {
    const weightKg = cycle.startWeightKg - index * 0.35 + Mock.Random.float(-0.15, 0.2, 1, 1)
    return {
      date: dayjs(cycle.startDate).add(index * 7, 'day').format('YYYY-MM-DD'),
      weightKg: Number(weightKg.toFixed(1)),
    }
  })
  const currentWeightKg = trend.at(-1).weightKg
  const expectedWeightKg = Number((cycle.startWeightKg - 2.5).toFixed(1))
  return {
    cycleId: cycle.id,
    progressPercent: cycle.progressPercent,
    currentWeightKg,
    expectedWeightKg,
    deviationKg: Number((currentWeightKg - expectedWeightKg).toFixed(1)),
    warning: Math.abs(currentWeightKg - expectedWeightKg) > 1,
    trend,
  }
}
