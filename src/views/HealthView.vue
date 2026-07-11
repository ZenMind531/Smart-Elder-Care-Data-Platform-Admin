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
          汇总体征采集、异常判断、复测队列和健康预警，护理人员可以在这里完成每日健康数据闭环。
        </p>
      </div>

      <button
        v-if="canCreateHealth"
        type="button"
        class="inline-flex w-fit items-center justify-center gap-2 rounded-lg bg-brand-600 px-4 py-2.5 text-theme-sm font-medium text-white shadow-theme-xs hover:bg-brand-700"
        @click="showForm = !showForm"
      >
        <Plus class="size-4" />
        {{ showForm ? '收起录入' : '录入体征' }}
      </button>
    </div>

    <p
      v-if="feedback"
      class="mb-4 w-fit rounded-lg border border-success-200 bg-success-50 px-3 py-2 text-theme-sm text-success-700 dark:border-success-500/30 dark:bg-success-500/10 dark:text-success-300"
    >
      {{ feedback }}
    </p>

    <p
      v-if="operations.loading || operations.error"
      class="mb-4 w-fit rounded-lg border px-3 py-2 text-theme-sm"
      :class="operations.error ? 'border-warning-200 bg-warning-50 text-warning-700 dark:border-warning-500/30 dark:bg-warning-500/10 dark:text-warning-300' : 'border-brand-200 bg-brand-50 text-brand-700 dark:border-brand-500/30 dark:bg-brand-500/10 dark:text-brand-300'"
    >
      {{ operations.loading ? '正在同步健康数据接口...' : `<span>健康数据接口暂不可用：</span>${operations.error}` }}
    </p>

    <HealthVitalsForm
      v-if="showForm && canCreateHealth"
      :record="newRecord"
      :elders="elderOptions"
      :elder-key="selectedElderKey"
      @submit="submitVitals"
      @reset="resetForm"
      @update:elder-key="selectedElderKey = $event"
    />

    <section class="grid grid-cols-1 gap-4 md:grid-cols-2 xl:grid-cols-4 md:gap-6">
      <article
        v-for="metric in metrics"
        :key="metric.label"
        class="rounded-xl border border-gray-200 bg-white p-5 shadow-theme-xs dark:border-gray-800 dark:bg-white/[0.03]"
      >
        <div class="flex items-start justify-between gap-3">
          <div>
            <p class="text-theme-sm text-gray-500 dark:text-gray-400">{{ metric.label }}</p>
            <div class="mt-2 flex items-end gap-1">
              <strong class="text-title-sm font-bold text-gray-900 tabular-nums dark:text-white">
                {{ metric.value }}
              </strong>
              <span class="mb-1 text-theme-sm text-gray-500 dark:text-gray-400">{{ metric.unit }}</span>
            </div>
          </div>
          <span class="rounded-full px-2 py-0.5 text-theme-xs font-medium" :class="metric.tone">
            {{ metric.badge }}
          </span>
        </div>
        <p class="mt-2 text-theme-xs text-gray-500 text-pretty dark:text-gray-400">
          {{ metric.description }}
        </p>
      </article>
    </section>

    <div class="mt-6 grid grid-cols-12 gap-4 md:gap-6">
      <section
        class="col-span-12 rounded-xl border border-gray-200 bg-white p-5 shadow-theme-xs dark:border-gray-800 dark:bg-white/[0.03] xl:col-span-7"
      >
        <div class="flex flex-col gap-3 sm:flex-row sm:items-start sm:justify-between">
          <div>
            <h2 class="text-lg font-semibold text-gray-800 text-balance dark:text-white/90">
              近 7 次体征趋势
            </h2>
            <p class="mt-1 text-theme-sm text-gray-500 dark:text-gray-400">
              收缩压和心率同步观察，异常点会进入复测队列。
            </p>
          </div>
          <div class="flex flex-wrap gap-3 text-theme-xs text-gray-500 dark:text-gray-400">
            <span class="inline-flex items-center gap-1.5"><span class="size-2 rounded-full bg-brand-600"></span>收缩压</span>
            <span class="inline-flex items-center gap-1.5"><span class="size-2 rounded-full bg-warning-500"></span>心率</span>
          </div>
        </div>

        <div class="mt-5 h-[260px] w-full overflow-hidden">
          <svg viewBox="0 0 680 260" role="img" aria-label="体征趋势图" class="size-full">
            <line x1="42" y1="218" x2="650" y2="218" class="stroke-gray-200 dark:stroke-gray-800" />
            <line x1="42" y1="164" x2="650" y2="164" class="stroke-gray-100 dark:stroke-gray-800" />
            <line x1="42" y1="110" x2="650" y2="110" class="stroke-gray-100 dark:stroke-gray-800" />
            <line x1="42" y1="56" x2="650" y2="56" class="stroke-gray-100 dark:stroke-gray-800" />

            <polyline
              fill="none"
              stroke="#465fff"
              stroke-width="3"
              stroke-linecap="round"
              stroke-linejoin="round"
              :points="systolicLine"
            />
            <polyline
              fill="none"
              stroke="#f59e0b"
              stroke-width="3"
              stroke-linecap="round"
              stroke-linejoin="round"
              :points="heartRateLine"
            />

            <g v-for="point in trendPoints" :key="point.key">
              <circle :cx="point.x" :cy="point.systolicY" r="4" fill="#465fff" />
              <text :x="point.x" :y="point.systolicY - 10" text-anchor="middle" class="fill-gray-700 text-[11px] font-medium dark:fill-gray-300">
                {{ point.systolic }}
              </text>
              <circle :cx="point.x" :cy="point.heartRateY" r="4" fill="#f59e0b" />
              <text :x="point.x" :y="point.heartRateY + 18" text-anchor="middle" class="fill-gray-700 text-[11px] font-medium dark:fill-gray-300">
                {{ point.heartRate }}
              </text>
              <text x="0" y="0" :transform="`translate(${point.x}, 246)`" text-anchor="middle" class="fill-gray-500 text-[11px] dark:fill-gray-400">
                {{ point.label }}
              </text>
            </g>
          </svg>
        </div>
      </section>

      <aside class="col-span-12 space-y-6 xl:col-span-5">
        <section class="rounded-xl border border-gray-200 bg-white p-5 shadow-theme-xs dark:border-gray-800 dark:bg-white/[0.03]">
          <div class="flex items-start justify-between gap-3">
            <div>
              <h2 class="text-lg font-semibold text-gray-800 text-balance dark:text-white/90">
                复测队列
              </h2>
              <p class="mt-1 text-theme-sm text-gray-500 dark:text-gray-400">
                {{ attentionRecords.length }} 位老人需要跟进
              </p>
            </div>
            <Activity class="size-5 text-brand-600 dark:text-brand-300" />
          </div>

          <div class="mt-4 max-h-[320px] space-y-2 overflow-y-auto custom-scrollbar">
            <article
              v-for="record in attentionRecords"
              :key="record.id"
              class="rounded-lg border border-gray-100 bg-gray-50 p-3 dark:border-gray-800 dark:bg-gray-900"
            >
              <div class="flex items-center justify-between gap-2">
                <div class="min-w-0">
                  <span class="text-theme-xs font-medium text-gray-800 dark:text-white/90">
                    {{ record.room }} {{ record.elderName }}
                  </span>
                  <span class="ml-1.5 text-theme-xs text-gray-500 dark:text-gray-400">
                    心率{{ record.heartRate }} · 血压{{ record.bloodPressure }}
                  </span>
                </div>
                <div class="flex shrink-0 items-center gap-1.5">
                  <span class="rounded-full px-1.5 py-0.5 text-[11px] font-medium" :class="statusClassMap[record.status]">
                    {{ record.status }}
                  </span>
                  <button
                    v-if="canResolveHealth"
                    type="button"
                    class="rounded-md border border-success-200 bg-success-50 px-2 py-1 text-[11px] font-medium text-success-700 hover:bg-success-100 dark:border-success-500/30 dark:bg-success-500/10 dark:text-success-300"
                    @click="markNormal(record)"
                  >
                    复测完成
                  </button>
                </div>
              </div>
            </article>

            <div
              v-if="attentionRecords.length === 0"
              class="rounded-xl border border-dashed border-gray-300 p-6 text-center dark:border-gray-700"
            >
              <p class="font-medium text-theme-sm text-gray-800 dark:text-white/90">暂无复测对象</p>
              <p class="mt-1 text-theme-sm text-gray-500 dark:text-gray-400">当前体征数据均处于正常范围。</p>
            </div>
          </div>
        </section>
      </aside>

      <section
        class="col-span-12 rounded-xl border border-gray-200 bg-white p-5 shadow-theme-xs dark:border-gray-800 dark:bg-white/[0.03]"
      >
        <div class="flex flex-col gap-4 xl:flex-row xl:items-center xl:justify-between">
          <div>
            <h2 class="text-lg font-semibold text-gray-800 text-balance dark:text-white/90">
              体征记录
            </h2>
            <p class="mt-1 text-theme-sm text-gray-500 dark:text-gray-400">
              当前展示 {{ filteredRecords.length }} 条记录
            </p>
          </div>

          <div class="grid gap-3 sm:grid-cols-[220px_150px_150px] xl:w-[560px]">
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

            <button
              type="button"
              class="inline-flex items-center justify-center gap-2 rounded-lg border border-gray-300 bg-white px-3 py-2.5 text-theme-sm font-medium text-gray-700 shadow-theme-xs hover:bg-gray-50 dark:border-gray-700 dark:bg-gray-800 dark:text-gray-300"
              @click="resetFilters"
            >
              <RotateCcw class="size-4" />
              重置筛选
            </button>
          </div>
        </div>

        <div class="mt-5 max-w-full overflow-x-auto custom-scrollbar">
          <table class="min-w-full">
            <thead>
              <tr class="border-t border-gray-100 dark:border-gray-800">
                <th class="py-3 pr-4 text-left text-theme-xs font-medium text-gray-500">老人</th>
                <th class="py-3 pr-4 text-left text-theme-xs font-medium text-gray-500">心率</th>
                <th class="py-3 pr-4 text-left text-theme-xs font-medium text-gray-500">血压</th>
                <th class="py-3 pr-4 text-left text-theme-xs font-medium text-gray-500">血糖</th>
                <th class="py-3 pr-4 text-left text-theme-xs font-medium text-gray-500">体温</th>
                <th class="py-3 pr-4 text-left text-theme-xs font-medium text-gray-500">血氧</th>
                <th class="py-3 pr-4 text-left text-theme-xs font-medium text-gray-500">状态</th>
                <th class="py-3 pr-4 text-left text-theme-xs font-medium text-gray-500">异常原因</th>
                <th v-if="canResolveHealth" class="py-3 text-left text-theme-xs font-medium text-gray-500">操作</th>
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
                  {{ record.bloodSugar ?? '-' }}
                </td>
                <td class="py-4 pr-4 text-theme-sm text-gray-700 tabular-nums dark:text-gray-300">
                  {{ record.temperature }}°C
                </td>
                <td class="py-4 pr-4 text-theme-sm text-gray-700 tabular-nums dark:text-gray-300">
                  {{ record.bloodOxygen }}%
                </td>
                <td class="py-4 pr-4 whitespace-nowrap">
                  <span
                    class="rounded-full px-2 py-0.5 text-theme-xs font-medium"
                    :class="statusClassMap[record.status]"
                  >
                    {{ record.status }}
                  </span>
                </td>
                <td class="max-w-[260px] py-4 pr-4">
                  <p class="line-clamp-2 text-theme-xs text-gray-500 text-pretty dark:text-gray-400">
                    {{ record.riskReason || '体征处于正常范围' }}
                  </p>
                </td>
                <td v-if="canResolveHealth" class="py-4 whitespace-nowrap">
                  <button
                    v-if="record.status === '正常'"
                    type="button"
                    class="inline-flex items-center gap-2 rounded-lg border border-gray-200 bg-white px-3 py-1.5 text-theme-xs font-medium text-gray-700 shadow-theme-xs hover:bg-gray-50 dark:border-gray-700 dark:bg-gray-900 dark:text-gray-300 dark:hover:bg-white/[0.03]"
                    @click="requestRetest(record)"
                  >
                    <Activity class="size-4" />
                    发起复测
                  </button>
                  <button
                    v-else
                    type="button"
                    class="inline-flex items-center gap-2 rounded-lg border border-success-200 bg-success-50 px-3 py-1.5 text-theme-xs font-medium text-success-700 shadow-theme-xs hover:bg-success-100 dark:border-success-500/30 dark:bg-success-500/10 dark:text-success-300"
                    @click="markNormal(record)"
                  >
                    <CheckCircle2 class="size-4" />
                    复测完成
                  </button>
                </td>
              </tr>
            </tbody>
          </table>

          <div
            v-if="filteredRecords.length === 0"
            class="mt-4 rounded-xl border border-dashed border-gray-300 p-8 text-center dark:border-gray-700"
          >
            <p class="font-medium text-theme-sm text-gray-800 dark:text-white/90">没有找到体征记录</p>
            <button
              type="button"
              class="mt-4 rounded-lg bg-brand-600 px-4 py-2.5 text-theme-sm font-medium text-white hover:bg-brand-700"
              @click="resetFilters"
            >
              清空筛选
            </button>
          </div>
        </div>
      </section>
    </div>
  </AdminLayout>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { Activity, CheckCircle2, Plus } from 'lucide-vue-next'
