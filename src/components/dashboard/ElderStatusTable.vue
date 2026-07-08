<template>
  <section
    class="overflow-hidden rounded-2xl border border-gray-200 bg-white px-4 pb-3 pt-4 shadow-theme-xs dark:border-gray-800 dark:bg-white/[0.03] sm:px-6"
  >
    <div class="mb-4 flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
      <div>
        <h2 class="text-lg font-semibold text-gray-800 text-balance dark:text-white/90">
          老人健康状态
        </h2>
        <p class="mt-1 text-theme-sm text-gray-500 text-pretty dark:text-gray-400">
          展示重点关注对象的实时体征和责任护理人员
        </p>
      </div>

      <RouterLink
        to="/elderly"
        class="inline-flex items-center justify-center rounded-lg border border-gray-300 bg-white px-4 py-2.5 text-theme-sm font-medium text-gray-700 shadow-theme-xs hover:bg-gray-50 hover:text-gray-800 dark:border-gray-700 dark:bg-gray-800 dark:text-gray-400 dark:hover:bg-white/[0.03] dark:hover:text-gray-200"
      >
        查看档案
      </RouterLink>
    </div>

    <div class="max-w-full overflow-x-auto custom-scrollbar">
      <table class="min-w-full">
        <thead>
          <tr class="border-t border-gray-100 dark:border-gray-800">
            <th class="py-3 pr-4 text-left">
              <p class="font-medium text-gray-500 text-theme-xs dark:text-gray-400">姓名</p>
            </th>
            <th class="py-3 pr-4 text-left">
              <p class="font-medium text-gray-500 text-theme-xs dark:text-gray-400">房间</p>
            </th>
            <th class="py-3 pr-4 text-left">
              <p class="font-medium text-gray-500 text-theme-xs dark:text-gray-400">心率</p>
            </th>
            <th class="py-3 pr-4 text-left">
              <p class="font-medium text-gray-500 text-theme-xs dark:text-gray-400">血压</p>
            </th>
            <th class="py-3 pr-4 text-left">
              <p class="font-medium text-gray-500 text-theme-xs dark:text-gray-400">体温</p>
            </th>
            <th class="py-3 pr-4 text-left">
              <p class="font-medium text-gray-500 text-theme-xs dark:text-gray-400">护理员</p>
            </th>
            <th class="py-3 text-left">
              <p class="font-medium text-gray-500 text-theme-xs dark:text-gray-400">状态</p>
            </th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="elder in dashboard.elders"
            :key="elder.id"
            class="border-t border-gray-100 hover:bg-gray-50 dark:border-gray-800 dark:hover:bg-white/[0.03]"
          >
            <td class="py-3 pr-4 whitespace-nowrap">
              <div>
                <p class="font-medium text-gray-800 text-theme-sm dark:text-white/90">
                  {{ elder.name }}
                </p>
                <span class="text-gray-500 text-theme-xs dark:text-gray-400">
                  {{ elder.age }} 岁
                </span>
              </div>
            </td>
            <td class="py-3 pr-4 whitespace-nowrap">
              <p class="text-gray-600 text-theme-sm tabular-nums dark:text-gray-300">
                {{ elder.room }}
              </p>
            </td>
            <td class="py-3 pr-4 whitespace-nowrap">
              <p class="text-gray-600 text-theme-sm tabular-nums dark:text-gray-300">
                {{ elder.heartRate }} bpm
              </p>
            </td>
            <td class="py-3 pr-4 whitespace-nowrap">
              <p class="text-gray-600 text-theme-sm tabular-nums dark:text-gray-300">
                {{ elder.bloodPressure }}
              </p>
            </td>
            <td class="py-3 pr-4 whitespace-nowrap">
              <p class="text-gray-600 text-theme-sm tabular-nums dark:text-gray-300">
                {{ elder.temperature }}°C
              </p>
            </td>
            <td class="py-3 pr-4 whitespace-nowrap">
              <p class="text-gray-600 text-theme-sm dark:text-gray-300">{{ elder.caregiver }}</p>
            </td>
            <td class="py-3 whitespace-nowrap">
              <span
                class="rounded-full px-2 py-0.5 text-theme-xs font-medium"
                :class="statusClassMap[elder.status]"
              >
                {{ elder.status }}
              </span>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </section>
</template>

<script setup lang="ts">
import { RouterLink } from 'vue-router'
import { useDashboardStore } from '@/stores/dashboard'
import type { ElderStatus } from '@/stores/dashboard'

const dashboard = useDashboardStore()

const statusClassMap: Record<ElderStatus['status'], string> = {
  正常: 'bg-success-50 text-success-700 dark:bg-success-500/15 dark:text-success-400',
  需关注: 'bg-warning-50 text-warning-700 dark:bg-warning-500/15 dark:text-warning-400',
  异常: 'bg-error-50 text-error-700 dark:bg-error-500/15 dark:text-error-400',
  离线: 'bg-gray-100 text-gray-600 dark:bg-gray-800 dark:text-gray-300',
}
</script>
