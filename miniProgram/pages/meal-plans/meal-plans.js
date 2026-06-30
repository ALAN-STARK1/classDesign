const mealService = require('../../services/meal')
const { today } = require('../../utils/date')
const { MealTypeLabel } = require('../../constants/labels')
const { labelOf } = require('../../utils/format')
const { showError } = require('../../utils/request')

Page({
  data: {
    date: today(),
    loading: true,
    generating: false,
    plan: null,
    items: [],
  },

  onShow() {
    if (!getApp().ensureLogin()) return
    this.loadPlan()
  },

  onDateChange(e) {
    this.setData({ date: e.detail.value }, () => this.loadPlan())
  },

  async loadPlan() {
    this.setData({ loading: true })
    try {
      const plan = await mealService.fetchDayMealPlan({ date: this.data.date })
      const items = (plan && plan.items ? plan.items : []).map((item) => Object.assign({}, item, {
        mealTypeLabel: labelOf(MealTypeLabel, item.mealType),
      }))
      this.setData({ plan, items, loading: false })
    } catch (err) {
      this.setData({ plan: null, items: [], loading: false })
      if (err.code !== 40400) showError(err)
    }
  },

  async onGenerate() {
    this.setData({ generating: true })
    try {
      await mealService.generateDayMealPlan({ date: this.data.date })
      wx.showToast({ title: '计划已生成', icon: 'success' })
      this.loadPlan()
    } catch (err) {
      showError(err)
    } finally {
      this.setData({ generating: false })
    }
  },

  goRecipeDetail(e) {
    const id = e.currentTarget.dataset.id
    if (id) wx.navigateTo({ url: `/pages/recipe-detail/recipe-detail?id=${id}` })
  },
})