import AdminLayout from '@/components/layout/AdminLayout.vue'
import HealthVitalsForm from '@/components/dashboard/HealthVitalsForm.vue'
import { getStoredUser } from '@/api/http'
import { canUseAction } from '@/config/roles'
import { useOperationsStore } from '@/stores/operations'
import type { HealthRecord, HealthRecordInput } from '@/stores/operations'

const operations = useOperationsStore()
const currentUser = getStoredUser()
const canCreateHealth = canUseAction(currentUser?.roleName, 'health:create')
const canResolveHealth = canUseAction(currentUser?.roleName, 'health:resolve')
const search = ref('')
const status = ref<HealthRecord['status'] | '全部状态'>('全部状态')
const feedback = ref('')
const showForm = ref(false)
const selectedElderKey = ref('')

onMounted(() => {
  void operations.fetchHealthRecords()
})

const createEmptyRecord = (): HealthRecordInput => ({
  elderName: '刘玉梅',
  room: 'C-212',
  heartRate: 82,
  systolicPressure: 128,
  diastolicPressure: 80,
  bloodSugar: 6.2,
  temperature: 36.6,
  bloodOxygen: 97,
  sleepHours: 6.8,
  remark: '',
})

const newRecord = ref<HealthRecordInput>(createEmptyRecord())

