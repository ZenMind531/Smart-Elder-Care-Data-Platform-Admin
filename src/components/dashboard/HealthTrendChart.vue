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

    <div class="mt-5 max-w-full overflow-x-auto custom-scrollbar">
      <div class="min-w-[720px] xl:min-w-full">
        <VueApexCharts type="line" height="310" :options="chartOptions" :series="series" />
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import VueApexCharts from 'vue3-apexcharts'
import { useDashboardStore } from '@/stores/dashboard'

const dashboard = useDashboardStore()
const options = ['本周', '本月']
const selected = ref('本周')

const series = computed(() => [
  {
    name: '平均心率',
    data: dashboard.healthTrend.heartRate,
  },
  {
    name: '平均收缩压',
    data: dashboard.healthTrend.bloodPressure,
  },
])

interface TooltipContext {
  series: number[][]
  dataPointIndex: number
}

const formatChange = (current: number, previous?: number, unit = '') => {
  if (typeof previous !== 'number') {
    return '首日记录'
  }

  const delta = current - previous

  if (delta === 0) {
    return '较前日持平'
  }

  return `较前日 ${delta > 0 ? '+' : ''}${delta}${unit}`
}

const getHealthTrendStatus = (heartRate: number, bloodPressure: number) => {
  if (heartRate >= 78 || bloodPressure >= 130) {
    return '建议关注整体波动'
  }

  return '整体趋势稳定'
}

const chartOptions = computed(() => ({
  chart: {
    fontFamily: 'Outfit, sans-serif',
    toolbar: {
      show: false,
    },
    zoom: {
      enabled: false,
    },
    animations: {
      enabled: false,
    },
  },
  colors: ['#0d9488', '#f79009'],
  stroke: {
    curve: 'smooth',
    width: [3, 3],
  },
  markers: {
    size: 5,
    strokeWidth: 2,
    strokeColors: '#ffffff',
    hover: {
      size: 5,
      sizeOffset: 0,
    },
  },
  dataLabels: {
    enabled: false,
  },
  grid: {
    borderColor: '#E4E7EC',
    strokeDashArray: 4,
  },
  legend: {
    position: 'top',
    horizontalAlign: 'left',
    fontFamily: 'Outfit',
    markers: {
      radius: 99,
    },
  },
  xaxis: {
    categories: dashboard.healthTrend.categories,
    axisBorder: {
      show: false,
    },
    axisTicks: {
      show: false,
    },
    crosshairs: {
      show: false,
    },
    tooltip: {
      enabled: false,
    },
  },
  yaxis: {
    labels: {
      formatter(value: number) {
        return Math.round(value).toString()
      },
    },
  },
  tooltip: {
    enabled: true,
    shared: false,
    intersect: true,
    followCursor: false,
    marker: {
      show: false,
    },
    custom({ series, dataPointIndex }: TooltipContext) {
      const day = dashboard.healthTrend.categories[dataPointIndex] ?? ''
      const heartRate = series[0]?.[dataPointIndex] ?? dashboard.healthTrend.heartRate[dataPointIndex] ?? 0
      const bloodPressure =
        series[1]?.[dataPointIndex] ?? dashboard.healthTrend.bloodPressure[dataPointIndex] ?? 0
      const previousHeartRate = dashboard.healthTrend.heartRate[dataPointIndex - 1]
      const previousBloodPressure = dashboard.healthTrend.bloodPressure[dataPointIndex - 1]
      const status = getHealthTrendStatus(heartRate, bloodPressure)

      return `
        <div class="health-trend-tooltip">
          <div class="health-trend-tooltip__header">
            <span>${day}健康明细</span>
            <strong>${status}</strong>
          </div>
          <div class="health-trend-tooltip__row">
            <span><i class="health-trend-tooltip__dot health-trend-tooltip__dot--heart"></i>平均心率</span>
            <strong>${heartRate} bpm</strong>
          </div>
          <p>${formatChange(heartRate, previousHeartRate, ' bpm')}</p>
          <div class="health-trend-tooltip__row">
            <span><i class="health-trend-tooltip__dot health-trend-tooltip__dot--pressure"></i>平均收缩压</span>
            <strong>${bloodPressure} mmHg</strong>
          </div>
          <p>${formatChange(bloodPressure, previousBloodPressure, ' mmHg')}</p>
        </div>
      `
    },
    y: {
      formatter(value: number) {
        return Math.round(value).toString()
      },
    },
  },
  states: {
    hover: {
      filter: {
        type: 'none',
      },
    },
    active: {
      filter: {
        type: 'none',
      },
    },
  },
}))
</script>
