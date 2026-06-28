<script setup>
import { Lock, User } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth'
import { handleRequestError } from '../../services/request/http'

const auth = useAuthStore()
const router = useRouter()
const route = useRoute()
const loading = ref(false)

const form = reactive({
  username: 'alice',
  password: 'user123',
})

async function submit() {
  loading.value = true
  try {
    await auth.login(form)
    ElMessage.success('登录成功')
    router.push(route.query.redirect || '/dashboard')
  } catch (error) {
    handleRequestError(error)
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <main class="auth-page">
    <section class="auth-panel">
      <div class="brand auth-brand">
        <span class="brand-mark"><Lock /></span>
        <div>
          <strong>INDRAS</strong>
          <small>HEALTH MIX</small>
        </div>
      </div>
      <div class="auth-copy">
        <span class="eyebrow">WELCOME BACK</span>
        <h1>登录健康控制台</h1>
        <p>用 Mock 账户进入第 1 阶段功能：认证、健康档案和目标周期。</p>
      </div>

      <el-form class="stack-form" :model="form" label-position="top" @keyup.enter="submit">
        <el-form-item label="用户名">
          <el-input v-model="form.username" :prefix-icon="User" autocomplete="username" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" :prefix-icon="Lock" type="password" show-password autocomplete="current-password" />
        </el-form-item>
        <el-button type="primary" :loading="loading" @click="submit">登录</el-button>
      </el-form>

      <RouterLink class="auth-link" to="/register">创建新账户</RouterLink>
    </section>
  </main>
</template>
