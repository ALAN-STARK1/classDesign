const nutritionService = require('../../services/nutrition')
const { today } = require('../../utils/date')
const { showError } = require('../../utils/request')

Page({
  data: {
    date: today(),
    loading: true,
    summary: null,
    gaps: [],
    risks: [],
  },

  onShow() {
    if (!getApp().ensureLogin()) return
    this.loadData()
  },

  onDateChange(e) {
    this.setData({ date: e.detail.value }, () => this.loadData())
  },

  async loadData() {
    this.setData({ loading: true })
    const { date } = this.data
    try {
      const [summary, gaps, risks] = await Promise.all([
        nutritionService.fetchTodayNutrition({ date }),
        nutritionService.fetchNutritionGap({ date }),
        nutritionService.fetchNutritionRisks({ days: 14 }),
      ])
      this.setData({ summary, gaps: gaps || [], risks: risks || [], loading: false })
    } catch (err) {
      showError(err)
      this.setData({ loading: false })
    }
  },

  goReports() {
    wx.navigateTo({ url: '/pages/nutrition-reports/nutrition-reports' })
  },
})
