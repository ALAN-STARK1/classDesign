import Mock from 'mockjs'
import { IngredientCategory, IngredientStatus, RecipeCategory, RecipeDifficulty, RecipeStatus } from '../../constants/enums'
import { pick } from '../helpers'

const ingredientNames = [
  ['燕麦', 'GRAIN'],
  ['糙米', 'GRAIN'],
  ['全麦面包', 'GRAIN'],
  ['西兰花', 'VEGETABLE'],
  ['番茄', 'VEGETABLE'],
  ['菠菜', 'VEGETABLE'],
  ['蓝莓', 'FRUIT'],
  ['苹果', 'FRUIT'],
  ['鸡胸肉', 'MEAT'],
  ['鸡蛋', 'MEAT'],
  ['三文鱼', 'SEAFOOD'],
  ['虾仁', 'SEAFOOD'],
  ['低脂牛奶', 'DAIRY'],
  ['无糖酸奶', 'DAIRY'],
  ['北豆腐', 'BEAN'],
  ['鹰嘴豆', 'BEAN'],
  ['杏仁', 'NUT'],
  ['橄榄油', 'SEASONING'],
]

export function createIngredient(overrides = {}) {
  const picked = pick(ingredientNames)
  const calorie = Mock.Random.integer(18, 390)
  const protein = Mock.Random.float(0.4, 32, 1, 1)
  const fat = Mock.Random.float(0.1, 22, 1, 1)
  const carbohydrate = Mock.Random.float(0.4, 72, 1, 1)
  return {
    id: Mock.Random.integer(1001, 9999),
    name: picked[0],
    category: picked[1] || pick(IngredientCategory),
    unit: pick(['g', 'ml', '份']),
    caloriePer100g: calorie,
    proteinPer100g: protein,
    fatPer100g: fat,
    carbohydratePer100g: carbohydrate,
    gi: Mock.Random.integer(15, 78),
    allergens: Mock.Random.boolean() ? [pick(['花生', '虾', '牛奶', '鸡蛋', '坚果', '小麦', '大豆'])] : [],
    status: pick(IngredientStatus),
    description: pick(['适合日常控糖饮食', '高蛋白低负担', '适合作为主餐配菜', '适合运动后补给']),
    updatedAt: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss'),
    ...overrides,
  }
}

export function createRecipeIngredient(ingredient = createIngredient(), overrides = {}) {
  const amount = Mock.Random.integer(50, 220)
  return {
    ingredientId: ingredient.id,
    name: ingredient.name,
    amount,
    unit: ingredient.unit,
    calorie: Math.round((ingredient.caloriePer100g * amount) / 100),
    protein: Number(((ingredient.proteinPer100g * amount) / 100).toFixed(1)),
    fat: Number(((ingredient.fatPer100g * amount) / 100).toFixed(1)),
    carbohydrate: Number(((ingredient.carbohydratePer100g * amount) / 100).toFixed(1)),
    ...overrides,
  }
}

export function sumNutrition(ingredients = []) {
  return ingredients.reduce(
    (total, item) => ({
      totalCalorie: total.totalCalorie + Number(item.calorie || 0),
      totalProtein: total.totalProtein + Number(item.protein || 0),
      totalFat: total.totalFat + Number(item.fat || 0),
      totalCarbohydrate: total.totalCarbohydrate + Number(item.carbohydrate || 0),
    }),
    { totalCalorie: 0, totalProtein: 0, totalFat: 0, totalCarbohydrate: 0 },
  )
}

export function createRecipe(seedIngredients = [], overrides = {}) {
  const ingredients = seedIngredients.slice(0, Mock.Random.integer(3, 5)).map((item) => createRecipeIngredient(item))
  const nutrition = sumNutrition(ingredients)
  const main = ingredients[0]?.name || pick(['鸡胸肉', '番茄', '西兰花', '燕麦', '三文鱼'])
  const style = pick(['能量碗', '轻食沙拉', '煎烤餐', '暖汤', '拌饭'])
  return {
    id: Mock.Random.integer(2001, 9999),
    name: `${main}${style}`,
    description: pick(['高蛋白低脂，适合工作日午餐。', '食材简单，适合控糖人群。', '饱腹感强，适合目标周期内使用。']),
    category: pick(RecipeCategory),
    difficulty: pick(RecipeDifficulty),
    cookMinutes: Mock.Random.integer(12, 55),
    servings: Mock.Random.integer(1, 4),
    coverColor: pick(['#2f6f4e', '#6c5ce7', '#d35400', '#0f766e', '#7f8c8d']),
    status: pick(RecipeStatus),
    favorite: Mock.Random.boolean(),
    suitabilityScore: Mock.Random.integer(62, 98),
    unsuitableReasons: Mock.Random.boolean() ? [] : ['钠摄入略高', '碳水比例偏高'],
    ingredients,
    steps: ['清洗并称量所有食材。', '按食材熟成时间依次烹调。', '装盘后记录实际摄入份量。'],
    ...nutrition,
    totalProtein: Number(nutrition.totalProtein.toFixed(1)),
    totalFat: Number(nutrition.totalFat.toFixed(1)),
    totalCarbohydrate: Number(nutrition.totalCarbohydrate.toFixed(1)),
    createdAt: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss'),
    ...overrides,
  }
}

export function createSuitability(recipe = createRecipe()) {
  const score = recipe.suitabilityScore || Mock.Random.integer(60, 98)
  return {
    recipeId: recipe.id,
    score,
    level: score >= 85 ? '适合' : score >= 70 ? '谨慎食用' : '不建议',
    highlights: ['蛋白质供给稳定', '热量处于目标范围', '食材结构清晰'],
    risks: recipe.unsuitableReasons?.length ? recipe.unsuitableReasons : ['暂无明显风险'],
    refreshedAt: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss'),
  }
}
