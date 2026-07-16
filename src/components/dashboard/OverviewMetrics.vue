<template>
  <div class="grid grid-cols-1 gap-4 sm:grid-cols-2 xl:grid-cols-4 md:gap-6">
    <article
      v-for="metric in items"
      :key="metric.key"
      class="rounded-[18px] border border-gray-200 bg-white p-5 shadow-theme-xs dark:border-gray-800 dark:bg-white/[0.03] md:p-6"
    >
      <div class="flex items-start justify-between gap-4">
        <div
          class="flex size-12 items-center justify-center rounded-xl"
          :class="toneClassMap[metric.tone].iconWrap"
        >
          <component
            :is="metricIconMap[metric.key]"
            class="size-6"
            :class="toneClassMap[metric.tone].icon"
            aria-hidden="true"
          />
        </div>

        <span
          class="rounded-full px-2.5 py-1 text-theme-xs font-medium tabular-nums"
          :class="toneClassMap[metric.tone].badge"
        >
          {{ metric.trend }}
        </span>
      </div>

      <div class="mt-5">
        <p class="text-sm text-gray-500 dark:text-gray-400">{{ metric.label }}</p>
        <div class="mt-2 flex items-end gap-1">
          <strong class="text-title-sm font-bold text-gray-800 tabular-nums dark:text-white/90">
            {{ metric.value }}
          </strong>
          <span class="mb-1 text-sm font-medium text-gray-500 dark:text-gray-400">
            {{ metric.unit }}
          </span>
        </div>
        <p class="mt-2 text-theme-xs text-gray-500 dark:text-gray-400 text-pretty">
          {{ metric.detail }}
        </p>
      </div>
    </article>
  </div>
</template>

<script setup lang="ts">
import { Activity, Bell, ClipboardList, Users } from 'lucide-vue-next'

export type OverviewMetricKey = 'elders' | 'alerts' | 'devices' | 'tasks'
export type OverviewTone = 'brand' | 'success' | 'warning' | 'error' | 'gray'

export interface OverviewMetric {
  key: OverviewMetricKey
  label: string
  value: string
  unit: string
  detail: string
  trend: string
  tone: OverviewTone
}

defineProps<{
  items: OverviewMetric[]
}>()

const metricIconMap: Record<OverviewMetricKey, unknown> = {
  elders: Users,
  alerts: Bell,
  devices: Activity,
  tasks: ClipboardList,
}

const toneClassMap: Record<
  OverviewTone,
  {
    iconWrap: string
    icon: string
    badge: string
  }
> = {
  brand: {
    iconWrap: 'bg-brand-50 dark:bg-brand-500/15',
    icon: 'text-brand-700 dark:text-brand-300',
    badge: 'bg-brand-50 text-brand-700 dark:bg-brand-500/15 dark:text-brand-300',
  },
  success: {
    iconWrap: 'bg-success-50 dark:bg-success-500/15',
    icon: 'text-success-700 dark:text-success-400',
    badge: 'bg-success-50 text-success-700 dark:bg-success-500/15 dark:text-success-400',
  },
  warning: {
    iconWrap: 'bg-warning-50 dark:bg-warning-500/15',
    icon: 'text-warning-700 dark:text-warning-400',
    badge: 'bg-warning-50 text-warning-700 dark:bg-warning-500/15 dark:text-warning-400',
  },
  error: {
    iconWrap: 'bg-error-50 dark:bg-error-500/15',
    icon: 'text-error-700 dark:text-error-400',
    badge: 'bg-error-50 text-error-700 dark:bg-error-500/15 dark:text-error-400',
  },
  gray: {
    iconWrap: 'bg-gray-100 dark:bg-gray-800',
    icon: 'text-gray-600 dark:text-gray-300',
    badge: 'bg-gray-100 text-gray-700 dark:bg-gray-800 dark:text-gray-300',
  },
}
</script>
