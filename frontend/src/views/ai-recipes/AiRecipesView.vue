<script setup>
import { Delete, DocumentAdd, Picture, Promotion, Search, Upload, View, MagicStick } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { computed, onBeforeUnmount, onMounted, reactive, ref } from 'vue'
import StateBlock from '../../components/common/StateBlock.vue'
import { AiRecipeSourceType, MealType } from '../../constants/enums'
import { AiRecipeSourceTypeLabel, AiRecipeStatusLabel, MealTypeLabel, RecipeCategoryLabel, toOptions } from '../../constants/labels'
import {
  confirmAiRecipe,
  convertAiRecipeToMealRecord,
  convertAiRecipeToRecipe,
  deleteAiRecipe,
  fetchAiRecipeDetail,
  fetchAiRecipeHistory,
  parseAiRecipeImage,
  parseAiRecipeText,
} from '../../services/modules/ai.service'
import { createCommunityPostFromAiRecipe, uploadCommunityPostImage } from '../../services/modules/community.service'
import { handleRequestError } from '../../services/request/http'
import { resolveUploadUrl } from '../../utils/media'
import { today } from '../../utils/date'
import { formatCalorie } from '../../utils/format'

const MAX_COMMUNITY_IMAGES = 9

const loading = ref(false)
const parsing = ref(false)
const converting = ref(false)
const uploadingPostImage = ref(false)
const error = ref('')
const history = ref([])
const detail = ref(null)
const detailVisible = ref(false)
const uploadFile = ref(null)
const uploadPreviewUrl = ref('')
const localPreviewUrl = ref('')
const pagination = reactive({ page: 1, size: 8, total: 0 })
const filters = reactive({ keyword: '', sourceType: '' })
const textForm = reactive({
  text: '鸡胸肉 150g，西兰花 120g，糙米饭 100g，橄榄油 5g，少盐煎熟后装盘。',
  prompt: '识别菜名、食材、步骤和营养估算',
})
const imageForm = reactive({
  prompt: '识别图片中的菜品和主要食材',
})
const recordForm = reactive({
  date: today(),
  mealType: 'LUNCH',
})
const postForm = reactive({
  title: '',
  content: '',
  useAiSourceImage: true,
  images: [],
})

const sourceOptions = computed(() => [{ label: '全部来源', value: '' }, ...toOptions(AiRecipeSourceType, AiRecipeSourceTypeLabel)])
const mealOptions = computed(() => toOptions(MealType, MealTypeLabel))
const canUseAiSourceImage = computed(() => Boolean(displaySourceImage.value))
const displaySourceImage = computed(() => {
  if (localPreviewUrl.value) return localPreviewUrl.value
  if (detail.value?.sourceImageUrl) return resolveUploadUrl(detail.value.sourceImageUrl)
  if (uploadPreviewUrl.value) return uploadPreviewUrl.value
  return ''
})
const communityImageCount = computed(() => postForm.images.length + (postForm.useAiSourceImage && canUseAiSourceImage.value ? 1 : 0))
const communityImageHint = computed(() => `${communityImageCount.value}/${MAX_COMMUNITY_IMAGES}`)

function revokeBlobUrl(url) {
  if (url?.startsWith('blob:')) {
    URL.revokeObjectURL(url)
  }
}

function clearPreviewUrls() {
  revokeBlobUrl(uploadPreviewUrl.value)
  revokeBlobUrl(localPreviewUrl.value)
  uploadPreviewUrl.value = ''
  localPreviewUrl.value = ''
}

function resetPostForm() {
  Object.assign(postForm, {
    title: detail.value ? `${detail.value.name} 分享` : '',
    content: '这份 AI 菜谱结构清晰，适合日常记录和转换。',
    useAiSourceImage: Boolean(displaySourceImage.value),
    images: [],
  })
}

