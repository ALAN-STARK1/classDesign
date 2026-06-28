<script setup>
import { Delete, Plus, Search } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import StateBlock from '../../components/common/StateBlock.vue'
import { RecipeCategory, RecipeDifficulty, RecipeStatus } from '../../constants/enums'
import { RecipeCategoryLabel, RecipeDifficultyLabel, RecipeStatusLabel, toOptions } from '../../constants/labels'
import {
  bindRecipeIngredients,
  createRecipe,
  fetchIngredients,
  fetchRecipeDetail,
  updateRecipe,
} from '../../services/modules/recipe.service'
import { handleRequestError } from '../../services/request/http'
import { formatCalorie } from '../../utils/format'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const saving = ref(false)
const error = ref('')
const ingredientOptions = ref([])
const ingredientKeyword = ref('')
const recipeId = computed(() => route.params.id)
const isEditing = computed(() => Boolean(recipeId.value))

const form = reactive({
  name: '',
  description: '',
  category: 'LUNCH',
  difficulty: 'NORMAL',
  cookMinutes: 25,
  servings: 1,
  status: 'DRAFT',
  stepsText: '清洗并称量所有食材。\n按食材熟成时间依次烹调。\n装盘后记录实际摄入份量。',
})

const rows = ref([])

const categoryOptions = computed(() => toOptions(RecipeCategory, RecipeCategoryLabel))
const difficultyOptions = computed(() => toOptions(RecipeDifficulty, RecipeDifficultyLabel))
const statusOptions = computed(() => toOptions(RecipeStatus, RecipeStatusLabel))
const selectedIngredientIds = computed(() => new Set(rows.value.map((row) => row.ingredientId)))
const nutritionPreview = computed(() =>
  rows.value.reduce(
    (total, row) => {
      const source = ingredientOptions.value.find((item) => item.id === row.ingredientId)
      const amount = Number(row.amount || 0)
      total.totalCalorie += Math.round(((source?.caloriePer100g || 0) * amount) / 100)
      total.totalProtein += ((source?.proteinPer100g || 0) * amount) / 100
      total.totalFat += ((source?.fatPer100g || 0) * amount) / 100
      total.totalCarbohydrate += ((source?.carbohydratePer100g || 0) * amount) / 100
      return total
    },
    { totalCalorie: 0, totalProtein: 0, totalFat: 0, totalCarbohydrate: 0 },
  ),
)

function fillRecipe(recipe) {
  Object.assign(form, {
    name: recipe?.name || '',
    description: recipe?.description || '',
    category: recipe?.category || 'LUNCH',
    difficulty: recipe?.difficulty || 'NORMAL',
    cookMinutes: recipe?.cookMinutes || 25,
    servings: recipe?.servings || 1,
    status: recipe?.status || 'DRAFT',
    stepsText: (recipe?.steps || []).join('\n') || form.stepsText,
  })
  rows.value = (recipe?.ingredients || []).map((item) => ({
    ingredientId: item.ingredientId,
    amount: item.amount,
    unit: item.unit || 'g',
  }))
}

async function loadIngredients() {
  const result = await fetchIngredients({ page: 1, size: 80, keyword: ingredientKeyword.value, status: 'ENABLED' })
  ingredientOptions.value = result?.items || []
}

async function load() {
  loading.value = true
  error.value = ''
  try {
    await loadIngredients()
    if (isEditing.value) fillRecipe(await fetchRecipeDetail(recipeId.value))
  } catch (err) {
    error.value = handleRequestError(err).message
  } finally {
    loading.value = false
  }
}

function addIngredient() {
  const next = ingredientOptions.value.find((item) => !selectedIngredientIds.value.has(item.id))
  if (!next) return
  rows.value.push({ ingredientId: next.id, amount: 100, unit: next.unit || 'g' })
}

function removeIngredient(index) {
  rows.value.splice(index, 1)
}

function getIngredient(id) {
  return ingredientOptions.value.find((item) => item.id === id)
}

function normalizeRows() {
  return rows.value.map((row) => {
    const source = getIngredient(row.ingredientId)
    return {
      ingredientId: row.ingredientId,
      amount: Number(row.amount || 0),
      unit: row.unit || source?.unit || 'g',
    }
  })
}

