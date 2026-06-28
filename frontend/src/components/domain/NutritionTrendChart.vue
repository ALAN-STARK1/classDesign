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
    grid: { left: 42, right: 18, top: 28, bottom: 34 },
    legend: { top: 0, right: 0, textStyle: { color: '#b3b3b3', fontWeight: 700 } },
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
      axisLabel: { color: '#b3b3b3' },
      splitLine: { lineStyle: { color: '#252525' } },
    },
    series: [
      {
        name: '热量',
        type: 'line',
        smooth: true,
        symbolSize: 7,
        data: props.trend.map((item) => item.calorie),
        lineStyle: { color: '#1ed760', width: 3 },
        itemStyle: { color: '#1ed760' },
      },
      {
        name: '蛋白质',
        type: 'line',
        smooth: true,
        symbolSize: 7,
        data: props.trend.map((item) => item.protein),
        lineStyle: { color: '#539df5', width: 2 },
        itemStyle: { color: '#539df5' },
      },
      {
        name: '评分',
        type: 'line',
        smooth: true,
        symbolSize: 7,
        data: props.trend.map((item) => item.score),
        lineStyle: { color: '#ffa42b', width: 2 },
        itemStyle: { color: '#ffa42b' },
      },
    ],
  })
}

function resize() {
  chart?.resize()
}

watch(
  () => props.trend,
  async () => {
    await nextTick()
    renderChart()
  },
  { immediate: true, deep: true },
)

window.addEventListener('resize', resize)

onBeforeUnmount(() => {
  window.removeEventListener('resize', resize)
  chart?.dispose()
})
</script>

<template>
  <div ref="chartRef" class="nutrition-chart" />
</template>
