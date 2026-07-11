<template>
  <AdminLayout>
    <div class="mb-6 flex flex-col gap-2">
      <span class="text-theme-sm font-medium text-error-700 dark:text-error-400">
        Medical Console
      </span>
      <div class="flex flex-col gap-3 lg:flex-row lg:items-end lg:justify-between">
        <div>
          <h1 class="text-title-sm font-bold text-gray-900 text-balance dark:text-white">
            医疗诊区
          </h1>
          <p class="mt-2 max-w-3xl text-theme-sm text-gray-500 text-pretty dark:text-gray-400">
            评估异常体征、给出诊断建议并跟踪处置结果。本工作台聚焦医疗决策，不展示护理任务和告警流转。
          </p>
        </div>
        <div
          class="inline-flex w-fit items-center gap-2 rounded-xl border border-gray-200 bg-white px-4 py-3 text-theme-sm text-gray-600 shadow-theme-xs dark:border-gray-800 dark:bg-white/[0.03] dark:text-gray-300"
        >
          <span class="size-2 rounded-full bg-error-500"></span>
          当前职责：{{ roleOption.name }}
        </div>
      </div>
    </div>

    <DashboardError :message="dashboardError" />

    <div class="grid grid-cols-1 gap-4 md:grid-cols-2 xl:grid-cols-4 md:gap-6">
      <MedicalMetric
        label="紧急异常"
        :value="String(criticalCount)"
        unit="例"
        detail="需立即评估"
        trend="最高优先级"
        tone="error"
      />
      <MedicalMetric
        label="待复测"
        :value="String(retestCount)"
        unit="人"
        detail="收缩压/血氧/血糖偏离"
        trend="复测"
        tone="warning"
      />
      <MedicalMetric
        label="在管老人"
        :value="String(totalElders)"
        unit="人"
        detail="医生可见档案"
        trend="稳定"
        tone="brand"
      />
      <MedicalMetric
        label="今日已评估"
        :value="String(evaluatedCount)"
        unit="例"
        detail="已出具处置意见"
        trend="持续"
        tone="success"
      />
    </div>

    <div class="mt-6 grid grid-cols-12 gap-4 md:gap-6">
      <div class="col-span-12 xl:col-span-8">
        <DiagnosisWorkbench />
      </div>
      <div class="col-span-12 space-y-6 xl:col-span-4">
        <EvaluatedAlertsCard />
      </div>
    </div>
  </AdminLayout>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import AdminLayout from '@/components/layout/AdminLayout.vue'
import DashboardError from '@/components/common/DashboardError.vue'
import MedicalMetric from '@/components/dashboard/MedicalMetric.vue'
import DiagnosisWorkbench from '@/components/dashboard/DiagnosisWorkbench.vue'
import EvaluatedAlertsCard from '@/components/dashboard/EvaluatedAlertsCard.vue'
import { getStoredUser } from '@/api/http'
import { getRoleOption } from '@/config/roles'
import { useOperationsStore } from '@/stores/operations'

const roleOption = getRoleOption(getStoredUser()?.roleName)
const operations = useOperationsStore()

onMounted(() => {
  if (operations.healthRecords.length === 0) void operations.fetchHealthRecords()
  if (operations.alerts.length === 0) void operations.fetchAlerts()
})

const dashboardError = computed(() => operations.error)

const criticalCount = computed(
  () => operations.healthRecords.filter((r) => r.status === '异常').length,
)
const retestCount = computed(
  () => operations.healthRecords.filter((r) => r.status === '需复测').length,
)
const totalElders = computed(
  () => new Set(operations.healthRecords.map((r) => r.elderName)).size,
)
const evaluatedCount = computed(
  () => operations.alerts.filter((a) => a.status === '已处理').length,
)
</script>
