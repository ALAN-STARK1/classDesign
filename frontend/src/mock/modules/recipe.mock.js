import Mock from 'mockjs'
import { createIngredient, createRecipe, createRecipeIngredient, createSuitability, sumNutrition } from '../factories/recipe'
import { failure, page, parseBody, parseQuery, success } from '../helpers'
import { withScenario } from '../scenario-store'

const baseUrl = import.meta.env.VITE_API_BASE_URL || '/api/v1'

let ingredients = Array.from({ length: 28 }, (_, index) =>
  createIngredient({
    id: 1000 + index,
    status: index % 9 === 0 ? 'DISABLED' : 'ENABLED',
  }),
)

let recipes = Array.from({ length: 22 }, (_, index) =>
  createRecipe(ingredients.slice(index, index + 6), {
    id: 2000 + index,
    status: index % 5 === 0 ? 'PENDING' : 'ONLINE',
    favorite: index % 4 === 0,
  }),
)

function normalizeList(items, query) {
  const pageNo = Number(query.page || 1)
  const size = Number(query.size || 10)
  const keyword = String(query.keyword || '').trim().toLowerCase()
  const category = query.category
  const difficulty = query.difficulty
  const status = query.status
  let filtered = items
  if (keyword) filtered = filtered.filter((item) => item.name.toLowerCase().includes(keyword))
  if (category) filtered = filtered.filter((item) => item.category === category)
  if (difficulty) filtered = filtered.filter((item) => item.difficulty === difficulty)
  if (status) filtered = filtered.filter((item) => item.status === status)
  const start = (pageNo - 1) * size
  return page(filtered.slice(start, start + size), pageNo, size, filtered.length)
}

function idFrom(url, name) {
  return Number(url.match(new RegExp(`${name}/(\\d+)`))?.[1])
}

function hydrateRecipeNutrition(recipe, nextIngredients = recipe.ingredients || []) {
  const nutrition = sumNutrition(nextIngredients)
  return {
    ...recipe,
    ingredients: nextIngredients,
    totalCalorie: Math.round(nutrition.totalCalorie),
    totalProtein: Number(nutrition.totalProtein.toFixed(1)),
    totalFat: Number(nutrition.totalFat.toFixed(1)),
    totalCarbohydrate: Number(nutrition.totalCarbohydrate.toFixed(1)),
  }
}

