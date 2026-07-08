<template>
  <AdminLayout>
    <div class="mb-6 flex flex-col gap-3 lg:flex-row lg:items-end lg:justify-between">
      <div>
        <span class="text-theme-sm font-medium text-brand-700 dark:text-brand-300">
          Alert Center
        </span>
        <h1 class="mt-2 text-title-sm font-bold text-gray-900 text-balance dark:text-white">
          告警中心
        </h1>
        <p class="mt-2 max-w-3xl text-theme-sm text-gray-500 text-pretty dark:text-gray-400">
          集中处理心率异常、夜间离床、设备离线和健康复测提醒，明确责任人和处理状态。
        </p>
      </div>

      <button
        type="button"
        class="inline-flex w-fit items-center justify-center rounded-lg bg-brand-600 px-4 py-2.5 text-theme-sm font-medium text-white shadow-theme-xs hover:bg-brand-700"
      >
        新增人工告警
      </button>
    </div>

    <section class="grid grid-cols-1 gap-4 md:grid-cols-3 md:gap-6">
      <article
        v-for="stat in stats"
        :key="stat.label"
        class="rounded-2xl border border-gray-200 bg-white p-5 shadow-theme-xs dark:border-gray-800 dark:bg-white/[0.03]"
      >
        <p class="text-theme-sm text-gray-500 dark:text-gray-400">{{ stat.label }}</p>
        <p class="mt-2 text-title-sm font-bold text-gray-900 tabular-nums dark:text-white">
          {{ stat.value }}
        </p>
        <p class="mt-2 text-theme-xs text-gray-500 dark:text-gray-400">{{ stat.note }}</p>
      </article>
    </section>

    <div class="mt-6 grid grid-cols-12 gap-4 md:gap-6">
      <section
        class="col-span-12 rounded-2xl border border-gray-200 bg-white p-5 shadow-theme-xs dark:border-gray-800 dark:bg-white/[0.03] xl:col-span-8"
      >
        <div class="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
          <div>
            <h2 class="text-lg font-semibold text-gray-800 text-balance dark:text-white/90">
              告警列表
            </h2>
            <p class="mt-1 text-theme-sm text-gray-500 dark:text-gray-400">
              当前筛选 {{ filteredAlerts.length }} 条
            </p>
          </div>

          <div class="grid gap-3 sm:grid-cols-[140px_140px]">
            <label class="sr-only" for="alert-level">告警等级</label>
            <select
              id="alert-level"
              v-model="level"
              class="h-11 rounded-lg border border-gray-300 bg-transparent px-3 py-2.5 text-theme-sm text-gray-800 shadow-theme-xs focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-gray-900 dark:text-white/90"
            >
              <option value="全部等级">全部等级</option>
              <option value="紧急">紧急</option>
              <option value="关注">关注</option>
              <option value="普通">普通</option>
            </select>
            <label class="sr-only" for="alert-status">处理状态</label>
            <select
              id="alert-status"
              v-model="status"
              class="h-11 rounded-lg border border-gray-300 bg-transparent px-3 py-2.5 text-theme-sm text-gray-800 shadow-theme-xs focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-gray-900 dark:text-white/90"
            >
              <option value="全部状态">全部状态</option>
              <option value="待处理">待处理</option>
              <option value="处理中">处理中</option>
              <option value="已处理">已处理</option>
            </select>
          </div>
        </div>

        <div class="mt-5 space-y-3">
          <article
            v-for="alert in filteredAlerts"
            :key="alert.id"
            class="rounded-xl border border-gray-100 bg-gray-50 p-4 dark:border-gray-800 dark:bg-gray-900"
          >
            <div class="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
              <div class="min-w-0">
                <div class="flex flex-wrap items-center gap-2">
                  <span class="rounded-full px-2 py-0.5 text-theme-xs font-medium" :class="levelClassMap[alert.level]">
                    {{ alert.level }}
                  </span>
                  <span class="text-theme-xs text-gray-500 dark:text-gray-400">{{ alert.time }}</span>
                </div>
                <h3 class="mt-2 font-semibold text-gray-800 text-theme-sm dark:text-white/90">
                  {{ alert.room }} {{ alert.elderName }} · {{ alert.type }}
                </h3>
                <p class="mt-1 text-theme-sm text-gray-500 dark:text-gray-400">
                  来源：{{ alert.source }} · 责任人：{{ alert.owner }}
                </p>
              </div>
              <span class="w-fit rounded-full px-2 py-0.5 text-theme-xs font-medium" :class="statusClassMap[alert.status]">
                {{ alert.status }}
              </span>
            </div>
          </article>
        </div>
      </section>

      <aside class="col-span-12 space-y-6 xl:col-span-4">
        <section class="rounded-2xl border border-gray-200 bg-white p-5 shadow-theme-xs dark:border-gray-800 dark:bg-white/[0.03]">
          <h2 class="text-lg font-semibold text-gray-800 text-balance dark:text-white/90">
            处置流程
          </h2>
          <div class="mt-5 space-y-4">
            <div v-for="step in workflow" :key="step.title" class="flex gap-3">
              <span class="mt-1 size-2 rounded-full bg-brand-600"></span>
              <div>
                <p class="font-medium text-theme-sm text-gray-800 dark:text-white/90">{{ step.title }}</p>
                <p class="mt-1 text-theme-xs text-gray-500 dark:text-gray-400">{{ step.desc }}</p>
              </div>
            </div>
          </div>
        </section>

        <section class="rounded-2xl border border-error-100 bg-error-25 p-5 dark:border-error-900 dark:bg-error-500/[0.08]">
          <h2 class="text-lg font-semibold text-gray-900 text-balance dark:text-white">
            紧急处理建议
          </h2>
          <p class="mt-3 text-theme-sm text-gray-600 text-pretty dark:text-gray-300">
            紧急告警超过 5 分钟未确认时，应自动升级通知护理管理员和当班医生。
          </p>
        </section>
      </aside>
    </div>
  </AdminLayout>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import AdminLayout from '@/components/layout/AdminLayout.vue'
