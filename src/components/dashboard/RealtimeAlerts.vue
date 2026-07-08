<template>
  <section
    class="h-full rounded-2xl border border-gray-200 bg-white p-5 shadow-theme-xs dark:border-gray-800 dark:bg-white/[0.03] sm:p-6"
  >
    <div class="flex items-start justify-between gap-4">
      <div>
        <h2 class="text-lg font-semibold text-gray-800 text-balance dark:text-white/90">
          实时告警
        </h2>
        <p class="mt-1 text-theme-sm text-gray-500 text-pretty dark:text-gray-400">
          优先处理紧急和未确认事件
        </p>
      </div>
      <RouterLink
        to="/alerts"
        class="rounded-lg border border-gray-200 px-3 py-2 text-theme-sm font-medium text-gray-700 shadow-theme-xs hover:bg-gray-50 dark:border-gray-800 dark:text-gray-300 dark:hover:bg-white/[0.03]"
      >
        全部
      </RouterLink>
    </div>

    <div class="mt-5 space-y-3">
      <article
        v-for="alert in dashboard.alerts"
        :key="alert.id"
        class="rounded-xl border border-gray-100 p-4 dark:border-gray-800"
        :class="alert.handled ? 'bg-white dark:bg-transparent' : 'bg-gray-50 dark:bg-gray-900'"
      >
        <div class="flex items-start justify-between gap-3">
          <div class="min-w-0">
            <div class="flex items-center gap-2">
              <span
                class="rounded-full px-2 py-0.5 text-theme-xs font-medium"
                :class="levelClassMap[alert.level]"
              >
                {{ alert.level }}
              </span>
              <span class="text-theme-xs text-gray-500 dark:text-gray-400">{{ alert.time }}</span>
            </div>
            <h3 class="mt-2 truncate text-theme-sm font-semibold text-gray-800 dark:text-white/90">
              {{ alert.room }} {{ alert.elder }}
            </h3>
            <p class="mt-1 text-theme-sm text-gray-500 text-pretty dark:text-gray-400">
              {{ alert.title }}
            </p>
          </div>
          <span
            class="shrink-0 rounded-full px-2 py-0.5 text-theme-xs font-medium"
            :class="
              alert.handled
                ? 'bg-success-50 text-success-700 dark:bg-success-500/15 dark:text-success-400'
                : 'bg-warning-50 text-warning-700 dark:bg-warning-500/15 dark:text-warning-400'
            "
          >
            {{ alert.handled ? '已确认' : '待处理' }}
          </span>
        </div>
      </article>
    </div>
  </section>
</template>

<script setup lang="ts">
import { RouterLink } from 'vue-router'
import { useDashboardStore } from '@/stores/dashboard'
import type { AlertItem } from '@/stores/dashboard'

const dashboard = useDashboardStore()

const levelClassMap: Record<AlertItem['level'], string> = {
  紧急: 'bg-error-50 text-error-700 dark:bg-error-500/15 dark:text-error-400',
  关注: 'bg-warning-50 text-warning-700 dark:bg-warning-500/15 dark:text-warning-400',
  普通: 'bg-brand-50 text-brand-700 dark:bg-brand-500/15 dark:text-brand-300',
}
</script>
