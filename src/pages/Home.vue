<template>
  <div class="pb-24">

    <!-- Apple frosted header -->
    <header class="sticky top-0 z-10 bg-[#f5f5f7]/80 backdrop-blur-xl">
      <div class="flex items-center justify-between px-5 h-14">
        <h1 class="text-ink text-2xl tracking-[-0.28px] font-semibold">CarePulse</h1>
        <router-link to="/profile" class="w-9 h-9 rounded-full bg-gray-100 flex items-center justify-center">
          <PhUser :size="18" class="text-muted" />
        </router-link>
      </div>
    </header>

    <!-- Greeting -->
    <div class="px-5 pt-6 pb-2">
      <p class="text-[14px] text-muted tracking-[-0.224px]">下午好</p>
      <p class="text-[21px] font-semibold text-ink mt-1 tracking-[-0.12px]">{{ userName }}</p>
    </div>

    <!-- Loading / Empty / Error -->
    <div v-if="error" class="px-5 py-20 text-center text-error">{{ error }}</div>

    <section v-else-if="!loading" class="px-5 mt-4 space-y-4 home-enter">
      <!-- Empty state -->
      <EmptyState
        v-if="elderlyList.length === 0"
        type="elderly"
        message="还没有绑定老人"
      >
        <template #action>
          <router-link
            to="/bind"
            class="inline-flex items-center gap-2 px-6 h-11 rounded-full bg-primary text-white text-[17px] font-medium active:scale-95 transition-all hover:bg-primary-active"
          >
            <PhPlus :size="18" />
            绑定老人
          </router-link>
        </template>
      </EmptyState>

      <!-- Elderly cards with health data -->
      <router-link
        v-for="elder in elderlyList"
        :key="elder.id"
        :to="`/elderly/${elder.id}`"
        class="block"
      >
        <article class="glass-card p-5 active:scale-[0.98] transition-transform">
          <div class="flex items-center gap-4">
            <div class="w-14 h-14 rounded-full bg-primary/10 flex items-center justify-center flex-shrink-0">
              <span class="text-xl font-semibold text-primary tracking-[-0.28px]">{{ elder.elderlyName?.charAt(0) }}</span>
            </div>
            <div class="flex-1 min-w-0">
              <h3 class="font-semibold text-ink text-[17px] truncate tracking-[-0.374px]">{{ elder.elderlyName }}</h3>
              <p class="text-[14px] text-muted mt-0.5 tracking-[-0.224px]">
                {{ elder.age }}岁 · {{ elder.gender === 'male' ? '男' : '女' }}
                <span v-if="elder.riskLevel" class="ml-2">
                  <span
                    class="inline-block w-2 h-2 rounded-full mr-1"
                    :class="riskDot(elder.riskLevel)"
                  ></span>
                  {{ riskLabel(elder.riskLevel) }}
                </span>
              </p>
            </div>
            <PhCaretRight :size="18" class="text-muted-soft flex-shrink-0" />
          </div>

          <!-- Latest health metrics -->
          <div v-if="elder._health" class="mt-3 pt-3 border-t border-hairline-soft">
            <div class="grid grid-cols-4 gap-2">
              <div class="text-center">
                <p class="text-[10px] text-muted-soft">血压</p>
                <p class="text-[13px] font-semibold text-ink mt-0.5">{{ elder._health.systolicPressure }}/{{ elder._health.diastolicPressure }}</p>
              </div>
              <div class="text-center">
                <p class="text-[10px] text-muted-soft">心率</p>
                <p class="text-[13px] font-semibold text-ink mt-0.5">{{ elder._health.heartRate }}</p>
              </div>
              <div class="text-center">
                <p class="text-[10px] text-muted-soft">血糖</p>
                <p class="text-[13px] font-semibold text-ink mt-0.5">{{ elder._health.bloodSugar }}</p>
              </div>
              <div class="text-center">
                <p class="text-[10px] text-muted-soft">体温</p>
                <p class="text-[13px] font-semibold text-ink mt-0.5">{{ elder._health.temperature }}</p>
              </div>
            </div>
            <p class="text-[10px] text-muted-soft text-right mt-1">{{ elder._health.recordTime?.slice(0, 16) }}</p>
          </div>
        </article>
      </router-link>

      <!-- Launcher-style icon grid -->
      <section v-if="elderlyList.length > 0" class="mt-4">
        <div class="grid grid-cols-4 gap-4 place-items-center">
          <router-link
            v-for="item in launcherItems" :key="item.label"
            :to="item.to"
            class="flex flex-col items-center gap-2 active:scale-[0.92] transition-transform"
          >
            <div
              class="w-16 h-16 rounded-[22px] flex items-center justify-center shadow-sm"
              :style="{ background: item.bg }"
            >
              <component :is="item.icon" :size="28" :style="{ color: item.fg }" />
            </div>
            <span class="text-[12px] font-medium text-ink">{{ item.label }}</span>
          </router-link>
        </div>
      </section>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { elderly, health } from '../api.js'
import { pageLoading } from '../loading.js'
import EmptyState from '../components/EmptyState.vue'
import { PhUser, PhPlus, PhCaretRight, PhCalendarCheck, PhClock, PhPlusCircle, PhGear } from '@phosphor-icons/vue'

const userName = ref(JSON.parse(localStorage.getItem('user') || '{}').name || '家属')
const elderlyList = ref([])
const loading = ref(true)
const error = ref('')

// Apple Watch style launcher grid
const launcherItems = [
  { label: '预约服务', icon: PhCalendarCheck, to: '/appointments/new', bg: '#0066cc', fg: '#ffffff' },
  { label: '我的预约', icon: PhClock,         to: '/appointments',      bg: '#e8f0fe', fg: '#0066cc' },
  { label: '绑定老人', icon: PhPlusCircle,    to: '/bind',              bg: '#e6f4ea', fg: '#34c759' },
  { label: '个人中心', icon: PhGear,          to: '/profile',           bg: '#f0f0f5', fg: '#7a7a7a' },
]

watch(loading, (v) => { pageLoading.value = v }, { immediate: true })

function riskDot(level) {
  return { high: 'bg-error', medium: 'bg-warning', low: 'bg-success' }[level] || 'bg-muted'
}
function riskLabel(level) {
  return { high: '高风险', medium: '中风险', low: '低风险' }[level] || ''
}

onMounted(async () => {
  try {
    elderlyList.value = await elderly.list()

    // Fetch latest health record for each elderly
    await Promise.all(
      elderlyList.value.map(async (elder) => {
        try {
          const h = await health.list(elder.id, 1, 1)
          if (h.records?.length) elder._health = h.records[0]
        } catch (_) { /* optional */ }
      })
    )
  } catch (e) {
    error.value = e.message
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
/* ── Card entrance ── */
@keyframes home-enter {
  from { opacity: 0; transform: translateY(8px); }
  to   { opacity: 1; transform: translateY(0); }
}
.home-enter {
  animation: home-enter 0.5s ease-out both;
}
</style>
