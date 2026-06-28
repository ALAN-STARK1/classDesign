import { ENDPOINTS } from '../../api/endpoints'
import { request } from '../request/http'

export function fetchIngredients(params) {
  return request.get(ENDPOINTS.ingredients.base, { params })
}

export function fetchIngredientDetail(id) {
  return request.get(ENDPOINTS.ingredients.byId(id))
}

export function createIngredient(payload) {
  return request.post(ENDPOINTS.ingredients.base, payload)
}

export function updateIngredient(id, payload) {
  return request.put(ENDPOINTS.ingredients.byId(id), payload)
}

export function disableIngredient(id) {
  return request.patch(ENDPOINTS.ingredients.disable(id))
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
