<script setup>
import { ElMessage } from 'element-plus'
import { computed, onMounted, reactive, ref } from 'vue'
import MetricCard from '../../components/common/MetricCard.vue'
import StateBlock from '../../components/common/StateBlock.vue'
import { ActivityLevel, Gender, HealthGoal } from '../../constants/enums'
import { ActivityLevelLabel, GenderLabel, HealthGoalLabel, toOptions } from '../../constants/labels'
import { fetchHealthProfile, fetchHealthSummary, saveHealthProfile } from '../../services/modules/health.service'
import { handleRequestError } from '../../services/request/http'
import { formatCalorie } from '../../utils/format'

const loading = ref(false)
const saving = ref(false)
const error = ref('')
const summary = ref(null)

const form = reactive({
  gender: 'UNKNOWN',
  birthday: '',
  heightCm: 170,
  weightKg: 62,
  targetWeightKg: 56,
  activityLevel: 'LIGHT',
  healthGoal: 'FAT_LOSS',
})

const genderOptions = computed(() => toOptions(Gender, GenderLabel))
const activityOptions = computed(() => toOptions(ActivityLevel, ActivityLevelLabel))
const goalOptions = computed(() => toOptions(HealthGoal, HealthGoalLabel))

function fill(data) {
  Object.assign(form, {
    gender: data?.gender || 'UNKNOWN',
    birthday: data?.birthday || '',
    heightCm: data?.heightCm || 170,
    weightKg: data?.weightKg || 62,
    targetWeightKg: data?.targetWeightKg || 56,
    activityLevel: data?.activityLevel || 'LIGHT',
    healthGoal: data?.healthGoal || 'FAT_LOSS',
  })
}

async function load() {
  loading.value = true
  error.value = ''
  try {
    const [profile, summaryData] = await Promise.all([fetchHealthProfile(), fetchHealthSummary()])
    fill(profile)
    summary.value = summaryData
  } catch (err) {
    error.value = handleRequestError(err).message
  } finally {
    loading.value = false
  }
}

async function submit() {
  saving.value = true
  try {
    const saved = await saveHealthProfile({ ...form })
    fill(saved)
    summary.value = await fetchHealthSummary()
    ElMessage.success('健康档案已保存')
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
    <StateBlock title="健康档案加载失败" :description="error" @action="load" />
  </section>

  <section v-else class="split-view" v-loading="loading">
    <article class="panel">
      <div class="section-heading">
        <div>
          <span class="eyebrow">PROFILE DATA</span>
          <h2>基础档案</h2>
        </div>
      </div>

      <el-form class="stack-form dense" :model="form" label-position="top">
        <el-form-item label="性别">
          <el-select v-model="form.gender">
            <el-option v-for="item in genderOptions" :key="item.value" v-bind="item" />
          </el-select>
        </el-form-item>
        <el-form-item label="生日">
          <el-date-picker v-model="form.birthday" type="date" value-format="YYYY-MM-DD" />
        </el-form-item>
        <div class="form-row">
          <el-form-item label="身高 cm">
            <el-input-number v-model="form.heightCm" :min="1" :max="260" />
          </el-form-item>
          <el-form-item label="体重 kg">
            <el-input-number v-model="form.weightKg" :min="1" :max="300" :precision="1" />
          </el-form-item>
        </div>
        <el-form-item label="目标体重 kg">
          <el-input-number v-model="form.targetWeightKg" :min="1" :max="300" :precision="1" />
        </el-form-item>
        <el-form-item label="活动水平">
          <el-select v-model="form.activityLevel">
            <el-option v-for="item in activityOptions" :key="item.value" v-bind="item" />
          </el-select>
        </el-form-item>
        <el-form-item label="健康目标">
          <el-segmented v-model="form.healthGoal" :options="goalOptions" />
        </el-form-item>
        <el-button type="primary" :loading="saving" @click="submit">保存档案</el-button>
      </el-form>
    </article>

    <aside class="side-stack">
      <MetricCard label="BMI" :value="summary?.bmi || '-'" :suffix="summary?.bmiStatus ? ` ${summary.bmiStatus}` : ''" tone="green" />
      <MetricCard label="基础代谢" :value="summary?.bmr || '-'" />
      <MetricCard label="每日消耗" :value="summary?.tdee || '-'" />
      <MetricCard label="建议热量" :value="formatCalorie(summary?.dailyCalorieTarget)" tone="green" />
    </aside>
  </section>
</template>
