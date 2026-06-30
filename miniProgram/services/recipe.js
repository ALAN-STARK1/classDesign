const ENDPOINTS = require('../api/endpoints')
const { get, post, del } = require('../utils/request')

function normalizeIngredient(item) {
  if (!item) return item
  return Object.assign({}, item, {
    caloriePer100g: item.caloriePer100g != null ? item.caloriePer100g : item.calorie,
    proteinPer100g: item.proteinPer100g != null ? item.proteinPer100g : item.protein,
    fatPer100g: item.fatPer100g != null ? item.fatPer100g : item.fat,
    carbohydratePer100g: item.carbohydratePer100g != null ? item.carbohydratePer100g : item.carbohydrate,
  })
}

function fetchIngredients(params) {
  return get(ENDPOINTS.ingredients.base, params).then((result) => {
    if (!result || !result.items) return result
    return Object.assign({}, result, {
      items: result.items.map(normalizeIngredient),
    })
  })
}

function fetchRecipes(params) {
  return get(ENDPOINTS.recipes.base, params)
}

function fetchRecipeDetail(id) {
  return get(ENDPOINTS.recipes.byId(id))
}

function favoriteRecipe(id) {
  return post(ENDPOINTS.recipes.favorite(id))
}

function unfavoriteRecipe(id) {
  return del(ENDPOINTS.recipes.favorite(id))
}

function fetchRecipeSuitability(id) {
  return get(ENDPOINTS.recipes.suitability(id))
}

module.exports = {
  fetchIngredients,
  fetchRecipes,
  fetchRecipeDetail,
  favoriteRecipe,
  unfavoriteRecipe,
  fetchRecipeSuitability,
}
