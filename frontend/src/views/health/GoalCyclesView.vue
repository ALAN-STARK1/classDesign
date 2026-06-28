<script setup>
import { Edit, Plus, Refresh, Select } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { computed, onMounted, reactive, ref } from 'vue'
import MetricCard from '../../components/common/MetricCard.vue'
import StateBlock from '../../components/common/StateBlock.vue'
import GoalProgressChart from '../../components/domain/GoalProgressChart.vue'
import { HealthGoal } from '../../constants/enums'
import { GoalCycleStatusLabel, HealthGoalLabel, toOptions } from '../../constants/labels'
import {
  cancelGoalCycle,
  completeGoalCycle,
  createGoalCycle,
  fetchCurrentGoalCycle,
  fetchGoalCycleProgress,
  fetchGoalCycles,
  updateGoalCycle,
} from '../../services/modules/health.service'
import { handleRequestError } from '../../services/request/http'
import { addDays, today } from '../../utils/date'
import { formatCalorie, formatKg, percent } from '../../utils/format'

const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const editingId = ref(null)
const error = ref('')
const cycles = ref([])
const currentCycle = ref(null)
const progress = ref(null)
const pagination = reactive({ page: 1, size: 10, total: 0 })

const form = reactive({
  goalType: 'FAT_LOSS',
  startDate: today(),
  endDate: addDays(today(), 56),
  startWeightKg: 62,
  targetWeightKg: 56,
  targetCalorie: 1600,
})

const goalOptions = computed(() => toOptions(HealthGoal, HealthGoalLabel))

function resetForm(cycle = null) {
  editingId.value = cycle?.id || null
  Object.assign(form, {
    goalType: cycle?.goalType || 'FAT_LOSS',
    startDate: cycle?.startDate || today(),
    endDate: cycle?.endDate || addDays(today(), 56),
    startWeightKg: cycle?.startWeightKg || 62,
    targetWeightKg: cycle?.targetWeightKg || 56,
    targetCalorie: cycle?.targetCalorie || 1600,
  })
}

async function load() {
  loading.value = true
  error.value = ''
  try {
    const [list, current] = await Promise.all([
      fetchGoalCycles({ page: pagination.page, size: pagination.size }),
      fetchCurrentGoalCycle().catch(() => null),
    ])
    cycles.value = list?.items || []
    pagination.total = list?.total || 0
    currentCycle.value = current
    progress.value = current ? await fetchGoalCycleProgress(current.id) : null
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
    if (editingId.value) await updateGoalCycle(editingId.value, { ...form })
    else await createGoalCycle({ ...form })
    ElMessage.success(editingId.value ? '目标周期已更新' : '目标周期已创建')
    dialogVisible.value = false
    await load()
  } catch (err) {
    handleRequestError(err)
  } finally {
    saving.value = false
  }
}

async function complete(row) {
  try {
    await ElMessageBox.confirm('确认将该目标周期标记为完成？', '完成目标', { type: 'warning' })
    await completeGoalCycle(row.id)
    ElMessage.success('目标周期已完成')
    await load()
  } catch (err) {
    if (err !== 'cancel') handleRequestError(err)
  }
}

async function cancel(row) {
  try {
    await ElMessageBox.confirm('确认取消该目标周期？', '取消目标', { type: 'warning' })
    await cancelGoalCycle(row.id)
    ElMessage.success('目标周期已取消')
    await load()
  } catch (err) {
    if (err !== 'cancel') handleRequestError(err)
  }
}

onMounted(load)
</script>

<template>
  <section v-if="error" class="content-section">
    <StateBlock title="目标周期加载失败" :description="error" @action="load" />
  </section>

  <section v-else class="goal-page" v-loading="loading">
    <div class="dashboard-grid compact-grid">
      <MetricCard label="当前状态" :value="currentCycle ? GoalCycleStatusLabel[currentCycle.status] : '无当前周期'" tone="green" />
      <MetricCard label="起始体重" :value="formatKg(currentCycle?.startWeightKg)" />
      <MetricCard label="目标体重" :value="formatKg(currentCycle?.targetWeightKg)" />
      <MetricCard label="目标热量" :value="formatCalorie(currentCycle?.targetCalorie)" />
    </div>

    <article class="panel">
      <div class="section-heading">
        <div>
          <span class="eyebrow">PROGRESS</span>
          <h2>当前目标进度</h2>
        </div>
        <el-button type="primary" :icon="Plus" @click="openCreate">新建周期</el-button>
      </div>

      <div v-if="currentCycle" class="progress-layout">
        <div class="progress-ring">
          <strong>{{ percent(currentCycle.progressPercent) }}</strong>
          <span>{{ HealthGoalLabel[currentCycle.goalType] }}</span>
        </div>
        <GoalProgressChart :trend="progress?.trend || []" />
      </div>
      <StateBlock v-else title="暂无当前目标周期" description="创建一个目标周期后，进度曲线会在这里出现。" :show-action="false" />
    </article>

    <article class="panel">
      <div class="section-heading">
        <div>
          <span class="eyebrow">HISTORY</span>
          <h2>目标周期列表</h2>
        </div>
        <el-button :icon="Refresh" @click="load">刷新</el-button>
      </div>
      <el-table :data="cycles" class="dark-table">
        <el-table-column prop="goalType" label="目标">
          <template #default="{ row }">{{ HealthGoalLabel[row.goalType] }}</template>
        </el-table-column>
        <el-table-column prop="startDate" label="开始" width="120" />
        <el-table-column prop="endDate" label="结束" width="120" />
        <el-table-column prop="targetWeightKg" label="目标体重" width="110">
          <template #default="{ row }">{{ formatKg(row.targetWeightKg) }}</template>
        </el-table-column>
        <el-table-column prop="progressPercent" label="进度" width="110">
          <template #default="{ row }">{{ percent(row.progressPercent) }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }"><el-tag>{{ GoalCycleStatusLabel[row.status] }}</el-tag></template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button :icon="Edit" @click="openEdit(row)">编辑</el-button>
            <el-button v-if="row.status === 'ACTIVE'" :icon="Select" @click="complete(row)">完成</el-button>
            <el-button v-if="row.status === 'ACTIVE'" @click="cancel(row)">取消</el-button>
          </template>
        </el-table-column>
      </el-table>
    </article>

    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑目标周期' : '新建目标周期'" width="520px">
      <el-form class="stack-form dense" :model="form" label-position="top">
        <el-form-item label="目标类型">
          <el-segmented v-model="form.goalType" :options="goalOptions" />
        </el-form-item>
        <div class="form-row">
          <el-form-item label="开始日期">
            <el-date-picker v-model="form.startDate" type="date" value-format="YYYY-MM-DD" />
          </el-form-item>
          <el-form-item label="结束日期">
            <el-date-picker v-model="form.endDate" type="date" value-format="YYYY-MM-DD" />
          </el-form-item>
        </div>
        <div class="form-row">
          <el-form-item label="起始体重 kg">
            <el-input-number v-model="form.startWeightKg" :min="1" :precision="1" />
          </el-form-item>
          <el-form-item label="目标体重 kg">
            <el-input-number v-model="form.targetWeightKg" :min="1" :precision="1" />
          </el-form-item>
        </div>
        <el-form-item label="每日目标热量">
          <el-input-number v-model="form.targetCalorie" :min="800" :max="5000" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submit">保存</el-button>
      </template>
    </el-dialog>
  </section>
</template>