const elderOptions = computed(() => {
  const map = new Map<string, { key: string; name: string; room: string; elderlyId?: number }>()

  operations.healthRecords.forEach((record) => {
    const key = `${record.room}-${record.elderName}`
    if (!map.has(key)) {
      map.set(key, {
        key,
        name: record.elderName,
        room: record.room,
        elderlyId: record.elderlyId,
      })
    }
  })

  return [...map.values()]
})

const evaluatePreviewStatus = (record: HealthRecordInput): HealthRecord['status'] => {
  if (
    record.systolicPressure >= 160 ||
    record.diastolicPressure >= 100 ||
    record.heartRate >= 115 ||
    record.heartRate <= 55 ||
    record.temperature >= 38 ||
    record.bloodOxygen <= 93 ||
    record.bloodSugar >= 11.1
  ) {
    return '异常'
  }

  if (
    record.systolicPressure >= 140 ||
    record.diastolicPressure >= 90 ||
    record.heartRate >= 100 ||
    record.heartRate <= 60 ||
    record.temperature >= 37.3 ||
    record.bloodOxygen <= 95 ||
    record.bloodSugar >= 7.8 ||
    record.sleepHours < 5
  ) {
    return '需复测'
  }

  return '正常'
}

const previewStatus = computed(() => evaluatePreviewStatus(newRecord.value))

