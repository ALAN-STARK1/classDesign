const aiService = require('../../services/ai')
const { showError } = require('../../utils/request')

Page({
  data: {
    id: null,
    loading: true,
    recipe: null,
  },

  onLoad(options) {
    if (!getApp().ensureLogin()) return
    this.setData({ id: options.id })
    this.loadDetail()
  },

  async loadDetail() {
    try {
      const recipe = await aiService.fetchAiRecipeDetail(this.data.id)
      this.setData({ recipe, loading: false })
      wx.setNavigationBarTitle({ title: recipe.name || 'AI 菜谱' })
    } catch (err) {
      showError(err)
      this.setData({ loading: false })
    }
  },

  async onConfirm() {
    try {
      await aiService.confirmAiRecipe(this.data.id)
      wx.showToast({ title: '已确认', icon: 'success' })
      this.loadDetail()
    } catch (err) {
      showError(err)
    }
  },

  async onDelete() {
    try {
      await aiService.deleteAiRecipe(this.data.id)
      wx.showToast({ title: '已删除', icon: 'success' })
      setTimeout(() => wx.navigateBack(), 500)
    } catch (err) {
      showError(err)
    }
  },
})
