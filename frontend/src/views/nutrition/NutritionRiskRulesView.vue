<script setup>
import { Edit, Plus, Refresh, SwitchButton } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { computed, onMounted, reactive, ref } from 'vue'
import StateBlock from '../../components/common/StateBlock.vue'
import { RiskRuleStatus, RiskSeverity } from '../../constants/enums'
import { RiskRuleStatusLabel, RiskSeverityLabel, toOptions } from '../../constants/labels'
import {
  createNutritionRiskRule,
  evaluateNutritionRiskRules,
  fetchNutritionRiskRules,
  updateNutritionRiskRule,
  updateNutritionRiskRuleStatus,
} from '../../services/modules/nutrition.service'
import { handleRequestError } from '../../services/request/http'
import { today } from '../../utils/date'

const loading = ref(false)
const saving = ref(false)
const evaluating = ref(false)
const error = ref('')
const dialogVisible = ref(false)
const editingId = ref(null)
const rules = ref([])
const evaluationResults = ref([])
const pagination = reactive({ page: 1, size: 10, total: 0 })

const form = reactive({
  name: '',
  nutrient: 'sodium',
  operator: 'GT',
  thresholdMin: 0,
  thresholdMax: 2000,
  severity: 'WARNING',
  status: 'ENABLED',
  message: '',
})

const severityOptions = computed(() => toOptions(RiskSeverity, RiskSeverityLabel))
const statusOptions = computed(() => toOptions(RiskRuleStatus, RiskRuleStatusLabel))
const nutrientOptions = [
  { label: '热量', value: 'calorie' },
  { label: '蛋白质', value: 'protein' },
  { label: '脂肪', value: 'fat' },
  { label: '碳水', value: 'carbohydrate' },
  { label: '膳食纤维', value: 'fiber' },
  { label: '钠', value: 'sodium' },
]
const operatorOptions = [
  { label: '大于', value: 'GT' },
  { label: '小于', value: 'LT' },
  { label: '区间', value: 'BETWEEN' },
]

function resetForm(row = null) {
  editingId.value = row?.id || null
  Object.assign(form, {
    name: row?.name || '',
    nutrient: row?.nutrient || 'sodium',
    operator: row?.operator || 'GT',
    thresholdMin: row?.thresholdMin ?? 0,
    thresholdMax: row?.thresholdMax ?? 2000,
    severity: row?.severity || 'WARNING',
    status: row?.status || 'ENABLED',
    message: row?.message || '',
  })
}

async function load() {
  loading.value = true
  error.value = ''
  try {
    const result = await fetchNutritionRiskRules({ page: pagination.page, size: pagination.size })
    rules.value = result?.items || []
    pagination.total = result?.total || 0
  } catch (err) {
    error.value = handleRequestError(err).message
  } finally {
    loading.value = false
  }
}

function openCreate() {
  resetForm()
  dialogVisible.value = true
}

function openEdit(row) {
  resetForm(row)
  dialogVisible.value = true
}

async function submit() {
  saving.value = true
  try {
    if (editingId.value) await updateNutritionRiskRule(editingId.value, { ...form })
    else await createNutritionRiskRule({ ...form })
    ElMessage.success(editingId.value ? '风险规则已更新' : '风险规则已创建')
    dialogVisible.value = false
    await load()
  } catch (err) {
    handleRequestError(err)
  } finally {
    saving.value = false
  }
}

async function toggleStatus(row) {
  try {
    const nextStatus = row.status === 'ENABLED' ? 'DISABLED' : 'ENABLED'
    await updateNutritionRiskRuleStatus(row.id, { status: nextStatus })
    ElMessage.success(nextStatus === 'ENABLED' ? '规则已启用' : '规则已停用')
    await load()
  } catch (err) {
    handleRequestError(err)
  }
}

async function evaluate() {
  evaluating.value = true
  try {
    evaluationResults.value = await evaluateNutritionRiskRules({ date: today() })
    ElMessage.success('规则执行完成')
  } catch (err) {
    handleRequestError(err)
  } finally {
    evaluating.value = false
  }
}

