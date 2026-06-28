<script setup>
import { ElMessage } from 'element-plus'
import { onMounted, ref } from 'vue'
import StateBlock from '../../components/common/StateBlock.vue'
import { CommonTags } from '../../constants/enums'
import { fetchHealthProfile, updateAllergens, updateRestrictions } from '../../services/modules/health.service'
import { handleRequestError } from '../../services/request/http'

const loading = ref(false)
const saving = ref(false)
const error = ref('')
const allergens = ref([])
const restrictions = ref([])

async function load() {
  loading.value = true
  error.value = ''
  try {
    const profile = await fetchHealthProfile()
    allergens.value = profile?.allergens || []
    restrictions.value = profile?.restrictions || []
  } catch (err) {
    error.value = handleRequestError(err).message
  } finally {
    loading.value = false
  }
}

async function save() {
  saving.value = true
  try {
    const [nextAllergens, nextRestrictions] = await Promise.all([
      updateAllergens(allergens.value),
      updateRestrictions(restrictions.value),
    ])
    allergens.value = nextAllergens
    restrictions.value = nextRestrictions
    ElMessage.success('过敏源和饮食禁忌已保存')
  } catch (err) {
    handleRequestError(err)
  } finally {
    saving.value = false
  }
}

onMounted(load)
</script>

<template>
  <section v-if="error" class="content-section">
    <StateBlock title="偏好加载失败" :description="error" @action="load" />
  </section>

  <section v-else class="split-view" v-loading="loading">
    <article class="panel">
      <div class="section-heading">
        <div>
          <span class="eyebrow">ALLERGENS</span>
          <h2>过敏源</h2>
        </div>
      </div>
      <el-select v-model="allergens" multiple filterable allow-create default-first-option class="wide-control">
        <el-option v-for="item in CommonTags.allergens" :key="item" :label="item" :value="item" />
      </el-select>
      <div class="tag-cloud">
        <el-tag v-for="item in allergens" :key="item" closable @close="allergens = allergens.filter((tag) => tag !== item)">
          {{ item }}
        </el-tag>
      </div>
    </article>

    <article class="panel">
      <div class="section-heading">
        <div>
          <span class="eyebrow">RESTRICTIONS</span>
          <h2>饮食禁忌</h2>
        </div>
      </div>
      <el-select v-model="restrictions" multiple filterable allow-create default-first-option class="wide-control">
        <el-option v-for="item in CommonTags.restrictions" :key="item" :label="item" :value="item" />
      </el-select>
      <div class="tag-cloud">
        <el-tag v-for="item in restrictions" :key="item" closable @close="restrictions = restrictions.filter((tag) => tag !== item)">
          {{ item }}
        </el-tag>
      </div>
    </article>

    <div class="footer-actions">
      <el-button type="primary" :loading="saving" @click="save">保存偏好</el-button>
    </div>
  </section>
</template>
