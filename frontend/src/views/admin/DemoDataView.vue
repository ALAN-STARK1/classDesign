<script setup>
import { Refresh } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ref } from 'vue'
import MetricCard from '../../components/common/MetricCard.vue'
import { resetDemoData } from '../../services/modules/admin.service'
import { handleRequestError } from '../../services/request/http'

const loading = ref(false)
const result = ref(null)

async function doReset() {
  try {
    await ElMessageBox.confirm(
      '此操作将重置所有演示数据（用户、菜谱、膳食记录），不可恢复。确认继续？',
      '重置演示数据',
      { type: 'warning', confirmButtonText: '确认重置', cancelButtonText: '取消' },
    )
    loading.value = true
    result.value = await resetDemoData()
    ElMessage.success('演示数据已重置')
  } catch (err) {
    if (err !== 'cancel') handleRequestError(err)
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <section class="admin-page">
    <article class="panel">
      <div class="section-heading">
        <div>
          <span class="eyebrow">ADMIN / DEMO</span>
          <h2>演示数据重置</h2>
        </div>
      </div>

      <p class="muted-copy" style="margin-bottom: 20px;">
        重置后将生成新的随机演示数据，覆盖现有的用户、菜谱和膳食记录。此操作不可逆，请在确认后执行。
      </p>

      <el-button type="danger" :icon="Refresh" :loading="loading" @click="doReset" size="large">
        重置演示数据
      </el-button>
    </article>

    <div v-if="result" class="dashboard-grid compact-grid" style="margin-top: 16px;">
      <MetricCard label="用户数" :value="result.usersCreated" tone="green" />
      <MetricCard label="菜谱数" :value="result.recipesCreated" />
      <MetricCard label="膳食记录数" :value="result.recordsCreated" />
      <MetricCard label="时间戳" :value="result.timestamp?.slice(0, 19) || '—'" />
    </div>
  </section>
</template>