import { useOperationsStore } from '@/stores/operations'
import type { AlertRecord } from '@/stores/operations'

const operations = useOperationsStore()
const level = ref<AlertRecord['level'] | '全部等级'>('全部等级')
const status = ref<AlertRecord['status'] | '全部状态'>('全部状态')

const stats = computed(() => [
  { label: '今日告警', value: operations.alerts.length, note: '含系统自动告警和人工记录' },
  { label: '待处理', value: operations.unresolvedAlerts, note: '需要当班人员确认' },
  { label: '紧急告警', value: operations.alerts.filter((item) => item.level === '紧急').length, note: '优先通知医生和护理管理员' },
])

const filteredAlerts = computed(() =>
  operations.alerts.filter((alert) => {
    const matchesLevel = level.value === '全部等级' || alert.level === level.value
    const matchesStatus = status.value === '全部状态' || alert.status === status.value

    return matchesLevel && matchesStatus
  }),
)

const workflow = [
  { title: '确认告警', desc: '护理员查看老人位置、设备来源和最近体征。' },
  { title: '现场处理', desc: '根据等级进行复测、巡房或联系医生。' },
  { title: '记录结果', desc: '填写处置说明，并同步到护理记录。' },
  { title: '复盘规则', desc: '高频误报需要调整设备阈值或通知规则。' },
]

const levelClassMap: Record<AlertRecord['level'], string> = {
  紧急: 'bg-error-50 text-error-700 dark:bg-error-500/15 dark:text-error-400',
  关注: 'bg-warning-50 text-warning-700 dark:bg-warning-500/15 dark:text-warning-400',
  普通: 'bg-brand-50 text-brand-700 dark:bg-brand-500/15 dark:text-brand-300',
}

const statusClassMap: Record<AlertRecord['status'], string> = {
  待处理: 'bg-error-50 text-error-700 dark:bg-error-500/15 dark:text-error-400',
  处理中: 'bg-warning-50 text-warning-700 dark:bg-warning-500/15 dark:text-warning-400',
  已处理: 'bg-success-50 text-success-700 dark:bg-success-500/15 dark:text-success-400',
}
</script>
