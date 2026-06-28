<script setup>
import { Delete, Plus, Search } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { computed, onMounted, reactive, ref } from 'vue'
import MetricCard from '../../components/common/MetricCard.vue'
import StateBlock from '../../components/common/StateBlock.vue'
import { MealType } from '../../constants/enums'
import { MealTypeLabel, toOptions } from '../../constants/labels'
import {
  createManualMealRecord,
  createMealRecordFromRecipe,
  deleteMealRecord,
  fetchDayMealRecords,
} from '../../services/modules/meal.service'
import { fetchRecipes } from '../../services/modules/recipe.service'
import { handleRequestError } from '../../services/request/http'
import { today } from '../../utils/date'
import { formatCalorie } from '../../utils/format'

const loading = ref(false)
const saving = ref(false)
const error = ref('')
const date = ref(today())
const records = ref([])
const summary = ref({ calorie: 0, protein: 0, fat: 0, carbohydrate: 0 })
const dialogVisible = ref(false)
const recipeDialogVisible = ref(false)
const recipeOptions = ref([])
const recipeKeyword = ref('')

const form = reactive({
  mealType: 'LUNCH',
  foodName: '',
  amount: 1,
  unit: '份',
  calorie: 400,
  protein: 20,
  fat: 10,
  carbohydrate: 45,
  note: '',
})

const recipeForm = reactive({
  recipeId: '',
  mealType: 'LUNCH',
  amount: 1,
  unit: '份',
})

const mealTypeOptions = computed(() => toOptions(MealType, MealTypeLabel))
const recordsByType = computed(() => {
  const grouped = Object.fromEntries(MealType.map((type) => [type, []]))
  for (const record of records.value) grouped[record.mealType]?.push(record)
  return grouped
})

async function load() {
  loading.value = true
  error.value = ''
  try {
    const result = await fetchDayMealRecords({ date: date.value })
    records.value = result?.records || []
    summary.value = result?.summary || { calorie: 0, protein: 0, fat: 0, carbohydrate: 0 }
  } catch (err) {
    error.value = handleRequestError(err).message
  } finally {
    loading.value = false
  }
}

function openManual() {
  Object.assign(form, {
    mealType: 'LUNCH',
    foodName: '',
    amount: 1,
    unit: '份',
    calorie: 400,
    protein: 20,
    fat: 10,
    carbohydrate: 45,
    note: '',
  })
  dialogVisible.value = true
}

async function submitManual() {
  saving.value = true
  try {
    await createManualMealRecord({ ...form, date: date.value })
    ElMessage.success('膳食记录已新增')
    dialogVisible.value = false
    await load()
  } catch (err) {
    handleRequestError(err)
  } finally {
    saving.value = false
  }
}

async function loadRecipes() {
  try {
    const result = await fetchRecipes({ page: 1, size: 20, keyword: recipeKeyword.value, status: 'ONLINE' })
    recipeOptions.value = result?.items || []
  } catch (err) {
    handleRequestError(err)
  }
}

async function openRecipeDialog() {
  Object.assign(recipeForm, { recipeId: '', mealType: 'LUNCH', amount: 1, unit: '份' })
  recipeKeyword.value = ''
  recipeDialogVisible.value = true
  await loadRecipes()
}

async function submitRecipeRecord() {
  const recipe = recipeOptions.value.find((item) => item.id === recipeForm.recipeId)
  if (!recipe) {
    ElMessage.error('请选择菜谱')
    return
  }
  saving.value = true
  try {
    await createMealRecordFromRecipe(recipe.id, {
      date: date.value,
      mealType: recipeForm.mealType,
      amount: recipeForm.amount,
      unit: recipeForm.unit,
      foodName: recipe.name,
      calorie: recipe.totalCalorie,
      protein: recipe.totalProtein,
      fat: recipe.totalFat,
      carbohydrate: recipe.totalCarbohydrate,
    })
    ElMessage.success('已从菜谱生成记录')
    recipeDialogVisible.value = false
    await load()
  } catch (err) {
    handleRequestError(err)
  } finally {
    saving.value = false
  }
}

async function removeRecord(row) {
  try {
    await ElMessageBox.confirm(`确认删除「${row.foodName}」？`, '删除记录', { type: 'warning' })
    await deleteMealRecord(row.id)
    ElMessage.success('记录已删除')
    await load()
  } catch (err) {
    if (err !== 'cancel') handleRequestError(err)
  }
}

onMounted(load)
</script>

