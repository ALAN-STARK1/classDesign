<script setup>
import { Refresh } from '@element-plus/icons-vue'
import { onMounted, ref, shallowRef } from 'vue'
import MetricCard from '../../components/common/MetricCard.vue'
import StateBlock from '../../components/common/StateBlock.vue'
import { fetchReportStatistics } from '../../services/modules/admin.service'
import { handleRequestError } from '../../services/request/http'
import { percent } from '../../utils/format'

const loading = ref(false)
const error = ref('')
const statistics = ref(null)

async function load() {
  loading.value = true
  error.value = ''
  try {
    statistics.value = await fetchReportStatistics()
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
    <StateBlock title="报告统计加载失败" :description="error" @action="load" />
  </section>

  <section v-else class="admin-page" v-loading="loading">
    <div class="dashboard-grid compact-grid">
      <MetricCard label="报告总数" :value="statistics?.totalReports || 0" tone="green" />
      <MetricCard label="周报数" :value="statistics?.weeklyCount || 0" />
      <MetricCard label="月报数" :value="statistics?.monthlyCount || 0" />
      <MetricCard label="平均评分" :value="statistics ? percent(statistics.averageScore) : '-'" tone="green" />
    </div>

    <div class="split-view">
      <article class="panel">
        <div class="section-heading">
          <div>
            <span class="eyebrow">STATISTICS</span>
            <h2>高频风险 TOP</h2>
          </div>
        </div>
        <StateBlock v-if="!statistics?.topRisks?.length" title="暂无数据" :show-action="false" />
        <template v-else>
          <el-table :data="statistics.topRisks" class="dark-table">
            <el-table-column prop="ruleName" label="风险规则" min-width="200" />
            <el-table-column prop="count" label="触发次数" width="140" />
          </el-table>
        </template>
      </article>

      <aside class="side-stack">
        <article class="panel">
          <div class="section-heading">
            <div>
              <span class="eyebrow">RATE</span>
              <h2>比例指标</h2>
            </div>
          </div>
          <dl class="detail-list">
            <div>
              <dt>用户报告率</dt>
              <dd>{{ statistics ? percent(statistics.userReportRate * 100) : '-' }}</dd>
            </div>
            <div>
              <dt>风险触发率</dt>
              <dd>{{ statistics ? percent(statistics.riskTriggerRate * 100) : '-' }}</dd>
            </div>
          </dl>
        </article>

        <article class="panel">
          <div class="section-heading">
            <div>
              <span class="eyebrow">DAILY TREND</span>
              <h2>每日生成量</h2>
            </div>
          </div>
          <StateBlock v-if="!statistics?.dailyReportTrend?.length" title="暂无数据" :show-action="false" />
          <div v-else class="timeline-list">
            <div v-for="item in statistics.dailyReportTrend" :key="item.date">
              <strong>{{ item.count }} 份报告</strong>
              <p>{{ item.date }}</p>
            </div>
          </div>
        </article>
      </aside>
    </div>
  </section>
</template>
