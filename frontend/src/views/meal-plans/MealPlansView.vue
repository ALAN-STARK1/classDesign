<script setup>
import { Check, EditPen, Refresh, Search, ShoppingCart, Switch, Tickets } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { computed, onMounted, reactive, ref } from 'vue'
import MetricCard from '../../components/common/MetricCard.vue'
import StateBlock from '../../components/common/StateBlock.vue'
import { MealFeedbackLevel, MealType } from '../../constants/enums'
import { MealFeedbackLevelLabel, MealItemStatusLabel, MealPlanStatusLabel, MealTypeLabel, toOptions } from '../../constants/labels'
import {
  convertPlanToRecords,
  fetchPlanCompletion,
  fetchPlanFeedback,
  fetchReplacementCandidates,
  fetchReplaceLogs,
  fetchDayMealPlan,
  fetchShoppingList,
  fetchWeekPoster,
  fetchWeekShoppingList,
  generateDayMealPlan,
  generateWeekMealPlan,
  replaceMealPlanItem,
  submitPlanItemFeedback,
} from '../../services/modules/meal.service'
import { handleRequestError } from '../../services/request/http'
import { today } from '../../utils/date'
import { formatCalorie, percent } from '../../utils/format'

const loading = ref(false)
const generating = ref(false)
const converting = ref(false)
const error = ref('')
const plan = ref(null)
const completion = ref(null)
const replaceLogs = ref([])
const feedbackList = ref([])
const candidates = ref([])
const candidateDrawer = ref(false)
const shoppingDrawer = ref(false)
const shoppingLoading = ref(false)
const shoppingList = ref(null)
const weekPosterDrawer = ref(false)
const weekPosterLoading = ref(false)
const weekPoster = ref(null)
const feedbackDialog = ref(false)
const activeItem = ref(null)
const candidateLoading = ref(false)

const query = reactive({
  date: today(),
  targetCalorie: 1650,
})

const feedbackForm = reactive({
  level: 'NORMAL',
  note: '',
})

const feedbackOptions = computed(() => toOptions(MealFeedbackLevel, MealFeedbackLevelLabel))
const itemsByType = computed(() => {
  const grouped = Object.fromEntries(MealType.map((type) => [type, []]))
  for (const item of plan.value?.items || []) grouped[item.mealType]?.push(item)
  return grouped
})

async function load() {
  loading.value = true
  error.value = ''
  try {
    const result = await fetchDayMealPlan({ date: query.date })
    plan.value = result
    await loadPlanSideData()
  } catch (err) {
    error.value = handleRequestError(err).message
  } finally {
    loading.value = false
  }
}

async function loadPlanSideData() {
  if (!plan.value?.id) {
    completion.value = null
    replaceLogs.value = []
    feedbackList.value = []
    return
  }
  const [completionData, logs, feedback] = await Promise.all([
    fetchPlanCompletion(plan.value.id).catch(() => null),
    fetchReplaceLogs(plan.value.id).catch(() => []),
    fetchPlanFeedback(plan.value.id).catch(() => []),
  ])
  completion.value = completionData
  replaceLogs.value = logs || []
  feedbackList.value = feedback || []
}

async function generate() {
  generating.value = true
  try {
    plan.value = await generateDayMealPlan({ date: query.date, targetCalorie: query.targetCalorie })
    await loadPlanSideData()
    ElMessage.success('今日膳食计划已生成')
  } catch (err) {
    handleRequestError(err)
  } finally {
    generating.value = false
  }
}

async function generateWeek() {
  generating.value = true
  try {
    await generateWeekMealPlan({ date: query.date, targetCalorie: query.targetCalorie })
    await load()
    ElMessage.success('一周膳食计划已生成')
  } catch (err) {
    handleRequestError(err)
  } finally {
    generating.value = false
  }
}

