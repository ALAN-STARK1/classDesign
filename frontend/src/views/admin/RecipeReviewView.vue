<script setup>
import { CircleCheck, CircleClose, Refresh, Search } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'
import StateBlock from '../../components/common/StateBlock.vue'
import { RecipeCategory, RecipeDifficulty, RecipeStatus } from '../../constants/enums'
import { RecipeCategoryLabel, RecipeDifficultyLabel } from '../../constants/labels'
import { fetchRecipes } from '../../services/modules/recipe.service'
import { reviewRecipe } from '../../services/modules/admin.service'
import { handleRequestError } from '../../services/request/http'

const loading = ref(false)
const error = ref('')
const recipes = ref([])
const pagination = reactive({ page: 1, size: 10, total: 0 })
const filters = reactive({ keyword: '', status: 'PENDING' })

const statusLabelMap = {
  DRAFT: '草稿',
  PENDING: '待审核',
  ONLINE: '已上线',
  OFFLINE: '已下线',
}

async function load() {
  loading.value = true
  error.value = ''
  try {
    const result = await fetchRecipes({
      page: pagination.page,
      size: pagination.size,
      keyword: filters.keyword,
      status: filters.status,
    })
    recipes.value = result?.items || []
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

async function doReview(row, status) {
  const action = status === 'ONLINE' ? '通过' : '拒绝'
  try {
    await ElMessageBox.confirm(`确认${action}菜谱「${row.name}」？`, `审核${action}`, { type: 'warning' })
    await reviewRecipe(row.id, { status })
    ElMessage.success(`菜谱已${action}`)
    await load()
  } catch (err) {
    if (err !== 'cancel') handleRequestError(err)
  }
}

onMounted(load)
</script>

<template>
  <section v-if="error" class="content-section">
    <StateBlock title="菜谱审核加载失败" :description="error" @action="load" />
  </section>

  <section v-else class="admin-page" v-loading="loading">
    <article class="panel">
      <div class="section-heading">
        <div>
          <span class="eyebrow">ADMIN / RECIPES</span>
          <h2>菜谱审核</h2>
        </div>
      </div>

      <div class="filter-bar compact-filter">
        <el-input v-model="filters.keyword" :prefix-icon="Search" placeholder="搜索菜谱名称" clearable @keyup.enter="search" />
        <el-select v-model="filters.status" placeholder="状态筛选">
          <el-option label="全部状态" value="" />
          <el-option v-for="s in RecipeStatus" :key="s" :label="statusLabelMap[s] || s" :value="s" />
        </el-select>
        <el-button :icon="Search" @click="search">筛选</el-button>
      </div>

      <StateBlock v-if="!recipes.length" title="暂无待审核菜谱" :show-action="false" />

      <template v-else>
        <el-table :data="recipes" class="dark-table">
          <el-table-column prop="name" label="菜谱" min-width="160" />
          <el-table-column prop="category" label="分类" width="90">
            <template #default="{ row }">{{ RecipeCategoryLabel[row.category] }}</template>
          </el-table-column>
          <el-table-column prop="difficulty" label="难度" width="80">
            <template #default="{ row }">{{ RecipeDifficultyLabel[row.difficulty] }}</template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="90">
            <template #default="{ row }">
              <el-tag :type="row.status === 'ONLINE' ? 'success' : row.status === 'PENDING' ? 'warning' : ''">
                {{ statusLabelMap[row.status] || row.status }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="160" fixed="right">
            <template #default="{ row }">
              <template v-if="row.status === 'PENDING' || row.status === 'DRAFT'">
                <el-button type="primary" :icon="CircleCheck" @click="doReview(row, 'ONLINE')">通过</el-button>
                <el-button type="danger" :icon="CircleClose" @click="doReview(row, 'OFFLINE')">拒绝</el-button>
              </template>
              <span v-else class="muted-copy">—</span>
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
  </section>
</template>
