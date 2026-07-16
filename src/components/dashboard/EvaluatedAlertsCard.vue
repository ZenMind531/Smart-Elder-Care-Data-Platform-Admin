<template>
  <section
    class="rounded-[18px] border border-gray-200 bg-white p-5 shadow-theme-xs dark:border-gray-800 dark:bg-white/[0.03] sm:p-6"
  >
    <div class="flex items-start justify-between gap-4">
      <div>
        <h2 class="text-lg font-semibold text-gray-800 text-balance dark:text-white/90">
          今日已评估
        </h2>
        <p class="mt-1 text-theme-sm text-gray-500 text-pretty dark:text-gray-400">
          已完成处置的告警，便于复盘和交接班。
        </p>
      </div>
      <RouterLink
        to="/alerts"
        class="rounded-lg border border-gray-200 px-3 py-2 text-theme-sm font-medium text-gray-700 shadow-theme-xs hover:bg-gray-50 dark:border-gray-800 dark:text-gray-300 dark:hover:bg-white/[0.03]"
      >
        全部告警
      </RouterLink>
    </div>

    <div class="mt-5 space-y-3">
      <article
        v-for="alert in evaluatedAlerts"
        :key="alert.id"
        class="rounded-xl border border-gray-100 bg-gray-50 p-4 dark:border-gray-800 dark:bg-gray-900"
      >
        <div class="flex items-start justify-between gap-3">
          <div class="min-w-0">
            <div class="flex flex-wrap items-center gap-2">
              <span class="rounded-full bg-success-50 px-2 py-0.5 text-theme-xs font-medium text-success-700 dark:bg-success-500/15 dark:text-success-400">
                已评估
              </span>
              <span class="text-theme-xs text-gray-500 dark:text-gray-400">{{ alert.time }}</span>
            </div>
            <h3 class="mt-2 truncate text-theme-sm font-semibold text-gray-800 dark:text-white/90">
              {{ alert.room }} {{ alert.elderName }} · {{ alert.type }}
            </h3>
            <p class="mt-1 line-clamp-2 text-theme-sm text-gray-500 text-pretty dark:text-gray-400">
              {{ alert.handleResult || alert.content || '已完成处置。' }}
            </p>
          </div>
          <span class="shrink-0 text-theme-xs text-gray-500 dark:text-gray-400">
            {{ alert.owner }}
          </span>
        </div>
      </article>
      <p
        v-if="evaluatedAlerts.length === 0"
        class="rounded-xl border border-dashed border-gray-300 p-6 text-center text-theme-sm text-gray-500 dark:border-gray-700 dark:text-gray-400"
      >
        今日暂无已评估的告警
      </p>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { RouterLink } from 'vue-router'
import { useOperationsStore } from '@/stores/operations'

const operations = useOperationsStore()

const evaluatedAlerts = computed(() => {
  return operations.alerts
    .filter((a) => a.status === '已处理')
    .slice(0, 5)
})
</script>