async function openCandidates(item) {
  activeItem.value = item
  candidateDrawer.value = true
  candidateLoading.value = true
  try {
    candidates.value = await fetchReplacementCandidates(plan.value.id, item.id)
  } catch (err) {
    handleRequestError(err)
  } finally {
    candidateLoading.value = false
  }
}

async function replaceWith(candidate) {
  try {
    plan.value = await replaceMealPlanItem(plan.value.id, activeItem.value.id, candidate)
    await loadPlanSideData()
    candidateDrawer.value = false
    ElMessage.success('餐项已替换')
  } catch (err) {
    handleRequestError(err)
  }
}

function openFeedback(item) {
  activeItem.value = item
  Object.assign(feedbackForm, {
    level: item.feedbackLevel || 'NORMAL',
    note: item.note || '',
  })
  feedbackDialog.value = true
}

async function submitFeedback() {
  try {
    await submitPlanItemFeedback(plan.value.id, activeItem.value.id, { ...feedbackForm })
    feedbackDialog.value = false
    ElMessage.success('反馈已提交')
    await load()
  } catch (err) {
    handleRequestError(err)
  }
}

async function openShoppingList() {
  if (!plan.value?.id) return
  shoppingDrawer.value = true
  shoppingLoading.value = true
  try {
    shoppingList.value = await fetchShoppingList(plan.value.id)
  } catch (err) {
    handleRequestError(err)
  } finally {
    shoppingLoading.value = false
  }
}

async function openWeekPoster() {
  weekPosterDrawer.value = true
  weekPosterLoading.value = true
  try {
    const [poster, shopping] = await Promise.all([
      fetchWeekPoster({ startDate: query.date }),
      fetchWeekShoppingList({ startDate: query.date }).catch(() => null),
    ])
    weekPoster.value = {
      ...poster,
      shoppingItems: poster?.shoppingItems?.length ? poster.shoppingItems : shopping?.items || [],
    }
  } catch (err) {
    handleRequestError(err)
  } finally {
    weekPosterLoading.value = false
  }
}

function printPoster() {
  window.print()
}

async function convertToRecords() {
  try {
    await ElMessageBox.confirm('确认把当前计划的所有餐项生成膳食记录？', '生成记录', { type: 'warning' })
    converting.value = true
    await convertPlanToRecords(plan.value.id)
    ElMessage.success('已生成膳食记录')
    await load()
  } catch (err) {
    if (err !== 'cancel') handleRequestError(err)
  } finally {
    converting.value = false
  }
}

onMounted(load)
</script>

