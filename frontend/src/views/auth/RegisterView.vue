<script setup>
import { Message, User } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { handleRequestError } from '../../services/request/http'
import { useAuthStore } from '../../stores/auth'

const router = useRouter()
const auth = useAuthStore()
const loading = ref(false)

const form = reactive({
  username: '',
  email: '',
  password: '',
})

async function submit() {
  loading.value = true
  try {
    await auth.register(form)
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch (error) {
    handleRequestError(error)
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <main class="auth-page">
    <section class="auth-panel compact">
      <div class="auth-copy">
        <span class="eyebrow">NEW PROFILE</span>
        <h1>创建账户</h1>
        <p>注册接口已接入 Mock，可在错误场景下验证字段提示。</p>
      </div>

      <el-form class="stack-form" :model="form" label-position="top" @keyup.enter="submit">
        <el-form-item label="用户名">
          <el-input v-model="form.username" :prefix-icon="User" autocomplete="username" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" :prefix-icon="Message" autocomplete="email" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" show-password autocomplete="new-password" />
        </el-form-item>
        <el-button type="primary" :loading="loading" @click="submit">注册</el-button>
      </el-form>

      <RouterLink class="auth-link" to="/login">已有账户，去登录</RouterLink>
    </section>
  </main>
</template>
