import { ENDPOINTS } from '../../api/endpoints'
import { AI_REQUEST_TIMEOUT, request } from '../request/http'

function normalizeAiRecipe(data) {
  if (!data) return data
  const nutrition = data.nutritionEstimate || {}
  return {
    ...data,
    name: data.name || data.recipeName,
    confidence: data.confidence ?? data.suitabilityScore ?? 0,
    totalCalorie: data.totalCalorie ?? nutrition.calories ?? 0,
    totalProtein: data.totalProtein ?? nutrition.protein ?? 0,
    totalFat: data.totalFat ?? nutrition.fat ?? 0,
    totalCarbohydrate: data.totalCarbohydrate ?? nutrition.carbohydrate ?? 0,
    steps: data.steps || data.cookingSteps || [],
    ingredients: data.ingredients || [],
    warnings: data.warnings || [],
    sourceImageUrl: data.sourceImageUrl,
  }
}

export function parseAiRecipeText(payload) {
  return request.post(ENDPOINTS.aiRecipes.parse, payload, { timeout: AI_REQUEST_TIMEOUT }).then(normalizeAiRecipe)
}

export function parseAiRecipeImage(file, prompt = '') {
  const formData = new FormData()
  formData.append('file', file)
  if (prompt) {
    formData.append('prompt', prompt)
  }
  return request
    .post(ENDPOINTS.aiRecipes.parseImage, formData, { timeout: AI_REQUEST_TIMEOUT })
    .then(normalizeAiRecipe)
}

export function fetchAiRecipeDetail(id) {
  return request.get(ENDPOINTS.aiRecipes.byId(id)).then(normalizeAiRecipe)
}

export function fetchAiRecipeHistory(params) {
  return request.get(ENDPOINTS.aiRecipes.history, { params }).then((result) => {
    if (!result?.items) return result
    return {
      ...result,
      items: result.items.map(normalizeAiRecipe),
    }
  })
}

export function confirmAiRecipe(id, payload = {}) {
  return request.patch(ENDPOINTS.aiRecipes.confirm(id), payload).then(normalizeAiRecipe)
}

export function convertAiRecipeToRecipe(id, payload = {}) {
  return request.post(ENDPOINTS.aiRecipes.toRecipe(id), payload)
}

export function convertAiRecipeToMealRecord(id, payload = {}) {
  return request.post(ENDPOINTS.aiRecipes.toMealRecord(id), payload)
}

export function deleteAiRecipe(id) {
  return request.delete(ENDPOINTS.aiRecipes.byId(id))
}
