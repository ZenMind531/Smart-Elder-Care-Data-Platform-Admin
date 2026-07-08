<template>
  <section
    class="h-full rounded-2xl border border-gray-200 bg-white p-5 shadow-theme-xs dark:border-gray-800 dark:bg-white/[0.03] sm:p-6"
  >
    <div>
      <h2 class="text-lg font-semibold text-gray-800 text-balance dark:text-white/90">
        设备状态分布
      </h2>
      <p class="mt-1 text-theme-sm text-gray-500 text-pretty dark:text-gray-400">
        共 352 台设备，6 台需要复联
      </p>
    </div>

    <div class="mt-6 space-y-5">
      <div v-for="device in dashboard.deviceStatuses" :key="device.id">
        <div class="mb-2 flex items-center justify-between gap-4">
          <div>
            <p class="font-medium text-gray-800 text-theme-sm dark:text-white/90">
              {{ device.name }}
            </p>
            <p class="mt-0.5 text-theme-xs text-gray-500 dark:text-gray-400">
              在线 {{ device.online }} / {{ device.total }} 台
            </p>
          </div>
          <span class="text-theme-sm font-semibold text-gray-800 tabular-nums dark:text-white/90">
            {{ getPercent(device.online, device.total) }}%
          </span>
        </div>
        <div class="h-2 overflow-hidden rounded-full bg-gray-100 dark:bg-gray-800">
          <div
            class="h-full rounded-full"
            :class="barClassMap[device.tone]"
            :style="{ width: `${getPercent(device.online, device.total)}%` }"
          ></div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { useDashboardStore } from '@/stores/dashboard'
import type { Tone } from '@/stores/dashboard'

const dashboard = useDashboardStore()

const barClassMap: Record<Tone, string> = {
  brand: 'bg-brand-600',
  success: 'bg-success-600',
  warning: 'bg-warning-500',
  error: 'bg-error-600',
  gray: 'bg-gray-500',
}

const getPercent = (online: number, total: number) => Math.round((online / total) * 100)
</script>
