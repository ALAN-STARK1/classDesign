const recipeService = require('../../services/recipe')
const { showError } = require('../../utils/request')

Page({
  data: {
    keyword: '',
    loading: true,
    items: [],
  },

  onShow() {
    if (!getApp().ensureLogin()) return
    this.loadData()
  },

  onKeywordInput(e) {
    this.setData({ keyword: e.detail.value })
  },

  onSearch() {
    this.loadData()
  },

  async loadData() {
    this.setData({ loading: true })
    try {
      const result = await recipeService.fetchIngredients({
        keyword: this.data.keyword,
        page: 1,
        pageSize: 50,
      })
      this.setData({ items: result.items || [], loading: false })
    } catch (err) {
      showError(err)
      this.setData({ loading: false })
    }
  },
})
