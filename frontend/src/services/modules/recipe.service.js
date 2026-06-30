import { ENDPOINTS } from '../../api/endpoints'
import { request } from '../request/http'

function normalizeIngredient(item) {
  if (!item) return item
  return {
    ...item,
    caloriePer100g: item.caloriePer100g ?? item.calorie,
    proteinPer100g: item.proteinPer100g ?? item.protein,
    fatPer100g: item.fatPer100g ?? item.fat,
    carbohydratePer100g: item.carbohydratePer100g ?? item.carbohydrate,
  }
}

function toIngredientPayload(payload) {
  return {
    name: payload.name,
    category: payload.category,
    unit: payload.unit,
    calorie: payload.calorie ?? payload.caloriePer100g,
    protein: payload.protein ?? payload.proteinPer100g,
    fat: payload.fat ?? payload.fatPer100g,
    carbohydrate: payload.carbohydrate ?? payload.carbohydratePer100g,
    sodium: payload.sodium,
    status: payload.status,
  }
}

export function fetchIngredients(params) {
  return request.get(ENDPOINTS.ingredients.base, { params }).then((result) => {
    if (!result?.items) return result
    return {
      ...result,
      items: result.items.map(normalizeIngredient),
    }
  })
}

export function fetchIngredientDetail(id) {
  return request.get(ENDPOINTS.ingredients.byId(id)).then(normalizeIngredient)
}

export function createIngredient(payload) {
  return request.post(ENDPOINTS.ingredients.base, toIngredientPayload(payload)).then(normalizeIngredient)
}

export function updateIngredient(id, payload) {
  return request.put(ENDPOINTS.ingredients.byId(id), toIngredientPayload(payload)).then(normalizeIngredient)
}

export function disableIngredient(id) {
  return request.patch(ENDPOINTS.ingredients.disable(id))
}

export function fetchIngredientPairings(id, params) {
  return request.get(ENDPOINTS.ingredients.pairings(id), { params })
}

export function fetchRecipes(params) {
  return request.get(ENDPOINTS.recipes.base, { params })
}

export function fetchRecipeDetail(id) {
  return request.get(ENDPOINTS.recipes.byId(id))
}

export function createRecipe(payload) {
  return request.post(ENDPOINTS.recipes.base, payload)
}

export function updateRecipe(id, payload) {
  return request.put(ENDPOINTS.recipes.byId(id), payload)
}

export function bindRecipeIngredients(id, ingredients) {
  return request.put(ENDPOINTS.recipes.ingredients(id), { ingredients })
}

export function calculateRecipeNutrition(id) {
  return request.post(ENDPOINTS.recipes.nutritionCalculate(id))
}

export function fetchRecipeSuitability(id) {
  return request.get(ENDPOINTS.recipes.suitability(id))
}

export function recalculateRecipeSuitability(payload = {}) {
  return request.post(ENDPOINTS.recipes.suitabilityRecalculate, payload)
}

export function favoriteRecipe(id) {
  return request.post(ENDPOINTS.recipes.favorite(id))
}

export function unfavoriteRecipe(id) {
  return request.delete(ENDPOINTS.recipes.favorite(id))
}
