import AppLayout from '../components/layout/AppLayout.vue'
import AiCallLogsView from '../views/admin/AiCallLogsView.vue'
import PostReviewView from '../views/admin/PostReviewView.vue'
import RecipeReviewView from '../views/admin/RecipeReviewView.vue'
import ReportStatisticsView from '../views/admin/ReportStatisticsView.vue'
import RiskResultsView from '../views/admin/RiskResultsView.vue'
import UserManagementView from '../views/admin/UserManagementView.vue'
import AiRecipesView from '../views/ai-recipes/AiRecipesView.vue'
import CommunityView from '../views/community/CommunityView.vue'
import DashboardView from '../views/dashboard/DashboardView.vue'
import LoginView from '../views/auth/LoginView.vue'
import RegisterView from '../views/auth/RegisterView.vue'
import HealthProfileView from '../views/health/HealthProfileView.vue'
import HealthTagsView from '../views/health/HealthTagsView.vue'
import GoalCyclesView from '../views/health/GoalCyclesView.vue'
import WeightRecordsView from '../views/health/WeightRecordsView.vue'
import AiAdvisorView from '../views/ai-advisor/AiAdvisorView.vue'
import IngredientsView from '../views/recipes/IngredientsView.vue'
import MealPlansView from '../views/meal-plans/MealPlansView.vue'
import MealRecordsView from '../views/meal-records/MealRecordsView.vue'
import NutritionDashboardView from '../views/nutrition/NutritionDashboardView.vue'
import NutritionReportsView from '../views/nutrition/NutritionReportsView.vue'
import NutritionRiskRulesView from '../views/nutrition/NutritionRiskRulesView.vue'
import RecipeDetailView from '../views/recipes/RecipeDetailView.vue'
import RecipeEditorView from '../views/recipes/RecipeEditorView.vue'
import RecipesView from '../views/recipes/RecipesView.vue'

export const routes = [
  {
    path: '/login',
    name: 'login',
    component: LoginView,
    meta: { guestOnly: true },
  },
  {
    path: '/register',
    name: 'register',
    component: RegisterView,
    meta: { guestOnly: true },
  },
  {
    path: '/',
    component: AppLayout,
    meta: { requiresAuth: true },
    children: [
      { path: '', redirect: '/dashboard' },
      { path: 'dashboard', name: 'dashboard', component: DashboardView, meta: { title: '今日概览' } },
      { path: 'health/profile', name: 'health-profile', component: HealthProfileView, meta: { title: '健康档案' } },
      { path: 'health/tags', name: 'health-tags', component: HealthTagsView, meta: { title: '过敏禁忌' } },
      { path: 'health/goals', name: 'health-goals', component: GoalCyclesView, meta: { title: '目标周期' } },
      { path: 'health/weight-records', name: 'weight-records', component: WeightRecordsView, meta: { title: '体重记录' } },
      { path: 'ingredients', name: 'ingredients', component: IngredientsView, meta: { title: '食材库' } },
      { path: 'recipes', name: 'recipes', component: RecipesView, meta: { title: '菜谱广场' } },
      { path: 'recipes/new', name: 'recipe-create', component: RecipeEditorView, meta: { title: '新建菜谱' } },
      { path: 'recipes/:id', name: 'recipe-detail', component: RecipeDetailView, meta: { title: '菜谱详情' } },
      { path: 'recipes/:id/edit', name: 'recipe-edit', component: RecipeEditorView, meta: { title: '编辑菜谱' } },
      { path: 'meal-plans', name: 'meal-plans', component: MealPlansView, meta: { title: '膳食计划' } },
      { path: 'meal-records', name: 'meal-records', component: MealRecordsView, meta: { title: '膳食记录' } },
      { path: 'nutrition', name: 'nutrition', component: NutritionDashboardView, meta: { title: '营养分析' } },
      { path: 'nutrition/reports', name: 'nutrition-reports', component: NutritionReportsView, meta: { title: '营养报告' } },
      { path: 'ai-recipes', name: 'ai-recipes', component: AiRecipesView, meta: { title: 'AI 菜谱' } },
      { path: 'ai-advisor', name: 'ai-advisor', component: AiAdvisorView, meta: { title: 'AI 营养顾问' } },
      { path: 'community', name: 'community', component: CommunityView, meta: { title: '社区' } },

      // Admin routes — only ADMIN role can access
      { path: 'admin/users', name: 'admin-users', component: UserManagementView, meta: { title: '用户管理', roles: ['ADMIN'] } },
      { path: 'admin/recipes/review', name: 'admin-recipes-review', component: RecipeReviewView, meta: { title: '菜谱审核', roles: ['ADMIN'] } },
      { path: 'admin/posts/review', name: 'admin-posts-review', component: PostReviewView, meta: { title: '帖子审核', roles: ['ADMIN'] } },
      { path: 'admin/ai-call-logs', name: 'admin-ai-call-logs', component: AiCallLogsView, meta: { title: 'AI 调用日志', roles: ['ADMIN'] } },
      { path: 'admin/nutrition-risk-results', name: 'admin-risk-results', component: RiskResultsView, meta: { title: '风险结果管理', roles: ['ADMIN'] } },
      { path: 'admin/nutrition-reports/statistics', name: 'admin-report-statistics', component: ReportStatisticsView, meta: { title: '报告统计', roles: ['ADMIN'] } },
      { path: 'admin/nutrition-risk-rules', name: 'admin-risk-rules', component: NutritionRiskRulesView, meta: { title: '风险规则配置', roles: ['ADMIN'] } },
    ],
  },
]