export function registerRecipeMocks() {
  Mock.mock(new RegExp(`${baseUrl}/ingredients(\\?.*)?$`), 'get', (options) =>
    withScenario('GET /ingredients', {
      success: () => success(normalizeList(ingredients, parseQuery(options.url))),
      empty: () => success(page([], 1, Number(parseQuery(options.url).size || 10), 0)),
      error: () => failure(50000, '食材库加载失败'),
      timeout: () => failure(40800, '食材库加载超时'),
    }),
  )

  Mock.mock(new RegExp(`${baseUrl}/ingredients/\\d+$`), 'get', (options) =>
    withScenario('GET /ingredients/{id}', {
      success: () => success(ingredients.find((item) => item.id === idFrom(options.url, 'ingredients')) || null),
      empty: () => success(null),
      error: () => failure(40400, '食材不存在'),
      timeout: () => failure(40800, '食材详情加载超时'),
    }),
  )

  Mock.mock(`${baseUrl}/ingredients`, 'post', (options) =>
    withScenario('POST /ingredients', {
      success: () => {
        const next = createIngredient({ ...parseBody(options), id: Mock.Random.integer(9000, 9999), status: 'ENABLED' })
        ingredients = [next, ...ingredients]
        return success(next)
      },
      empty: () => success(null),
      error: () =>
        failure(40001, '食材参数校验失败', {
          fieldErrors: [{ field: 'name', message: '食材名称不能为空' }],
        }),
      timeout: () => failure(40800, '新增食材超时'),
    }),
  )

  Mock.mock(new RegExp(`${baseUrl}/ingredients/\\d+$`), 'put', (options) =>
    withScenario('PUT /ingredients/{id}', {
      success: () => {
        const id = idFrom(options.url, 'ingredients')
        const current = ingredients.find((item) => item.id === id)
        const next = { ...current, ...parseBody(options), id }
        ingredients = ingredients.map((item) => (item.id === id ? next : item))
        return success(next)
      },
      empty: () => success(null),
      error: () => failure(40001, '食材参数校验失败'),
      timeout: () => failure(40800, '更新食材超时'),
    }),
  )

  Mock.mock(new RegExp(`${baseUrl}/ingredients/\\d+/disable$`), 'patch', (options) =>
    withScenario('PATCH /ingredients/{id}/disable', {
      success: () => {
        const id = idFrom(options.url, 'ingredients')
        ingredients = ingredients.map((item) => (item.id === id ? { ...item, status: 'DISABLED' } : item))
        return success(ingredients.find((item) => item.id === id))
      },
      empty: () => success(null),
      error: () => failure(40300, '当前用户无权停用食材'),
      timeout: () => failure(40800, '停用食材超时'),
    }),
  )

  Mock.mock(new RegExp(`${baseUrl}/recipes(\\?.*)?$`), 'get', (options) =>
    withScenario('GET /recipes', {
      success: () => success(normalizeList(recipes, parseQuery(options.url))),
      empty: () => success(page([], 1, Number(parseQuery(options.url).size || 10), 0)),
      error: () => failure(50000, '菜谱列表加载失败'),
      timeout: () => failure(40800, '菜谱列表加载超时'),
    }),
  )

  Mock.mock(new RegExp(`${baseUrl}/recipes/\\d+$`), 'get', (options) =>
    withScenario('GET /recipes/{id}', {
      success: () => success(recipes.find((item) => item.id === idFrom(options.url, 'recipes')) || null),
      empty: () => success(null),
      error: () => failure(40400, '菜谱不存在'),
      timeout: () => failure(40800, '菜谱详情加载超时'),
    }),
  )

  Mock.mock(`${baseUrl}/recipes`, 'post', (options) =>
    withScenario('POST /recipes', {
      success: () => {
        const body = parseBody(options)
        const recipe = createRecipe(ingredients, {
          ...body,
          id: Mock.Random.integer(9000, 9999),
          ingredients: [],
          totalCalorie: 0,
          totalProtein: 0,
          totalFat: 0,
          totalCarbohydrate: 0,
          favorite: false,
          status: body.status || 'DRAFT',
        })
        recipes = [recipe, ...recipes]
        return success(recipe)
      },
      empty: () => success(null),
      error: () =>
        failure(40001, '菜谱参数校验失败', {
          fieldErrors: [{ field: 'name', message: '菜谱名称不能为空' }],
        }),
      timeout: () => failure(40800, '创建菜谱超时'),
    }),
  )

  Mock.mock(new RegExp(`${baseUrl}/recipes/\\d+$`), 'put', (options) =>
    withScenario('PUT /recipes/{id}', {
      success: () => {
        const id = idFrom(options.url, 'recipes')
        const current = recipes.find((item) => item.id === id)
        const next = { ...current, ...parseBody(options), id }
        recipes = recipes.map((item) => (item.id === id ? next : item))
        return success(next)
      },
      empty: () => success(null),
      error: () => failure(40001, '菜谱参数校验失败'),
      timeout: () => failure(40800, '更新菜谱超时'),
    }),
  )

  Mock.mock(new RegExp(`${baseUrl}/recipes/\\d+/ingredients$`), 'put', (options) =>
    withScenario('PUT /recipes/{id}/ingredients', {
      success: () => {
        const id = idFrom(options.url, 'recipes')
        const body = parseBody(options)
        const nextIngredients = (body.ingredients || []).map((item) => {
          const ingredient = ingredients.find((source) => source.id === item.ingredientId) || createIngredient({ id: item.ingredientId })
          return createRecipeIngredient(ingredient, { ...item, name: ingredient.name, unit: item.unit || ingredient.unit })
        })
        const current = recipes.find((item) => item.id === id)
        const next = hydrateRecipeNutrition(current, nextIngredients)
        recipes = recipes.map((item) => (item.id === id ? next : item))
        return success(next)
      },
      empty: () => success([]),
      error: () => failure(42200, '菜谱食材绑定失败'),
      timeout: () => failure(40800, '菜谱食材绑定超时'),
    }),
  )

  Mock.mock(new RegExp(`${baseUrl}/recipes/\\d+/nutrition/calculate$`), 'post', (options) =>
    withScenario('POST /recipes/{id}/nutrition/calculate', {
      success: () => {
        const recipe = recipes.find((item) => item.id === idFrom(options.url, 'recipes'))
        const next = hydrateRecipeNutrition(recipe)
        recipes = recipes.map((item) => (item.id === next.id ? next : item))
        return success({
          totalCalorie: next.totalCalorie,
          totalProtein: next.totalProtein,
          totalFat: next.totalFat,
          totalCarbohydrate: next.totalCarbohydrate,
        })
      },
      empty: () => success(null),
      error: () => failure(42200, '缺少食材，无法计算营养'),
      timeout: () => failure(40800, '营养计算超时'),
    }),
  )

  Mock.mock(new RegExp(`${baseUrl}/recipes/\\d+/suitability$`), 'get', (options) =>
    withScenario('GET /recipes/{id}/suitability', {
      success: () => success(createSuitability(recipes.find((item) => item.id === idFrom(options.url, 'recipes')))),
      empty: () => success(null),
      error: () => failure(42200, '缺少健康档案，无法计算适配评分'),
      timeout: () => failure(40800, '适配评分刷新超时'),
    }),
  )

  Mock.mock(`${baseUrl}/recipes/suitability/recalculate`, 'post', () =>
    withScenario('POST /recipes/suitability/recalculate', {
      success: () => {
        recipes = recipes.map((item) => ({ ...item, suitabilityScore: Mock.Random.integer(60, 98) }))
        return success({ affected: recipes.length })
      },
      empty: () => success({ affected: 0 }),
      error: () => failure(50200, '适配评分服务暂不可用'),
      timeout: () => failure(40800, '适配评分批量刷新超时'),
    }),
  )

  Mock.mock(new RegExp(`${baseUrl}/recipes/\\d+/favorite$`), 'post', (options) =>
    withScenario('POST /recipes/{id}/favorite', {
      success: () => {
        const id = idFrom(options.url, 'recipes')
        recipes = recipes.map((item) => (item.id === id ? { ...item, favorite: true } : item))
        return success(true)
      },
      empty: () => success(false),
      error: () => failure(40100, '请先登录后再收藏菜谱'),
      timeout: () => failure(40800, '收藏菜谱超时'),
    }),
  )

  Mock.mock(new RegExp(`${baseUrl}/recipes/\\d+/favorite$`), 'delete', (options) =>
    withScenario('DELETE /recipes/{id}/favorite', {
      success: () => {
        const id = idFrom(options.url, 'recipes')
        recipes = recipes.map((item) => (item.id === id ? { ...item, favorite: false } : item))
        return success(true)
      },
      empty: () => success(false),
      error: () => failure(40100, '请先登录后再取消收藏'),
      timeout: () => failure(40800, '取消收藏超时'),
    }),
  )
}
