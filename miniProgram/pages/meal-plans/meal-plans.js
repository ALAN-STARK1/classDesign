const mealService = require('../../services/meal')
const advisorService = require('../../services/advisor')
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
    showShopping: false,
    shoppingList: null,
    shoppingLoading: false,
    showReplace: false,
    replaceItem: null,
    replaceCandidates: [],
    replaceLoading: false,
    replacing: false,
    posterLoading: false,
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

  async onShowShopping() {
    if (!this.data.plan || !this.data.plan.id) {
      wx.showToast({ title: '请先生成计划', icon: 'none' })
      return
    }
    this.setData({ showShopping: true, shoppingLoading: true, shoppingList: null })
    try {
      const shoppingList = await mealService.fetchPlanShoppingList(this.data.plan.id)
      this.setData({ shoppingList, shoppingLoading: false })
    } catch (err) {
      showError(err)
      this.setData({ shoppingLoading: false })
    }
  },

  onCloseShopping() {
    this.setData({ showShopping: false })
  },

  async onOpenReplace(e) {
    const itemId = e.currentTarget.dataset.id
    const item = this.data.items.find((i) => i.id === itemId)
    if (!this.data.plan || !item) return
    this.setData({
      showReplace: true,
      replaceItem: item,
      replaceCandidates: [],
      replaceLoading: true,
    })
    try {
      const replaceCandidates = await mealService.fetchReplacementCandidates(
        this.data.plan.id,
        item.id,
        { limit: 5 },
      )
      this.setData({ replaceCandidates: replaceCandidates || [], replaceLoading: false })
    } catch (err) {
      showError(err)
      this.setData({ replaceLoading: false })
    }
  },

  onCloseReplace() {
    this.setData({ showReplace: false, replaceItem: null })
  },

  async onConfirmReplace(e) {
    const newRecipeId = e.currentTarget.dataset.recipeId
    const item = this.data.replaceItem
    if (!item || !newRecipeId) return
    this.setData({ replacing: true })
    try {
      await mealService.replacePlanItem(this.data.plan.id, item.id, { newRecipeId })
      wx.showToast({ title: '已替换', icon: 'success' })
      this.setData({ showReplace: false, replaceItem: null })
      this.loadPlan()
    } catch (err) {
      showError(err)
    } finally {
      this.setData({ replacing: false })
    }
  },

  async onWeekPoster() {
    if (!this.data.plan || !this.data.plan.id) {
      wx.showToast({ title: '请先生成计划', icon: 'none' })
      return
    }
    this.setData({ posterLoading: true })
    try {
      const result = await advisorService.weekPoster(this.data.plan.id)
      wx.showModal({
        title: '一周饮食海报',
        content: result.content || '暂无内容',
        showCancel: false,
      })
    } catch (err) {
      showError(err)
    } finally {
      this.setData({ posterLoading: false })
    }
  },
})
