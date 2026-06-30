<script setup>
import { Edit, Plus, Search, View } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { computed, onMounted, reactive, ref } from 'vue'
import StateBlock from '../../components/common/StateBlock.vue'
import { IngredientCategory } from '../../constants/enums'
import { IngredientCategoryLabel, IngredientStatusLabel, toOptions } from '../../constants/labels'
import {
  createIngredient,
  disableIngredient,
  fetchIngredientDetail,
  fetchIngredientPairings,
  fetchIngredients,
  updateIngredient,
} from '../../services/modules/recipe.service'
import { handleRequestError } from '../../services/request/http'

const loading = ref(false)
const saving = ref(false)
const error = ref('')
const ingredients = ref([])
const detail = ref(null)
const pairings = ref([])
const pairingsLoading = ref(false)
const detailVisible = ref(false)
const dialogVisible = ref(false)
const editingId = ref(null)
const pagination = reactive({ page: 1, size: 10, total: 0 })
const filters = reactive({ keyword: '', category: '', status: '' })

const form = reactive({
  name: '',
  category: 'VEGETABLE',
  unit: 'g',
  caloriePer100g: 30,
  proteinPer100g: 2,
  fatPer100g: 0.4,
  carbohydratePer100g: 4,
  gi: 35,
  allergens: [],
  description: '',
})

const categoryOptions = computed(() => toOptions(IngredientCategory, IngredientCategoryLabel))
const statusOptions = [
  { label: '全部状态', value: '' },
  { label: '启用', value: 'ENABLED' },
  { label: '停用', value: 'DISABLED' },
]
const allergenOptions = ['花生', '虾', '蟹', '牛奶', '鸡蛋', '坚果', '小麦', '大豆']

function resetForm(row = null) {
  editingId.value = row?.id || null
  Object.assign(form, {
    name: row?.name || '',
    category: row?.category || 'VEGETABLE',
    unit: row?.unit || 'g',
    caloriePer100g: row?.caloriePer100g ?? 30,
    proteinPer100g: row?.proteinPer100g ?? 2,
    fatPer100g: row?.fatPer100g ?? 0.4,
    carbohydratePer100g: row?.carbohydratePer100g ?? 4,
    gi: row?.gi ?? 35,
    allergens: row?.allergens ? [...row.allergens] : [],
    description: row?.description || '',
  })
}

