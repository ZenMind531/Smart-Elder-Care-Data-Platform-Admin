<template>
  <AdminLayout>
    <div class="mb-6 flex flex-col gap-2">
      <span class="text-theme-sm font-medium text-brand-700 dark:text-brand-300">
        Smart Elder Care
      </span>
      <div class="flex flex-col gap-3 lg:flex-row lg:items-end lg:justify-between">
        <div>
          <h1 class="text-title-sm font-bold text-gray-900 text-balance dark:text-white">
            智慧养老监护总览
          </h1>
          <p class="mt-2 max-w-3xl text-theme-sm text-gray-500 text-pretty dark:text-gray-400">
            集中查看老人健康状态、实时告警、设备在线情况和今日护理任务，帮助护理人员快速发现需要优先处理的事项。
          </p>
        </div>
        <div
          class="inline-flex w-fit items-center gap-2 rounded-xl border border-gray-200 bg-white px-4 py-3 text-theme-sm text-gray-600 shadow-theme-xs dark:border-gray-800 dark:bg-white/[0.03] dark:text-gray-300"
        >
          <span class="size-2 rounded-full bg-success-500"></span>
          当前值班：A 区护理组
        </div>
      </div>
    </div>

    <div class="grid grid-cols-12 gap-4 md:gap-6">
      <div class="col-span-12">
        <OverviewMetrics />
      </div>

      <div class="col-span-12 space-y-6 xl:col-span-7">
        <CareProgressCard />
        <HealthTrendChart />
      </div>

      <div class="col-span-12 xl:col-span-5">
        <RealtimeAlerts />
      </div>

      <div class="col-span-12 xl:col-span-7">
        <ElderStatusTable />
      </div>

      <div class="col-span-12 space-y-6 xl:col-span-5">
        <AlertTrendChart />
        <DeviceStatusCard />
      </div>

      <div class="col-span-12">
        <CareTaskList />
      </div>
    </div>
  </AdminLayout>
</template>

<script setup lang="ts">
import AdminLayout from '@/components/layout/AdminLayout.vue'
import AlertTrendChart from '@/components/dashboard/AlertTrendChart.vue'
import CareProgressCard from '@/components/dashboard/CareProgressCard.vue'
import CareTaskList from '@/components/dashboard/CareTaskList.vue'
import DeviceStatusCard from '@/components/dashboard/DeviceStatusCard.vue'
import ElderStatusTable from '@/components/dashboard/ElderStatusTable.vue'
import HealthTrendChart from '@/components/dashboard/HealthTrendChart.vue'
import OverviewMetrics from '@/components/dashboard/OverviewMetrics.vue'
import RealtimeAlerts from '@/components/dashboard/RealtimeAlerts.vue'
</script>
