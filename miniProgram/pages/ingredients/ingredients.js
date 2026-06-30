const recipeService = require('../../services/recipe')
const { showError } = require('../../utils/request')

Page({
  data: {
    keyword: '',
    loading: true,
    items: [],
    showPairings: false,
    pairingsLoading: false,
    pairings: [],
    pairingName: '',
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

  async onShowPairings(e) {
    const id = e.currentTarget.dataset.id
    const name = e.currentTarget.dataset.name
    this.setData({
      showPairings: true,
      pairingsLoading: true,
      pairings: [],
      pairingName: name,
    })
    try {
      const pairings = await recipeService.fetchIngredientPairings(id, { limit: 10 })
      this.setData({ pairings: pairings || [], pairingsLoading: false })
    } catch (err) {
      showError(err)
      this.setData({ pairingsLoading: false })
    }
  },

  onClosePairings() {
    this.setData({ showPairings: false })
  },
})