async function load() {
  loading.value = true
  error.value = ''
  try {
    const result = await fetchIngredients({
      page: pagination.page,
      size: pagination.size,
      keyword: filters.keyword,
      category: filters.category,
      status: filters.status,
    })
    ingredients.value = result?.items || []
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
  resetForm()
  dialogVisible.value = true
}

function openEdit(row) {
  resetForm(row)
  dialogVisible.value = true
}

async function openDetail(row) {
  try {
    detail.value = await fetchIngredientDetail(row.id)
    detailVisible.value = true
    pairingsLoading.value = true
    pairings.value = await fetchIngredientPairings(row.id).catch(() => [])
  } catch (err) {
    handleRequestError(err)
  } finally {
    pairingsLoading.value = false
  }
}

async function submit() {
  saving.value = true
  try {
    const payload = { ...form, allergens: [...form.allergens] }
    if (editingId.value) await updateIngredient(editingId.value, payload)
    else await createIngredient(payload)
    ElMessage.success(editingId.value ? '食材已更新' : '食材已新增')
    dialogVisible.value = false
    await load()
  } catch (err) {
    handleRequestError(err)
  } finally {
    saving.value = false
  }
}

async function disable(row) {
  try {
    await ElMessageBox.confirm(`确认停用「${row.name}」？停用后不会进入新菜谱绑定。`, '停用食材', { type: 'warning' })
    await disableIngredient(row.id)
    ElMessage.success('食材已停用')
    await load()
  } catch (err) {
    if (err !== 'cancel') handleRequestError(err)
  }
}

onMounted(load)
</script>

<template>
  <section v-if="error" class="content-section">
    <StateBlock title="食材库加载失败" :description="error" @action="load" />
  </section>

  <section v-else class="library-page" v-loading="loading">
    <article class="panel">
      <div class="section-heading">
        <div>
          <span class="eyebrow">INGREDIENTS</span>
          <h2>食材库</h2>
        </div>
        <el-button type="primary" :icon="Plus" @click="openCreate">新增食材</el-button>
      </div>

      <div class="filter-bar">
        <el-input v-model="filters.keyword" :prefix-icon="Search" placeholder="搜索食材名称" clearable @keyup.enter="search" />
        <el-select v-model="filters.category" placeholder="全部分类" clearable>
          <el-option v-for="item in categoryOptions" :key="item.value" v-bind="item" />
        </el-select>
        <el-select v-model="filters.status" placeholder="全部状态">
          <el-option v-for="item in statusOptions" :key="item.value" v-bind="item" />
        </el-select>
        <el-button :icon="Search" @click="search">筛选</el-button>
      </div>

      <StateBlock
        v-if="!ingredients.length"
        title="暂无食材"
        description="调整筛选条件，或新增一个标准食材。"
        action-text="新增食材"
        @action="openCreate"
      />

      <template v-else>
        <el-table :data="ingredients" class="dark-table">
          <el-table-column prop="name" label="食材" min-width="140">
            <template #default="{ row }">
              <button class="text-link" type="button" @click="openDetail(row)">{{ row.name }}</button>
            </template>
          </el-table-column>
          <el-table-column prop="category" label="分类" width="120">
            <template #default="{ row }">{{ IngredientCategoryLabel[row.category] || row.category }}</template>
          </el-table-column>
          <el-table-column prop="caloriePer100g" label="热量/100g" width="120">
            <template #default="{ row }">{{ Math.round(row.caloriePer100g) }} kcal</template>
          </el-table-column>
          <el-table-column prop="proteinPer100g" label="蛋白质" width="100" />
          <el-table-column prop="fatPer100g" label="脂肪" width="90" />
          <el-table-column prop="carbohydratePer100g" label="碳水" width="90" />
          <el-table-column prop="status" label="状态" width="90">
            <template #default="{ row }">
              <el-tag :type="row.status === 'ENABLED' ? 'success' : 'info'">{{ IngredientStatusLabel[row.status] }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="220" fixed="right">
            <template #default="{ row }">
              <el-button :icon="View" @click="openDetail(row)">详情</el-button>
              <el-button :icon="Edit" @click="openEdit(row)">编辑</el-button>
              <el-button v-if="row.status === 'ENABLED'" @click="disable(row)">停用</el-button>
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

    <el-drawer v-model="detailVisible" title="食材详情" size="420px">
      <div v-if="detail" class="side-stack">
        <div class="ingredient-hero">
          <strong>{{ detail.name }}</strong>
          <span>{{ IngredientCategoryLabel[detail.category] }} · {{ detail.unit }}</span>
        </div>
        <dl class="detail-list">
          <div><dt>热量</dt><dd>{{ Math.round(detail.caloriePer100g) }} kcal / 100g</dd></div>
          <div><dt>蛋白质</dt><dd>{{ detail.proteinPer100g }} g</dd></div>
          <div><dt>脂肪</dt><dd>{{ detail.fatPer100g }} g</dd></div>
          <div><dt>碳水</dt><dd>{{ detail.carbohydratePer100g }} g</dd></div>
          <div><dt>GI</dt><dd>{{ detail.gi }}</dd></div>
        </dl>
        <div class="tag-cloud">
          <el-tag v-for="item in detail.allergens" :key="item">{{ item }}</el-tag>
          <el-tag v-if="!detail.allergens?.length">无常见过敏源</el-tag>
        </div>
        <p class="muted-copy">{{ detail.description }}</p>

        <div class="section-heading">
          <div>
            <span class="eyebrow">PAIRINGS</span>
            <h3>常见搭配</h3>
          </div>
        </div>
        <div v-loading="pairingsLoading" class="side-stack">
          <StateBlock
            v-if="!pairings.length"
            title="暂无搭配推荐"
            description="该食材尚未有足够共现数据。"
            :show-action="false"
          />
          <article v-for="item in pairings" :key="item.ingredientId" class="candidate-card">
            <div>
              <strong>{{ item.name }}</strong>
              <p>{{ item.recommendReason || `共现 ${item.coOccurrenceCount || 0} 次` }}</p>
            </div>
            <div class="nutrition-strip">
              <span>{{ Math.round(item.calorie || 0) }} kcal</span>
              <span>蛋白 {{ item.protein || 0 }}g</span>
            </div>
          </article>
        </div>
      </div>
    </el-drawer>

    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑食材' : '新增食材'" width="620px">
      <el-form class="stack-form dense" :model="form" label-position="top">
        <div class="form-row">
          <el-form-item label="名称">
            <el-input v-model="form.name" placeholder="例如：西兰花" />
          </el-form-item>
          <el-form-item label="分类">
            <el-select v-model="form.category">
              <el-option v-for="item in categoryOptions" :key="item.value" v-bind="item" />
            </el-select>
          </el-form-item>
        </div>
        <div class="form-row">
          <el-form-item label="单位">
            <el-input v-model="form.unit" />
          </el-form-item>
          <el-form-item label="GI">
            <el-input-number v-model="form.gi" :min="0" :max="120" />
          </el-form-item>
        </div>
        <div class="form-row">
          <el-form-item label="热量 kcal/100g">
            <el-input-number v-model="form.caloriePer100g" :min="0" />
          </el-form-item>
          <el-form-item label="蛋白质 g/100g">
            <el-input-number v-model="form.proteinPer100g" :min="0" :precision="1" />
          </el-form-item>
        </div>
        <div class="form-row">
          <el-form-item label="脂肪 g/100g">
            <el-input-number v-model="form.fatPer100g" :min="0" :precision="1" />
          </el-form-item>
          <el-form-item label="碳水 g/100g">
            <el-input-number v-model="form.carbohydratePer100g" :min="0" :precision="1" />
          </el-form-item>
        </div>
        <el-form-item label="过敏源">
          <el-select v-model="form.allergens" multiple clearable>
            <el-option v-for="item in allergenOptions" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item label="说明">
          <el-input v-model="form.description" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submit">保存</el-button>
      </template>
    </el-dialog>
  </section>
</template>
