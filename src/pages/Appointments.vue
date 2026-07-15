<template>
  <div class="pb-24">
    <header class="sticky top-0 z-10 bg-[#f5f5f7]/80 backdrop-blur-md">
      <div class="flex items-center justify-between h-14 px-5">
        <h1 class="font-semibold text-[21px] text-ink tracking-tight">我的预约</h1>
        <router-link
          to="/appointments/new"
          class="flex items-center gap-1.5 text-sm font-medium text-primary"
        >
          <PhPlus :size="16" />
          新建
        </router-link>
      </div>
    </header>

    <!-- Error -->
    <div v-if="error" class="px-5 py-20 text-center text-error">{{ error }}</div>

    <!-- Empty -->
    <EmptyState
      v-else-if="!loading && list.length === 0"
      type="calendar"
      message="暂无预约"
    />

    <!-- List -->
    <div v-else-if="!loading" class="px-5 mt-4 space-y-3">
      <div
        v-for="apt in list"
        :key="apt.id"
        class="glass-card p-4"
      >
        <div class="flex items-start justify-between">
          <div class="flex-1 min-w-0">
            <div class="flex items-center gap-2 mb-1">
              <span
                class="text-xs px-2 py-0.5 rounded-full"
                :class="statusStyle(apt.status)"
              >{{ statusLabel(apt.status) }}</span>
              <span class="text-xs text-muted">{{ apt.serviceType }}</span>
            </div>
            <p class="text-sm font-medium text-ink mt-1">
              {{ apt.elderlyName }}
            </p>
            <div class="flex items-center gap-3 mt-2 text-sm text-muted">
              <span class="flex items-center gap-1">
                <PhCalendar :size="14" />
                {{ apt.serviceDate }}
              </span>
              <span class="flex items-center gap-1">
                <PhClock :size="14" />
                {{ apt.serviceTime }}
              </span>
            </div>
            <p v-if="apt.remark" class="text-xs text-muted-soft mt-2">{{ apt.remark }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { reservations } from '../api.js'
import { pageLoading } from '../loading.js'
import EmptyState from '../components/EmptyState.vue'
import { PhPlus, PhCalendarCheck, PhCalendar, PhClock } from '@phosphor-icons/vue'

const list = ref([])
const loading = ref(true)
const error = ref('')

watch(loading, (v) => { pageLoading.value = v }, { immediate: true })

function statusStyle(s) {
  return { pending: 'bg-warning/10 text-warning' }[s] || 'bg-muted/10 text-muted'
}
function statusLabel(s) {
  return { pending: '待确认' }[s] || s
}

onMounted(async () => {
  try {
    list.value = await reservations.list()
  } catch (e) {
    error.value = e.message
  } finally {
    loading.value = false
  }
})
</script>