<template>
  <section v-if="error" class="content-section">
    <StateBlock title="膳食计划加载失败" :description="error" @action="load" />
  </section>

  <section v-else class="meal-plan-page" v-loading="loading">
    <article class="panel">
      <div class="section-heading">
        <div>
          <span class="eyebrow">MEAL PLAN</span>
          <h2>今日膳食计划</h2>
        </div>
        <div class="action-group">
          <el-date-picker v-model="query.date" type="date" value-format="YYYY-MM-DD" />
          <el-input-number v-model="query.targetCalorie" :min="900" :max="4200" />
          <el-button :icon="Search" @click="load">查询</el-button>
          <el-button type="primary" :icon="Refresh" :loading="generating" @click="generate">生成计划</el-button>
          <el-button :icon="Tickets" :loading="generating" @click="generateWeek">生成一周</el-button>
          <el-button :icon="ShoppingCart" @click="openWeekPoster">一周海报</el-button>
        </div>
      </div>
    </article>

    <StateBlock
      v-if="!plan"
      title="当前日期暂无计划"
      description="生成计划后，早餐、午餐、晚餐和加餐会按统一餐次展示。"
      action-text="生成计划"
      :loading="generating"
      @action="generate"
    />

    <template v-else>
      <div class="dashboard-grid compact-grid">
        <MetricCard label="计划状态" :value="MealPlanStatusLabel[plan.status] || plan.status" tone="green" />
        <MetricCard label="目标热量" :value="formatCalorie(plan.targetCalorie)" />
        <MetricCard label="计划热量" :value="formatCalorie(plan.summary?.calorie)" />
        <MetricCard label="完成度" :value="percent(completion?.percent)" tone="green" />
      </div>

      <div class="split-view">
        <article class="meal-board">
          <section v-for="type in MealType" :key="type" class="meal-lane">
            <div class="meal-lane-heading">
              <span>{{ MealTypeLabel[type] }}</span>
              <small>{{ itemsByType[type]?.length || 0 }} 项</small>
            </div>
            <article v-for="item in itemsByType[type]" :key="item.id" class="meal-item-card">
              <div class="meal-item-main">
                <div>
                  <strong>{{ item.recipeName }}</strong>
                  <p>{{ item.description }}</p>
                </div>
                <el-tag>{{ MealItemStatusLabel[item.status] }}</el-tag>
              </div>
              <div class="nutrition-strip">
                <span>{{ formatCalorie(item.calorie) }}</span>
                <span>蛋白 {{ item.protein }}g</span>
                <span>适配 {{ percent(item.suitabilityScore) }}</span>
              </div>
              <div class="card-actions">
                <el-button :icon="Switch" @click="openCandidates(item)">替换</el-button>
                <el-button :icon="EditPen" @click="openFeedback(item)">反馈</el-button>
              </div>
            </article>
          </section>
        </article>

        <aside class="side-stack">
          <article class="panel">
            <div class="section-heading">
              <div>
                <span class="eyebrow">SUMMARY</span>
                <h2>营养汇总</h2>
              </div>
            </div>
            <dl class="detail-list">
              <div><dt>蛋白质</dt><dd>{{ plan.summary?.protein }} g</dd></div>
              <div><dt>脂肪</dt><dd>{{ plan.summary?.fat }} g</dd></div>
              <div><dt>碳水</dt><dd>{{ plan.summary?.carbohydrate }} g</dd></div>
              <div><dt>已完成</dt><dd>{{ completion?.completed || 0 }} / {{ completion?.total || 0 }}</dd></div>
            </dl>
            <el-button class="wide-control" type="primary" :icon="Tickets" :loading="converting" @click="convertToRecords">
              转为记录
            </el-button>
            <el-button class="wide-control" :icon="ShoppingCart" @click="openShoppingList">采购清单</el-button>
          </article>

          <article class="panel">
            <div class="section-heading">
              <div>
                <span class="eyebrow">REPLACE LOG</span>
                <h2>替换历史</h2>
              </div>
            </div>
            <div v-if="replaceLogs.length" class="timeline-list">
              <div v-for="log in replaceLogs" :key="log.id">
                <strong>{{ log.fromRecipeName }} → {{ log.toRecipeName }}</strong>
                <p>{{ log.reason }} · {{ log.createdAt }}</p>
              </div>
            </div>
            <StateBlock v-else title="暂无替换历史" description="替换餐项后会记录在这里。" :show-action="false" />
          </article>

          <article class="panel">
            <div class="section-heading">
              <div>
                <span class="eyebrow">FEEDBACK</span>
                <h2>执行反馈</h2>
              </div>
            </div>
            <div v-if="feedbackList.length" class="timeline-list">
              <div v-for="feedback in feedbackList" :key="feedback.itemId">
                <strong>{{ feedback.recipeName }} · {{ MealFeedbackLevelLabel[feedback.level] }}</strong>
                <p>{{ feedback.note || '无备注' }}</p>
              </div>
            </div>
            <StateBlock v-else title="暂无反馈" description="提交餐项反馈后会显示在这里。" :show-action="false" />
          </article>
        </aside>
      </div>
    </template>

    <el-drawer v-model="candidateDrawer" title="替换候选" size="460px">
      <div class="side-stack" v-loading="candidateLoading">
        <StateBlock v-if="!candidates.length" title="暂无候选" description="当前餐项没有可替换菜谱。" :show-action="false" />
        <article v-for="candidate in candidates" :key="candidate.recipeId" class="candidate-card">
          <div>
            <strong>{{ candidate.recipeName }}</strong>
            <p>{{ candidate.reason }}</p>
          </div>
          <div class="nutrition-strip">
            <span>{{ formatCalorie(candidate.calorie) }}</span>
            <span>蛋白 {{ candidate.protein }}g</span>
            <span>适配 {{ percent(candidate.suitabilityScore) }}</span>
          </div>
          <el-button type="primary" :icon="Check" @click="replaceWith(candidate)">选择</el-button>
        </article>
      </div>
    </el-drawer>

    <el-drawer v-model="shoppingDrawer" title="采购清单" size="460px">
      <div v-loading="shoppingLoading" class="side-stack">
        <StateBlock
          v-if="!shoppingList?.items?.length"
          title="暂无采购项"
          description="当前计划未汇总出可采购食材。"
          :show-action="false"
        />
        <template v-else>
          <p class="muted-copy">计划日期：{{ shoppingList.planDate }}</p>
          <el-table :data="shoppingList.items" class="dark-table">
            <el-table-column prop="name" label="食材" min-width="120" />
            <el-table-column prop="amount" label="用量" width="100">
              <template #default="{ row }">{{ row.amount }} {{ row.unit }}</template>
            </el-table-column>
            <el-table-column prop="category" label="分类" width="100" />
          </el-table>
        </template>
      </div>
    </el-drawer>

    <el-drawer v-model="weekPosterDrawer" title="一周膳食海报" size="760px">
      <div v-loading="weekPosterLoading" class="side-stack">
        <StateBlock
          v-if="!weekPoster?.days?.length"
          title="暂无周计划"
          description="先生成一周计划后，即可查看海报与周采购清单。"
          :show-action="false"
        />
        <template v-else>
          <section class="weekly-poster">
            <div class="poster-cover">
              <span class="eyebrow">WEEKLY MEAL POSTER</span>
              <h2>{{ weekPoster.startDate }} 至 {{ weekPoster.endDate }}</h2>
              <p>{{ weekPoster.summary }}</p>
              <div class="nutrition-strip">
                <span>目标 {{ weekPoster.healthGoal || '均衡饮食' }}</span>
                <span>总热量 {{ formatCalorie(weekPoster.totalCalorie) }}</span>
                <span>日目标 {{ formatCalorie(weekPoster.targetCalorie) }}</span>
              </div>
            </div>
            <div class="week-grid">
              <article v-for="day in weekPoster.days" :key="day.date" class="week-day-card">
                <strong>{{ day.date }}</strong>
                <small>{{ formatCalorie(day.totalCalorie) }}</small>
                <p v-for="meal in day.meals" :key="meal.id">
                  {{ MealTypeLabel[meal.mealType] }}：{{ meal.recipeName }}
                </p>
              </article>
            </div>
            <article class="panel inset-panel">
              <div class="section-heading">
                <div>
                  <span class="eyebrow">SHOPPING</span>
                  <h2>周采购清单</h2>
                </div>
              </div>
              <div class="shopping-chip-list">
                <span v-for="item in weekPoster.shoppingItems" :key="item.name">
                  {{ item.name }} {{ item.amount }}{{ item.unit }}
                </span>
              </div>
            </article>
          </section>
          <el-button type="primary" class="wide-control" :icon="Tickets" @click="printPoster">打印/导出海报</el-button>
        </template>
      </div>
    </el-drawer>

    <el-dialog v-model="feedbackDialog" title="餐项反馈" width="520px">
      <el-form class="stack-form dense" :model="feedbackForm" label-position="top">
        <el-form-item label="反馈">
          <el-segmented v-model="feedbackForm.level" :options="feedbackOptions" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="feedbackForm.note" type="textarea" :rows="4" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="feedbackDialog = false">取消</el-button>
        <el-button type="primary" @click="submitFeedback">提交</el-button>
      </template>
    </el-dialog>
  </section>
</template>
