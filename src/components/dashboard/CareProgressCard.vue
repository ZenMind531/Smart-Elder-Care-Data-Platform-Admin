<template>
  <section
    class="rounded-2xl border border-gray-200 bg-white p-5 shadow-theme-xs dark:border-gray-800 dark:bg-white/[0.03] sm:p-6"
  >
    <div class="flex items-start justify-between gap-4">
      <div>
        <h2 class="text-lg font-semibold text-gray-800 text-balance dark:text-white/90">
          今日护理完成率
        </h2>
        <p class="mt-1 text-theme-sm text-gray-500 text-pretty dark:text-gray-400">
          覆盖巡房、服药、体征记录和康复训练
        </p>
      </div>
      <span
        class="rounded-full bg-brand-50 px-2.5 py-1 text-theme-xs font-medium text-brand-700 tabular-nums dark:bg-brand-500/15 dark:text-brand-300"
      >
        {{ dashboard.careCompletionRate }}%
      </span>
    </div>

    <div class="mt-5 grid gap-5 md:grid-cols-[220px_1fr] md:items-center">
      <div class="mx-auto w-full max-w-[220px]">
        <VueApexCharts type="radialBar" height="220" :options="chartOptions" :series="series" />
      </div>

      <div class="grid grid-cols-3 gap-3">
        <div
          v-for="item in summary"
          :key="item.label"
          class="rounded-xl border border-gray-100 bg-gray-50 p-3 dark:border-gray-800 dark:bg-gray-900"
        >
          <p class="text-theme-xs text-gray-500 dark:text-gray-400">{{ item.label }}</p>
          <p class="mt-1 text-lg font-semibold text-gray-800 tabular-nums dark:text-white/90">
            {{ item.value }}
          </p>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import VueApexCharts from 'vue3-apexcharts'
import { useDashboardStore } from '@/stores/dashboard'

const dashboard = useDashboardStore()

const series = computed(() => [dashboard.careCompletionRate])

const summary = [
  { label: '已完成', value: '62 项' },
  { label: '进行中', value: '8 项' },
  { label: '待处理', value: '19 项' },
]

const chartOptions = {
  chart: {
    fontFamily: 'Outfit, sans-serif',
    sparkline: {
      enabled: true,
    },
    animations: {
      enabled: false,
    },
  },
  colors: ['#0d9488'],
  plotOptions: {
    radialBar: {
      startAngle: -120,
      endAngle: 120,
      hollow: {
        size: '72%',
      },
      track: {
        background: '#E4E7EC',
        strokeWidth: '100%',
        margin: 5,
      },
      dataLabels: {
        name: {
          show: false,
        },
        value: {
          fontSize: '34px',
          fontWeight: '700',
          offsetY: 8,
          color: '#1D2939',
          formatter(value: number) {
            return `${Math.round(value)}%`
          },
        },
      },
    },
  },
  stroke: {
    lineCap: 'round',
  },
}
</script>
