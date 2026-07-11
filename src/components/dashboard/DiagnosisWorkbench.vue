<template>
  <section
    class="rounded-2xl border border-gray-200 bg-white p-5 shadow-theme-xs dark:border-gray-800 dark:bg-white/[0.03] sm:p-6"
  >
    <div class="flex items-start justify-between gap-4">
      <div>
        <h2 class="text-lg font-semibold text-gray-800 text-balance dark:text-white/90">
          异常诊断工作台
        </h2>
        <p class="mt-1 text-theme-sm text-gray-500 text-pretty dark:text-gray-400">
          按风险等级排序的待评估对象，重点关注收缩压、舒张压、心率和血氧的偏离趋势。
        </p>
      </div>
      <span
        class="hidden rounded-full bg-error-50 px-2.5 py-1 text-theme-xs font-medium text-error-700 dark:bg-error-500/15 dark:text-error-400 sm:inline-flex"
      >
        紧急 {{ criticalCount }} 例
      </span>
    </div>

    <div class="mt-5 max-w-full overflow-x-auto custom-scrollbar">
      <table class="min-w-full">
        <thead>
          <tr class="border-t border-gray-100 dark:border-gray-800">
            <th class="py-3 pr-4 text-left text-theme-xs font-medium text-gray-500">风险等级</th>
            <th class="py-3 pr-4 text-left text-theme-xs font-medium text-gray-500">老人</th>
            <th class="py-3 pr-4 text-left text-theme-xs font-medium text-gray-500">关键体征</th>
            <th class="py-3 pr-4 text-left text-theme-xs font-medium text-gray-500">诊断</th>
            <th class="py-3 pr-4 text-left text-theme-xs font-medium text-gray-500">采集时间</th>
            <th class="py-3 text-left text-theme-xs font-medium text-gray-500">建议处置</th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="row in diagnosisRows"
            :key="row.id"
            class="border-t border-gray-100 hover:bg-gray-50 dark:border-gray-800 dark:hover:bg-white/[0.03]"
          >
            <td class="py-3 pr-4 whitespace-nowrap">
              <span
                class="rounded-full px-2 py-0.5 text-theme-xs font-medium"
                :class="riskToneClass[row.riskLevel]"
              >
                {{ riskLabel[row.riskLevel] }}
              </span>
            </td>
            <td class="py-3 pr-4 whitespace-nowrap">
              <p class="font-medium text-gray-800 text-theme-sm dark:text-white/90">
                {{ row.room }} · {{ row.elderName }}
              </p>
              <p class="text-theme-xs text-gray-500 dark:text-gray-400">ID {{ row.elderlyId ?? '—' }}</p>
            </td>
            <td class="py-3 pr-4 text-theme-xs tabular-nums text-gray-700 dark:text-gray-300">
              <p>收缩压 <span class="font-semibold">{{ row.systolic }}</span> / 舒张压 {{ row.diastolic }}</p>
              <p>心率 <span class="font-semibold">{{ row.heartRate }}</span> bpm · 血氧 {{ row.bloodOxygen }}%</p>
              <p v-if="row.bloodSugar !== null">血糖 {{ row.bloodSugar }} mmol/L</p>
            </td>
            <td class="py-3 pr-4 text-theme-sm text-gray-700 max-w-[260px] dark:text-gray-300">
              <p class="text-pretty">{{ row.diagnosis }}</p>
            </td>
            <td class="py-3 pr-4 text-theme-xs text-gray-500 dark:text-gray-400">
              {{ row.measuredAt }}
            </td>
            <td class="py-3 text-theme-xs text-gray-600 dark:text-gray-300">
              {{ row.suggestion }}
            </td>
          </tr>
          <tr v-if="diagnosisRows.length === 0">
            <td colspan="6" class="py-8 text-center text-theme-sm text-gray-500 dark:text-gray-400">
              当前无待诊断对象
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </section>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useOperationsStore } from '@/stores/operations'

const operations = useOperationsStore()

type RiskLevel = 'critical' | 'warning' | 'attention'

