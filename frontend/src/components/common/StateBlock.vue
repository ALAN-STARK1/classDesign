<script setup>
import { Refresh } from '@element-plus/icons-vue'

defineProps({
  title: { type: String, default: '暂无数据' },
  description: { type: String, default: '' },
  actionText: { type: String, default: '重试' },
  loading: { type: Boolean, default: false },
  showAction: { type: Boolean, default: true },
})

defineEmits(['action'])
</script>

<template>
  <div class="state-block" v-loading="loading">
    <div class="state-orb">
      <el-icon><Refresh /></el-icon>
    </div>
    <strong>{{ title }}</strong>
    <p v-if="description">{{ description }}</p>
    <el-button v-if="showAction" type="primary" :icon="Refresh" @click="$emit('action')">
      {{ actionText }}
    </el-button>
  </div>
</template>
