const authService = require('../../services/auth')
const { showError } = require('../../utils/request')

Page({
  data: {
    user: null,
    menus: [
      { title: '健康档案', url: '/pages/health-profile/health-profile' },
      { title: '健康标签', url: '/pages/health-tags/health-tags' },
      { title: '体重记录', url: '/pages/weight-records/weight-records' },
      { title: '目标周期', url: '/pages/goal-cycles/goal-cycles' },
      { title: '膳食记录', url: '/pages/meal-records/meal-records' },
      { title: '营养分析', url: '/pages/nutrition/nutrition' },
      { title: '营养报告', url: '/pages/nutrition-reports/nutrition-reports' },
      { title: 'AI 膳食顾问', url: '/pages/ai-advisor/ai-advisor' },
      { title: 'AI 菜谱', url: '/pages/ai-recipes/ai-recipes' },
      { title: '食材库', url: '/pages/ingredients/ingredients' },
      { title: '社区', url: '/pages/community/community' },
    ],
  },

  onShow() {
    if (!getApp().ensureLogin()) return
    this.loadUser()
  },

  async loadUser() {
    try {
      const user = await authService.fetchMe()
      this.setData({ user })
      getApp().setAuth(getApp().globalData.token, user)
    } catch (err) {
      this.setData({ user: getApp().globalData.user })
    }
  },

  goPage(e) {
    wx.navigateTo({ url: e.currentTarget.dataset.url })
  },

  onLogout() {
    getApp().clearAuth()
    wx.reLaunch({ url: '/pages/login/login' })
  },
})
