import axios from 'axios'
import { AppError } from './request/errors'

const bridge = axios.create({
  baseURL: '/ai-bridge',
  timeout: 65000,
})

function buildTextRequestBody(payload) {
  const text = (payload.text || '').trim()
  const prompt = (payload.prompt || '').trim()
  const userInput = [prompt, text].filter(Boolean).join('\n\n')
  return {
    userId: null,
    userInput,
    recognitionResults: [],
    healthProfile: null,
    preferences: null,
    restrictions: null,
  }
}

function buildImageFormData(file, prompt) {
  const formData = new FormData()
  formData.append('image', file)
  formData.append(
    'context',
    JSON.stringify({
      userId: null,
      userInput: (prompt || '').trim(),
      recognitionResults: [],
      healthProfile: null,
      preferences: null,
      restrictions: null,
    }),
  )
  formData.append('topK', 5)
  return formData
}

function transformRecipeResult(data, sourceType, rawText, prompt) {
  if (!data) return null
  return {
    id: Date.now(),
    name: data.recipeName || '未命名菜谱',
    sourceType,
    status: 'PARSED',
    rawText: rawText || '',
    prompt: prompt || '',
    confidence: data.suitability?.score ?? 0,
    category: inferCategory(data.recipeName, data.description),
    servings: 1,
    cookMinutes: 0,
    totalCalorie: data.nutritionEstimate?.calories ?? 0,
    totalProtein: data.nutritionEstimate?.protein ?? 0,
    totalFat: data.nutritionEstimate?.fat ?? 0,
    totalCarbohydrate: data.nutritionEstimate?.carbohydrate ?? 0,
    ingredients: (data.ingredients || []).map((item) => ({
      name: item.name,
      amount: item.amount,
      unit: item.unit,
    })),
    steps: data.cookingSteps || [],
    warnings: [...(data.warnings || []), ...(data.healthTips || [])],
    sourceImageUrl: sourceType === 'IMAGE' ? data.sourceImageUrl || '' : '',
    createdAt: new Date().toISOString().replace('T', ' ').slice(0, 19),
  }
}

function inferCategory(name, description) {
  const text = `${name || ''} ${description || ''}`
  if (/汤|炖|羹/.test(text)) return 'DINNER'
  if (/沙拉|凉拌|生/.test(text)) return 'SNACK'
  if (/早餐|燕麦|蛋饼/.test(text)) return 'BREAKFAST'
  return 'LUNCH'
}

bridge.interceptors.response.use(
  (response) => {
    const body = response.data
    if (body && body.success) {
      return body.data
    }
    throw new AppError({
      code: 50200,
      message: body?.errorMessage || 'AI 服务处理失败',
      type: 'business',
    })
  },
  (error) => {
    if (error instanceof AppError) throw error
    if (error.code === 'ECONNABORTED') {
      throw new AppError({ code: 40800, message: 'AI 服务请求超时', type: 'timeout' })
    }
    const msg = error.response?.data?.errorMessage || error.message || 'AI 服务连接异常'
    throw new AppError({ code: 50200, message: msg, type: 'network' })
  },
)

export async function bridgeParseText(payload) {
  const requestBody = buildTextRequestBody(payload)
  const rawText = payload.text || ''
  const prompt = payload.prompt || ''
  const res = await bridge.post('/recipes/parse', requestBody)
  const result = transformRecipeResult(res, 'TEXT', rawText, prompt)
  injectIntoMockStore(result)
  return result
}

export async function bridgeParseImage(file, prompt) {
  const formData = buildImageFormData(file, prompt)
  const res = await bridge.post('/recipes/parse-from-image', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  })
  const previewUrl = URL.createObjectURL(file)
  const result = transformRecipeResult({ ...res, sourceImageUrl: previewUrl }, 'IMAGE', '', prompt)
  injectIntoMockStore(result)
  return result
}

async function injectIntoMockStore(recipe) {
  if (!recipe) return
  try {
    const { injectAiRecipe } = await import('../mock/modules/ai.mock')
    injectAiRecipe(recipe)
  } catch {
    // Ignore when mock is disabled.
  }
}
