<template>
  <AdminLayout>
    <div class="mb-6 flex flex-col gap-2">
      <span class="text-theme-sm font-medium text-brand-700 dark:text-brand-300">
        Admin Console
      </span>
      <div class="flex flex-col gap-3 lg:flex-row lg:items-end lg:justify-between">
        <div>
          <h1 class="text-title-sm font-bold text-gray-900 text-balance dark:text-white">
            智慧养老后台运营总览
          </h1>
          <p class="mt-2 max-w-3xl text-theme-sm text-gray-500 text-pretty dark:text-gray-400">
            集中查看平台告警、设备在线、账号审核和关键运营指标，帮助管理员快速定位系统运行状态。
          </p>
        </div>
        <div
          class="inline-flex w-fit items-center gap-2 rounded-xl border border-gray-200 bg-white px-4 py-3 text-theme-sm text-gray-600 shadow-theme-xs dark:border-gray-800 dark:bg-white/[0.03] dark:text-gray-300"
        >
          <span class="size-2 rounded-full bg-brand-500"></span>
          当前职责：{{ roleOption.name }}
        </div>
      </div>
    </div>

    <div class="grid grid-cols-12 gap-4 md:gap-6">
      <div class="col-span-12">
        <DashboardError :message="dashboardError" />
        <AdministrativeMetrics :items="adminMetrics" />
      </div>

      <div class="col-span-12 grid grid-cols-12 items-start gap-4 md:gap-6">
        <div class="col-span-12 space-y-6 xl:col-span-7">
          <PendingApprovalCard />
          <AlertTrendChart />
          <DeviceStatusCard />
        </div>

        <div class="col-span-12 space-y-6 xl:col-span-5">
          <RealtimeAlerts />
          <RoleDistributionCard />
        </div>
      </div>
    </div>
  </AdminLayout>
</template>

<script setup lang="ts">
import AdminLayout from '@/components/layout/AdminLayout.vue'
import AdministrativeMetrics from '@/components/dashboard/AdministrativeMetrics.vue'
import AlertTrendChart from '@/components/dashboard/AlertTrendChart.vue'
import DeviceStatusCard from '@/components/dashboard/DeviceStatusCard.vue'
import RealtimeAlerts from '@/components/dashboard/RealtimeAlerts.vue'
import PendingApprovalCard from '@/components/dashboard/PendingApprovalCard.vue'
import RoleDistributionCard from '@/components/dashboard/RoleDistributionCard.vue'
import { getStoredUser } from '@/api/http'
import { getRoleOption } from '@/config/roles'
import { useOperationsStore } from '@/stores/operations'
import { computed, onMounted } from 'vue'
import {
  AlertTriangle,
  Shield,
  Users,
  Wifi,
} from 'lucide-vue-next'
import type { AdminMetric } from '@/components/dashboard/AdministrativeMetrics.vue'

import DashboardError from '@/components/common/DashboardError.vue'

const roleOption = getRoleOption(getStoredUser()?.roleName)
const operations = useOperationsStore()

onMounted(() => {
  if (operations.alerts.length === 0) void operations.fetchAlerts()
  if (operations.devices.length === 0) void operations.fetchDevices()
})

const dashboardError = computed(() => operations.error)

const criticalAlerts = computed(() => operations.alerts.filter((a) => a.level === '紧急').length)
const pendingAlerts = computed(() => operations.alerts.filter((a) => a.status === '待处理').length)
const onlineDevices = computed(() => operations.devices.filter((d) => d.status === '在线').length)
const totalDevices = computed(() => operations.devices.length)

const adminMetrics = computed<AdminMetric[]>(() => [
  {
    key: 'platform',
    icon: Shield,
    label: '平台告警',
    value: String(criticalAlerts.value),
    unit: '条',
    detail: '紧急级别需优先处置',
    trend: criticalAlerts.value > 0 ? '异常' : '正常',
    tone: criticalAlerts.value > 0 ? 'error' : 'success',
  },
  {
    key: 'pending',
    icon: AlertTriangle,
    label: '待确认告警',
    value: String(pendingAlerts.value),
    unit: '条',
    detail: '护理人员未确认',
    trend: pendingAlerts.value > 0 ? '待处理' : '无',
    tone: pendingAlerts.value > 0 ? 'warning' : 'success',
  },
  {
    key: 'devices',
    icon: Wifi,
    label: '在线设备',
    value: `${onlineDevices.value}/${totalDevices.value}`,
    unit: '台',
    detail: '设备接入状态监控',
    trend: '持续',
    tone: 'success',
  },
  {
    key: 'roles',
    icon: Users,
    label: '账号总览',
    value: '—',
    unit: '',
    detail: '系统配置与权限管理',
    trend: '运维',
    tone: 'brand',
  },
])
</script>
