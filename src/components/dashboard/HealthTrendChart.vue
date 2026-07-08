<template>
  <section
    class="rounded-2xl border border-gray-200 bg-white px-5 pb-5 pt-5 shadow-theme-xs dark:border-gray-800 dark:bg-white/[0.03] sm:px-6 sm:pt-6"
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

      <div class="flex items-center gap-2 rounded-lg bg-gray-100 p-0.5 dark:bg-gray-900">
        <button
          v-for="option in options"
          :key="option"
          type="button"
          class="rounded-md px-3 py-2 text-theme-sm font-medium"
          :class="
            selected === option
              ? 'bg-white text-gray-900 shadow-theme-xs dark:bg-gray-800 dark:text-white'
              : 'text-gray-500 hover:text-gray-900 dark:text-gray-400 dark:hover:text-white'
          "
          @click="selected = option"
        >
          {{ option }}
        </button>
      </div>
    </div>

    <div class="mt-4 flex flex-wrap items-center gap-4 text-theme-xs text-gray-500 dark:text-gray-400">
      <span class="inline-flex items-center gap-2">
        <span class="size-2.5 rounded-full bg-teal-600"></span>
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

          <path :d="pressurePath" fill="none" stroke="#f79009" stroke-linecap="round" stroke-width="3" />
          <path :d="heartPath" fill="none" stroke="#0d9488" stroke-linecap="round" stroke-width="3" />

          <g v-for="point in pressurePoints" :key="`pressure-${point.label}`">
            <circle :cx="point.x" :cy="point.y" r="4.5" fill="#f79009" stroke="#ffffff" stroke-width="2" />
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
            <circle :cx="point.x" :cy="point.y" r="4.5" fill="#0d9488" stroke="#ffffff" stroke-width="2" />
            <text
              :x="point.x"
              :y="point.y - 12"
              text-anchor="middle"
              class="fill-teal-700 text-[11px] font-semibold tabular-nums dark:fill-teal-300"
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
import { computed, ref } from 'vue'
import { useDashboardStore } from '@/stores/dashboard'

interface ChartPoint {
  label: string
  value: number
  x: number
  y: number
}

const dashboard = useDashboardStore()
const options = ['本周', '本月']
const selected = ref('本周')

const chartWidth = 720
const chartHeight = 310
const plotLeft = 48
const plotRight = 696
const plotTop = 44
const plotBottom = 264
const minValue = 70
const maxValue = 140
const yTicks = [70, 80, 90, 100, 110, 120, 130, 140]

const scaleX = (index: number) => {
  const steps = Math.max(dashboard.healthTrend.categories.length - 1, 1)

  return plotLeft + (index / steps) * (plotRight - plotLeft)
}

const scaleY = (value: number) =>
  plotBottom - ((value - minValue) / (maxValue - minValue)) * (plotBottom - plotTop)

const createPoints = (values: number[]) =>
  values.map((value, index) => ({
    label: dashboard.healthTrend.categories[index] ?? '',
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

const heartPoints = computed(() => createPoints(dashboard.healthTrend.heartRate))
const pressurePoints = computed(() => createPoints(dashboard.healthTrend.bloodPressure))
const heartPath = computed(() => smoothPath(heartPoints.value))
const pressurePath = computed(() => smoothPath(pressurePoints.value))
</script>
