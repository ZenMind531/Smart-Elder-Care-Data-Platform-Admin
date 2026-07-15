<template>
  <div
    class="ring-gauge flex flex-col items-center gap-1"
    :class="{ 'ring-paused': !animate }"
    :style="{ '--ring-delay': `${delay}s`, '--circ-o': circO, '--circ-i': circI }"
  >
    <div class="relative" :style="{ width: `${size}px`, height: `${size}px` }">
      <svg :width="size" :height="size" viewBox="0 0 100 100">
        <!-- Outer track: always full arc -->
        <circle cx="50" cy="50" :r="Ro" fill="none" stroke="#e8e8ed" :stroke-width="sw"
          stroke-linecap="round"
          :stroke-dasharray="`${fullDashO} ${fullGapO}`"
          transform="rotate(135 50 50)"
        />
        <!-- Inner track (BP only) -->
        <circle v-if="inner" cx="50" cy="50" :r="Ri" fill="none" stroke="#e8e8ed" :stroke-width="sw"
          stroke-linecap="round"
          :stroke-dasharray="`${fullDashI} ${fullGapI}`"
          transform="rotate(135 50 50)"
        />
        <!-- Outer progress: proportional fill, draw-on animation -->
        <circle cx="50" cy="50" :r="Ro" fill="none" :stroke="color" :stroke-width="sw"
          stroke-linecap="round"
          :stroke-dasharray="`${progDashO} ${progGapO}`"
          class="ring-progress-o"
          transform="rotate(135 50 50)"
          :style="{ '--dash-o': progDashO, '--gap-o': progGapO }"
        />
        <!-- Inner progress (diastolic) -->
        <circle v-if="inner" cx="50" cy="50" :r="Ri" fill="none" :stroke="inner.color" :stroke-width="sw"
          stroke-linecap="round"
          :stroke-dasharray="`${progDashI} ${progGapI}`"
          class="ring-progress-i"
          transform="rotate(135 50 50)"
          :style="{ '--dash-i': progDashI, '--gap-i': progGapI }"
        />
      </svg>
      <!-- Center: just the number -->
      <div class="absolute inset-0 flex items-center justify-center">
        <span class="font-bold text-ink leading-none tracking-[-0.28px]" :style="{ fontSize: `${fontSize + 4}px` }">{{ value }}</span>
      </div>
    </div>
    <!-- Label + unit merged below -->
    <span class="text-[11px] text-muted text-center leading-tight">{{ subtitle }}</span>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  value:    { type: [String, Number], required: true },
  label:    { type: String, required: true },
  unit:     { type: String, default: '' },
  color:    { type: String, default: '#34c759' },
  maxValue: { type: Number, default: 0 },
  minValue: { type: Number, default: 0 },
  delay:    { type: Number, default: 0 },
  size:     { type: Number, default: 90 },
  fontSize: { type: Number, default: 18 },
  unitSize: { type: Number, default: 10 },
  inner:    { type: Object, default: null },
  /** Set false to pause entrance animation (for scroll-triggered control) */
  animate:  { type: Boolean, default: true },
})

const sw   = 6
const arc  = 0.78   // 280° visible, 80° gap

/** Label line below ring: merges label + unit; for BP shows diastolic */
const subtitle = computed(() => {
  if (props.inner) {
    const d = props.inner.value ?? '--'
    return `低压 ${d} · mmHg`
  }
  return props.unit ? `${props.label} · ${props.unit}` : props.label
})

// Outer ring geometry
const Ro   = 35
const circO = +(2 * Math.PI * Ro).toFixed(2)
const fullDashO = +(circO * arc).toFixed(2)
const fullGapO  = +(circO * (1 - arc)).toFixed(2)

const fillO = computed(() => {
  const v = Number(props.value)
  if (!v || !props.maxValue) return 1
  const range = props.maxValue - props.minValue
  if (range <= 0) return 1
  return Math.max(0, Math.min((v - props.minValue) / range, 1))
})
const progDashO = computed(() => +(circO * arc * fillO.value).toFixed(2))
const progGapO  = computed(() => +(circO - progDashO.value).toFixed(2))

// Inner ring geometry (BP diastolic)
const Ri   = 25
const circI = +(2 * Math.PI * Ri).toFixed(2)
const fullDashI = +(circI * arc).toFixed(2)
const fullGapI  = +(circI * (1 - arc)).toFixed(2)

const fillI = computed(() => {
  if (!props.inner) return 1
  const v = Number(props.inner.value)
  const max = props.inner.maxValue || 0
  if (!v || !max) return 1
  const min = props.inner.minValue || 0
  const range = max - min
  if (range <= 0) return 1
  return Math.max(0, Math.min((v - min) / range, 1))
})
const progDashI = computed(() => +(circI * arc * fillI.value).toFixed(2))
const progGapI  = computed(() => +(circI - progDashI.value).toFixed(2))
</script>

<style scoped>
@keyframes gauge-enter {
  from { opacity: 0; transform: translateY(12px); }
  to   { opacity: 1; transform: translateY(0); }
}
@keyframes ring-draw {
  from { stroke-dasharray: 0 var(--circ-o); }
  to   { stroke-dasharray: var(--dash-o) var(--gap-o); }
}
@keyframes ring-draw-inner {
  from { stroke-dasharray: 0 var(--circ-i); }
  to   { stroke-dasharray: var(--dash-i) var(--gap-i); }
}

.ring-gauge {
  opacity: 0;
  animation: gauge-enter 0.5s ease-out forwards;
  animation-delay: var(--ring-delay);
}
.ring-progress-o {
  animation: ring-draw 0.9s cubic-bezier(0.4, 0, 0.2, 1) forwards;
  animation-delay: calc(var(--ring-delay) + 0.1s);
}
.ring-progress-i {
  animation: ring-draw-inner 0.9s cubic-bezier(0.4, 0, 0.2, 1) forwards;
  animation-delay: calc(var(--ring-delay) + 0.2s);
}

/* Paused state: hide progress rings until triggered */
.ring-paused .ring-progress-o,
.ring-paused .ring-progress-i {
  animation: none;
  stroke-dasharray: 0 var(--ring-circ-o);
}
.ring-paused {
  animation: none;
  opacity: 0;
  transform: translateY(12px);
}
</style>
