const aiService = require('../../services/ai')
const { showError } = require('../../utils/request')

const PREVIEW_KEY = 'ai_recipe_local_preview'

Page({
  data: {
    text: '',
    parsing: false,
    imageParsing: false,
    loading: true,
    history: [],
  },

  onShow() {
    if (!getApp().ensureLogin()) return
    this.loadHistory()
  },

  onTextInput(e) {
    this.setData({ text: e.detail.value })
  },

  async onParse() {
    if (!this.data.text.trim()) {
      wx.showToast({ title: '请输入菜谱文本', icon: 'none' })
      return
    }
    this.setData({ parsing: true })
    try {
      const result = await aiService.parseAiRecipeText({ text: this.data.text })
      wx.navigateTo({ url: `/pages/ai-recipe-detail/ai-recipe-detail?id=${result.id}` })
      this.setData({ text: '' })
      this.loadHistory()
    } catch (err) {
      showError(err)
    } finally {
      this.setData({ parsing: false })
    }
  },

  onChooseImage() {
    wx.chooseMedia({
      count: 1,
      mediaType: ['image'],
      sourceType: ['album', 'camera'],
      success: (res) => {
        const filePath = res.tempFiles[0].tempFilePath
        this.uploadImage(filePath)
      },
    })
  },

  async uploadImage(filePath) {
    this.setData({ imageParsing: true })
    try {
      const result = await aiService.parseAiRecipeImage(filePath)
      wx.setStorageSync(PREVIEW_KEY, { id: result.id, filePath })
      wx.navigateTo({ url: `/pages/ai-recipe-detail/ai-recipe-detail?id=${result.id}` })
      this.loadHistory()
    } catch (err) {
      showError(err)
    } finally {
      this.setData({ imageParsing: false })
    }
  },

  async loadHistory() {
    this.setData({ loading: true })
    try {
      const result = await aiService.fetchAiRecipeHistory({ page: 1, pageSize: 20 })
      this.setData({ history: result.items || [], loading: false })
    } catch (err) {
      showError(err)
      this.setData({ loading: false })
    }
  },

  goDetail(e) {
    wx.navigateTo({ url: `/pages/ai-recipe-detail/ai-recipe-detail?id=${e.currentTarget.dataset.id}` })
  },
})
