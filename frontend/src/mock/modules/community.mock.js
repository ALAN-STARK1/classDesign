import Mock from 'mockjs'
import { createAiRecipe, createCommunityComment, createCommunityImage, createCommunityPost } from '../factories/ai'
import { failure, page, parseBody, parseQuery, success } from '../helpers'
import { withScenario } from '../scenario-store'

const baseUrl = import.meta.env.VITE_API_BASE_URL || '/api/v1'
const tempImages = []

let posts = Array.from({ length: 18 }, (_, index) =>
  createCommunityPost({
    id: 32000 + index,
    status: index % 6 === 0 ? 'PENDING' : 'PUBLISHED',
  }),
)

function idFrom(url, pattern = /community\/posts\/(\d+)/) {
  return Number(url.match(pattern)?.[1])
}

function getQuery(options) {
  return parseQuery(options.url)
}

function normalizePostSummary(post) {
  return {
    ...post,
    coverImageUrl: post.coverImageUrl || post.images?.[0]?.url || '',
    imageCount: post.imageCount ?? post.images?.length ?? 0,
    images: undefined,
  }
}

function buildPostImages(imageIds, aiSourceImageUrl = '', useAiSourceImage = false) {
  const activeImages = []

  if (useAiSourceImage && aiSourceImageUrl) {
    activeImages.push(
      createCommunityImage({
        id: Mock.Random.integer(800000, 899999),
        url: aiSourceImageUrl,
        sortNo: 1,
        status: 'ACTIVE',
      }),
    )
  }

  for (const imageId of imageIds || []) {
    const item = tempImages.find((image) => image.id === imageId && image.status === 'TEMP')
    if (item) {
      activeImages.push({
        ...item,
        status: 'ACTIVE',
      })
    }
  }

  return activeImages.map((item, index) => ({
    ...item,
    sortNo: index + 1,
  }))
}

function validateImageIds(imageIds) {
  const normalized = Array.isArray(imageIds) ? imageIds.filter(Boolean) : []
  if (!normalized.length) {
    return { valid: false, code: 40001, message: '新帖子至少需要上传 1 张图片' }
  }
  if (normalized.length > 9) {
    return { valid: false, code: 40001, message: '帖子图片最多 9 张' }
  }
  const allOwned = normalized.every((imageId) => tempImages.some((item) => item.id === imageId && item.status === 'TEMP'))
  if (!allOwned) {
    return { valid: false, code: 40003, message: '存在不可用的临时图片，请重新上传' }
  }
  return { valid: true, imageIds: normalized }
}

function paginate(items, query) {
  const pageNo = Number(query.page || 1)
  const size = Number(query.size || 10)
  const keyword = String(query.keyword || '').trim().toLowerCase()
  const status = String(query.status || '').trim()
  let filtered = items

  if (keyword) {
    filtered = filtered.filter((item) => item.title.toLowerCase().includes(keyword) || item.content.toLowerCase().includes(keyword))
  }

  if (status) {
    filtered = filtered.filter((item) => item.status === status)
  }

  const start = (pageNo - 1) * size
  return page(filtered.slice(start, start + size).map(normalizePostSummary), pageNo, size, filtered.length)
}

export function updateCommunityPostReview(id, status) {
  let updated = null
  posts = posts.map((item) => {
    if (item.id !== id) return item
    updated = { ...item, status }
    return updated
  })
  return updated
}

