<template>
  <section
    class="rounded-2xl border border-gray-200 bg-white p-5 shadow-theme-xs dark:border-gray-800 dark:bg-white/[0.03] sm:p-6"
  >
    <div class="flex items-start justify-between gap-4">
      <div>
        <h2 class="text-lg font-semibold text-gray-800 text-balance dark:text-white/90">
          今日护理任务
        </h2>
        <p class="mt-1 text-theme-sm text-gray-500 text-pretty dark:text-gray-400">
          按时间顺序展示今日需要跟进的护理事项
        </p>
      </div>
      <RouterLink
        to="/care-records"
        class="rounded-lg border border-gray-200 px-3 py-2 text-theme-sm font-medium text-gray-700 shadow-theme-xs hover:bg-gray-50 dark:border-gray-800 dark:text-gray-300 dark:hover:bg-white/[0.03]"
      >
        记录
      </RouterLink>
    </div>

    <div class="mt-5 grid gap-3 md:grid-cols-2">
      <article
        v-for="task in dashboard.careTasks"
        :key="task.id"
        class="rounded-xl border border-gray-100 bg-gray-50 p-4 dark:border-gray-800 dark:bg-gray-900"
      >
        <div class="flex items-center justify-between gap-3">
          <span class="text-theme-sm font-semibold text-gray-800 tabular-nums dark:text-white/90">
            {{ task.time }}
          </span>
          <span
            class="rounded-full px-2 py-0.5 text-theme-xs font-medium"
            :class="statusClassMap[task.status]"
          >
            {{ task.status }}
          </span>
        </div>
        <h3 class="mt-3 text-theme-sm font-semibold text-gray-800 text-pretty dark:text-white/90">
          {{ task.title }}
        </h3>
        <p class="mt-2 text-theme-xs text-gray-500 dark:text-gray-400">
          {{ task.area }} · {{ task.owner }}
        </p>
      </article>
    </div>
  </section>
</template>

<script setup lang="ts">
import { RouterLink } from 'vue-router'
import { useDashboardStore } from '@/stores/dashboard'
import type { CareTask } from '@/stores/dashboard'

const dashboard = useDashboardStore()

const statusClassMap: Record<CareTask['status'], string> = {
  待处理: 'bg-warning-50 text-warning-700 dark:bg-warning-500/15 dark:text-warning-400',
  进行中: 'bg-brand-50 text-brand-700 dark:bg-brand-500/15 dark:text-brand-300',
  已完成: 'bg-success-50 text-success-700 dark:bg-success-500/15 dark:text-success-400',
}
</script>
