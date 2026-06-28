<script setup>
import { Refresh, Search, Warning } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { computed, onMounted, reactive, ref } from 'vue'
import MetricCard from '../../components/common/MetricCard.vue'
import StateBlock from '../../components/common/StateBlock.vue'
import NutritionTrendChart from '../../components/domain/NutritionTrendChart.vue'
import { RiskSeverityLabel } from '../../constants/labels'
import {
  evaluateNutritionRiskRules,
  fetchNutritionGap,
  fetchNutritionRange,
  fetchNutritionRisks,
  fetchTodayNutrition,
} from '../../services/modules/nutrition.service'
import { handleRequestError } from '../../services/request/http'
import { today } from '../../utils/date'
import { formatCalorie, percent } from '../../utils/format'

const loading = ref(false)
const evaluating = ref(false)
const error = ref('')
const summary = ref(null)
const trend = ref([])
const gaps = ref([])
const risks = ref([])
const filters = reactive({
  date: today(),
  days: 14,
})

const caloriePercent = computed(() => {
  if (!summary.value?.targetCalorie) return '-'
  return percent((summary.value.calorie / summary.value.targetCalorie) * 100)
})

function riskClass(severity) {
  return {
    INFO: 'risk-info',
    WARNING: 'risk-warning',
    DANGER: 'risk-danger',
  }[severity] || 'risk-info'
}

async function load() {
  loading.value = true
  error.value = ''
  try {
    const [todayData, rangeData, gapData, riskData] = await Promise.all([
      fetchTodayNutrition({ date: filters.date }),
      fetchNutritionRange({ days: filters.days }),
      fetchNutritionGap({ date: filters.date }),
      fetchNutritionRisks({ date: filters.date }),
    ])
    summary.value = todayData
    trend.value = rangeData || []
    gaps.value = gapData || []
    risks.value = riskData || []
  } catch (err) {
    error.value = handleRequestError(err).message
  } finally {
    loading.value = false
  }
}

async function evaluateRisks() {
  evaluating.value = true
  try {
    risks.value = await evaluateNutritionRiskRules({ date: filters.date })
    ElMessage.success('风险规则已执行')
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
    <StateBlock title="营养分析加载失败" :description="error" @action="load" />
  </section>

  <section v-else class="nutrition-page" v-loading="loading">
    <article class="panel">
      <div class="section-heading">
        <div>
          <span class="eyebrow">NUTRITION</span>
          <h2>营养仪表盘</h2>
        </div>
        <div class="action-group">
          <el-date-picker v-model="filters.date" type="date" value-format="YYYY-MM-DD" />
          <el-input-number v-model="filters.days" :min="7" :max="60" />
          <el-button :icon="Search" @click="load">查询</el-button>
          <el-button type="primary" :icon="Refresh" :loading="evaluating" @click="evaluateRisks">执行风险评估</el-button>
        </div>
      </div>
    </article>

    <StateBlock
      v-if="!summary"
      title="暂无营养数据"
      description="添加膳食记录后，今日汇总和趋势分析会出现在这里。"
      :show-action="false"
    />

    <template v-else>
      <div class="dashboard-grid compact-grid">
        <MetricCard label="今日热量" :value="formatCalorie(summary.calorie)" :suffix="` / ${formatCalorie(summary.targetCalorie)}`" tone="green" />
        <MetricCard label="达成率" :value="caloriePercent" />
        <MetricCard label="蛋白质" :value="`${summary.protein} g`" />
        <MetricCard label="营养评分" :value="percent(summary.score)" tone="green" />
      </div>

      <div class="split-view nutrition-layout">
        <article class="panel wide-panel">
          <div class="section-heading">
            <div>
              <span class="eyebrow">TREND</span>
              <h2>范围趋势</h2>
            </div>
          </div>
          <NutritionTrendChart :trend="trend" />
        </article>

        <aside class="side-stack">
          <article class="panel">
            <div class="section-heading">
              <div>
                <span class="eyebrow">GAP</span>
                <h2>缺口建议</h2>
              </div>
            </div>
            <div class="gap-list">
              <div v-for="gap in gaps" :key="gap.nutrient" class="gap-row">
                <div>
                  <strong>{{ gap.label }}</strong>
                  <p>{{ gap.suggestion }}</p>
                </div>
                <span :class="gap.status.toLowerCase()">{{ gap.gap > 0 ? '+' : '' }}{{ gap.gap }}</span>
              </div>
            </div>
          </article>
        </aside>
      </div>

      <article class="panel">
        <div class="section-heading">
          <div>
            <span class="eyebrow">RISK</span>
            <h2>营养风险</h2>
          </div>
        </div>
        <StateBlock v-if="!risks.length" title="暂无风险" description="当前记录未触发风险规则。" :show-action="false" />
        <div v-else class="risk-grid">
          <article v-for="risk in risks" :key="risk.id" class="risk-card" :class="riskClass(risk.severity)">
            <el-icon><Warning /></el-icon>
            <div>
              <strong>{{ risk.title }}</strong>
              <span>{{ RiskSeverityLabel[risk.severity] }}</span>
              <p>{{ risk.description }}</p>
              <small>{{ risk.suggestion }}</small>
            </div>
          </article>
        </div>
      </article>
    </template>
  </section>
</template>
