<template>
  <section class="mb-6 rounded-xl border border-gray-200 bg-white p-5 shadow-theme-xs dark:border-gray-800 dark:bg-white/[0.03]">
    <div class="flex flex-col gap-2 sm:flex-row sm:items-start sm:justify-between">
      <div>
        <h2 class="text-lg font-semibold text-gray-800 text-balance dark:text-white/90">体征录入</h2>
        <p class="mt-1 text-theme-sm text-gray-500 text-pretty dark:text-gray-400">
          保存后会自动判断状态，异常或需复测数据会同步进入告警中心。
        </p>
      </div>
      <span class="w-fit rounded-full px-2.5 py-1 text-theme-xs font-medium" :class="previewClass">
        预判：{{ previewLabel }}
      </span>
    </div>

    <form class="mt-5 grid gap-4 md:grid-cols-2 xl:grid-cols-4" @submit.prevent="$emit('submit', record)">
      <label class="block">
        <span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300">老人</span>
        <select
          :value="elderKey"
          class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-3 py-2.5 text-theme-sm text-gray-800 shadow-theme-xs focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-gray-900 dark:text-white/90"
          @change="onElderSelect(($event.target as HTMLSelectElement).value)"
        >
          <option value="">手动录入</option>
          <option v-for="elder in elders" :key="elder.key" :value="elder.key">{{ elder.room }} · {{ elder.name }}</option>
        </select>
      </label>

      <label class="block">
        <span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300">姓名</span>
        <input v-model="record.elderName" type="text" required class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-theme-sm text-gray-800 shadow-theme-xs focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90" />
      </label>

      <label class="block">
        <span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300">房间</span>
        <input v-model="record.room" type="text" required class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-theme-sm text-gray-800 shadow-theme-xs focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90" />
      </label>

      <label class="block">
        <span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300">心率 bpm</span>
        <input v-model.number="record.heartRate" type="number" min="35" max="180" required class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-theme-sm text-gray-800 shadow-theme-xs focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90" />
      </label>

      <label class="block">
        <span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300">收缩压</span>
        <input v-model.number="record.systolicPressure" type="number" min="70" max="240" required class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-theme-sm text-gray-800 shadow-theme-xs focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90" />
      </label>

      <label class="block">
        <span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300">舒张压</span>
        <input v-model.number="record.diastolicPressure" type="number" min="40" max="160" required class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-theme-sm text-gray-800 shadow-theme-xs focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90" />
      </label>

      <label class="block">
        <span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300">血糖 mmol/L</span>
        <input v-model.number="record.bloodSugar" type="number" min="2" max="25" step="0.1" required class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-theme-sm text-gray-800 shadow-theme-xs focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90" />
      </label>

      <label class="block">
        <span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300">体温 °C</span>
        <input v-model.number="record.temperature" type="number" min="34" max="42" step="0.1" required class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-theme-sm text-gray-800 shadow-theme-xs focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90" />
      </label>

      <label class="block">
        <span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300">血氧 %</span>
        <input v-model.number="record.bloodOxygen" type="number" min="70" max="100" required class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-theme-sm text-gray-800 shadow-theme-xs focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90" />
      </label>

      <label class="block">
        <span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300">睡眠 h</span>
        <input v-model.number="record.sleepHours" type="number" min="0" max="16" step="0.1" required class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-theme-sm text-gray-800 shadow-theme-xs focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90" />
      </label>

      <label class="block md:col-span-2">
        <span class="mb-1.5 block text-theme-sm font-medium text-gray-700 dark:text-gray-300">备注</span>
        <input v-model="record.remark" type="text" placeholder="例如 服药后复测 / 设备补采" class="h-11 w-full rounded-lg border border-gray-300 bg-transparent px-4 py-2.5 text-theme-sm text-gray-800 shadow-theme-xs placeholder:text-gray-400 focus:border-brand-300 focus:outline-hidden focus:ring-3 focus:ring-brand-500/10 dark:border-gray-700 dark:bg-white/[0.03] dark:text-white/90" />
      </label>

      <div class="flex flex-col gap-3 md:col-span-2 xl:col-span-4 sm:flex-row sm:items-center">
        <button type="submit" class="inline-flex w-fit items-center justify-center gap-2 rounded-lg bg-brand-600 px-4 py-2.5 text-theme-sm font-medium text-white shadow-theme-xs hover:bg-brand-700">
          <Save class="size-4" /> 保存体征
        </button>
        <button type="button" class="inline-flex w-fit items-center justify-center gap-2 rounded-lg border border-gray-300 bg-white px-4 py-2.5 text-theme-sm font-medium text-gray-700 shadow-theme-xs hover:bg-gray-50 dark:border-gray-700 dark:bg-gray-800 dark:text-gray-300 dark:hover:bg-white/[0.03]" @click="reset">
          <RotateCcw class="size-4" /> 重置
        </button>
      </div>
    </form>
  </section>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { RotateCcw, Save } from 'lucide-vue-next'
import type { HealthRecordInput } from '@/stores/operations'

export interface ElderPreset {
  key: string
  name: string
  room: string
  elderlyId?: number
}

const props = defineProps<{
  record: HealthRecordInput
  elders: ElderPreset[]
  elderKey: string
}>()

const emit = defineEmits<{
  submit: [record: HealthRecordInput]
  reset: []
  'update:elderKey': [key: string]
}>()

const previewLabel = computed(() => {
  const s = props.record.systolicPressure
  const d = props.record.diastolicPressure
  const hr = props.record.heartRate
  const o2 = props.record.bloodOxygen
  const sugar = props.record.bloodSugar
  if (s >= 160 || d >= 100 || hr >= 115 || hr <= 55 || o2 <= 93 || sugar >= 11.1) return '异常'
  if (s >= 140 || d >= 90 || hr >= 100 || hr <= 60 || o2 <= 95 || sugar >= 7.8) return '需复测'
  return '正常'
})

const previewClass = computed(() => {
  if (previewLabel.value === '异常') return 'bg-error-50 text-error-700 dark:bg-error-500/15 dark:text-error-400'
  if (previewLabel.value === '需复测') return 'bg-warning-50 text-warning-700 dark:bg-warning-500/15 dark:text-warning-400'
  return 'bg-success-50 text-success-700 dark:bg-success-500/15 dark:text-success-400'
})

const syncElder = (key: string) => {
  emit('update:elderKey', key)
  const elder = props.elders.find((item) => item.key === key)
  if (elder) {
    props.record.elderName = elder.name
    props.record.room = elder.room
    props.record.elderlyId = elder.elderlyId
  }
}

const onElderSelect = (key: string) => {
  syncElder(key)
}

const reset = () => {
  emit('update:elderKey', '')
  emit('reset')
}
</script>
