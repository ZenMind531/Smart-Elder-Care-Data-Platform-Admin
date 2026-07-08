<template>
  <AdminLayout>
    <div class="mb-6 flex flex-col gap-3 lg:flex-row lg:items-end lg:justify-between">
      <div>
        <span class="text-theme-sm font-medium text-brand-700 dark:text-brand-300">
          Health Monitoring
        </span>
        <h1 class="mt-2 text-title-sm font-bold text-gray-900 text-balance dark:text-white">
          健康监测
        </h1>
        <p class="mt-2 max-w-3xl text-theme-sm text-gray-500 text-pretty dark:text-gray-400">
          汇总心率、血压、体温、血氧和睡眠数据，帮助护理人员快速定位异常体征和需要复测的老人。
        </p>
      </div>

      <button
        type="button"
        class="inline-flex w-fit items-center justify-center rounded-lg bg-brand-600 px-4 py-2.5 text-theme-sm font-medium text-white shadow-theme-xs hover:bg-brand-700"
        @click="addHealthRecord"
      >
        新增体征记录
      </button>
    </div>

    <p
      v-if="feedback"
      class="mb-4 w-fit rounded-lg border border-success-200 bg-success-50 px-3 py-2 text-theme-sm text-success-700 dark:border-success-500/30 dark:bg-success-500/10 dark:text-success-300"
    >
      {{ feedback }}
    </p>

    <section class="grid grid-cols-1 gap-4 md:grid-cols-2 xl:grid-cols-4 md:gap-6">
      <article
        v-for="metric in metrics"
        :key="metric.label"
        class="rounded-2xl border border-gray-200 bg-white p-5 shadow-theme-xs dark:border-gray-800 dark:bg-white/[0.03]"
      >
        <p class="text-theme-sm text-gray-500 dark:text-gray-400">{{ metric.label }}</p>
        <div class="mt-2 flex items-end gap-1">
          <strong class="text-title-sm font-bold text-gray-900 tabular-nums dark:text-white">
            {{ metric.value }}
          </strong>
          <span class="mb-1 text-theme-sm text-gray-500 dark:text-gray-400">{{ metric.unit }}</span>
        </div>
        <p class="mt-2 text-theme-xs text-gray-500 text-pretty dark:text-gray-400">
          {{ metric.description }}
        </p>
      </article>
    </section>

    <div class="mt-6 grid grid-cols-12 gap-4 md:gap-6">
      <section
        class="col-span-12 rounded-2xl border border-gray-200 bg-white p-5 shadow-theme-xs dark:border-gray-800 dark:bg-white/[0.03] xl:col-span-8"
      >
        <div class="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
          <div>
            <h2 class="text-lg font-semibold text-gray-800 text-balance dark:text-white/90">
              实时体征列表
            </h2>
            <p class="mt-1 text-theme-sm text-gray-500 dark:text-gray-400">
              当前展示 {{ filteredRecords.length }} 条记录
            </p>
          </div>

          <div class="grid gap-3 sm:grid-cols-[220px_140px]">
            <label class="sr-only" for="health-search">搜索老人或房间</label>
            <input
              id="health-search"
              v-model="search"
              type="search"
              placeholder="搜索姓名 / 房间"
              class="h-11 rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-theme-sm text-gray-800 shadow-theme-xs placeholder:text-gray-400 focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90"
            />
            <label class="sr-only" for="health-status">筛选状态</label>
            <select
              id="health-status"
              v-model="status"
              class="h-11 rounded-lg border border-gray-300 bg-transparent px-3 py-2.5 text-theme-sm text-gray-800 shadow-theme-xs focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-gray-900 dark:text-white/90"
            >
              <option value="全部状态">全部状态</option>
              <option value="正常">正常</option>
              <option value="需复测">需复测</option>
              <option value="异常">异常</option>
            </select>
          </div>
        </div>

        <div class="mt-5 max-w-full overflow-x-auto custom-scrollbar">
          <table class="min-w-full">
            <thead>
              <tr class="border-t border-gray-100 dark:border-gray-800">
                <th class="py-3 pr-4 text-left text-theme-xs font-medium text-gray-500">老人</th>
                <th class="py-3 pr-4 text-left text-theme-xs font-medium text-gray-500">心率</th>
                <th class="py-3 pr-4 text-left text-theme-xs font-medium text-gray-500">血压</th>
                <th class="py-3 pr-4 text-left text-theme-xs font-medium text-gray-500">体温</th>
                <th class="py-3 pr-4 text-left text-theme-xs font-medium text-gray-500">血氧</th>
                <th class="py-3 pr-4 text-left text-theme-xs font-medium text-gray-500">睡眠</th>
                <th class="py-3 pr-4 text-left text-theme-xs font-medium text-gray-500">状态</th>
                <th class="py-3 text-left text-theme-xs font-medium text-gray-500">操作</th>
              </tr>
            </thead>
            <tbody>
              <tr
                v-for="record in filteredRecords"
                :key="record.id"
                class="border-t border-gray-100 hover:bg-gray-50 dark:border-gray-800 dark:hover:bg-white/[0.03]"
              >
                <td class="py-4 pr-4 whitespace-nowrap">
                  <p class="font-medium text-gray-800 text-theme-sm dark:text-white/90">
                    {{ record.room }} · {{ record.elderName }}
                  </p>
                  <p class="text-theme-xs text-gray-500 dark:text-gray-400">{{ record.measuredAt }}</p>
                </td>
                <td class="py-4 pr-4 text-theme-sm text-gray-700 tabular-nums dark:text-gray-300">
                  {{ record.heartRate }} bpm
                </td>
                <td class="py-4 pr-4 text-theme-sm text-gray-700 tabular-nums dark:text-gray-300">
                  {{ record.bloodPressure }}
                </td>
                <td class="py-4 pr-4 text-theme-sm text-gray-700 tabular-nums dark:text-gray-300">
                  {{ record.temperature }}°C
                </td>
                <td class="py-4 pr-4 text-theme-sm text-gray-700 tabular-nums dark:text-gray-300">
                  {{ record.bloodOxygen }}%
                </td>
                <td class="py-4 pr-4 text-theme-sm text-gray-700 tabular-nums dark:text-gray-300">
                  {{ record.sleepHours }}h
                </td>
                <td class="py-4 pr-4 whitespace-nowrap">
                  <span
                    class="rounded-full px-2 py-0.5 text-theme-xs font-medium"
                    :class="statusClassMap[record.status]"
                  >
                    {{ record.status }}
                  </span>
                </td>
                <td class="py-4 whitespace-nowrap">
                  <button
                    v-if="record.status === '正常'"
                    type="button"
                    class="rounded-lg border border-gray-200 bg-white px-3 py-1.5 text-theme-xs font-medium text-gray-700 shadow-theme-xs hover:bg-gray-50 dark:border-gray-700 dark:bg-gray-900 dark:text-gray-300 dark:hover:bg-white/[0.03]"
                    @click="requestRetest(record)"
                  >
                    发起复测
                  </button>
                  <button
                    v-else
                    type="button"
                    class="rounded-lg border border-success-200 bg-success-50 px-3 py-1.5 text-theme-xs font-medium text-success-700 shadow-theme-xs hover:bg-success-100 dark:border-success-500/30 dark:bg-success-500/10 dark:text-success-300"
                    @click="markNormal(record)"
                  >
                    复测完成
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </section>

      <aside class="col-span-12 space-y-6 xl:col-span-4">
        <section
          class="rounded-2xl border border-gray-200 bg-white p-5 shadow-theme-xs dark:border-gray-800 dark:bg-white/[0.03]"
        >
          <h2 class="text-lg font-semibold text-gray-800 text-balance dark:text-white/90">
            复测提醒
          </h2>
          <div class="mt-5 space-y-3">
            <article
              v-for="record in attentionRecords"
              :key="record.id"
              class="rounded-xl border border-gray-100 bg-gray-50 p-4 dark:border-gray-800 dark:bg-gray-900"
            >
              <div class="flex items-start justify-between gap-3">
                <div>
                  <p class="font-semibold text-gray-800 text-theme-sm dark:text-white/90">
                    {{ record.room }} {{ record.elderName }}
                  </p>
                  <p class="mt-1 text-theme-xs text-gray-500 dark:text-gray-400">
                    心率 {{ record.heartRate }} · 血压 {{ record.bloodPressure }}
                  </p>
                </div>
                <span class="rounded-full px-2 py-0.5 text-theme-xs font-medium" :class="statusClassMap[record.status]">
                  {{ record.status }}
                </span>
              </div>
              <button
                type="button"
                class="mt-3 rounded-lg border border-success-200 bg-success-50 px-3 py-1.5 text-theme-xs font-medium text-success-700 shadow-theme-xs hover:bg-success-100 dark:border-success-500/30 dark:bg-success-500/10 dark:text-success-300"
                @click="markNormal(record)"
              >
                复测完成
              </button>
            </article>
          </div>
        </section>

        <section
          class="rounded-2xl border border-brand-100 bg-brand-25 p-5 dark:border-brand-900 dark:bg-brand-500/[0.08]"
        >
          <h2 class="text-lg font-semibold text-gray-900 text-balance dark:text-white">
            今日健康建议
          </h2>
          <ul class="mt-4 space-y-3 text-theme-sm text-gray-600 dark:text-gray-300">
            <li>优先复核 B-118 的心率和血压，并记录处置结果。</li>
            <li>对睡眠低于 5 小时的老人安排午后状态观察。</li>
            <li>离线设备恢复后补录最近一次体征数据。</li>
          </ul>
        </section>
      </aside>
    </div>
  </AdminLayout>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import AdminLayout from '@/components/layout/AdminLayout.vue'