const statusClassMap: Record<HealthRecord['status'], string> = {
  正常: 'bg-success-50 text-success-700 dark:bg-success-500/15 dark:text-success-400',
  需复测: 'bg-warning-50 text-warning-700 dark:bg-warning-500/15 dark:text-warning-400',
  异常: 'bg-error-50 text-error-700 dark:bg-error-500/15 dark:text-error-400',
}

const records = computed(() => operations.healthRecords)
const attentionRecords = computed(() => records.value.filter((record) => record.status !== '正常'))
const abnormalCount = computed(() => records.value.filter((record) => record.status === '异常').length)
const retestCount = computed(() => records.value.filter((record) => record.status === '需复测').length)

const average = (values: number[]) => {
  if (!values.length) return 0
  return Math.round(values.reduce((sum, value) => sum + value, 0) / values.length)
}

const metrics = computed(() => {
  const systolicValues = records.value
    .map((record) => record.systolicPressure || Number(record.bloodPressure.split('/')[0]))
    .filter(Boolean)
  const oxygenValues = records.value.map((record) => record.bloodOxygen).filter(Boolean)

  return [
    {
      label: '今日采集',
      value: records.value.length,
      unit: '条',
      badge: '实时',
      tone: 'bg-brand-50 text-brand-700 dark:bg-brand-500/15 dark:text-brand-300',
      description: '含设备同步和人工录入体征',
    },
    {
      label: '异常体征',
      value: abnormalCount.value,
      unit: '人',
      badge: '优先',
      tone: 'bg-error-50 text-error-700 dark:bg-error-500/15 dark:text-error-400',
      description: '达到高危阈值的数据会生成紧急告警',
    },
    {
      label: '复测队列',
      value: retestCount.value,
      unit: '人',
      badge: '跟进',
      tone: 'bg-warning-50 text-warning-700 dark:bg-warning-500/15 dark:text-warning-400',
      description: '需要护理人员二次确认',
    },
    {
      label: '平均血氧',
      value: average(oxygenValues),
      unit: '%',
      badge: `收缩压 ${average(systolicValues)}`,
      tone: 'bg-success-50 text-success-700 dark:bg-success-500/15 dark:text-success-400',
      description: '用于快速判断整体采集质量',
    },
  ]
})

