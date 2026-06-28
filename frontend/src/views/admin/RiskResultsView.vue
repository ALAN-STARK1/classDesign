<script setup>
import { Refresh, Search } from '@element-plus/icons-vue'
import { onMounted, reactive, ref } from 'vue'
import StateBlock from '../../components/common/StateBlock.vue'
import { RiskSeverityLabel } from '../../constants/labels'
import { fetchAdminRiskResults } from '../../services/modules/admin.service'
import { handleRequestError } from '../../services/request/http'

const loading = ref(false)
const error = ref('')
const results = ref([])
const pagination = reactive({ page: 1, size: 10, total: 0 })

function severityClass(severity) {
  return {
    INFO: 'risk-info',
    WARNING: 'risk-warning',
    DANGER: 'risk-danger',
  }[severity] || ''
}

async function load() {
  loading.value = true
  error.value = ''
  try {
    const result = await fetchAdminRiskResults({ page: pagination.page, size: pagination.size })
    results.value = result?.items || []
    pagination.total = result?.total || 0
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
    <StateBlock title="风险结果加载失败" :description="error" @action="load" />
  </section>

  <section v-else class="admin-page" v-loading="loading">
    <article class="panel">
      <div class="section-heading">
        <div>
          <span class="eyebrow">ADMIN / RISK RESULTS</span>
          <h2>营养风险结果</h2>
        </div>
      </div>

      <StateBlock v-if="!results.length" title="暂无风险结果" description="用户风险规则评估结果会汇总到这里。" :show-action="false" />

      <template v-else>
        <el-table :data="results" class="dark-table">
          <el-table-column prop="username" label="用户" width="120" />
          <el-table-column prop="ruleName" label="规则" min-width="140" />
          <el-table-column prop="severity" label="级别" width="90">
            <template #default="{ row }">
              <el-tag :type="row.severity === 'DANGER' ? 'danger' : row.severity === 'WARNING' ? 'warning' : ''">
                {{ RiskSeverityLabel[row.severity] }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="value" label="实际值" width="100" />
          <el-table-column prop="threshold" label="阈值" width="100" />
          <el-table-column prop="date" label="日期" width="120" />
          <el-table-column prop="triggeredAt" label="触发时间" width="160" />
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
      </template>
    </article>
  </section>
</template>
