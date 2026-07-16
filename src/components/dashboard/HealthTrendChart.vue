<template>
  <section
    class="rounded-[18px] border border-gray-200 bg-white px-5 pb-5 pt-5 shadow-theme-xs dark:border-gray-800 dark:bg-white/[0.03] sm:px-6 sm:pt-6"
  >
    <div class="flex flex-col gap-4 sm:flex-row sm:items-start sm:justify-between">
      <div>
        <h2 class="text-lg font-semibold text-gray-800 text-balance dark:text-white/90">
          近 7 日健康趋势
        </h2>
        <p class="mt-1 text-theme-sm text-gray-500 text-pretty dark:text-gray-400">
          院区平均心率与收缩压趋势，用于快速发现整体波动
        </p>
      </div>
    </div>

    <div class="mt-4 flex flex-wrap items-center gap-4 text-theme-xs text-gray-500 dark:text-gray-400">
      <span class="inline-flex items-center gap-2">
        <span class="size-2.5 rounded-full bg-brand-500"></span>
        平均心率
      </span>
      <span class="inline-flex items-center gap-2">
        <span class="size-2.5 rounded-full bg-warning-500"></span>
        平均收缩压
      </span>
    </div>

    <div class="mt-3 max-w-full overflow-x-auto custom-scrollbar">
      <div class="min-w-[720px] xl:min-w-full">
        <svg
          class="h-[310px] w-full overflow-visible"
          :viewBox="`0 0 ${chartWidth} ${chartHeight}`"
          role="img"
          aria-label="近 7 日平均心率与平均收缩压趋势图"
        >
          <g>
            <line
              v-for="tick in yTicks"
              :key="tick"
              :x1="plotLeft"
              :x2="plotRight"
              :y1="scaleY(tick)"
              :y2="scaleY(tick)"
              class="stroke-gray-100 dark:stroke-gray-800"
              stroke-dasharray="4 5"
            />
            <text
              v-for="tick in yTicks"
              :key="`label-${tick}`"
              :x="plotLeft - 14"
              :y="scaleY(tick) + 4"
              text-anchor="end"
              class="fill-gray-500 text-[11px] tabular-nums dark:fill-gray-400"
            >
              {{ tick }}
            </text>
          </g>

          <path :d="pressurePath" fill="none" stroke="#0066cc" stroke-linecap="round" stroke-width="3" />
          <path :d="heartPath" fill="none" stroke="#0066cc" stroke-linecap="round" stroke-width="3" />

          <g v-for="point in pressurePoints" :key="`pressure-${point.label}`">
            <circle :cx="point.x" :cy="point.y" r="4.5" fill="#0066cc" stroke="#ffffff" stroke-width="2" />
            <text
              :x="point.x"
              :y="point.y + 21"
              text-anchor="middle"
              class="fill-warning-600 text-[11px] font-semibold tabular-nums dark:fill-warning-400"
            >
              {{ point.value }}
            </text>
          </g>

          <g v-for="point in heartPoints" :key="`heart-${point.label}`">
            <circle :cx="point.x" :cy="point.y" r="4.5" fill="#0066cc" stroke="#ffffff" stroke-width="2" />
            <text
              :x="point.x"
              :y="point.y - 12"
              text-anchor="middle"
              class="fill-brand-700 text-[11px] font-semibold tabular-nums dark:fill-brand-300"
            >
              {{ point.value }}
            </text>
          </g>

          <g>
            <text
              v-for="point in heartPoints"
              :key="`day-${point.label}`"
              :x="point.x"
              :y="chartHeight - 12"
              text-anchor="middle"
              class="fill-gray-500 text-[12px] dark:fill-gray-400"
            >
              {{ point.label }}
            </text>
          </g>
        </svg>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useOperationsStore } from '@/stores/operations'

interface ChartPoint {
  label: string
  value: number
  x: number
  y: number
}

const operations = useOperationsStore()

onMounted(() => {
  if (operations.healthRecords.length === 0) void operations.fetchHealthRecords()
})

const chartWidth = 720
const chartHeight = 310
const plotLeft = 48
const plotRight = 696
const plotTop = 44
const plotBottom = 264
const minValue = 70
const maxValue = 140
const yTicks = [70, 80, 90, 100, 110, 120, 130, 140]

// 简单把体征按 measuredAt 字符串排序后取最近 7 条作为"周趋势"
const trendData = computed(() => {
  const sorted = [...operations.healthRecords]
    .sort((a, b) => (a.measuredAt < b.measuredAt ? -1 : 1))
    .slice(-7)
  return {
    categories: sorted.map((r) => r.measuredAt.replace(/^.*?(\d{1,2}:\d{2}).*$/, '$1') || r.measuredAt),
    heartRate: sorted.map((r) => r.heartRate || 0),
    bloodPressure: sorted.map((r) => r.systolicPressure || 0),
  }
})

const scaleX = (index: number) => {
  const steps = Math.max(trendData.value.categories.length - 1, 1)
  return plotLeft + (index / steps) * (plotRight - plotLeft)
}

const scaleY = (value: number) =>
  plotBottom - ((value - minValue) / (maxValue - minValue)) * (plotBottom - plotTop)

const createPoints = (values: number[]) =>
  values.map((value, index) => ({
    label: trendData.value.categories[index] ?? '',
    value,
    x: scaleX(index),
    y: scaleY(value),
  }))

const smoothPath = (points: ChartPoint[]) => {
  if (points.length === 0) {
    return ''
  }

  if (points.length === 1) {
    return `M ${points[0].x} ${points[0].y}`
  }

  return points.reduce((path, point, index) => {
    if (index === 0) {
      return `M ${point.x} ${point.y}`
    }

    const previous = points[index - 1]
    const controlX = previous.x + (point.x - previous.x) / 2

    return `${path} C ${controlX} ${previous.y}, ${controlX} ${point.y}, ${point.x} ${point.y}`
  }, '')
}

const heartPoints = computed(() => createPoints(trendData.value.heartRate))
const pressurePoints = computed(() => createPoints(trendData.value.bloodPressure))
const heartPath = computed(() => smoothPath(heartPoints.value))
const pressurePath = computed(() => smoothPath(pressurePoints.value))
</script>
