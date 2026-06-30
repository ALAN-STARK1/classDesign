const recipeService = require('../../services/recipe')
const { showError } = require('../../utils/request')

Page({
  data: {
    keyword: '',
    loading: true,
    items: [],
    page: 1,
    pageSize: 20,
    total: 0,
  },

  onShow() {
    if (!getApp().ensureLogin()) return
    this.loadRecipes(true)
  },

  onKeywordInput(e) {
    this.setData({ keyword: e.detail.value })
  },

  onSearch() {
    this.loadRecipes(true)
  },

  async loadRecipes(reset) {
    const page = reset ? 1 : this.data.page
    this.setData({ loading: true })
    try {
      const result = await recipeService.fetchRecipes({
        keyword: this.data.keyword,
        page,
        pageSize: this.data.pageSize,
      })
      const items = reset ? (result.items || []) : this.data.items.concat(result.items || [])
      this.setData({
        items,
        page: page + 1,
        total: result.total || items.length,
        loading: false,
      })
    } catch (err) {
      showError(err)
      this.setData({ loading: false })
    }
  },

  onReachBottom() {
    if (this.data.items.length < this.data.total) {
      this.loadRecipes(false)
    }
  },

  goDetail(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({ url: `/pages/recipe-detail/recipe-detail?id=${id}` })
  },
})
