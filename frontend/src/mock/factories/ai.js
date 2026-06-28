import Mock from 'mockjs'
import { AiRecipeSourceType, AiRecipeStatus, CommunityPostStatus, RecipeCategory } from '../../constants/enums'
import { today } from '../../utils/date'
import { pick } from '../helpers'

const aiNames = ['番茄牛肉燕麦饭', '低脂鸡胸蔬菜卷', '三文鱼豆腐汤', '鹰嘴豆能量沙拉', '虾仁西兰花焖饭']
const commentAuthors = ['alice', 'mika', 'runner', 'chef-lin']
const commentSamples = ['这个搭配很适合工作日。', '我把主食换成藜麦了，口感更好。', '蛋白质很足，收藏了。']
const tagPool = ['高蛋白', '控糖', '低脂', '快手', '午餐', '晚餐', '便当']
const imageSeeds = [
  'https://images.unsplash.com/photo-1547592180-85f173990554?auto=format&fit=crop&w=1200&q=80',
  'https://images.unsplash.com/photo-1512058564366-18510be2db19?auto=format&fit=crop&w=1200&q=80',
  'https://images.unsplash.com/photo-1515003197210-e0cd71810b5f?auto=format&fit=crop&w=1200&q=80',
  'https://images.unsplash.com/photo-1498837167922-ddd27525d352?auto=format&fit=crop&w=1200&q=80',
  'https://images.unsplash.com/photo-1490645935967-10de6ba17061?auto=format&fit=crop&w=1200&q=80',
]

function randomImageUrl(seedOffset = 0) {
  const index = (Mock.Random.integer(0, imageSeeds.length - 1) + seedOffset) % imageSeeds.length
  return imageSeeds[index]
}

export function createCommunityImage(overrides = {}) {
  return {
    id: Mock.Random.integer(500000, 999999),
    url: randomImageUrl(),
    sortNo: 1,
    width: 1280,
    height: 960,
    fileSize: Mock.Random.integer(180000, 2600000),
    status: 'ACTIVE',
    ...overrides,
  }
}

export function createAiRecipe(overrides = {}) {
  const totalCalorie = Mock.Random.integer(320, 760)
  const sourceType = overrides.sourceType || pick(AiRecipeSourceType)
  const sourceImageUrl = Object.prototype.hasOwnProperty.call(overrides, 'sourceImageUrl')
    ? overrides.sourceImageUrl
    : sourceType === 'IMAGE'
      ? randomImageUrl()
      : ''

  return {
    id: Mock.Random.integer(9000, 99999),
    name: pick(aiNames),
    sourceType,
    status: overrides.status || pick(AiRecipeStatus.filter((item) => item !== 'DELETED')),
    rawText: '鸡胸肉 150g，西兰花 120g，糙米饭 100g，少量橄榄油煎熟后装盘。',
    prompt: '识别菜名、食材、步骤和营养估算',
    confidence: Mock.Random.integer(72, 98),
    category: pick(RecipeCategory),
    servings: Mock.Random.integer(1, 3),
    cookMinutes: Mock.Random.integer(10, 45),
    totalCalorie,
    totalProtein: Mock.Random.float(22, 62, 1, 1),
    totalFat: Mock.Random.float(6, 28, 1, 1),
    totalCarbohydrate: Mock.Random.float(24, 86, 1, 1),
    ingredients: [
      { name: '鸡胸肉', amount: 150, unit: 'g' },
      { name: '西兰花', amount: 120, unit: 'g' },
      { name: '糙米饭', amount: 100, unit: 'g' },
    ],
    steps: ['处理并称量食材', '低油煎熟主食材', '蔬菜焯水后装盘'],
    warnings: Mock.Random.boolean() ? ['钠含量需要按实际调味复核'] : [],
    sourceImageUrl,
    createdAt: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss'),
    ...overrides,
  }
}

export function createCommunityComment(overrides = {}) {
  return {
    id: Mock.Random.integer(10000, 99999),
    authorName: pick(commentAuthors),
    content: pick(commentSamples),
    createdAt: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss'),
    ...overrides,
  }
}

export function createCommunityPost(overrides = {}) {
  const aiRecipe = createAiRecipe({ status: 'CONFIRMED', sourceType: 'IMAGE' })
  const images = overrides.images
    ? overrides.images.map((item, index) => createCommunityImage({ sortNo: index + 1, ...item }))
    : Array.from({ length: Mock.Random.integer(1, 4) }, (_, index) =>
        createCommunityImage({ sortNo: index + 1, url: randomImageUrl(index) }),
      )

  return {
    id: Mock.Random.integer(30000, 99999),
    title: `${aiRecipe.name} 分享`,
    content: '今天用 AI 解析了一份低负担菜谱，整体热量和蛋白质比例都不错。',
    authorName: pick(commentAuthors),
    author: pick(commentAuthors),
    aiRecipeId: aiRecipe.id,
    recipeName: aiRecipe.name,
    status: pick(CommunityPostStatus),
    likeCount: Mock.Random.integer(0, 128),
    favoriteCount: Mock.Random.integer(0, 64),
    commentCount: Mock.Random.integer(0, 16),
    liked: Mock.Random.boolean(),
    favorite: Mock.Random.boolean(),
    tags: [pick(tagPool), pick(tagPool)].filter((item, index, arr) => arr.indexOf(item) === index),
    nutrition: {
      calorie: aiRecipe.totalCalorie,
      protein: aiRecipe.totalProtein,
      fat: aiRecipe.totalFat,
      carbohydrate: aiRecipe.totalCarbohydrate,
    },
    coverImageUrl: images[0]?.url || '',
    imageCount: images.length,
    images,
    comments: Array.from({ length: Mock.Random.integer(1, 4) }, () => createCommunityComment()),
    createdAt: Mock.Random.datetime('yyyy-MM-dd HH:mm:ss'),
    publishedAt: today(),
    ...overrides,
  }
}