<template>
  <section v-if="error" class="content-section">
    <StateBlock title="膳食记录加载失败" :description="error" @action="load" />
  </section>

  <section v-else class="meal-record-page" v-loading="loading">
    <article class="panel">
      <div class="section-heading">
        <div>
          <span class="eyebrow">MEAL RECORDS</span>
          <h2>每日膳食记录</h2>
        </div>
        <div class="action-group">
          <el-date-picker v-model="date" type="date" value-format="YYYY-MM-DD" />
          <el-button :icon="Search" @click="load">查询</el-button>
          <el-button @click="openRecipeDialog">从菜谱生成</el-button>
          <el-button type="primary" :icon="Plus" @click="openManual">手动记录</el-button>
        </div>
      </div>
    </article>

    <div class="dashboard-grid compact-grid">
      <MetricCard label="摄入热量" :value="formatCalorie(summary.calorie)" tone="green" />
      <MetricCard label="蛋白质" :value="`${summary.protein || 0} g`" />
      <MetricCard label="脂肪" :value="`${summary.fat || 0} g`" />
      <MetricCard label="碳水" :value="`${summary.carbohydrate || 0} g`" />
    </div>

    <StateBlock
      v-if="!records.length"
      title="暂无膳食记录"
      description="手动记录一餐，或从正式菜谱生成记录。"
      action-text="手动记录"
      @action="openManual"
    />

    <article v-else class="meal-record-board">
      <section v-for="type in MealType" :key="type" class="panel">
        <div class="meal-lane-heading">
          <span>{{ MealTypeLabel[type] }}</span>
          <small>{{ recordsByType[type]?.length || 0 }} 条</small>
        </div>
        <StateBlock v-if="!recordsByType[type]?.length" title="暂无记录" :show-action="false" />
        <el-table v-else :data="recordsByType[type]" class="dark-table">
          <el-table-column prop="foodName" label="食物" min-width="160" />
          <el-table-column prop="sourceType" label="来源" width="110">
            <template #default="{ row }"><el-tag>{{ row.sourceType }}</el-tag></template>
          </el-table-column>
          <el-table-column prop="amount" label="份量" width="110">
            <template #default="{ row }">{{ row.amount }} {{ row.unit }}</template>
          </el-table-column>
          <el-table-column prop="calorie" label="热量" width="110">
            <template #default="{ row }">{{ formatCalorie(row.calorie) }}</template>
          </el-table-column>
          <el-table-column prop="protein" label="蛋白质" width="100" />
          <el-table-column prop="fat" label="脂肪" width="90" />
          <el-table-column prop="carbohydrate" label="碳水" width="90" />
          <el-table-column label="操作" width="90">
            <template #default="{ row }">
              <el-button circle :icon="Delete" @click="removeRecord(row)" />
            </template>
          </el-table-column>
        </el-table>
      </section>
    </article>

    <el-dialog v-model="dialogVisible" title="手动记录" width="620px">
      <el-form class="stack-form dense" :model="form" label-position="top">
        <div class="form-row">
          <el-form-item label="餐次">
            <el-segmented v-model="form.mealType" :options="mealTypeOptions" />
          </el-form-item>
          <el-form-item label="食物名称">
            <el-input v-model="form.foodName" placeholder="例如：鸡胸肉沙拉" />
          </el-form-item>
        </div>
        <div class="form-row">
          <el-form-item label="份量">
            <el-input-number v-model="form.amount" :min="0.1" :precision="1" />
          </el-form-item>
          <el-form-item label="单位">
            <el-input v-model="form.unit" />
          </el-form-item>
        </div>
        <div class="form-row">
          <el-form-item label="热量 kcal">
            <el-input-number v-model="form.calorie" :min="0" />
          </el-form-item>
          <el-form-item label="蛋白质 g">
            <el-input-number v-model="form.protein" :min="0" :precision="1" />
          </el-form-item>
        </div>
        <div class="form-row">
          <el-form-item label="脂肪 g">
            <el-input-number v-model="form.fat" :min="0" :precision="1" />
          </el-form-item>
          <el-form-item label="碳水 g">
            <el-input-number v-model="form.carbohydrate" :min="0" :precision="1" />
          </el-form-item>
        </div>
        <el-form-item label="备注">
          <el-input v-model="form.note" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submitManual">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="recipeDialogVisible" title="从菜谱生成记录" width="560px">
      <el-form class="stack-form dense" :model="recipeForm" label-position="top">
        <div class="filter-bar compact-filter">
          <el-input v-model="recipeKeyword" :prefix-icon="Search" placeholder="搜索菜谱" clearable @keyup.enter="loadRecipes" />
          <el-button :icon="Search" @click="loadRecipes">搜索</el-button>
        </div>
        <el-form-item label="菜谱">
          <el-select v-model="recipeForm.recipeId" filterable>
            <el-option v-for="recipe in recipeOptions" :key="recipe.id" :label="recipe.name" :value="recipe.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="餐次">
          <el-segmented v-model="recipeForm.mealType" :options="mealTypeOptions" />
        </el-form-item>
        <div class="form-row">
          <el-form-item label="份量">
            <el-input-number v-model="recipeForm.amount" :min="0.1" :precision="1" />
          </el-form-item>
          <el-form-item label="单位">
            <el-input v-model="recipeForm.unit" />
          </el-form-item>
        </div>
      </el-form>
      <template #footer>
        <el-button @click="recipeDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submitRecipeRecord">生成记录</el-button>
      </template>
    </el-dialog>
  </section>
</template>
