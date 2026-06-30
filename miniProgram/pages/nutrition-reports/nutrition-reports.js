const nutritionService = require('../../services/nutrition')
const { showError } = require('../../utils/request')

Page({
  data: {
    loading: true,
    reports: [],
    generating: false,
  },

  onShow() {
    if (!getApp().ensureLogin()) return
    this.loadReports()
  },

  async loadReports() {
    this.setData({ loading: true })
    try {
      const result = await nutritionService.fetchNutritionReports({ page: 1, pageSize: 20 })
      this.setData({ reports: result.items || result || [], loading: false })
    } catch (err) {
      showError(err)
      this.setData({ loading: false })
    }
  },

  async onGenerateWeekly() {
    this.setData({ generating: true })
    try {
      await nutritionService.generateWeeklyReport({})
      wx.showToast({ title: '周报已生成', icon: 'success' })
      this.loadReports()
    } catch (err) {
      showError(err)
    } finally {
      this.setData({ generating: false })
    }
  },

  async onGenerateMonthly() {
    this.setData({ generating: true })
    try {
      await nutritionService.generateMonthlyReport({})
      wx.showToast({ title: '月报已生成', icon: 'success' })
      this.loadReports()
    } catch (err) {
      showError(err)
    } finally {
      this.setData({ generating: false })
    }
  },

  goDetail(e) {
    const id = e.currentTarget.dataset.id
    if (id) wx.navigateTo({ url: `/pages/nutrition-report-detail/nutrition-report-detail?id=${id}` })
  },
})
