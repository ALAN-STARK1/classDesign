import Mock from 'mockjs'
import { createAiRecipe } from '../factories/ai'
import { failure, page, parseBody, parseQuery, success } from '../helpers'
import { withScenario } from '../scenario-store'

const baseUrl = import.meta.env.VITE_API_BASE_URL || '/api/v1'

let aiRecipes = Array.from({ length: 12 }, (_, index) =>
  createAiRecipe({
    id: 9100 + index,
    sourceType: index % 3 === 0 ? 'IMAGE' : 'TEXT',
    status: index % 4 === 0 ? 'CONVERTED' : 'PARSED',
  }),
)

function idFrom(url) {
  return Number(url.match(/ai-recipes\/(\d+)/)?.[1])
}

function paginate(items, query) {
  const pageNo = Number(query.page || 1)
  const size = Number(query.size || 10)
  const keyword = String(query.keyword || '').trim().toLowerCase()
  const sourceType = query.sourceType
  let filtered = items.filter((item) => item.status !== 'DELETED')
  if (keyword) filtered = filtered.filter((item) => item.name.toLowerCase().includes(keyword))
  if (sourceType) filtered = filtered.filter((item) => item.sourceType === sourceType)
  const start = (pageNo - 1) * size
  return page(filtered.slice(start, start + size), pageNo, size, filtered.length)
}

export function registerAiMocks() {
  Mock.mock(`${baseUrl}/ai-recipes/parse`, 'post', (options) =>
    withScenario('POST /ai-recipes/parse', {
      success: () => {
        const body = parseBody(options)
        const recipe = createAiRecipe({ rawText: body.text || '', prompt: body.prompt || '', sourceType: 'TEXT', status: 'PARSED', sourceImageUrl: '' })
        aiRecipes = [recipe, ...aiRecipes]
        return success(recipe)
      },
      empty: () => success(null),
      error: () => failure(50201, 'AI 返回格式无法识别'),
      timeout: () => failure(40800, 'AI 文本解析超时'),
    }),
  )

  Mock.mock(`${baseUrl}/ai-recipes/parse-image`, 'post', () =>
    withScenario('POST /ai-recipes/parse-image', {
      success: () => {
        const recipe = createAiRecipe({ sourceType: 'IMAGE', status: 'PARSED', prompt: '图片识别菜谱' })
        aiRecipes = [recipe, ...aiRecipes]
        return success(recipe)
      },
      empty: () => success(null),
      error: () => failure(50200, 'AI 图片识别服务暂不可用'),
      timeout: () => failure(40800, 'AI 图片解析超时'),
    }),
  )

  Mock.mock(new RegExp(`${baseUrl}/ai-recipes/history(\\?.*)?$`), 'get', (options) =>
    withScenario('GET /ai-recipes/history', {
      success: () => success(paginate(aiRecipes, parseQuery(options.url))),
      empty: () => success(page([], 1, 10, 0)),
      error: () => failure(50000, 'AI 菜谱历史加载失败'),
      timeout: () => failure(40800, 'AI 菜谱历史加载超时'),
    }),
  )

  Mock.mock(new RegExp(`${baseUrl}/ai-recipes/\\d+$`), 'get', (options) =>
    withScenario('GET /ai-recipes/{id}', {
      success: () => success(aiRecipes.find((item) => item.id === idFrom(options.url)) || null),
      empty: () => success(null),
      error: () => failure(40400, 'AI 菜谱不存在'),
      timeout: () => failure(40800, 'AI 菜谱详情加载超时'),
    }),
  )

  Mock.mock(new RegExp(`${baseUrl}/ai-recipes/\\d+/confirm$`), 'patch', (options) =>
    withScenario('PATCH /ai-recipes/{id}/confirm', {
      success: () => {
        const id = idFrom(options.url)
        const body = parseBody(options)
        const next = { ...aiRecipes.find((item) => item.id === id), ...body, id, status: 'CONFIRMED' }
        aiRecipes = aiRecipes.map((item) => (item.id === id ? next : item))
        return success(next)
      },
      empty: () => success(null),
      error: () => failure(40001, 'AI 菜谱确认参数校验失败'),
      timeout: () => failure(40800, 'AI 菜谱确认超时'),
    }),
  )

  Mock.mock(new RegExp(`${baseUrl}/ai-recipes/\\d+/to-recipe$`), 'post', (options) =>
    withScenario('POST /ai-recipes/{id}/to-recipe', {
      success: () => {
        const id = idFrom(options.url)
        const source = aiRecipes.find((item) => item.id === id)
        aiRecipes = aiRecipes.map((item) => (item.id === id ? { ...item, status: 'CONVERTED' } : item))
        return success({
          id: Mock.Random.integer(8800, 9999),
          name: source?.name || 'AI 转正式菜谱',
          status: 'DRAFT',
          aiRecipeId: id,
        })
      },
      empty: () => success(null),
      error: () => failure(42200, 'AI 菜谱尚未确认，无法转正式菜谱'),
      timeout: () => failure(40800, '转正式菜谱超时'),
    }),
  )

  Mock.mock(new RegExp(`${baseUrl}/ai-recipes/\\d+/to-meal-record$`), 'post', (options) =>
    withScenario('POST /ai-recipes/{id}/to-meal-record', {
      success: () => {
        const id = idFrom(options.url)
        const body = parseBody(options)
        const source = aiRecipes.find((item) => item.id === id)
        return success({
          id: Mock.Random.integer(7800, 9999),
          aiRecipeId: id,
          date: body.date,
          mealType: body.mealType || source?.category || 'LUNCH',
          foodName: source?.name || 'AI 菜谱记录',
          calorie: source?.totalCalorie || 0,
        })
      },
      empty: () => success(null),
      error: () => failure(42200, 'AI 菜谱无法生成膳食记录'),
      timeout: () => failure(40800, 'AI 菜谱转记录超时'),
    }),
  )

  Mock.mock(new RegExp(`${baseUrl}/ai-recipes/\\d+$`), 'delete', (options) =>
    withScenario('DELETE /ai-recipes/{id}', {
      success: () => {
        const id = idFrom(options.url)
        aiRecipes = aiRecipes.map((item) => (item.id === id ? { ...item, status: 'DELETED' } : item))
        return success(true)
      },
      empty: () => success(false),
      error: () => failure(40300, '无权删除该 AI 菜谱'),
      timeout: () => failure(40800, '删除 AI 菜谱超时'),
    }),
  )
}

export function injectAiRecipe(recipe) {
  if (!recipe || !recipe.id) return
  if (aiRecipes.some((item) => item.id === recipe.id)) return
  aiRecipes.unshift(recipe)
}
