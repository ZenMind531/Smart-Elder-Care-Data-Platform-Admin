<template>
  <AdminLayout>
    <div class="mb-6 flex flex-col gap-2">
      <span class="text-theme-sm font-medium text-success-700 dark:text-success-400">
        Care Console
      </span>
      <div class="flex flex-col gap-3 lg:flex-row lg:items-end lg:justify-between">
        <div>
          <h1 class="text-title-sm font-bold text-gray-900 text-balance dark:text-white">
            护理工作区
          </h1>
          <p class="mt-2 max-w-3xl text-theme-sm text-gray-500 text-pretty dark:text-gray-400">
            执行当日巡房、服药和护理任务，跟进老人状态和设备告警。本工作台聚焦执行处置，不展示医疗诊断。
          </p>
        </div>
        <div
          class="inline-flex w-fit items-center gap-2 rounded-xl border border-gray-200 bg-white px-4 py-3 text-theme-sm text-gray-600 shadow-theme-xs dark:border-gray-800 dark:bg-white/[0.03] dark:text-gray-300"
        >
          <span class="size-2 rounded-full bg-success-500"></span>
          当前职责：{{ roleOption.name }}
        </div>
      </div>
    </div>

    <DashboardError :message="dashboardError" />

    <OverviewMetrics :items="metrics" />

    <div class="mt-6 grid grid-cols-12 gap-4 md:gap-6">
      <div class="col-span-12 space-y-6 xl:col-span-7">
        <CareTaskWorkbench />
        <ElderStatusTable />
        <CareProgressCard />
      </div>
      <div class="col-span-12 space-y-6 xl:col-span-5">
        <RealtimeAlerts />
        <DeviceStatusCard />
      </div>
    </div>
  </AdminLayout>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import AdminLayout from '@/components/layout/AdminLayout.vue'
import OverviewMetrics, { type OverviewMetric } from '@/components/dashboard/OverviewMetrics.vue'
import CareTaskWorkbench from '@/components/dashboard/CareTaskWorkbench.vue'
import ElderStatusTable from '@/components/dashboard/ElderStatusTable.vue'
import CareProgressCard from '@/components/dashboard/CareProgressCard.vue'
import RealtimeAlerts from '@/components/dashboard/RealtimeAlerts.vue'
import DeviceStatusCard from '@/components/dashboard/DeviceStatusCard.vue'
import { getStoredUser } from '@/api/http'
import { getRoleOption } from '@/config/roles'
import { useOperationsStore } from '@/stores/operations'
import DashboardError from '@/components/common/DashboardError.vue'

const roleOption = getRoleOption(getStoredUser()?.roleName)
const operations = useOperationsStore()

onMounted(() => {
  if (operations.alerts.length === 0) void operations.fetchAlerts()
  if (operations.devices.length === 0) void operations.fetchDevices()
  if (operations.healthRecords.length === 0) void operations.fetchHealthRecords()
})

const dashboardError = computed(() => operations.error)

const eldersCount = computed(() => {
  const set = new Set<string>()
  operations.healthRecords.forEach((r) => set.add(r.elderName))
  operations.careRecords.forEach((r) => set.add(r.elderName))
  return set.size
})

const pendingAlerts = computed(
  () => operations.alerts.filter((a) => a.status === '待处理' || a.status === '处理中').length,
)
const onlineDevices = computed(
  () => operations.devices.filter((d) => d.status === '在线').length,
)
const todayTasks = computed(
  () => operations.careRecords.filter((r) => r.status === '待补录').length,
)

const metrics = computed<OverviewMetric[]>(() => [
  {
    key: 'elders',
    label: '在管老人',
    value: String(eldersCount.value),
    unit: '人',
    detail: '当前可执行护理',
    trend: '稳定',
    tone: 'brand',
  },
  {
    key: 'alerts',
    label: '待处理告警',
    value: String(pendingAlerts.value),
    unit: '条',
    detail: '需要护理人员确认',
    trend: pendingAlerts.value > 0 ? '待处理' : '无',
    tone: pendingAlerts.value > 0 ? 'error' : 'success',
  },
  {
    key: 'devices',
    label: '在线设备',
    value: String(onlineDevices.value),
    unit: '台',
    detail: '实时同步中',
    trend: '持续',
    tone: 'success',
  },
  {
    key: 'tasks',
    label: '待完成护理',
    value: String(todayTasks.value),
    unit: '项',
    detail: '今日护理任务',
    trend: todayTasks.value > 0 ? '跟进中' : '已完成',
    tone: todayTasks.value > 0 ? 'warning' : 'success',
  },
])
</script>
