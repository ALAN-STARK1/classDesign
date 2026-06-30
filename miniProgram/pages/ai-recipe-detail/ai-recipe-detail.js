const aiService = require('../../services/ai')
const { resolveUploadUrl } = require('../../utils/media')
const { showError } = require('../../utils/request')

const PREVIEW_KEY = 'ai_recipe_local_preview'

Page({
  data: {
    id: null,
    loading: true,
    recipe: null,
    sourceImage: '',
  },

  onLoad(options) {
    if (!getApp().ensureLogin()) return
    this.setData({ id: options.id })
    this.loadDetail()
  },

  onUnload() {
    const cached = wx.getStorageSync(PREVIEW_KEY)
    if (cached && String(cached.id) === String(this.data.id)) {
      wx.removeStorageSync(PREVIEW_KEY)
    }
  },

  resolveSourceImage(recipe) {
    const cached = wx.getStorageSync(PREVIEW_KEY)
    if (cached && String(cached.id) === String(this.data.id) && cached.filePath) {
      return cached.filePath
    }
    return resolveUploadUrl(recipe && recipe.sourceImageUrl)
  },

  async loadDetail() {
    try {
      const recipe = await aiService.fetchAiRecipeDetail(this.data.id)
      this.setData({
        recipe,
        sourceImage: this.resolveSourceImage(recipe),
        loading: false,
      })
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
