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
    size: 0,
    strokeWidth: 0,
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
  },
  yaxis: {
    labels: {
      formatter(value: number) {
        return Math.round(value).toString()
      },
    },
  },
  tooltip: {
    shared: true,
    intersect: false,
    followCursor: false,
    marker: {
      show: false,
    },
    onDatasetHover: {
      highlightDataSeries: false,
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
