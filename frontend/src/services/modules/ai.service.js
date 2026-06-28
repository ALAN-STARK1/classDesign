import { ENDPOINTS } from '../../api/endpoints'
import { request } from '../request/http'
import { bridgeParseImage, bridgeParseText } from '../ai-bridge'

// ===== AI 解析走 ai-bridge 直连 ai-service =====
// 其他 CRUD（history / detail / confirm / convert / delete）仍需后端 DB，继续走 Mock

export function parseAiRecipeText(payload) {
  return bridgeParseText(payload)
}

export function parseAiRecipeImage(file, prompt = '') {
  return bridgeParseImage(file, prompt)
}

// ===== 以下接口依赖后端 DB，暂保持 Mock =====

export function fetchAiRecipeDetail(id) {
  return request.get(ENDPOINTS.aiRecipes.byId(id))
}

export function fetchAiRecipeHistory(params) {
  return request.get(ENDPOINTS.aiRecipes.history, { params })
}

export function confirmAiRecipe(id, payload = {}) {
  return request.patch(ENDPOINTS.aiRecipes.confirm(id), payload)
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
