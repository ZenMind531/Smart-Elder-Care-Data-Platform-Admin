<template>
  <!-- Animated empty state: breathing ring + icon + message -->
  <div class="flex flex-col items-center py-16 px-5 empty-enter">
    <!-- Breathing ring with icon -->
    <div class="relative mb-5 empty-ring-wrap">
      <svg width="88" height="88" viewBox="0 0 88 88" fill="none" class="empty-breath">
        <circle cx="44" cy="44" r="38" stroke="#e8e8ed" stroke-width="1.5" />
        <circle cx="44" cy="44" r="38" :stroke="accent" stroke-width="1.5"
          stroke-dasharray="190 48" stroke-linecap="round"
          class="empty-ring-arc"
          transform="rotate(120 44 44)" />
      </svg>
      <component :is="icon" :size="28" class="absolute inset-0 m-auto" :class="iconClass" />
    </div>

    <p class="text-muted text-[15px] mb-4 text-center max-w-[260px] leading-relaxed">{{ message }}</p>

    <slot name="action" />
  </div>
</template>

<script setup>
import { computed } from 'vue'
import {
  PhHeartbeat, PhHeart, PhCalendarCheck, PhClipboardText,
} from '@phosphor-icons/vue'

const props = defineProps({
  type:    { type: String, default: 'generic' },
  message: { type: String, required: true },
})

const iconMap = {
  health:      PhHeartbeat,
  elderly:     PhHeart,
  calendar:    PhCalendarCheck,
  generic:     PhClipboardText,
}

const icon       = computed(() => iconMap[props.type] || iconMap.generic)
const accent     = computed(() => props.type === 'elderly' ? '#34c759' : '#0066cc')
const iconClass  = computed(() => props.type === 'elderly' ? 'text-success' : 'text-primary')
</script>

<style scoped>
/* ── Entrance: fade up ── */
@keyframes empty-enter {
  from { opacity: 0; transform: translateY(16px); }
  to   { opacity: 1; transform: translateY(0); }
}
.empty-enter {
  animation: empty-enter 0.6s ease-out forwards;
}

/* ── Ring: slow breathing scale ── */
@keyframes empty-breathe {
  0%, 100% { transform: scale(0.96); opacity: 0.7; }
  50%      { transform: scale(1.04); opacity: 1; }
}
.empty-breath {
  animation: empty-breathe 3s ease-in-out infinite;
}

/* ── Arc: slow rotation ── */
@keyframes empty-arc-spin {
  from { transform: rotate(120deg); }
  to   { transform: rotate(480deg); }
}
.empty-ring-arc {
  transform-origin: 44px 44px;
  animation: empty-arc-spin 12s linear infinite;
}
</style>
