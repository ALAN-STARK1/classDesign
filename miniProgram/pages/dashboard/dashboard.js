const healthService = require('../../services/health')
const nutritionService = require('../../services/nutrition')
const mealService = require('../../services/meal')
const { today } = require('../../utils/date')
const { formatCalorie, percent, labelOf } = require('../../utils/format')
const { HealthGoalLabel } = require('../../constants/labels')
const { showError } = require('../../utils/request')

Page({
  data: {
    loading: true,
    date: today(),
    summary: null,
    nutrition: null,
    goalCycle: null,
    plan: null,
    risks: [],
  },

  onShow() {
    if (!getApp().ensureLogin()) return
    this.loadData()
  },

  async loadData() {
    this.setData({ loading: true })
    const date = this.data.date
    try {
      const [summary, nutrition, goalCycle, plan, risks] = await Promise.all([
        healthService.fetchHealthSummary().catch(() => null),
        nutritionService.fetchTodayNutrition({ date }).catch(() => null),
        healthService.fetchCurrentGoalCycle().catch(() => null),
        mealService.fetchDayMealPlan({ date }).catch(() => null),
        nutritionService.fetchNutritionRisks({ days: 7 }).catch(() => []),
      ])
      this.setData({
        summary,
        nutrition,
        goalCycle: goalCycle ? Object.assign({}, goalCycle, {
          goalLabel: labelOf(HealthGoalLabel, goalCycle.goalType),
        }) : null,
        plan,
        risks: (risks || []).slice(0, 3),
        loading: false,
      })
    } catch (err) {
      showError(err)
      this.setData({ loading: false })
    }
  },

  formatCalorie,
  percent,

  goNutrition() {
    wx.navigateTo({ url: '/pages/nutrition/nutrition' })
  },

  goMealRecords() {
    wx.navigateTo({ url: '/pages/meal-records/meal-records' })
  },

  goMealPlans() {
    wx.switchTab({ url: '/pages/meal-plans/meal-plans' })
  },
})
