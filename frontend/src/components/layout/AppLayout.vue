<script setup>
import {
  Aim,
  ChatDotRound,
  Comment,
  DataAnalysis,
  Dish,
  Document,
  Food,
  Goods,
  House,
  Lock,
  MagicStick,
  Notebook,
  Setting,
  SwitchButton,
  Tickets,
  TrendCharts,
  User,
  Warning,
} from '@element-plus/icons-vue'
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()

const navItems = [
  { label: '今日概览', path: '/dashboard', icon: House },
  { label: '健康档案', path: '/health/profile', icon: User },
  { label: '过敏禁忌', path: '/health/tags', icon: Warning },
  { label: '目标周期', path: '/health/goals', icon: Aim },
  { label: '体重记录', path: '/health/weight-records', icon: TrendCharts },
  { label: '食材库', path: '/ingredients', icon: Goods },
  { label: '菜谱广场', path: '/recipes', icon: Dish },
  { label: '膳食计划', path: '/meal-plans', icon: Tickets },
  { label: '膳食记录', path: '/meal-records', icon: Notebook },
  { label: '营养分析', path: '/nutrition', icon: DataAnalysis },
  { label: '营养报告', path: '/nutrition/reports', icon: Document },
  { label: 'AI 菜谱', path: '/ai-recipes', icon: MagicStick },
  { label: 'AI 营养顾问', path: '/ai-advisor', icon: Comment },
  { label: '社区', path: '/community', icon: ChatDotRound },
]

const adminNavItems = [
  { label: '用户管理', path: '/admin/users', icon: User },
  { label: '菜谱审核', path: '/admin/recipes/review', icon: Dish },
  { label: '帖子审核', path: '/admin/posts/review', icon: ChatDotRound },
  { label: 'AI 调用日志', path: '/admin/ai-call-logs', icon: MagicStick },
  { label: '风险结果', path: '/admin/nutrition-risk-results', icon: Warning },
  { label: '报告统计', path: '/admin/nutrition-reports/statistics', icon: DataAnalysis },
  { label: '风险规则', path: '/admin/nutrition-risk-rules', icon: Setting },
]

const pageTitle = computed(() => route.meta.title || '健康控制台')

function isActive(item) {
  return route.path === item.path || (item.path !== '/dashboard' && route.path.startsWith(`${item.path}/`))
}

function logout() {
  auth.logout()
  router.push('/login')
}
</script>

<template>
  <div class="app-shell">
    <aside class="sidebar">
      <div class="brand">
        <span class="brand-mark"><Food /></span>
        <div>
          <strong>INDRAS</strong>
          <small>HEALTH MIX</small>
        </div>
      </div>

      <nav class="nav-list">
        <RouterLink
          v-for="item in navItems"
          :key="item.path"
          :to="item.path"
          class="nav-link"
          :class="{ active: isActive(item) }"
        >
          <el-icon><component :is="item.icon" /></el-icon>
          <span>{{ item.label }}</span>
        </RouterLink>
      </nav>

      <nav v-if="auth.role === 'ADMIN'" class="nav-section">
        <span class="nav-section-label">管理</span>
        <div class="nav-list">
          <RouterLink
            v-for="item in adminNavItems"
            :key="item.path"
            :to="item.path"
            class="nav-link"
            :class="{ active: isActive(item) }"
          >
            <el-icon><component :is="item.icon" /></el-icon>
            <span>{{ item.label }}</span>
          </RouterLink>
        </div>
      </nav>

    </aside>

    <main class="main-panel">
      <header class="topbar">
        <div>
          <span class="eyebrow">PHASE 6</span>
          <h1>{{ pageTitle }}</h1>
        </div>
        <div class="user-pill">
          <span class="avatar">{{ auth.user?.username?.slice(0, 1)?.toUpperCase() || 'U' }}</span>
          <div class="user-meta">
            <strong>{{ auth.user?.username || 'User' }}</strong>
            <small>{{ auth.user?.email || 'mock user' }}</small>
          </div>
          <el-button circle :icon="SwitchButton" @click="logout" />
        </div>
      </header>

      <RouterView />
    </main>

    <nav class="mobile-tabs">
      <RouterLink v-for="item in navItems" :key="item.path" :to="item.path">
        <el-icon><component :is="item.icon" /></el-icon>
      </RouterLink>
      <button type="button" @click="logout">
        <el-icon><Lock /></el-icon>
      </button>
    </nav>
  </div>
</template>
