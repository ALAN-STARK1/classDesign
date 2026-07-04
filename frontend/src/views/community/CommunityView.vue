<script setup>
import { ChatDotRound, Collection, Delete, Picture, Plus, Search, Star, StarFilled, Upload, View } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { computed, onMounted, reactive, ref } from 'vue'
import StateBlock from '../../components/common/StateBlock.vue'
import { CommunityPostStatusLabel } from '../../constants/labels'
import {
  createCommunityComment,
  createCommunityPost,
  favoriteCommunityPost,
  fetchCommunityPostDetail,
  fetchCommunityPosts,
  likeCommunityPost,
  uploadCommunityPostImage,
} from '../../services/modules/community.service'
import { handleRequestError } from '../../services/request/http'
import { formatCalorie } from '../../utils/format'
import { resolveUploadUrl } from '../../utils/media'

const MAX_IMAGES = 9

const loading = ref(false)
const saving = ref(false)
const uploading = ref(false)
const error = ref('')
const posts = ref([])
const detail = ref(null)
const detailVisible = ref(false)
const dialogVisible = ref(false)
const activeDetailImage = ref('')
const pagination = reactive({ page: 1, size: 9, total: 0 })
const filters = reactive({ keyword: '' })
const form = reactive({
  title: '',
  content: '',
  recipeName: '',
  tagsText: '高蛋白,午餐',
  images: [],
})
const commentForm = reactive({
  content: '',
})

const createImageCountText = computed(() => `${form.images.length}/${MAX_IMAGES}`)

function syncDetailImage() {
  activeDetailImage.value = imageSrc(detail.value?.images?.[0]?.url || detail.value?.coverImageUrl)
}

function imageSrc(url) {
  return resolveUploadUrl(url)
}

function resetCreateForm() {
  Object.assign(form, {
    title: '',
    content: '',
    recipeName: '',
    tagsText: '高蛋白,午餐',
    images: [],
  })
}

async function load() {
  loading.value = true
  error.value = ''
  try {
    const result = await fetchCommunityPosts({
      page: pagination.page,
      size: pagination.size,
      keyword: filters.keyword,
    })
    posts.value = result?.items || []
    pagination.total = result?.total || 0
  } catch (err) {
    error.value = handleRequestError(err).message
  } finally {
    loading.value = false
  }
}

function search() {
  pagination.page = 1
  load()
}

function openCreate() {
  resetCreateForm()
  dialogVisible.value = true
}

function removeCreateImage(index) {
  form.images.splice(index, 1)
}

async function uploadCreateImage(file) {
  if (form.images.length >= MAX_IMAGES) {
    ElMessage.error(`最多上传 ${MAX_IMAGES} 张图片`)
    return false
  }

  uploading.value = true
  try {
    const uploaded = await uploadCommunityPostImage(file)
    form.images.push({
      id: uploaded.imageId,
      url: imageSrc(uploaded.imageUrl),
      width: uploaded.width,
      height: uploaded.height,
      fileSize: uploaded.fileSize,
      status: uploaded.status,
    })
    ElMessage.success('图片上传成功')
  } catch (err) {
    handleRequestError(err)
  } finally {
    uploading.value = false
  }
  return false
}

async function handleCreateImageChange(uploadFile) {
  if (!uploadFile?.raw) return
  await uploadCreateImage(uploadFile.raw)
}

async function submitPost() {
  if (!form.images.length) {
    ElMessage.error('请至少上传 1 张图片')
    return
  }

  saving.value = true
  try {
    await createCommunityPost({
      title: form.title,
      content: form.content,
      recipeName: form.recipeName,
      imageIds: form.images.map((item) => item.id),
      tags: form.tagsText
        .split(',')
        .map((item) => item.trim())
        .filter(Boolean),
    })
    ElMessage.success('帖子已发布')
    dialogVisible.value = false
    resetCreateForm()
    await load()
  } catch (err) {
    handleRequestError(err)
  } finally {
    saving.value = false
  }
}

