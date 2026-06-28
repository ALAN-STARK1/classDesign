<script setup>
import { Edit, Plus, Refresh, Search, Star, StarFilled, View } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import StateBlock from '../../components/common/StateBlock.vue'
import { RecipeCategory, RecipeDifficulty } from '../../constants/enums'
import { RecipeCategoryLabel, RecipeDifficultyLabel, RecipeStatusLabel, toOptions } from '../../constants/labels'
import { favoriteRecipe, fetchRecipes, recalculateRecipeSuitability, unfavoriteRecipe } from '../../services/modules/recipe.service'
import { handleRequestError } from '../../services/request/http'
import { formatCalorie, percent } from '../../utils/format'

const router = useRouter()
const loading = ref(false)
const recalculating = ref(false)
const error = ref('')
const recipes = ref([])
const pagination = reactive({ page: 1, size: 8, total: 0 })
const filters = reactive({ keyword: '', category: '', difficulty: '' })

const categoryOptions = computed(() => toOptions(RecipeCategory, RecipeCategoryLabel))
const difficultyOptions = computed(() => toOptions(RecipeDifficulty, RecipeDifficultyLabel))

async function load() {
  loading.value = true
  error.value = ''
  try {
    const result = await fetchRecipes({
      page: pagination.page,
      size: pagination.size,
      keyword: filters.keyword,
      category: filters.category,
      difficulty: filters.difficulty,
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

async function toggleFavorite(recipe) {
  try {
    if (recipe.favorite) await unfavoriteRecipe(recipe.id)
    else await favoriteRecipe(recipe.id)
    recipe.favorite = !recipe.favorite
    ElMessage.success(recipe.favorite ? '已收藏菜谱' : '已取消收藏')
  } catch (err) {
    handleRequestError(err)
  }
}

async function recalculate() {
  recalculating.value = true
  try {
    await recalculateRecipeSuitability()
    ElMessage.success('适配评分已刷新')
    await load()
  } catch (err) {
    handleRequestError(err)
  } finally {
    recalculating.value = false
  }
}

onMounted(load)
</script>

<template>
  <section v-if="error" class="content-section">
    <StateBlock title="菜谱广场加载失败" :description="error" @action="load" />
  </section>

  <section v-else class="library-page" v-loading="loading">
    <article class="panel">
      <div class="section-heading">
        <div>
          <span class="eyebrow">RECIPES</span>
          <h2>菜谱广场</h2>
        </div>
        <div class="action-group">
          <el-button :icon="Refresh" :loading="recalculating" @click="recalculate">刷新评分</el-button>
          <el-button type="primary" :icon="Plus" @click="router.push('/recipes/new')">新建菜谱</el-button>
        </div>
      </div>

      <div class="filter-bar">
        <el-input v-model="filters.keyword" :prefix-icon="Search" placeholder="搜索菜谱名称" clearable @keyup.enter="search" />
        <el-select v-model="filters.category" placeholder="餐次" clearable>
          <el-option v-for="item in categoryOptions" :key="item.value" v-bind="item" />
        </el-select>
        <el-select v-model="filters.difficulty" placeholder="难度" clearable>
          <el-option v-for="item in difficultyOptions" :key="item.value" v-bind="item" />
        </el-select>
        <el-button :icon="Search" @click="search">筛选</el-button>
      </div>

      <StateBlock
        v-if="!recipes.length"
        title="暂无菜谱"
        description="调整搜索条件，或创建一个正式菜谱。"
        action-text="新建菜谱"
        @action="router.push('/recipes/new')"
      />

      <template v-else>
        <div class="recipe-grid">
          <article v-for="recipe in recipes" :key="recipe.id" class="recipe-card">
            <button class="recipe-art" :style="{ backgroundColor: recipe.coverColor }" type="button" @click="router.push(`/recipes/${recipe.id}`)">
              <span>{{ recipe.name.slice(0, 2) }}</span>
            </button>
            <div class="recipe-body">
              <div class="recipe-title-row">
                <button class="text-link title-link" type="button" @click="router.push(`/recipes/${recipe.id}`)">{{ recipe.name }}</button>
                <el-button circle :icon="recipe.favorite ? StarFilled : Star" @click="toggleFavorite(recipe)" />
              </div>
              <p>{{ recipe.description }}</p>
              <div class="tag-cloud compact-tags">
                <el-tag>{{ RecipeCategoryLabel[recipe.category] }}</el-tag>
                <el-tag>{{ RecipeDifficultyLabel[recipe.difficulty] }}</el-tag>
                <el-tag>{{ RecipeStatusLabel[recipe.status] }}</el-tag>
              </div>
              <div class="nutrition-strip">
                <span>{{ formatCalorie(recipe.totalCalorie) }}</span>
                <span>蛋白 {{ recipe.totalProtein }}g</span>
                <span>适配 {{ percent(recipe.suitabilityScore) }}</span>
              </div>
              <div class="card-actions">
                <el-button :icon="View" @click="router.push(`/recipes/${recipe.id}`)">详情</el-button>
                <el-button :icon="Edit" @click="router.push(`/recipes/${recipe.id}/edit`)">编辑</el-button>
              </div>
            </div>
          </article>
        </div>

        <div class="pagination-row">
          <el-pagination
            v-model:current-page="pagination.page"
            v-model:page-size="pagination.size"
            background
            layout="prev, pager, next, sizes, total"
            :page-sizes="[8, 12, 16]"
            :total="pagination.total"
            @current-change="load"
            @size-change="search"
          />
        </div>
      </template>
    </article>
  </section>
</template>
