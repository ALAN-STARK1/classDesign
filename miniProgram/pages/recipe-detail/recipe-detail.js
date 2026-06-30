const recipeService = require('../../services/recipe')
const mealService = require('../../services/meal')
const { today } = require('../../utils/date')
const { MealTypeLabel } = require('../../constants/labels')
const { showError } = require('../../utils/request')

Page({
  data: {
    id: null,
    loading: true,
    recipe: null,
    suitability: null,
    mealTypes: ['BREAKFAST', 'LUNCH', 'DINNER', 'SNACK'],
    mealTypeIndex: 1,
  },

  onLoad(options) {
    if (!getApp().ensureLogin()) return
    this.setData({ id: options.id })
    this.loadDetail()
  },

  async loadDetail() {
    try {
      const [recipe, suitability] = await Promise.all([
        recipeService.fetchRecipeDetail(this.data.id),
        recipeService.fetchRecipeSuitability(this.data.id).catch(() => null),
      ])
      this.setData({ recipe, suitability, loading: false })
      wx.setNavigationBarTitle({ title: recipe.name || '菜谱详情' })
    } catch (err) {
      showError(err)
      this.setData({ loading: false })
    }
  },

  async onFavorite() {
    try {
      if (this.data.recipe.favorited) {
        await recipeService.unfavoriteRecipe(this.data.id)
        wx.showToast({ title: '已取消收藏', icon: 'none' })
      } else {
        await recipeService.favoriteRecipe(this.data.id)
        wx.showToast({ title: '已收藏', icon: 'success' })
      }
      this.loadDetail()
    } catch (err) {
      showError(err)
    }
  },

  onMealTypeChange(e) {
    this.setData({ mealTypeIndex: Number(e.detail.value) })
  },

  async onRecordMeal() {
    try {
      await mealService.createMealRecordFromRecipe(this.data.id, {
        date: today(),
        mealType: this.data.mealTypes[this.data.mealTypeIndex],
        servingRatio: 1,
      })
      wx.showToast({ title: '已记入膳食', icon: 'success' })
    } catch (err) {
      showError(err)
    }
  },

  MealTypeLabel,
})
