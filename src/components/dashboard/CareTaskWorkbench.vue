<template>
  <section
    class="rounded-[18px] border border-gray-200 bg-white p-5 shadow-theme-xs dark:border-gray-800 dark:bg-white/[0.03] sm:p-6"
  >
    <div class="flex items-start justify-between gap-4">
      <div>
        <h2 class="text-lg font-semibold text-gray-800 text-balance dark:text-white/90">
          今日护理任务
        </h2>
        <p class="mt-1 text-theme-sm text-gray-500 text-pretty dark:text-gray-400">
          按时间顺序展示护理员当天需要跟进的事项。
        </p>
      </div>
      <RouterLink
        to="/care-records"
        class="rounded-lg border border-gray-200 px-3 py-2 text-theme-sm font-medium text-gray-700 shadow-theme-xs hover:bg-gray-50 dark:border-gray-800 dark:text-gray-300 dark:hover:bg-white/[0.03]"
      >
        全部记录
      </RouterLink>
    </div>

    <div class="mt-5 space-y-3">
      <article
        v-for="task in sortedTasks"
        :key="task.id"
        class="rounded-xl border p-4 dark:border-gray-800"
        :class="
          task.status === '已完成'
            ? 'border-gray-100 bg-white dark:bg-transparent'
            : task.status === '进行中'
              ? 'border-brand-100 bg-brand-50 dark:bg-brand-500/[0.08]'
              : 'border-warning-100 bg-warning-50 dark:bg-warning-500/[0.08]'
        "
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
        <h3 class="mt-2 text-theme-sm font-semibold text-gray-800 text-pretty dark:text-white/90">
          {{ task.room }} · {{ task.title }}
        </h3>
        <p class="mt-1 text-theme-xs text-gray-500 dark:text-gray-400">
          {{ task.area }} · 负责人：{{ task.owner }}
        </p>
      </article>
      <p
        v-if="sortedTasks.length === 0"
        class="rounded-xl border border-dashed border-gray-300 p-6 text-center text-theme-sm text-gray-500 dark:border-gray-700 dark:text-gray-400"
      >
        今日暂无护理任务
      </p>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { RouterLink } from 'vue-router'
import { useOperationsStore } from '@/stores/operations'

interface CareTaskRow {
  id: number
  time: string
  title: string
  room: string
  area: string
  owner: string
  status: '已完成' | '进行中' | '待处理'
}

const operations = useOperationsStore()

const statusClassMap: Record<CareTaskRow['status'], string> = {
  已完成: 'bg-success-50 text-success-700 dark:bg-success-500/15 dark:text-success-400',
  进行中: 'bg-brand-50 text-brand-700 dark:bg-brand-500/15 dark:text-brand-300',
  待处理: 'bg-warning-50 text-warning-700 dark:bg-warning-500/15 dark:text-warning-400',
}

const sortedTasks = computed<CareTaskRow[]>(() => {
  return [...operations.careRecords]
    .sort((a, b) => a.time.localeCompare(b.time))
    .slice(0, 6)
    .map((r) => ({
      id: r.id,
      time: r.time,
      title: r.content,
      room: r.room,
      area: '—',
      owner: r.caregiver,
      status:
        r.status === '已完成' ? '已完成' : r.status === '进行中' ? '进行中' : '待处理',
    }))
})
</script>
