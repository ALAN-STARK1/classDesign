<script setup>
import { Delete, DocumentAdd, Search, View } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { computed, onMounted, reactive, ref } from 'vue'
import StateBlock from '../../components/common/StateBlock.vue'
import { ReportStatus, ReportType } from '../../constants/enums'
import { ReportStatusLabel, ReportTypeLabel, toOptions } from '../../constants/labels'
import {
  deleteNutritionReport,
  fetchNutritionReportDetail,
  fetchNutritionReports,
  generateMonthlyNutritionReport,
  generateWeeklyNutritionReport,
} from '../../services/modules/nutrition.service'
import { handleRequestError } from '../../services/request/http'
import { today } from '../../utils/date'
import { formatCalorie, percent } from '../../utils/format'

const loading = ref(false)
const generating = ref(false)
const error = ref('')
const reports = ref([])
const detail = ref(null)
const detailVisible = ref(false)
const pagination = reactive({ page: 1, size: 10, total: 0 })
const filters = reactive({ type: '', status: '' })

const typeOptions = computed(() => [{ label: '全部类型', value: '' }, ...toOptions(ReportType, ReportTypeLabel)])
const statusOptions = computed(() => [{ label: '全部状态', value: '' }, ...toOptions(ReportStatus, ReportStatusLabel)])

async function load() {
  loading.value = true
  error.value = ''
  try {
    const result = await fetchNutritionReports({
      page: pagination.page,
      size: pagination.size,
      type: filters.type,
      status: filters.status,
    })
    reports.value = result?.items || []
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

async function generate(type) {
  generating.value = true
  try {
    if (type === 'WEEKLY') await generateWeeklyNutritionReport({ endDate: today() })
    else await generateMonthlyNutritionReport({ endDate: today() })
    ElMessage.success(`${ReportTypeLabel[type]}已生成`)
    await load()
  } catch (err) {
    handleRequestError(err)
  } finally {
    generating.value = false
  }
}

async function openDetail(row) {
  try {
    detail.value = await fetchNutritionReportDetail(row.id)
    detailVisible.value = true
  } catch (err) {
    handleRequestError(err)
  }
}

async function remove(row) {
  try {
    await ElMessageBox.confirm(`确认删除「${row.title}」？`, '删除报告', { type: 'warning' })
    await deleteNutritionReport(row.id)
    ElMessage.success('报告已删除')
    await load()
  } catch (err) {
    if (err !== 'cancel') handleRequestError(err)
  }
}

onMounted(load)
</script>

<template>
  <section v-if="error" class="content-section">
    <StateBlock title="营养报告加载失败" :description="error" @action="load" />
  </section>

  <section v-else class="nutrition-report-page" v-loading="loading">
    <article class="panel">
      <div class="section-heading">
        <div>
          <span class="eyebrow">REPORTS</span>
          <h2>营养报告</h2>
        </div>
        <div class="action-group">
          <el-button :icon="DocumentAdd" :loading="generating" @click="generate('WEEKLY')">生成周报</el-button>
          <el-button type="primary" :icon="DocumentAdd" :loading="generating" @click="generate('MONTHLY')">生成月报</el-button>
        </div>
      </div>

      <div class="filter-bar compact-filter">
        <el-select v-model="filters.type" placeholder="全部类型">
          <el-option v-for="item in typeOptions" :key="item.value" v-bind="item" />
        </el-select>
        <el-select v-model="filters.status" placeholder="全部状态">
          <el-option v-for="item in statusOptions" :key="item.value" v-bind="item" />
        </el-select>
        <el-button :icon="Search" @click="search">筛选</el-button>
      </div>

      <StateBlock v-if="!reports.length" title="暂无报告" description="生成周报或月报后会出现在这里。" :show-action="false" />

      <template v-else>
        <el-table :data="reports" class="dark-table">
          <el-table-column prop="title" label="报告" min-width="180">
            <template #default="{ row }">
              <button class="text-link" type="button" @click="openDetail(row)">{{ row.title }}</button>
            </template>
          </el-table-column>
          <el-table-column prop="type" label="类型" width="100">
            <template #default="{ row }">{{ ReportTypeLabel[row.type] }}</template>
          </el-table-column>
          <el-table-column prop="startDate" label="开始" width="120" />
          <el-table-column prop="endDate" label="结束" width="120" />
          <el-table-column prop="score" label="评分" width="100">
            <template #default="{ row }">{{ percent(row.score) }}</template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }"><el-tag>{{ ReportStatusLabel[row.status] }}</el-tag></template>
          </el-table-column>
          <el-table-column label="操作" width="180" fixed="right">
            <template #default="{ row }">
              <el-button :icon="View" @click="openDetail(row)">详情</el-button>
              <el-button circle :icon="Delete" @click="remove(row)" />
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

    <el-drawer v-model="detailVisible" title="报告详情" size="480px">
      <div v-if="detail" class="side-stack">
        <div class="report-hero">
          <span>{{ ReportTypeLabel[detail.type] }}</span>
          <strong>{{ detail.title }}</strong>
          <small>{{ detail.startDate }} 至 {{ detail.endDate }}</small>
        </div>
        <dl class="detail-list">
          <div><dt>评分</dt><dd>{{ percent(detail.score) }}</dd></div>
          <div><dt>平均热量</dt><dd>{{ formatCalorie(detail.averageCalorie) }}</dd></div>
          <div><dt>平均蛋白质</dt><dd>{{ detail.averageProtein }} g</dd></div>
          <div><dt>风险数量</dt><dd>{{ detail.riskCount }}</dd></div>
        </dl>
        <article class="recommend-list">
          <strong>摘要</strong>
          <p>{{ detail.summary }}</p>
        </article>
        <article class="recommend-list">
          <strong>建议</strong>
          <p v-for="item in detail.recommendations" :key="item">{{ item }}</p>
        </article>
      </div>
    </el-drawer>
  </section>
</template>