async function load() {
  loading.value = true
  error.value = ''
  try {
    const result = await fetchAiRecipeHistory({
      page: pagination.page,
      size: pagination.size,
      keyword: filters.keyword,
      sourceType: filters.sourceType,
    })
    history.value = result?.items || []
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

async function parseText() {
  parsing.value = true
  try {
    detail.value = await parseAiRecipeText({ ...textForm })
    resetPostForm()
    detailVisible.value = true
    ElMessage.success('文本解析完成')
    await load()
  } catch (err) {
    handleRequestError(err)
  } finally {
    parsing.value = false
  }
}

function onFileChange(file) {
  uploadFile.value = file.raw
  revokeBlobUrl(uploadPreviewUrl.value)
  uploadPreviewUrl.value = URL.createObjectURL(file.raw)
}

async function parseImage() {
  if (!uploadFile.value) {
    ElMessage.error('请选择图片文件')
    return
  }
  parsing.value = true
  localPreviewUrl.value = uploadPreviewUrl.value
  uploadPreviewUrl.value = ''
  try {
    detail.value = await parseAiRecipeImage(uploadFile.value, imageForm.prompt)
    if (detail.value?.sourceImageUrl) {
      revokeBlobUrl(localPreviewUrl.value)
      localPreviewUrl.value = ''
    }
    resetPostForm()
    detailVisible.value = true
    uploadFile.value = null
    ElMessage.success('图片解析完成')
    await load()
  } catch (err) {
    revokeBlobUrl(localPreviewUrl.value)
    localPreviewUrl.value = ''
    handleRequestError(err)
  } finally {
    parsing.value = false
  }
}

async function openDetail(row) {
  try {
    clearPreviewUrls()
    detail.value = await fetchAiRecipeDetail(row.id)
    resetPostForm()
    detailVisible.value = true
  } catch (err) {
    handleRequestError(err)
  }
}

async function confirmDetail() {
  try {
    detail.value = await confirmAiRecipe(detail.value.id, detail.value)
    ElMessage.success('AI 菜谱已确认')
    await load()
  } catch (err) {
    handleRequestError(err)
  }
}

async function toRecipe() {
  converting.value = true
  try {
    await convertAiRecipeToRecipe(detail.value.id)
    ElMessage.success('已转为正式菜谱草稿')
    await load()
  } catch (err) {
    handleRequestError(err)
  } finally {
    converting.value = false
  }
}

async function toMealRecord() {
  converting.value = true
  try {
    await convertAiRecipeToMealRecord(detail.value.id, { ...recordForm })
    ElMessage.success('已生成膳食记录')
  } catch (err) {
    handleRequestError(err)
  } finally {
    converting.value = false
  }
}

function removePostImage(index) {
  postForm.images.splice(index, 1)
}

async function uploadPostImage(file) {
  if (communityImageCount.value >= MAX_COMMUNITY_IMAGES) {
    ElMessage.error(`社区帖子最多 ${MAX_COMMUNITY_IMAGES} 张图片`)
    return false
  }

  uploadingPostImage.value = true
  try {
    const uploaded = await uploadCommunityPostImage(file)
    postForm.images.push({
      id: uploaded.imageId,
      url: resolveUploadUrl(uploaded.imageUrl),
      width: uploaded.width,
      height: uploaded.height,
      fileSize: uploaded.fileSize,
      status: uploaded.status,
    })
    ElMessage.success('图片上传成功')
  } catch (err) {
    handleRequestError(err)
  } finally {
    uploadingPostImage.value = false
  }
  return false
}

async function handlePostImageChange(uploadFile) {
  if (!uploadFile?.raw) return
  await uploadPostImage(uploadFile.raw)
}

async function publishPost() {
  if (!detail.value) return

  if (!communityImageCount.value) {
    ElMessage.error('发布社区帖子至少需要 1 张图片')
    return
  }

  converting.value = true
  try {
    await createCommunityPostFromAiRecipe(detail.value.id, {
      title: postForm.title,
      content: postForm.content,
      useAiSourceImage: postForm.useAiSourceImage,
      aiSourceImageUrl: detail.value.sourceImageUrl || '',
      imageIds: postForm.images.map((item) => item.id),
    })
    ElMessage.success('已发布到社区')
    resetPostForm()
  } catch (err) {
    handleRequestError(err)
  } finally {
    converting.value = false
  }
}

async function remove(row) {
  try {
    await ElMessageBox.confirm(`确认删除《${row.name}》？`, '删除 AI 菜谱', { type: 'warning' })
    await deleteAiRecipe(row.id)
    ElMessage.success('AI 菜谱已删除')
    await load()
  } catch (err) {
    if (err !== 'cancel') handleRequestError(err)
  }
}

onMounted(load)
onBeforeUnmount(clearPreviewUrls)
</script>

<template>
  <section v-if="error" class="content-section">
    <StateBlock title="AI 菜谱加载失败" :description="error" @action="load" />
  </section>

  <section v-else class="ai-page" v-loading="loading">
    <div class="split-view ai-workbench">
      <article class="panel">
        <div class="section-heading">
          <div>
            <span class="eyebrow">AI PARSE</span>
            <h2>文本解析</h2>
          </div>
          <el-button type="primary" :icon="MagicStick" :loading="parsing" @click="parseText">解析文本</el-button>
        </div>
        <el-form class="stack-form dense" :model="textForm" label-position="top">
          <el-form-item label="菜谱文本">
            <el-input v-model="textForm.text" type="textarea" :rows="7" />
          </el-form-item>
          <el-form-item label="提示词">
            <el-input v-model="textForm.prompt" />
          </el-form-item>
        </el-form>
      </article>

      <aside class="side-stack">
        <article class="panel">
          <div class="section-heading">
            <div>
              <span class="eyebrow">IMAGE</span>
              <h2>图片解析</h2>
            </div>
          </div>
          <el-upload drag :auto-upload="false" :limit="1" :show-file-list="false" :on-change="onFileChange">
            <template v-if="uploadPreviewUrl">
              <div class="upload-image-preview">
                <img :src="uploadPreviewUrl" alt="待解析菜品图" />
                <span>点击或拖拽可重新选择</span>
              </div>
            </template>
            <template v-else>
              <el-icon><Upload /></el-icon>
              <div class="upload-copy">选择菜品图片</div>
            </template>
          </el-upload>
          <el-form class="stack-form dense image-prompt" :model="imageForm" label-position="top">
            <el-form-item label="提示词">
              <el-input v-model="imageForm.prompt" />
            </el-form-item>
            <el-button type="primary" :icon="MagicStick" :loading="parsing" @click="parseImage">解析图片</el-button>
          </el-form>
        </article>
      </aside>
    </div>

    <article class="panel">
      <div class="section-heading">
        <div>
          <span class="eyebrow">HISTORY</span>
          <h2>AI 菜谱历史</h2>
        </div>
      </div>
      <div class="filter-bar">
        <el-input v-model="filters.keyword" :prefix-icon="Search" placeholder="搜索 AI 菜谱" clearable @keyup.enter="search" />
        <el-select v-model="filters.sourceType" placeholder="全部来源">
          <el-option v-for="item in sourceOptions" :key="item.value" v-bind="item" />
        </el-select>
        <el-button :icon="Search" @click="search">筛选</el-button>
      </div>

      <StateBlock v-if="!history.length" title="暂无 AI 菜谱" description="解析文本或图片后会出现在这里。" :show-action="false" />
      <template v-else>
        <el-table :data="history" class="dark-table">
          <el-table-column prop="name" label="菜谱" min-width="170">
            <template #default="{ row }">
              <button class="text-link" type="button" @click="openDetail(row)">{{ row.name }}</button>
            </template>
          </el-table-column>
          <el-table-column prop="sourceType" label="来源" width="110">
            <template #default="{ row }">{{ AiRecipeSourceTypeLabel[row.sourceType] }}</template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }"><el-tag>{{ AiRecipeStatusLabel[row.status] }}</el-tag></template>
          </el-table-column>
          <el-table-column prop="confidence" label="置信度" width="100">
            <template #default="{ row }">{{ row.confidence }}%</template>
          </el-table-column>
          <el-table-column prop="totalCalorie" label="热量" width="110">
            <template #default="{ row }">{{ formatCalorie(row.totalCalorie) }}</template>
          </el-table-column>
          <el-table-column label="操作" width="180" fixed="right">
            <template #default="{ row }">
              <el-button :icon="View" @click="openDetail(row)">详情</el-button>
              <el-button circle :icon="Delete" @click="remove(row)" />
            </template>
          </el-table-column>
        </el-table>
        <div class="pagination-row">
          <el-pagination
            v-model:current-page="pagination.page"
            v-model:page-size="pagination.size"
            background
            layout="prev, pager, next, sizes, total"
            :total="pagination.total"
            @current-change="load"
            @size-change="search"
          />
        </div>
      </template>
    </article>

    <el-drawer v-model="detailVisible" title="AI 菜谱详情" size="560px">
      <div v-if="detail" class="side-stack">
        <div class="ai-hero">
          <span>{{ AiRecipeSourceTypeLabel[detail.sourceType] }} · {{ AiRecipeStatusLabel[detail.status] }}</span>
          <strong>{{ detail.name }}</strong>
          <small>{{ RecipeCategoryLabel[detail.category] }} · {{ detail.confidence }}% 置信度</small>
        </div>
        <div v-if="displaySourceImage" class="ai-source-image">
          <img :src="displaySourceImage" :alt="detail.name" />
          <small v-if="detail.sourceType === 'IMAGE'">识别原图</small>
        </div>
        <div class="nutrition-strip">
          <span>{{ formatCalorie(detail.totalCalorie) }}</span>
          <span>蛋白 {{ detail.totalProtein }}g</span>
          <span>碳水 {{ detail.totalCarbohydrate }}g</span>
        </div>
        <dl class="detail-list">
          <div v-for="item in detail.ingredients" :key="item.name">
            <dt>{{ item.name }}</dt>
            <dd>{{ item.amount }} {{ item.unit }}</dd>
          </div>
        </dl>
        <article class="recommend-list">
          <strong>步骤</strong>
          <p v-for="step in detail.steps" :key="step">{{ step }}</p>
        </article>
        <article v-if="detail.warnings?.length" class="recommend-list warning-list">
          <strong>提示</strong>
          <p v-for="item in detail.warnings" :key="item">{{ item }}</p>
        </article>
        <div class="drawer-actions">
          <el-button type="primary" :icon="DocumentAdd" @click="confirmDetail">确认</el-button>
          <el-button :loading="converting" @click="toRecipe">转正式菜谱</el-button>
        </div>
        <article class="panel inset-panel">
          <div class="section-heading">
            <div>
              <span class="eyebrow">RECORD</span>
              <h2>转膳食记录</h2>
            </div>
          </div>
          <el-form class="stack-form dense" :model="recordForm" label-position="top">
            <el-form-item label="日期">
              <el-date-picker v-model="recordForm.date" type="date" value-format="YYYY-MM-DD" />
            </el-form-item>
            <el-form-item label="餐次">
              <el-segmented v-model="recordForm.mealType" :options="mealOptions" />
            </el-form-item>
            <el-button :loading="converting" @click="toMealRecord">生成记录</el-button>
          </el-form>
        </article>
        <article class="panel inset-panel">
          <div class="section-heading">
            <div>
              <span class="eyebrow">COMMUNITY</span>
              <h2>发布社区</h2>
            </div>
            <span class="muted-copy">图片 {{ communityImageHint }}</span>
          </div>
          <el-form class="stack-form dense" :model="postForm" label-position="top">
            <el-form-item label="标题">
              <el-input v-model="postForm.title" />
            </el-form-item>
            <el-form-item label="内容">
              <el-input v-model="postForm.content" type="textarea" :rows="3" />
            </el-form-item>
            <el-form-item label="图片来源">
              <div class="image-upload-stack">
                <div v-if="canUseAiSourceImage" class="source-image-card">
                  <label class="source-image-toggle">
                    <input v-model="postForm.useAiSourceImage" type="checkbox" />
                    <span>优先复用 AI 原图</span>
                  </label>
                  <div class="source-image-preview">
                    <img :src="displaySourceImage" :alt="detail.name" />
                  </div>
                </div>
                <div v-else class="source-image-empty">
                  <el-icon><Picture /></el-icon>
                  <span>当前 AI 菜谱没有原图，需要手动上传社区图片</span>
                </div>
                <el-upload
                  drag
                  :show-file-list="false"
                  :auto-upload="false"
                  accept=".jpg,.jpeg,.png,.webp"
                  :on-change="handlePostImageChange"
                >
                  <el-icon><Upload /></el-icon>
                  <div class="upload-copy">{{ uploadingPostImage ? '上传中...' : '补充社区图片' }}</div>
                  <small>最多再上传 {{ MAX_COMMUNITY_IMAGES - (postForm.useAiSourceImage && canUseAiSourceImage ? 1 : 0) }} 张</small>
                </el-upload>
                <div v-if="postForm.images.length" class="thumb-grid">
                  <div v-for="(image, index) in postForm.images" :key="image.id" class="thumb-card">
                    <img :src="image.url" :alt="`社区图片 ${index + 1}`" />
                    <button class="thumb-remove" type="button" @click="removePostImage(index)">
                      <el-icon><Delete /></el-icon>
                    </button>
                  </div>
                </div>
              </div>
            </el-form-item>
            <el-button :icon="Promotion" :loading="converting" @click="publishPost">提交审核</el-button>
          </el-form>
        </article>
      </div>
    </el-drawer>
  </section>
</template>
