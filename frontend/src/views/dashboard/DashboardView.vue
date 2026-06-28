<script setup>
import { onMounted, ref } from 'vue'
import MetricCard from '../../components/common/MetricCard.vue'
import StateBlock from '../../components/common/StateBlock.vue'
import GoalProgressChart from '../../components/domain/GoalProgressChart.vue'
import { fetchCurrentGoalCycle, fetchGoalCycleProgress, fetchHealthProfile, fetchHealthSummary } from '../../services/modules/health.service'
import { handleRequestError } from '../../services/request/http'
import { formatCalorie, formatKg, percent } from '../../utils/format'

const loading = ref(true)
const error = ref('')
const profile = ref(null)
const summary = ref(null)
const currentCycle = ref(null)
const progress = ref(null)

async function load() {
  loading.value = true
  error.value = ''
  try {
    const [profileData, summaryData, cycleData] = await Promise.all([
      fetchHealthProfile(),
      fetchHealthSummary(),
      fetchCurrentGoalCycle(),
    ])
    profile.value = profileData
    summary.value = summaryData
    currentCycle.value = cycleData
    progress.value = cycleData ? await fetchGoalCycleProgress(cycleData.id) : null
  } catch (err) {
    error.value = handleRequestError(err).message
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<template>
  <section v-if="error" class="content-section">
    <StateBlock title="概览加载失败" :description="error" @action="load" />
  </section>

  <section v-else class="dashboard-grid" v-loading="loading">
    <MetricCard label="BMI" :value="summary?.bmi || '-'" :suffix="summary?.bmiStatus ? ` ${summary.bmiStatus}` : ''" tone="green" />
    <MetricCard label="目标热量" :value="formatCalorie(summary?.dailyCalorieTarget)" />
    <MetricCard label="当前体重" :value="formatKg(profile?.weightKg)" />
    <MetricCard label="目标进度" :value="percent(currentCycle?.progressPercent)" tone="green" />

    <article class="panel wide-panel">
      <div class="section-heading">
        <div>
          <span class="eyebrow">CURRENT CYCLE</span>
          <h2>目标趋势</h2>
        </div>
        <el-tag v-if="progress?.warning" type="warning">偏离预期</el-tag>
      </div>
      <GoalProgressChart v-if="progress?.trend?.length" :trend="progress.trend" />
      <StateBlock v-else title="暂无当前目标周期" description="创建目标周期后会显示体重趋势。" :show-action="false" />
    </article>

    <article class="panel">
      <div class="section-heading">
        <div>
          <span class="eyebrow">PROFILE</span>
          <h2>健康摘要</h2>
        </div>
      </div>
      <dl class="detail-list">
        <div><dt>BMR</dt><dd>{{ summary?.bmr || '-' }}</dd></div>
        <div><dt>TDEE</dt><dd>{{ summary?.tdee || '-' }}</dd></div>
        <div><dt>目标体重</dt><dd>{{ formatKg(profile?.targetWeightKg) }}</dd></div>
        <div><dt>过敏源</dt><dd>{{ profile?.allergens?.join(' / ') || '无' }}</dd></div>
      </dl>
    </article>
  </section>
</template>
