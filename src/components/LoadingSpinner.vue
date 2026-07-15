<template>
  <div class="fixed z-40 pointer-events-none" style="top:50%;left:50%;transform:translate(-50%,-50%)">
    <svg
      viewBox="0 0 100 100"
      class="w-14 h-14"
      fill="none"
      aria-hidden="true"
    >
      <g ref="groupRef">
        <path
          ref="pathRef"
          :stroke="color"
          stroke-linecap="round"
          stroke-linejoin="round"
          opacity="0.12"
          :stroke-width="config.strokeWidth"
        />
        <circle
          v-for="i in config.particleCount"
          :key="i"
          :fill="color"
        />
      </g>
    </svg>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'

defineProps({
  color: { type: String, default: '#cc785c' },
})

const config = {
  particleCount: 78,
  trailSpan: 0.32,
  durationMs: 5400,
  rotationDurationMs: 28000,
  pulseDurationMs: 4600,
  strokeWidth: 4.5,
  roseA: 9.2,
  roseABoost: 0.6,
  roseBreathBase: 0.72,
  roseBreathBoost: 0.28,
  roseK: 5,
  roseScale: 3.25,
  rotate: true,
  point(progress, detailScale) {
    const t = progress * Math.PI * 2
    const a = this.roseA + detailScale * this.roseABoost
    const r = a * (this.roseBreathBase + detailScale * this.roseBreathBoost) * Math.cos(this.roseK * t)
    return {
      x: 50 + Math.cos(t) * r * this.roseScale,
      y: 50 + Math.sin(t) * r * this.roseScale,
    }
  },
}

const groupRef = ref(null)
const pathRef = ref(null)
let raf = 0
let startedAt = 0

function normalizeProgress(p) {
  return ((p % 1) + 1) % 1
}

function getDetailScale(time) {
  const pulseProgress = (time % config.pulseDurationMs) / config.pulseDurationMs
  const pulseAngle = pulseProgress * Math.PI * 2
  return 0.52 + ((Math.sin(pulseAngle + 0.55) + 1) / 2) * 0.48
}

function getRotation(time) {
  if (!config.rotate) return 0
  return -((time % config.rotationDurationMs) / config.rotationDurationMs) * 360
}

function buildPath(detailScale, steps = 240) {
  return Array.from({ length: steps + 1 }, (_, i) => {
    const pt = config.point(i / steps, detailScale)
    return `${i === 0 ? 'M' : 'L'} ${pt.x.toFixed(2)} ${pt.y.toFixed(2)}`
  }).join(' ')
}

function render(now) {
  const time = now - startedAt
  const progress = (time % config.durationMs) / config.durationMs
  const detailScale = getDetailScale(time)

  const path = pathRef.value
  const group = groupRef.value
  if (!path || !group) {
    raf = requestAnimationFrame(render)
    return
  }

  group.setAttribute('transform', `rotate(${getRotation(time)} 50 50)`)
  path.setAttribute('d', buildPath(detailScale))

  const circles = group.querySelectorAll('circle')
  circles.forEach((node, i) => {
    const tail = i / (config.particleCount - 1)
    const pt = config.point(
      normalizeProgress(progress - tail * config.trailSpan),
      detailScale,
    )
    const fade = (1 - tail) ** 0.56
    node.setAttribute('cx', pt.x.toFixed(2))
    node.setAttribute('cy', pt.y.toFixed(2))
    node.setAttribute('r', (0.9 + fade * 2.7).toFixed(2))
    node.setAttribute('opacity', (0.04 + fade * 0.96).toFixed(3))
  })

  raf = requestAnimationFrame(render)
}

onMounted(() => {
  startedAt = performance.now()
  raf = requestAnimationFrame(render)
})

onBeforeUnmount(() => {
  cancelAnimationFrame(raf)
})
</script>
