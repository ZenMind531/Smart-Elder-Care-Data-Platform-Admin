<template>
  <div class="pb-24">
    <!-- Header -->
    <header class="sticky top-0 z-10 bg-white/80 backdrop-blur-md border-b border-hairline">
      <div class="flex items-center h-14 px-4 gap-3">
        <button @click="$router.back()" class="w-9 h-9 flex items-center justify-center -ml-1">
          <PhCaretLeft :size="22" class="text-primary" />
        </button>
        <h1 class="font-semibold text-[21px] text-ink tracking-tight">老人详情</h1>
      </div>
    </header>

    <!-- Loading / Error -->
    <div v-if="error" class="px-5 py-20 text-center text-error">{{ error }}</div>

    <template v-else-if="!loading">
      <!-- Elderly Card -->
      <div class="px-5 mt-5">
        <article class="relative bg-white border border-hairline rounded-[18px] p-5 overflow-hidden">
          <div class="absolute top-0 right-0 w-32 h-32 bg-white border border-hairline-soft rounded-bl-full opacity-60"></div>
          <div class="relative flex items-center gap-4">
            <div class="w-16 h-16 rounded-full bg-primary/15 flex items-center justify-center flex-shrink-0">
              <span class="font-display text-2xl text-primary">{{ info.elderlyName?.charAt(0) }}</span>
            </div>
            <div class="flex-1 min-w-0">
              <h3 class="font-semibold text-xl text-ink">{{ info.elderlyName }}</h3>
              <p class="text-sm text-muted mt-0.5">{{ info.age }}岁 · {{ info.gender === 'male' ? '男' : '女' }}</p>
              <p v-if="info.riskLevel" class="mt-2">
                <span
                  class="inline-flex items-center gap-1.5 text-xs px-2.5 py-1 rounded-full"
                  :class="riskBadge(info.riskLevel)"
                >
                  <span class="w-1.5 h-1.5 rounded-full" :class="riskDot(info.riskLevel)"></span>
                  {{ riskLabel(info.riskLevel) }}
                </span>
              </p>
            </div>
          </div>
        </article>
      </div>

      <!-- Details -->
      <section class="px-5 mt-5 space-y-3">
        <div class="bg-white border border-hairline rounded-[18px] p-5">
          <p class="text-xs text-muted uppercase tracking-wider mb-3">基本信息</p>
          <dl class="space-y-3">
            <div class="flex justify-between">
              <dt class="text-sm text-muted">身份证号</dt>
              <dd class="text-[14px] text-ink">{{ info.idCard || '未填写' }}</dd>
            </div>
            <div class="flex justify-between">
              <dt class="text-sm text-muted">手机号</dt>
              <dd class="text-[14px] text-ink">{{ info.phoneNumber || '未填写' }}</dd>
            </div>
            <div class="flex justify-between">
              <dt class="text-sm text-muted">地址</dt>
              <dd class="text-[14px] text-ink text-right max-w-[60%]">{{ info.address || '未填写' }}</dd>
            </div>
          </dl>
        </div>

        <div class="bg-white border border-hairline rounded-[18px] p-5">
          <p class="text-xs text-muted uppercase tracking-wider mb-3">紧急联系人</p>
          <dl class="space-y-3">
            <div class="flex justify-between">
              <dt class="text-sm text-muted">联系人</dt>
              <dd class="text-[14px] text-ink">{{ info.emergencyContact || '未填写' }}</dd>
            </div>
            <div class="flex justify-between">
              <dt class="text-sm text-muted">联系电话</dt>
              <dd class="text-[14px] text-ink">{{ info.emergencyPhone || '未填写' }}</dd>
            </div>
          </dl>
        </div>

        <div class="bg-white border border-hairline rounded-[18px] p-5">
          <p class="text-xs text-muted uppercase tracking-wider mb-3">健康信息</p>
          <dl class="space-y-3">
            <div class="flex justify-between">
              <dt class="text-sm text-muted">既往病史</dt>
              <dd class="text-[14px] text-ink text-right max-w-[60%]">{{ info.medicalHistory || '无' }}</dd>
            </div>
            <div class="flex justify-between">
              <dt class="text-sm text-muted">过敏史</dt>
              <dd class="text-[14px] text-ink text-right max-w-[60%]">{{ info.allergyHistory || '无' }}</dd>
            </div>
          </dl>
        </div>
      </section>

      <!-- Latest Health Metrics — Apple Watch rings -->
      <section v-if="latestHealth" class="px-5 mt-5">
        <div class="bg-white border border-hairline rounded-[18px] p-5">
          <div class="flex items-center justify-between mb-2">
            <p class="text-xs text-muted uppercase tracking-wider">最新健康数据</p>
            <span class="text-[12px] text-muted-soft">{{ latestHealth.recordTime?.slice(0, 16) }}</span>
          </div>
          <div class="grid grid-cols-2 gap-y-4 gap-x-2 place-items-center py-2">
            <RingGauge
              :value="latestHealth.systolicPressure"
              label="血压"
              :color="sysColor(latestHealth.systolicPressure)"
              :max-value="200"
              :inner="{ value: latestHealth.diastolicPressure, color: diaColor(latestHealth.diastolicPressure), maxValue: 130 }"
              :delay="0"
              :size="90"
              :font-size="18"
            />
            <RingGauge
              :value="latestHealth.heartRate"
              label="心率"
              unit="bpm"
              :color="hrColor(latestHealth.heartRate)"
              :max-value="150"
              :delay="0.12"
              :size="90"
              :font-size="18"
            />
            <RingGauge
              :value="latestHealth.bloodSugar"
              label="血糖"
              unit="mmol/L"
              :color="bsColor(latestHealth.bloodSugar)"
              :max-value="15"
              :delay="0.24"
              :size="90"
              :font-size="18"
            />
            <RingGauge
              :value="latestHealth.temperature"
              label="体温"
              unit="°C"
              :color="tempColor(latestHealth.temperature)"
              :max-value="42"
              :min-value="35"
              :delay="0.36"
              :size="90"
              :font-size="18"
            />
          </div>
          <router-link
            :to="`/elderly/${info.id}/health`"
            class="mt-2 flex items-center justify-center gap-1.5 text-[13px] text-primary font-medium py-2 active:opacity-70"
          >
            查看全部健康数据
            <PhCaretRight :size="14" />
          </router-link>
        </div>
      </section>

      <!-- Quick Actions -->
      <section class="px-5 mt-6 mb-4">
        <div class="grid grid-cols-3 gap-3">
          <router-link
            :to="`/elderly/new?edit=${info.id}`"
            class="flex items-center gap-3 p-4 rounded-[18px] bg-white border border-hairline-soft active:scale-[0.98] transition-transform"
          >
            <PhPencilSimple :size="20" class="text-primary" />
            <span class="text-sm font-medium text-ink">编辑</span>
          </router-link>
          <router-link
            :to="`/appointments/new?elderlyId=${info.id}`"
            class="flex items-center gap-3 p-4 rounded-[18px] bg-white border border-hairline-soft active:scale-[0.98] transition-transform"
          >
            <PhCalendarCheck :size="20" class="text-primary" />
            <span class="text-sm font-medium text-ink">预约服务</span>
          </router-link>
          <router-link
            to="/appointments"
            class="flex items-center gap-3 p-4 rounded-[18px] bg-white border border-hairline-soft active:scale-[0.98] transition-transform"
          >
            <PhClock :size="20" class="text-primary" />
            <span class="text-sm font-medium text-ink">查看预约</span>
          </router-link>
        </div>
      </section>
    </template>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { elderly, health } from '../api.js'
