<script setup>
import { CircleClose, Refresh, Search } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'
import StateBlock from '../../components/common/StateBlock.vue'
import { UserRole, UserStatus } from '../../constants/enums'
import { toOptions } from '../../constants/labels'
import { disableUser, fetchAdminUsers } from '../../services/modules/admin.service'
import { handleRequestError } from '../../services/request/http'

const loading = ref(false)
const error = ref('')
const users = ref([])
const pagination = reactive({ page: 1, size: 10, total: 0 })
const filters = reactive({ keyword: '' })

const roleLabels = { USER: '普通用户', ADMIN: '管理员' }
const statusLabels = { ENABLED: '启用', DISABLED: '停用' }

async function load() {
  loading.value = true
  error.value = ''
  try {
    const result = await fetchAdminUsers({ page: pagination.page, size: pagination.size, keyword: filters.keyword })
    users.value = result?.items || []
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

async function toggleDisable(row) {
  const action = row.status === 'ENABLED' ? '停用' : '启用'
  try {
    await ElMessageBox.confirm(`确认${action}用户「${row.username}」？`, `${action}用户`, { type: 'warning' })
    const nextStatus = row.status === 'ENABLED' ? 'DISABLED' : 'ENABLED'
    await disableUser(row.id, { status: nextStatus })
    ElMessage.success(`用户已${action}`)
    await load()
  } catch (err) {
    if (err !== 'cancel') handleRequestError(err)
  }
}

onMounted(load)
</script>

<template>
  <section v-if="error" class="content-section">
    <StateBlock title="用户管理加载失败" :description="error" @action="load" />
  </section>

  <section v-else class="admin-page" v-loading="loading">
    <article class="panel">
      <div class="section-heading">
        <div>
          <span class="eyebrow">ADMIN / USERS</span>
          <h2>用户管理</h2>
        </div>
      </div>

      <div class="filter-bar compact-filter">
        <el-input v-model="filters.keyword" :prefix-icon="Search" placeholder="搜索用户名或邮箱" clearable @keyup.enter="search" />
        <el-button :icon="Search" @click="search">搜索</el-button>
      </div>

      <StateBlock v-if="!users.length" title="暂无用户数据" :show-action="false" />

      <template v-else>
        <el-table :data="users" class="dark-table">
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="username" label="用户名" min-width="120" />
          <el-table-column prop="email" label="邮箱" min-width="180" />
          <el-table-column prop="role" label="角色" width="100">
            <template #default="{ row }">{{ roleLabels[row.role] || row.role }}</template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="90">
            <template #default="{ row }">
              <el-tag :type="row.status === 'ENABLED' ? '' : 'danger'">{{ statusLabels[row.status] || row.status }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createdAt" label="注册时间" width="160" />
          <el-table-column prop="lastLoginAt" label="最近登录" width="160" />
          <el-table-column label="操作" width="120" fixed="right">
            <template #default="{ row }">
              <el-button
                :type="row.status === 'ENABLED' ? 'danger' : 'primary'"
                :icon="CircleClose"
                @click="toggleDisable(row)"
              >
                {{ row.status === 'ENABLED' ? '停用' : '启用' }}
              </el-button>
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
