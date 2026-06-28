<script setup>
import { Back, Edit, Refresh, Star, StarFilled } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import MetricCard from '../../components/common/MetricCard.vue'
import StateBlock from '../../components/common/StateBlock.vue'
import { RecipeCategoryLabel, RecipeDifficultyLabel, RecipeStatusLabel } from '../../constants/labels'
import {
  calculateRecipeNutrition,
  favoriteRecipe,
  fetchRecipeDetail,
  fetchRecipeSuitability,
  unfavoriteRecipe,
} from '../../services/modules/recipe.service'
import { handleRequestError } from '../../services/request/http'
import { formatCalorie, percent } from '../../utils/format'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const calculating = ref(false)
const error = ref('')
const recipe = ref(null)
const suitability = ref(null)

async function load() {
  loading.value = true
  error.value = ''
  try {
    const id = route.params.id
    const [detail, score] = await Promise.all([fetchRecipeDetail(id), fetchRecipeSuitability(id).catch(() => null)])
    recipe.value = detail
    suitability.value = score
  } catch (err) {
    error.value = handleRequestError(err).message
  } finally {
    loading.value = false
  }
}

async function toggleFavorite() {
  try {
    if (recipe.value.favorite) await unfavoriteRecipe(recipe.value.id)
    else await favoriteRecipe(recipe.value.id)
    recipe.value.favorite = !recipe.value.favorite
    ElMessage.success(recipe.value.favorite ? '已收藏菜谱' : '已取消收藏')
  } catch (err) {
    handleRequestError(err)
  }
}

async function calculateNutrition() {
  calculating.value = true
  try {
    const result = await calculateRecipeNutrition(recipe.value.id)
    Object.assign(recipe.value, result)
    ElMessage.success('营养数据已重新计算')
  } catch (err) {
    handleRequestError(err)
  } finally {
    calculating.value = false
  }
}

onMounted(load)
</script>

<template>
  <section v-if="error" class="content-section">
    <StateBlock title="菜谱详情加载失败" :description="error" @action="load" />
  </section>

  <section v-else-if="recipe" class="recipe-detail-page" v-loading="loading">
    <article class="recipe-hero-panel" :style="{ '--recipe-cover': recipe.coverColor }">
      <button class="circle-control" type="button" @click="router.push('/recipes')">
        <el-icon><Back /></el-icon>
      </button>
      <div class="recipe-hero-art">
        <span>{{ recipe.name.slice(0, 2) }}</span>
      </div>
      <div class="recipe-hero-copy">
        <span class="eyebrow">RECIPE DETAIL</span>
        <h2>{{ recipe.name }}</h2>
        <p>{{ recipe.description }}</p>
        <div class="tag-cloud compact-tags">
          <el-tag>{{ RecipeCategoryLabel[recipe.category] }}</el-tag>
          <el-tag>{{ RecipeDifficultyLabel[recipe.difficulty] }}</el-tag>
          <el-tag>{{ RecipeStatusLabel[recipe.status] }}</el-tag>
        </div>
      </div>
      <div class="action-group">
        <el-button :icon="recipe.favorite ? StarFilled : Star" @click="toggleFavorite">
          {{ recipe.favorite ? '已收藏' : '收藏' }}
        </el-button>
        <el-button :icon="Edit" @click="router.push(`/recipes/${recipe.id}/edit`)">编辑</el-button>
      </div>
    </article>

    <div class="dashboard-grid compact-grid">
      <MetricCard label="总热量" :value="formatCalorie(recipe.totalCalorie)" tone="green" />
      <MetricCard label="蛋白质" :value="`${recipe.totalProtein || 0} g`" />
      <MetricCard label="脂肪" :value="`${recipe.totalFat || 0} g`" />
      <MetricCard label="碳水" :value="`${recipe.totalCarbohydrate || 0} g`" />
    </div>

    <div class="split-view">
      <article class="panel">
        <div class="section-heading">
          <div>
            <span class="eyebrow">INGREDIENT BINDING</span>
            <h2>菜谱食材</h2>
          </div>
          <el-button :icon="Refresh" :loading="calculating" @click="calculateNutrition">营养计算</el-button>
        </div>
        <el-table :data="recipe.ingredients || []" class="dark-table">
          <el-table-column prop="name" label="食材" min-width="140" />
          <el-table-column prop="amount" label="用量" width="110">
            <template #default="{ row }">{{ row.amount }} {{ row.unit }}</template>
          </el-table-column>
          <el-table-column prop="calorie" label="热量" width="100">
            <template #default="{ row }">{{ row.calorie }} kcal</template>
          </el-table-column>
          <el-table-column prop="protein" label="蛋白质" width="100" />
          <el-table-column prop="fat" label="脂肪" width="90" />
          <el-table-column prop="carbohydrate" label="碳水" width="90" />
        </el-table>
      </article>

      <aside class="side-stack">
        <article class="panel">
          <div class="section-heading">
            <div>
              <span class="eyebrow">SUITABILITY</span>
              <h2>适配评分</h2>
            </div>
          </div>
          <div class="score-ring">
            <strong>{{ percent(suitability?.score || recipe.suitabilityScore) }}</strong>
            <span>{{ suitability?.level || '待评估' }}</span>
          </div>
          <div class="recommend-list">
            <strong>优势</strong>
            <p v-for="item in suitability?.highlights || []" :key="item">{{ item }}</p>
          </div>
          <div class="recommend-list warning-list">
            <strong>风险</strong>
            <p v-for="item in suitability?.risks || []" :key="item">{{ item }}</p>
          </div>
        </article>

        <article class="panel">
          <div class="section-heading">
            <div>
              <span class="eyebrow">STEPS</span>
              <h2>制作步骤</h2>
            </div>
          </div>
          <ol class="step-list">
            <li v-for="step in recipe.steps || []" :key="step">{{ step }}</li>
          </ol>
        </article>
      </aside>
    </div>
  </section>
</template>