export function registerCommunityMocks() {
  Mock.mock(`${baseUrl}/community/post-images`, 'post', () =>
    withScenario('POST /community/post-images', {
      success: () => {
        const image = createCommunityImage({
          status: 'TEMP',
          id: Mock.Random.integer(900000, 999999),
        })
        tempImages.unshift(image)
        return success({
          imageId: image.id,
          imageUrl: image.url,
          width: image.width,
          height: image.height,
          fileSize: image.fileSize,
          status: 'TEMP',
        })
      },
      empty: () => success(null),
      error: () => failure(40001, '图片格式不支持，仅允许 jpg/jpeg/png/webp'),
      timeout: () => failure(40800, '社区图片上传超时'),
    }),
  )

  Mock.mock(`${baseUrl}/community/posts`, 'post', (options) =>
    withScenario('POST /community/posts', {
      success: () => {
        const body = parseBody(options)
        const validation = validateImageIds(body.imageIds)
        if (!validation.valid) {
          return failure(validation.code, validation.message)
        }

        const images = buildPostImages(validation.imageIds)
        const post = createCommunityPost({
          ...body,
          id: Mock.Random.integer(90000, 99999),
          status: 'PENDING',
          comments: [],
          images,
          coverImageUrl: images[0]?.url || '',
          imageCount: images.length,
          commentCount: 0,
        })

        posts = [post, ...posts]
        return success(post)
      },
      empty: () => success(null),
      error: () => failure(40001, '帖子内容校验失败'),
      timeout: () => failure(40800, '发布帖子超时'),
    }),
  )

  Mock.mock(new RegExp(`${baseUrl}/community/posts/from-ai-recipe/\\d+$`), 'post', (options) =>
    withScenario('POST /community/posts/from-ai-recipe/{aiRecipeId}', {
      success: () => {
        const aiRecipeId = idFrom(options.url, /from-ai-recipe\/(\d+)/)
        const body = parseBody(options)
        const aiRecipe = createAiRecipe({
          id: aiRecipeId,
          status: 'CONFIRMED',
          sourceType: body.useAiSourceImage === false ? 'TEXT' : 'IMAGE',
          sourceImageUrl: body.aiSourceImageUrl || body.sourceImageUrl || '',
        })

        const useAiSourceImage = body.useAiSourceImage ?? Boolean(aiRecipe.sourceImageUrl)
        const extraImageIds = Array.isArray(body.imageIds) ? body.imageIds.filter(Boolean) : []
        const tempValid = extraImageIds.every((imageId) => tempImages.some((item) => item.id === imageId && item.status === 'TEMP'))
        if (!tempValid) {
          return failure(40003, '存在不可用的临时图片，请重新上传')
        }

        const totalCount = extraImageIds.length + (useAiSourceImage && aiRecipe.sourceImageUrl ? 1 : 0)
        if (!totalCount) {
          return failure(40001, 'AI 分享帖子至少需要 1 张图片')
        }
        if (totalCount > 9) {
          return failure(40001, '帖子图片最多 9 张')
        }

        const images = buildPostImages(extraImageIds, aiRecipe.sourceImageUrl, useAiSourceImage)
        const post = createCommunityPost({
          ...body,
          aiRecipeId,
          recipeName: aiRecipe.name,
          title: body.title || `${aiRecipe.name} 分享`,
          id: Mock.Random.integer(90000, 99999),
          status: 'PENDING',
          comments: [],
          images,
          coverImageUrl: images[0]?.url || '',
          imageCount: images.length,
          commentCount: 0,
        })

        posts = [post, ...posts]
        return success(post)
      },
      empty: () => success(null),
      error: () => failure(42200, 'AI 菜谱未确认，无法发布社区帖子'),
      timeout: () => failure(40800, '从 AI 菜谱发布帖子超时'),
    }),
  )

  Mock.mock(new RegExp(`${baseUrl}/community/posts(\\?.*)?$`), 'get', (options) =>
    withScenario('GET /community/posts', {
      success: () => success(paginate(posts, getQuery(options))),
      empty: () => success(page([], 1, 10, 0)),
      error: () => failure(50000, '社区帖子加载失败'),
      timeout: () => failure(40800, '社区帖子加载超时'),
    }),
  )

  Mock.mock(new RegExp(`${baseUrl}/community/posts/\\d+$`), 'get', (options) =>
    withScenario('GET /community/posts/{id}', {
      success: () => success(posts.find((item) => item.id === idFrom(options.url)) || null),
      empty: () => success(null),
      error: () => failure(40400, '社区帖子不存在'),
      timeout: () => failure(40800, '社区帖子详情加载超时'),
    }),
  )

  Mock.mock(new RegExp(`${baseUrl}/community/posts/\\d+/comments$`), 'post', (options) =>
    withScenario('POST /community/posts/{id}/comments', {
      success: () => {
        const id = idFrom(options.url)
        const body = parseBody(options)
        const comment = createCommunityComment({ content: body.content || '不错的菜谱' })
        posts = posts.map((item) =>
          item.id === id
            ? { ...item, comments: [...(item.comments || []), comment], commentCount: (item.commentCount || 0) + 1 }
            : item,
        )
        return success(comment)
      },
      empty: () => success(null),
      error: () => failure(40100, '请登录后再评论'),
      timeout: () => failure(40800, '发表评论超时'),
    }),
  )

  Mock.mock(new RegExp(`${baseUrl}/community/posts/\\d+/like$`), 'post', (options) =>
    withScenario('POST /community/posts/{id}/like', {
      success: () => {
        const id = idFrom(options.url)
        posts = posts.map((item) =>
          item.id === id ? { ...item, liked: !item.liked, likeCount: item.liked ? Math.max(0, item.likeCount - 1) : item.likeCount + 1 } : item,
        )
        return success(posts.find((item) => item.id === id))
      },
      empty: () => success(null),
      error: () => failure(40100, '请登录后再点赞'),
      timeout: () => failure(40800, '点赞超时'),
    }),
  )

  Mock.mock(new RegExp(`${baseUrl}/community/posts/\\d+/favorite$`), 'post', (options) =>
    withScenario('POST /community/posts/{id}/favorite', {
      success: () => {
        const id = idFrom(options.url)
        posts = posts.map((item) =>
          item.id === id
            ? { ...item, favorite: !item.favorite, favoriteCount: item.favorite ? Math.max(0, item.favoriteCount - 1) : item.favoriteCount + 1 }
            : item,
        )
        return success(posts.find((item) => item.id === id))
      },
      empty: () => success(null),
      error: () => failure(40100, '请登录后再收藏'),
      timeout: () => failure(40800, '收藏帖子超时'),
    }),
  )
}
