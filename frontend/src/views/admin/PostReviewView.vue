<script setup>
import { CircleCheck, CircleClose, Delete, Picture, Search, View } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'
import StateBlock from '../../components/common/StateBlock.vue'
import { CommunityPostStatus } from '../../constants/enums'
import { CommunityPostStatusLabel } from '../../constants/labels'
import { fetchCommunityPostDetail, fetchCommunityPosts } from '../../services/modules/community.service'
import { deletePost, reviewPost } from '../../services/modules/admin.service'
import { handleRequestError } from '../../services/request/http'
import { resolveUploadUrl } from '../../utils/media'

const loading = ref(false)
const error = ref('')
const posts = ref([])
const detail = ref(null)
const detailVisible = ref(false)
const activeImage = ref('')
const pagination = reactive({ page: 1, size: 10, total: 0 })
const filters = reactive({ keyword: '', status: 'PENDING' })

const statusOptions = [
  { label: '全部状态', value: '' },
  ...CommunityPostStatus.map((s) => ({ label: CommunityPostStatusLabel[s], value: s })),
]

async function load() {
  loading.value = true
  error.value = ''
  try {
    const result = await fetchCommunityPosts({
      page: pagination.page,
      size: pagination.size,
      keyword: filters.keyword,
      status: filters.status || 'ALL',
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

function imageSrc(url) {
  return resolveUploadUrl(url)
}

async function doReview(row, status) {
  const action = status === 'PUBLISHED' ? '通过' : '拒绝'
  try {
    await ElMessageBox.confirm(`确认${action}帖子《${row.title}》？`, `审核${action}`, { type: 'warning' })
    await reviewPost(row.id, { status })
    ElMessage.success(`帖子已${action}`)
    await load()
  } catch (err) {
    if (err !== 'cancel') handleRequestError(err)
  }
}

async function openDetail(row) {
  try {
    detail.value = await fetchCommunityPostDetail(row.id)
    activeImage.value = imageSrc(detail.value?.images?.[0]?.url || detail.value?.coverImageUrl)
    detailVisible.value = true
  } catch (err) {
    handleRequestError(err)
  }
}

async function doDelete(row) {
  try {
    await ElMessageBox.confirm(`确认删除已发布帖子《${row.title}》？删除后将不再展示在社区。`, '删除帖子', { type: 'warning' })
    await deletePost(row.id)
    ElMessage.success('帖子已删除')
    await load()
  } catch (err) {
    if (err !== 'cancel') handleRequestError(err)
  }
}

onMounted(load)
</script>

<template>
  <section v-if="error" class="content-section">
    <StateBlock title="帖子审核加载失败" :description="error" @action="load" />
  </section>

  <section v-else class="admin-page" v-loading="loading">
    <article class="panel">
      <div class="section-heading">
        <div>
          <span class="eyebrow">ADMIN / POSTS</span>
          <h2>帖子审核</h2>
        </div>
      </div>

      <div class="filter-bar compact-filter">
        <el-input v-model="filters.keyword" :prefix-icon="Search" placeholder="搜索帖子标题" clearable @keyup.enter="search" />
        <el-select v-model="filters.status" placeholder="状态筛选">
          <el-option v-for="item in statusOptions" :key="item.value" v-bind="item" />
        </el-select>
        <el-button :icon="Search" @click="search">筛选</el-button>
      </div>

      <StateBlock v-if="!posts.length" title="暂无待审核帖子" :show-action="false" />

      <template v-else>
        <el-table :data="posts" class="dark-table">
          <el-table-column label="封面" width="120">
            <template #default="{ row }">
              <div v-if="row.coverImageUrl" class="review-post-cover">
                <img :src="imageSrc(row.coverImageUrl)" :alt="row.title" />
              </div>
              <div v-else class="review-post-cover review-post-cover-empty">
                <el-icon><Picture /></el-icon>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="title" label="标题" min-width="220" />
          <el-table-column prop="content" label="正文" min-width="260" show-overflow-tooltip />
          <el-table-column prop="recipeName" label="关联菜谱" min-width="150" show-overflow-tooltip />
          <el-table-column label="标签" min-width="160">
            <template #default="{ row }">
              <el-tag v-for="tag in row.tags || []" :key="tag" size="small">{{ tag }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="author" label="作者" width="120">
            <template #default="{ row }">{{ row.author || row.authorName || row.username }}</template>
          </el-table-column>
          <el-table-column prop="imageCount" label="图片数" width="90" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === 'PUBLISHED' ? 'success' : row.status === 'PENDING' ? 'warning' : 'danger'">
                {{ CommunityPostStatusLabel[row.status] }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="240" fixed="right">
            <template #default="{ row }">
              <el-button :icon="View" @click="openDetail(row)">详情</el-button>
              <template v-if="row.status === 'PENDING'">
                <el-button type="primary" :icon="CircleCheck" @click="doReview(row, 'PUBLISHED')">通过</el-button>
                <el-button type="danger" :icon="CircleClose" @click="doReview(row, 'REJECTED')">拒绝</el-button>
              </template>
              <el-button v-else-if="row.status === 'PUBLISHED'" type="danger" :icon="Delete" @click="doDelete(row)">
                删除
              </el-button>
              <span v-else class="muted-copy">已处理</span>
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

    <el-drawer v-model="detailVisible" title="帖子审核详情" size="680px">
      <div v-if="detail" class="side-stack">
        <article class="panel inset-panel">
          <div class="section-heading">
            <div>
              <span class="eyebrow">POST DETAIL</span>
              <h2>{{ detail.title }}</h2>
            </div>
            <el-tag>{{ CommunityPostStatusLabel[detail.status] || detail.status }}</el-tag>
          </div>
          <dl class="detail-list">
            <div><dt>作者</dt><dd>{{ detail.authorName }}</dd></div>
            <div><dt>关联菜谱</dt><dd>{{ detail.recipeName || '-' }}</dd></div>
            <div><dt>图片数</dt><dd>{{ detail.imageCount || 0 }}</dd></div>
            <div><dt>发布时间</dt><dd>{{ detail.publishedAt || '-' }}</dd></div>
          </dl>
          <p class="community-copy">{{ detail.content }}</p>
          <div class="tag-cloud compact-tags">
            <el-tag v-for="tag in detail.tags || []" :key="tag">{{ tag }}</el-tag>
          </div>
        </article>

        <div v-if="detail.images?.length" class="community-gallery">
          <div class="community-gallery-main">
            <img :src="activeImage" :alt="detail.title" />
          </div>
          <div class="community-gallery-strip">
            <button
              v-for="image in detail.images"
              :key="image.id"
              class="community-gallery-thumb"
              :class="{ active: activeImage === imageSrc(image.url) }"
              type="button"
              @click="activeImage = imageSrc(image.url)"
            >
              <img :src="imageSrc(image.url)" :alt="detail.title" />
            </button>
          </div>
        </div>
      </div>
    </el-drawer>
  </section>
</template>
