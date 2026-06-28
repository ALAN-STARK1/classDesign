<script setup>
defineProps({
  label: { type: String, required: true },
  value: { type: [String, Number], required: true },
  suffix: { type: String, default: '' },
  tone: { type: String, default: 'neutral' },
})
</script>

<template>
  <div class="metric-card" :class="`tone-${tone}`">
    <span>{{ label }}</span>
    <strong>{{ value }}<small>{{ suffix }}</small></strong>
  </div>
</template>