onMounted(load)
</script>

<template>
  <section v-if="error" class="content-section">
    <StateBlock title="风险规则加载失败" :description="error" @action="load" />
  </section>

  <section v-else class="risk-rule-page" v-loading="loading">
    <div class="split-view">
      <article class="panel">
        <div class="section-heading">
          <div>
            <span class="eyebrow">RULES</span>
            <h2>风险规则配置</h2>
          </div>
          <div class="action-group">
            <el-button :icon="Refresh" :loading="evaluating" @click="evaluate">执行评估</el-button>
            <el-button type="primary" :icon="Plus" @click="openCreate">新增规则</el-button>
          </div>
        </div>

        <el-table :data="rules" class="dark-table">
          <el-table-column prop="name" label="规则" min-width="140" />
          <el-table-column prop="nutrient" label="指标" width="110" />
          <el-table-column prop="operator" label="条件" width="90" />
          <el-table-column prop="thresholdMax" label="阈值" width="110">
            <template #default="{ row }">{{ row.operator === 'BETWEEN' ? `${row.thresholdMin}-${row.thresholdMax}` : row.thresholdMax }}</template>
          </el-table-column>
          <el-table-column prop="severity" label="级别" width="100">
            <template #default="{ row }"><el-tag>{{ RiskSeverityLabel[row.severity] }}</el-tag></template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">{{ RiskRuleStatusLabel[row.status] }}</template>
          </el-table-column>
          <el-table-column label="操作" width="180" fixed="right">
            <template #default="{ row }">
              <el-button :icon="Edit" @click="openEdit(row)">编辑</el-button>
              <el-button circle :icon="SwitchButton" @click="toggleStatus(row)" />
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
            @size-change="load"
          />
        </div>
      </article>

      <aside class="side-stack">
        <article class="panel">
          <div class="section-heading">
            <div>
              <span class="eyebrow">EVALUATION</span>
              <h2>执行结果</h2>
            </div>
          </div>
          <StateBlock v-if="!evaluationResults.length" title="暂无执行结果" description="点击执行评估后展示触发的风险。" :show-action="false" />
          <div v-else class="timeline-list">
            <div v-for="item in evaluationResults" :key="item.id">
              <strong>{{ item.title }} · {{ RiskSeverityLabel[item.severity] }}</strong>
              <p>{{ item.suggestion }}</p>
            </div>
          </div>
        </article>
      </aside>
    </div>

    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑风险规则' : '新增风险规则'" width="620px">
      <el-form class="stack-form dense" :model="form" label-position="top">
        <div class="form-row">
          <el-form-item label="规则名称">
            <el-input v-model="form.name" />
          </el-form-item>
          <el-form-item label="指标">
            <el-select v-model="form.nutrient">
              <el-option v-for="item in nutrientOptions" :key="item.value" v-bind="item" />
            </el-select>
          </el-form-item>
        </div>
        <div class="form-row">
          <el-form-item label="条件">
            <el-select v-model="form.operator">
              <el-option v-for="item in operatorOptions" :key="item.value" v-bind="item" />
            </el-select>
          </el-form-item>
          <el-form-item label="风险级别">
            <el-select v-model="form.severity">
              <el-option v-for="item in severityOptions" :key="item.value" v-bind="item" />
            </el-select>
          </el-form-item>
        </div>
        <div class="form-row">
          <el-form-item label="最小阈值">
            <el-input-number v-model="form.thresholdMin" :min="0" />
          </el-form-item>
          <el-form-item label="最大阈值">
            <el-input-number v-model="form.thresholdMax" :min="0" />
          </el-form-item>
        </div>
        <el-form-item label="状态">
          <el-segmented v-model="form.status" :options="statusOptions" />
        </el-form-item>
        <el-form-item label="提示文案">
          <el-input v-model="form.message" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submit">保存</el-button>
      </template>
    </el-dialog>
  </section>
</template>