async function openDetail(row) {
  try {
    detail.value = await fetchCommunityPostDetail(row.id)
    commentForm.content = ''
    syncDetailImage()
    detailVisible.value = true
  } catch (err) {
    handleRequestError(err)
  }
}

async function likePost(post) {
  try {
    const next = await likeCommunityPost(post.id)
    Object.assign(post, next)
    if (detail.value?.id === post.id) {
      detail.value = next
      syncDetailImage()
    }
  } catch (err) {
    handleRequestError(err)
  }
}

async function favoritePost(post) {
  try {
    const next = await favoriteCommunityPost(post.id)
    Object.assign(post, next)
    if (detail.value?.id === post.id) {
      detail.value = next
      syncDetailImage()
    }
  } catch (err) {
    handleRequestError(err)
  }
}

async function submitComment() {
  if (!commentForm.content.trim()) {
    ElMessage.error('请输入评论内容')
    return
  }
  try {
    await createCommunityComment(detail.value.id, { content: commentForm.content })
    ElMessage.success('评论已发布')
    detail.value = await fetchCommunityPostDetail(detail.value.id)
    syncDetailImage()
    commentForm.content = ''
    await load()
  } catch (err) {
    handleRequestError(err)
  }
}

onMounted(load)
</script>

<template>
  <section v-if="error" class="content-section">
    <StateBlock title="社区加载失败" :description="error" @action="load" />
  </section>

  <section v-else class="community-page" v-loading="loading">
    <article class="panel">
      <div class="section-heading">
        <div>
          <span class="eyebrow">COMMUNITY</span>
          <h2>社区帖子</h2>
        </div>
        <el-button type="primary" :icon="Plus" @click="openCreate">发布帖子</el-button>
      </div>
      <div class="filter-bar compact-filter">
        <el-input v-model="filters.keyword" :prefix-icon="Search" placeholder="搜索帖子" clearable @keyup.enter="search" />
        <el-button :icon="Search" @click="search">筛选</el-button>
      </div>
    </article>

    <StateBlock v-if="!posts.length" title="暂无帖子" description="发布一条图文分享后会显示在这里。" :show-action="false" />

    <template v-else>
      <div class="community-grid">
        <article v-for="post in posts" :key="post.id" class="community-card">
          <button v-if="post.coverImageUrl" class="community-cover" type="button" @click="openDetail(post)">
            <img :src="imageSrc(post.coverImageUrl)" :alt="post.title" />
            <span v-if="post.imageCount > 1" class="community-count">{{ post.imageCount }} 张</span>
          </button>
          <div v-else class="community-cover community-cover-fallback">
            <el-icon><Picture /></el-icon>
            <span>历史无图帖子</span>
          </div>
          <div class="community-card-head">
            <div>
              <button class="text-link title-link" type="button" @click="openDetail(post)">{{ post.title }}</button>
              <p>{{ post.authorName }} · {{ CommunityPostStatusLabel[post.status] }}</p>
            </div>
            <el-tag>{{ post.recipeName }}</el-tag>
          </div>
          <p class="community-copy">{{ post.content }}</p>
          <div class="tag-cloud compact-tags">
            <el-tag v-for="tag in post.tags" :key="tag">{{ tag }}</el-tag>
          </div>
          <div class="nutrition-strip">
            <span>{{ formatCalorie(post.nutrition?.calorie) }}</span>
            <span>蛋白 {{ post.nutrition?.protein }}g</span>
            <span>{{ post.commentCount }} 评论</span>
          </div>
          <div class="card-actions">
            <el-button :icon="post.liked ? StarFilled : Star" @click="likePost(post)">{{ post.likeCount }}</el-button>
            <el-button :icon="Collection" @click="favoritePost(post)">{{ post.favoriteCount }}</el-button>
            <el-button :icon="View" @click="openDetail(post)">详情</el-button>
          </div>
        </article>
      </div>
      <div class="pagination-row">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          background
          layout="prev, pager, next, sizes, total"
          :page-sizes="[9, 12, 18]"
          :total="pagination.total"
          @current-change="load"
          @size-change="search"
        />
      </div>
    </template>

    <el-dialog v-model="dialogVisible" title="发布帖子" width="720px">
      <el-form class="stack-form dense" :model="form" label-position="top">
        <el-form-item label="标题">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="关联菜谱名称">
          <el-input v-model="form.recipeName" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="form.content" type="textarea" :rows="5" />
        </el-form-item>
        <el-form-item label="帖子图片">
          <div class="image-upload-stack">
            <el-upload
              drag
              :show-file-list="false"
              :auto-upload="false"
              accept=".jpg,.jpeg,.png,.webp"
              :on-change="handleCreateImageChange"
            >
              <el-icon><Upload /></el-icon>
              <div class="upload-copy">{{ uploading ? '上传中...' : '上传社区图片' }}</div>
              <small>支持 jpg/jpeg/png/webp，单帖 1-9 张</small>
            </el-upload>
            <div class="image-upload-meta">
              <span>已上传 {{ createImageCountText }}</span>
              <span>新帖子必须带图</span>
            </div>
            <div v-if="form.images.length" class="thumb-grid">
              <div v-for="(image, index) in form.images" :key="image.id" class="thumb-card">
                <img :src="image.url" :alt="`帖子图片 ${index + 1}`" />
                <button class="thumb-remove" type="button" @click="removeCreateImage(index)">
                  <el-icon><Delete /></el-icon>
                </button>
              </div>
            </div>
          </div>
        </el-form-item>
        <el-form-item label="标签，用英文逗号分隔">
          <el-input v-model="form.tagsText" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submitPost">提交审核</el-button>
      </template>
    </el-dialog>

    <el-drawer v-model="detailVisible" title="帖子详情" size="680px">
      <div v-if="detail" class="side-stack">
        <div class="community-hero">
          <span>{{ CommunityPostStatusLabel[detail.status] }} · {{ detail.authorName }}</span>
          <strong>{{ detail.title }}</strong>
          <small>{{ detail.recipeName }}</small>
        </div>
        <div v-if="detail.images?.length" class="community-gallery">
          <div class="community-gallery-main">
            <img :src="activeDetailImage" :alt="detail.title" />
          </div>
          <div class="community-gallery-strip">
            <button
              v-for="image in detail.images"
              :key="image.id"
              class="community-gallery-thumb"
              :class="{ active: activeDetailImage === imageSrc(image.url) }"
              type="button"
              @click="activeDetailImage = imageSrc(image.url)"
            >
              <img :src="imageSrc(image.url)" :alt="detail.title" />
            </button>
          </div>
        </div>
        <p class="community-copy">{{ detail.content }}</p>
        <div class="nutrition-strip">
          <span>{{ formatCalorie(detail.nutrition?.calorie) }}</span>
          <span>蛋白 {{ detail.nutrition?.protein }}g</span>
          <span>脂肪 {{ detail.nutrition?.fat }}g</span>
        </div>
        <div class="drawer-actions">
          <el-button :icon="detail.liked ? StarFilled : Star" @click="likePost(detail)">{{ detail.likeCount }}</el-button>
          <el-button :icon="Collection" @click="favoritePost(detail)">{{ detail.favoriteCount }}</el-button>
        </div>
        <article class="panel inset-panel">
          <div class="section-heading">
            <div>
              <span class="eyebrow">COMMENTS</span>
              <h2>评论</h2>
            </div>
          </div>
          <div v-if="detail.comments?.length" class="timeline-list">
            <div v-for="comment in detail.comments" :key="comment.id">
              <strong>{{ comment.authorName }}</strong>
              <p>{{ comment.content }}</p>
            </div>
          </div>
          <StateBlock v-else title="暂无评论" :show-action="false" />
          <el-form class="stack-form dense comment-form" :model="commentForm" label-position="top">
            <el-form-item label="发表评论">
              <el-input v-model="commentForm.content" type="textarea" :rows="3" />
            </el-form-item>
            <el-button type="primary" :icon="ChatDotRound" @click="submitComment">评论</el-button>
          </el-form>
        </article>
      </div>
    </el-drawer>
  </section>
</template>