const filteredRecords = computed(() => {
  const keyword = search.value.trim().toLowerCase()

  return records.value.filter((record) => {
    const matchesKeyword =
      !keyword ||
      record.elderName.toLowerCase().includes(keyword) ||
      record.room.toLowerCase().includes(keyword)
    const matchesStatus = status.value === '全部状态' || record.status === status.value

    return matchesKeyword && matchesStatus
  })
})

const trendSource = computed(() => records.value.slice(0, 7).reverse())

const scaleY = (value: number, min: number, max: number) => {
  const clamped = Math.min(Math.max(value, min), max)
  return 218 - ((clamped - min) / (max - min)) * 162
}

const trendPoints = computed(() => {
  const total = Math.max(trendSource.value.length - 1, 1)

  return trendSource.value.map((record, index) => {
    const systolic = record.systolicPressure || Number(record.bloodPressure.split('/')[0]) || 0
    const x = 54 + (index / total) * 580

    return {
      key: record.id,
      label: record.room,
      x,
      systolic,
      heartRate: record.heartRate,
      systolicY: scaleY(systolic, 90, 170),
      heartRateY: scaleY(record.heartRate, 55, 120),
    }
  })
})

const systolicLine = computed(() =>
  trendPoints.value.map((point) => `${point.x},${point.systolicY}`).join(' '),
)

const heartRateLine = computed(() =>
  trendPoints.value.map((point) => `${point.x},${point.heartRateY}`).join(' '),
)

const resetForm = () => {
  selectedElderKey.value = ''
  newRecord.value = createEmptyRecord()
}

const resetFilters = () => {
  search.value = ''
  status.value = '全部状态'
}

const submitVitals = async () => {
  if (!canCreateHealth) {
    feedback.value = '当前角色没有录入体征权限'
    return
  }

  const statusText = previewStatus.value
  await operations.addHealthRecord({ ...newRecord.value })
  feedback.value =
    statusText === '正常'
      ? `${newRecord.value.room} ${newRecord.value.elderName} 体征已保存`
      : `${newRecord.value.room} ${newRecord.value.elderName} 已保存，并生成${statusText}跟进`
  resetForm()
}

const requestRetest = (record: HealthRecord) => {
  if (!canResolveHealth) {
    feedback.value = '当前角色没有发起复测权限'
    return
  }

  operations.requestHealthRetest(record.id)
  feedback.value = `${record.room} ${record.elderName} 已加入复测队列`
}

const markNormal = (record: HealthRecord) => {
  if (!canResolveHealth) {
    feedback.value = '当前角色没有完成复测权限'
    return
  }

  operations.markHealthNormal(record.id)
  feedback.value = `${record.room} ${record.elderName} 复测完成，相关告警已同步处理`
}
</script>
