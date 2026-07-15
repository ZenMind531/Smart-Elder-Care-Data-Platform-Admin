<template>
  <div class="pb-24">
    <!-- Header -->
    <header class="sticky top-0 z-10 bg-white/80 backdrop-blur-md border-b border-hairline">
      <div class="flex items-center h-14 px-4 gap-3">
        <button @click="$router.back()" class="w-9 h-9 flex items-center justify-center -ml-1">
          <PhCaretLeft :size="22" class="text-primary" />
        </button>
        <h1 class="font-semibold text-[21px] text-ink tracking-tight">健康数据</h1>
      </div>
    </header>

    <!-- Loading -->
    <div v-if="loading" class="px-5 py-20 text-center text-muted">加载中...</div>

    <!-- Error -->
    <div v-else-if="error" class="px-5 py-20 text-center text-error">{{ error }}</div>

    <!-- Empty -->
    <EmptyState
      v-else-if="!records.length"
      type="health"
      message="暂无健康数据记录"
    />

    <!-- Records List -->
    <div v-else class="px-5 mt-5 space-y-3">
      <article
        v-for="r in records"
        :key="r.id"
        class="bg-white border border-hairline rounded-[18px] p-4"
      >
        <div class="flex items-center justify-between mb-3">
          <span class="text-[12px] text-muted-soft">{{ r.recordTime?.slice(0, 16) }}</span>
          <span
            v-if="isAbnormal(r)"
            class="text-[11px] text-error bg-error/10 px-2 py-0.5 rounded-full"
          >异常</span>
          <span
            v-else
            class="text-[11px] text-success bg-success/10 px-2 py-0.5 rounded-full"
          >正常</span>
        </div>
        <div class="grid grid-cols-2 gap-y-3 gap-x-2 place-items-center">
          <RingGauge
            :value="r.systolicPressure"
            label="血压"
            :color="sysColor(r.systolicPressure)"
            :max-value="200"
            :inner="{ value: r.diastolicPressure, color: diaColor(r.diastolicPressure), maxValue: 130 }"
            :delay="0"
            :size="78"
            :font-size="15"
            :unit-size="9"
          />
          <RingGauge
            :value="r.heartRate"
            label="心率"
            unit="bpm"
            :color="hrColor(r.heartRate)"
            :max-value="150"
            :delay="0.1"
            :size="78"
            :font-size="16"
            :unit-size="9"
          />
          <RingGauge
            :value="r.bloodSugar"
            label="血糖"
            unit="mmol/L"
            :color="bsColor(r.bloodSugar)"
            :max-value="15"
            :delay="0.2"
            :size="78"
            :font-size="16"
            :unit-size="9"
          />
          <RingGauge
            :value="r.temperature"
            label="体温"
            unit="°C"
            :color="tempColor(r.temperature)"
            :max-value="42"
            :min-value="35"
            :delay="0.3"
            :size="78"
            :font-size="16"
            :unit-size="9"
          />
        </div>
        <p v-if="r.remark" class="mt-3 text-[13px] text-muted">{{ r.remark }}</p>
      </article>

      <!-- Pagination -->
      <div v-if="total > pageSize" class="flex items-center justify-center gap-4 py-4">
        <button
          :disabled="page <= 1"
          @click="loadPage(page - 1)"
          class="px-4 py-2 text-sm text-primary disabled:text-muted-soft disabled:opacity-50"
        >上一页</button>
        <span class="text-sm text-muted">{{ page }} / {{ totalPages }}</span>
        <button
          :disabled="page >= totalPages"
          @click="loadPage(page + 1)"
          class="px-4 py-2 text-sm text-primary disabled:text-muted-soft disabled:opacity-50"
        >下一页</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { health } from '../api.js'
import RingGauge from '../components/RingGauge.vue'
import EmptyState from '../components/EmptyState.vue'
import { PhCaretLeft } from '@phosphor-icons/vue'

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
const elderlyId = Number(route.params.id)

const loading = ref(true)
const error = ref('')
const records = ref([])
const page = ref(1)
const total = ref(0)
const pageSize = 10

const totalPages = computed(() => Math.ceil(total.value / pageSize) || 1)

function isAbnormal(r) {
  return r.systolicPressure > 140 || r.diastolicPressure > 90 ||
    r.heartRate > 100 || r.heartRate < 60 ||
    r.bloodSugar > 7.0 || r.temperature > 37.3
}

async function loadPage(p) {
  loading.value = true
  try {
    const data = await health.list(elderlyId, p, pageSize)
    records.value = data.records || []
    total.value = data.total || 0
    page.value = p
  } catch (e) {
    error.value = e.message
  } finally {
    loading.value = false
  }
}

onMounted(() => loadPage(1))
</script>
