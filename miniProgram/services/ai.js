const ENDPOINTS = require('../api/endpoints')
const { AI_REQUEST_TIMEOUT } = require('../config/index')
const { get, post, patch, del, upload } = require('../utils/request')

function normalizeAiRecipe(data) {
  if (!data) return data
  const nutrition = data.nutritionEstimate || {}
  return Object.assign({}, data, {
    name: data.name || data.recipeName,
    confidence: data.confidence != null ? data.confidence : (data.suitabilityScore || 0),
    totalCalorie: data.totalCalorie != null ? data.totalCalorie : (nutrition.calories || 0),
    steps: data.steps || data.cookingSteps || [],
    sourceImageUrl: data.sourceImageUrl || '',
  })
}

function parseAiRecipeText(payload) {
  return post(ENDPOINTS.aiRecipes.parse, payload, { timeout: AI_REQUEST_TIMEOUT }).then(normalizeAiRecipe)
}

function parseAiRecipeImage(filePath, prompt) {
  return upload(
    ENDPOINTS.aiRecipes.parseImage,
    filePath,
    prompt ? { prompt } : {},
    { timeout: AI_REQUEST_TIMEOUT },
  ).then(normalizeAiRecipe)
}

function fetchAiRecipeHistory(params) {
  return get(ENDPOINTS.aiRecipes.history, params).then((result) => {
    if (!result || !result.items) return result
    return Object.assign({}, result, { items: result.items.map(normalizeAiRecipe) })
  })
}

function fetchAiRecipeDetail(id) {
  return get(ENDPOINTS.aiRecipes.byId(id)).then(normalizeAiRecipe)
}

function confirmAiRecipe(id) {
  return patch(ENDPOINTS.aiRecipes.confirm(id), {}).then(normalizeAiRecipe)
}

function deleteAiRecipe(id) {
  return del(ENDPOINTS.aiRecipes.byId(id))
}

module.exports = {
  parseAiRecipeText,
  parseAiRecipeImage,
  fetchAiRecipeHistory,
  fetchAiRecipeDetail,
  confirmAiRecipe,
  deleteAiRecipe,
}
