<script setup>
import * as echarts from 'echarts'
import { nextTick, onBeforeUnmount, ref, watch } from 'vue'

const props = defineProps({
  trend: { type: Array, default: () => [] },
})

const chartRef = ref(null)
let chart = null

function renderChart() {
  if (!chartRef.value || !props.trend.length) return
  chart = chart || echarts.init(chartRef.value)
  chart.setOption({
    backgroundColor: 'transparent',
    grid: { left: 36, right: 16, top: 20, bottom: 34 },
    tooltip: { trigger: 'axis', backgroundColor: '#252525', borderColor: '#4d4d4d', textStyle: { color: '#fff' } },
    xAxis: {
      type: 'category',
      data: props.trend.map((item) => item.date.slice(5)),
      axisLine: { lineStyle: { color: '#4d4d4d' } },
      axisLabel: { color: '#b3b3b3' },
      axisTick: { show: false },
    },
    yAxis: {
      type: 'value',
      scale: true,
      axisLabel: { color: '#b3b3b3' },
      splitLine: { lineStyle: { color: '#252525' } },
    },
    series: [
      {
        name: '体重',
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 8,
        data: props.trend.map((item) => item.weightKg),
        lineStyle: { color: '#1ed760', width: 3 },
        itemStyle: { color: '#1ed760' },
        areaStyle: { color: 'rgba(30, 215, 96, 0.12)' },
      },
    ],
  })
}

watch(
  () => props.trend,
  async () => {
    await nextTick()
    renderChart()
  },
  { immediate: true, deep: true },
)

window.addEventListener('resize', () => chart?.resize())

onBeforeUnmount(() => {
  chart?.dispose()
})
</script>

<template>
  <div ref="chartRef" class="goal-chart" />
</template>