async function submit() {
  saving.value = true
  try {
    const payload = {
      name: form.name,
      description: form.description,
      category: form.category,
      difficulty: form.difficulty,
      cookMinutes: form.cookMinutes,
      servings: form.servings,
      status: form.status,
      steps: form.stepsText.split('\n').map((item) => item.trim()).filter(Boolean),
    }
    const saved = isEditing.value ? await updateRecipe(recipeId.value, payload) : await createRecipe(payload)
    await bindRecipeIngredients(saved.id, normalizeRows())
    ElMessage.success(isEditing.value ? '菜谱已更新' : '菜谱已创建')
    router.push(`/recipes/${saved.id}`)
  } catch (err) {
    handleRequestError(err)
  } finally {
    saving.value = false
  }
}

onMounted(load)
</script>

<template>
  <section v-if="error" class="content-section">
    <StateBlock title="菜谱编辑器加载失败" :description="error" @action="load" />
  </section>

  <section v-else class="recipe-editor-page" v-loading="loading">
    <article class="panel">
      <div class="section-heading">
        <div>
          <span class="eyebrow">RECIPE STUDIO</span>
          <h2>{{ isEditing ? '编辑菜谱' : '新建菜谱' }}</h2>
        </div>
        <el-button type="primary" :loading="saving" @click="submit">保存菜谱</el-button>
      </div>

      <el-form class="stack-form dense" :model="form" label-position="top">
        <div class="form-row">
          <el-form-item label="菜谱名称">
            <el-input v-model="form.name" placeholder="例如：鸡胸肉能量碗" />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="form.status">
              <el-option v-for="item in statusOptions" :key="item.value" v-bind="item" />
            </el-select>
          </el-form-item>
        </div>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="3" />
        </el-form-item>
        <div class="form-row">
          <el-form-item label="餐次">
            <el-segmented v-model="form.category" :options="categoryOptions" />
          </el-form-item>
          <el-form-item label="难度">
            <el-segmented v-model="form.difficulty" :options="difficultyOptions" />
          </el-form-item>
        </div>
        <div class="form-row">
          <el-form-item label="烹饪时间 min">
            <el-input-number v-model="form.cookMinutes" :min="1" :max="240" />
          </el-form-item>
          <el-form-item label="份数">
            <el-input-number v-model="form.servings" :min="1" :max="12" />
          </el-form-item>
        </div>
        <el-form-item label="制作步骤">
          <el-input v-model="form.stepsText" type="textarea" :rows="5" />
        </el-form-item>
      </el-form>
    </article>

    <div class="split-view">
      <article class="panel">
        <div class="section-heading">
          <div>
            <span class="eyebrow">INGREDIENTS</span>
            <h2>食材绑定</h2>
          </div>
          <div class="action-group">
            <el-input v-model="ingredientKeyword" :prefix-icon="Search" placeholder="搜索可选食材" clearable @keyup.enter="loadIngredients" />
            <el-button :icon="Search" @click="loadIngredients">搜索</el-button>
            <el-button type="primary" :icon="Plus" @click="addIngredient">添加</el-button>
          </div>
        </div>

        <StateBlock
          v-if="!rows.length"
          title="还没有绑定食材"
          description="添加食材后，系统会按用量计算热量、蛋白质、脂肪和碳水。"
          action-text="添加食材"
          @action="addIngredient"
        />

        <el-table v-else :data="rows" class="dark-table">
          <el-table-column label="食材" min-width="180">
            <template #default="{ row }">
              <el-select v-model="row.ingredientId">
                <el-option
                  v-for="item in ingredientOptions"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="用量" width="150">
            <template #default="{ row }">
              <el-input-number v-model="row.amount" :min="1" :max="2000" />
            </template>
          </el-table-column>
          <el-table-column label="单位" width="100">
            <template #default="{ row }">
              <el-input v-model="row.unit" />
            </template>
          </el-table-column>
          <el-table-column label="预估热量" width="120">
            <template #default="{ row }">
              {{ Math.round(((getIngredient(row.ingredientId)?.caloriePer100g || 0) * row.amount) / 100) }} kcal
            </template>
          </el-table-column>
          <el-table-column label="操作" width="90">
            <template #default="{ $index }">
              <el-button circle :icon="Delete" @click="removeIngredient($index)" />
            </template>
          </el-table-column>
        </el-table>
      </article>

      <aside class="side-stack">
        <MetricCard label="预估热量" :value="formatCalorie(nutritionPreview.totalCalorie)" tone="green" />
        <MetricCard label="蛋白质" :value="`${nutritionPreview.totalProtein.toFixed(1)} g`" />
        <MetricCard label="脂肪" :value="`${nutritionPreview.totalFat.toFixed(1)} g`" />
        <MetricCard label="碳水" :value="`${nutritionPreview.totalCarbohydrate.toFixed(1)} g`" />
      </aside>
    </div>
  </section>
</template>