import { pageLoading } from '../loading.js'
import RingGauge from '../components/RingGauge.vue'
import { PhCaretLeft, PhCaretRight, PhCalendarCheck, PhClock, PhPencilSimple } from '@phosphor-icons/vue'

// ── Ring color helpers ──
const G = '#34c759', Y = '#ff9500', R = '#ff3b30'
function sysColor(v) {
  if (v >= 140) return R
  if (v >= 120) return Y
  return G
}
function diaColor(v) {
  if (v >= 90) return R
  if (v >= 80) return Y
  return G
}
function hrColor(v) {
  if (v > 110 || v < 50) return R
  if (v > 100 || v < 60) return Y
  return G
}
function bsColor(v) {
  if (v > 10) return R
  if (v > 7.0) return Y
  return G
}
function tempColor(v) {
  if (v > 38.5) return R
  if (v > 37.3) return Y
  return G
}

const route = useRoute()
const loading = ref(true)
const error = ref('')
const latestHealth = ref(null)

watch(loading, (v) => { pageLoading.value = v }, { immediate: true })

const info = ref({})

function riskDot(level) {
  return { high: 'bg-error', medium: 'bg-warning', low: 'bg-success' }[level] || 'bg-muted'
}
function riskLabel(level) {
  return { high: '高风险', medium: '中风险', low: '低风险' }[level] || ''
}
function riskBadge(level) {
  return {
    high: 'bg-error/10 text-error',
    medium: 'bg-warning/10 text-warning',
    low: 'bg-success/10 text-success',
  }[level] || ''
}

onMounted(async () => {
  try {
    const id = Number(route.params.id)
    const list = await elderly.list()
    const found = list.find((e) => e.id === id)
    if (!found) throw new Error('未找到该老人信息')
    info.value = found

    // Fetch latest health record
    try {
      const h = await health.list(id, 1, 1)
      if (h.records?.length) latestHealth.value = h.records[0]
    } catch (_) { /* health data optional */ }
  } catch (e) {
    error.value = e.message
  } finally {
    loading.value = false
  }
})
</script>