interface DiagnosisRow {
  id: number
  elderlyId?: number
  elderName: string
  room: string
  systolic: number
  diastolic: number
  heartRate: number
  bloodOxygen: number
  bloodSugar: number | null
  measuredAt: string
  diagnosis: string
  suggestion: string
  riskLevel: RiskLevel
}

const riskLabel: Record<RiskLevel, string> = {
  critical: '紧急',
  warning: '复测',
  attention: '关注',
}

const riskToneClass: Record<RiskLevel, string> = {
  critical: 'bg-error-50 text-error-700 dark:bg-error-500/15 dark:text-error-400',
  warning: 'bg-warning-50 text-warning-700 dark:bg-warning-500/15 dark:text-warning-400',
  attention: 'bg-brand-50 text-brand-700 dark:bg-brand-500/15 dark:text-brand-300',
}

const buildSuggestion = (
  systolic: number,
  diastolic: number,
  heartRate: number,
  oxygen: number,
  sugar: number | null,
): string => {
  const parts: string[] = []
  if (systolic >= 160 || diastolic >= 100) {
    parts.push('立即复核血压，评估降压药方案')
  } else if (systolic >= 140 || diastolic >= 90) {
    parts.push('调整监测频率，连续记录 3 日')
  }
  if (heartRate >= 115 || heartRate <= 55) {
    parts.push('排查心律失常，必要时 24h 动态心电')
  } else if (heartRate >= 100 || heartRate <= 60) {
    parts.push('关注心率变化趋势')
  }
  if (oxygen <= 93) {
    parts.push('吸氧并监测血氧饱和度')
  } else if (oxygen <= 95) {
    parts.push('记录呼吸情况，排查呼吸道症状')
  }
  if (sugar !== null && sugar >= 11.1) {
    parts.push('复查空腹血糖及糖化血红蛋白')
  } else if (sugar !== null && sugar >= 7.8) {
    parts.push('加强饮食记录与餐后血糖')
  }
  if (parts.length === 0) parts.push('维持当前治疗方案')
  return parts.join('；')
}

const computeRisk = (record: {
  systolicPressure?: number
  diastolicPressure?: number
  heartRate?: number
  bloodOxygen?: number
  bloodSugar?: number
}): RiskLevel => {
  const s = record.systolicPressure ?? 0
  const d = record.diastolicPressure ?? 0
  const hr = record.heartRate ?? 0
  const o2 = record.bloodOxygen ?? 100
  const sugar = record.bloodSugar ?? 0
  if (s >= 160 || d >= 100 || hr >= 115 || hr <= 55 || o2 <= 93 || sugar >= 11.1) {
    return 'critical'
  }
  if (s >= 140 || d >= 90 || hr >= 100 || hr <= 60 || o2 <= 95 || sugar >= 7.8) {
    return 'warning'
  }
  return 'attention'
}

const diagnosisRows = computed<DiagnosisRow[]>(() => {
  return operations.healthRecords
    .filter((r) => r.status === '异常' || r.status === '需复测')
    .map((r) => {
      const s = r.systolicPressure ?? 0
      const d = r.diastolicPressure ?? 0
      const hr = r.heartRate ?? 0
      const o2 = r.bloodOxygen ?? 100
      const sugar = r.bloodSugar ?? null
      return {
        id: r.id,
        elderlyId: r.elderlyId,
        elderName: r.elderName,
        room: r.room,
        systolic: s,
        diastolic: d,
        heartRate: hr,
        bloodOxygen: o2,
        bloodSugar: sugar,
        measuredAt: r.measuredAt,
        diagnosis: r.riskReason || '体征偏离参考范围',
        suggestion: buildSuggestion(s, d, hr, o2, sugar),
        riskLevel: computeRisk({
          systolicPressure: s,
          diastolicPressure: d,
          heartRate: hr,
          bloodOxygen: o2,
          bloodSugar: sugar ?? 0,
        }),
      }
    })
    .sort((a, b) => {
      const order: Record<RiskLevel, number> = { critical: 0, warning: 1, attention: 2 }
      return order[a.riskLevel] - order[b.riskLevel]
    })
})

const criticalCount = computed(
  () => diagnosisRows.value.filter((r) => r.riskLevel === 'critical').length,
)
</script>
