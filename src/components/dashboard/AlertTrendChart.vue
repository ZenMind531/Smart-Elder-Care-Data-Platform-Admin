<template>
  <section
    class="rounded-2xl border border-gray-200 bg-white px-5 pb-5 pt-5 shadow-theme-xs dark:border-gray-800 dark:bg-white/[0.03] sm:px-6 sm:pt-6"
  >
    <div class="flex items-start justify-between gap-4">
      <div>
        <h2 class="text-lg font-semibold text-gray-800 text-balance dark:text-white/90">
          近 7 日告警趋势
        </h2>
        <p class="mt-1 text-theme-sm text-gray-500 text-pretty dark:text-gray-400">
          今日告警数量偏高，建议优先复核 B 区设备和夜间离床规则
        </p>
      </div>
    </div>

    <div class="mt-5 max-w-full overflow-x-auto custom-scrollbar">
      <div class="min-w-[420px] xl:min-w-full">
        <VueApexCharts type="bar" height="260" :options="chartOptions" :series="series" />
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import VueApexCharts from 'vue3-apexcharts'
import { useDashboardStore } from '@/stores/dashboard'

const dashboard = useDashboardStore()

const series = computed(() => [
  {
    name: '告警数',
    data: dashboard.alertTrend,
  },
])

const chartOptions = computed(() => ({
  chart: {
    fontFamily: 'Outfit, sans-serif',
    toolbar: {
      show: false,
    },
    animations: {
      enabled: false,
    },
  },
  colors: ['#f79009'],
  plotOptions: {
    bar: {
      borderRadius: 6,
      borderRadiusApplication: 'end',
      columnWidth: '45%',
    },
  },
  dataLabels: {
    enabled: false,
  },
  grid: {
    borderColor: '#E4E7EC',
    strokeDashArray: 4,
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
    y: {
      formatter(value: number) {
        return `${value} 条`
      },
    },
  },
}))
</script>
