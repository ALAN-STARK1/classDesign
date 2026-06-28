<script setup>
import { Refresh, Search } from '@element-plus/icons-vue'
import { onMounted, reactive, ref } from 'vue'
import StateBlock from '../../components/common/StateBlock.vue'
import { fetchAiCallLogs } from '../../services/modules/admin.service'
import { handleRequestError } from '../../services/request/http'

const loading = ref(false)
const error = ref('')
const logs = ref([])
const pagination = reactive({ page: 1, size: 10, total: 0 })
const filters = reactive({ model: '', status: '' })

const modelOptions = [
  { label: '全部模型', value: '' },
  { label: 'Fable 5', value: 'fable-5' },
  { label: 'Opus 4.8', value: 'opus-4-8' },
  { label: 'Sonnet 4.6', value: 'sonnet-4-6' },
  { label: 'Haiku 4.5', value: 'haiku-4-5' },
]

const statusOptions = [
  { label: '全部状态', value: '' },
  { label: '成功', value: 'SUCCESS' },
  { label: '错误', value: 'ERROR' },
  { label: '超时', value: 'TIMEOUT' },
]

const statusLabels = {
  SUCCESS: '成功',
  ERROR: '错误',
  TIMEOUT: '超时',
}

const endpointLabels = {
  '/ai-recipes/parse': '文本解析',
  '/ai-recipes/parse-image': '图片识别',
  '/meal-plans/generate/day': '计划生成',
  '/nutrition-risk-rules/evaluate': '风险评估',
}

function formatLatency(ms) {
  if (ms >= 1000) return `${(ms / 1000).toFixed(1)}s`
  return `${ms}ms`
}

async function load() {
  loading.value = true
  error.value = ''
  try {
    const result = await fetchAiCallLogs({
      page: pagination.page,
      size: pagination.size,
      model: filters.model,
      status: filters.status,
    })
    logs.value = result?.items || []
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

onMounted(load)
</script>

<template>
  <section v-if="error" class="content-section">
    <StateBlock title="AI 调用日志加载失败" :description="error" @action="load" />
  </section>

  <section v-else class="admin-page" v-loading="loading">
    <article class="panel">
      <div class="section-heading">
        <div>
          <span class="eyebrow">ADMIN / AI</span>
          <h2>AI 调用日志</h2>
        </div>
      </div>

      <div class="filter-bar">
        <el-select v-model="filters.model" placeholder="全部模型">
          <el-option v-for="item in modelOptions" :key="item.value" v-bind="item" />
        </el-select>
        <el-select v-model="filters.status" placeholder="全部状态">
          <el-option v-for="item in statusOptions" :key="item.value" v-bind="item" />
        </el-select>
        <el-button :icon="Search" @click="search">筛选</el-button>
      </div>

      <StateBlock v-if="!logs.length" title="暂无调用日志" :show-action="false" />

      <template v-else>
        <el-table :data="logs" class="dark-table">
          <el-table-column prop="createdAt" label="时间" width="160" />
          <el-table-column prop="model" label="模型" width="110" />
          <el-table-column prop="endpoint" label="接口" min-width="130">
            <template #default="{ row }">{{ endpointLabels[row.endpoint] || row.endpoint }}</template>
          </el-table-column>
          <el-table-column prop="callerName" label="调用者" width="110" />
          <el-table-column prop="latencyMs" label="耗时" width="90">
            <template #default="{ row }">{{ formatLatency(row.latencyMs) }}</template>
          </el-table-column>
          <el-table-column prop="inputTokens" label="输入 Token" width="110" />
          <el-table-column prop="outputTokens" label="输出 Token" width="110" />
          <el-table-column prop="status" label="状态" width="90">
            <template #default="{ row }">
              <el-tag
                :type="row.status === 'SUCCESS' ? 'success' : row.status === 'TIMEOUT' ? 'warning' : 'danger'"
              >
                {{ statusLabels[row.status] || row.status }}
              </el-tag>
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
  </section>
</template>