import { useOperationsStore } from '@/stores/operations'
import type { HealthRecord } from '@/stores/operations'

const operations = useOperationsStore()
const search = ref('')
const status = ref<HealthRecord['status'] | '全部状态'>('全部状态')
const feedback = ref('')

const metrics = computed(() => {
  const records = operations.healthRecords
  const avgHeart = Math.round(records.reduce((sum, item) => sum + item.heartRate, 0) / records.length)
  const avgOxygen = Math.round(records.reduce((sum, item) => sum + item.bloodOxygen, 0) / records.length)
  const abnormal = records.filter((item) => item.status !== '正常').length

  return [
    { label: '平均心率', value: avgHeart, unit: 'bpm', description: '全院最近一次体征均值' },
    { label: '平均血氧', value: avgOxygen, unit: '%', description: '低于 95% 自动纳入关注' },
    { label: '异常/复测', value: abnormal, unit: '人', description: '需要护理员跟进复测' },
    { label: '数据覆盖', value: 96, unit: '%', description: '今日已完成体征采集覆盖率' },
  ]
})

const filteredRecords = computed(() => {
  const keyword = search.value.trim().toLowerCase()

  return operations.healthRecords.filter((record) => {
    const matchesKeyword =
      !keyword ||
      record.elderName.toLowerCase().includes(keyword) ||
      record.room.toLowerCase().includes(keyword)
    const matchesStatus = status.value === '全部状态' || record.status === status.value

    return matchesKeyword && matchesStatus
  })
})

const attentionRecords = computed(() =>
  operations.healthRecords.filter((record) => record.status !== '正常'),
)

const statusClassMap: Record<HealthRecord['status'], string> = {
  正常: 'bg-success-50 text-success-700 dark:bg-success-500/15 dark:text-success-400',
  需复测: 'bg-warning-50 text-warning-700 dark:bg-warning-500/15 dark:text-warning-400',
  异常: 'bg-error-50 text-error-700 dark:bg-error-500/15 dark:text-error-400',
}

const addHealthRecord = () => {
  operations.addHealthRecord()
  status.value = '全部状态'
  feedback.value = '已新增一条实时体征记录'
}

const requestRetest = (record: HealthRecord) => {
  operations.requestHealthRetest(record.id)
  feedback.value = `${record.room} ${record.elderName} 已加入复测提醒`
}

const markNormal = (record: HealthRecord) => {
  operations.markHealthNormal(record.id)
  feedback.value = `${record.room} ${record.elderName} 复测完成，状态已恢复正常`
}
</script>
