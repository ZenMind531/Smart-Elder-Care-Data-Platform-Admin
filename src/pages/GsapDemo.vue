<template>
  <div class="pb-24">
    <header class="sticky top-0 z-10 bg-[#f5f5f7]/80 backdrop-blur-md">
      <div class="flex items-center h-14 px-4">
        <h1 class="font-semibold text-[21px] text-ink tracking-tight">GSAP Demo</h1>
      </div>
    </header>

    <!-- 1. 卡片弹入 -->
    <section class="px-5 mt-6">
      <h2 class="text-sm font-semibold text-muted uppercase tracking-wider mb-3">卡片依次弹入 (back.out)</h2>
      <div class="space-y-3">
        <div v-for="n in 4" :key="n" class="card bg-white rounded-[18px] border border-hairline p-5 flex items-center gap-4">
          <div class="w-12 h-12 rounded-full bg-primary/15 flex items-center justify-center shrink-0">
            <span class="text-lg font-bold text-primary">{{ n }}</span>
          </div>
          <div class="flex-1"><div class="h-4 bg-gray-100 rounded w-2/3 mb-2" /><div class="h-3 bg-gray-50 rounded w-1/2" /></div>
        </div>
      </div>
    </section>

    <!-- 2. 心跳线描边 -->
    <section class="px-5 mt-8" ref="ecg">
      <h2 class="text-sm font-semibold text-muted uppercase tracking-wider mb-3">心跳线描边 (ScrollTrigger)</h2>
      <p class="text-xs text-muted-soft mb-3">滚到这里，线条自动画出来</p>
      <div class="bg-white rounded-[18px] border border-hairline p-5 overflow-hidden">
        <svg viewBox="0 0 300 80" class="w-full">
          <path ref="ecgPath" d="M0,40 L20,40 L25,40 L30,20 L40,60 L50,40 L300,40"
            fill="none" stroke="#0066cc" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"
            stroke-dasharray="400" stroke-dashoffset="400" />
        </svg>
      </div>
    </section>

    <!-- 3. 数字滚动 -->
    <section class="px-5 mt-8 mb-12" ref="counter">
      <h2 class="text-sm font-semibold text-muted uppercase tracking-wider mb-3">数字滚动 (ScrollTrigger)</h2>
      <p class="text-xs text-muted-soft mb-3">滚到这里，数字从 0 跳到 82</p>
      <div class="bg-white rounded-[18px] border border-hairline p-8 text-center">
        <p ref="countEl" class="text-5xl font-bold text-ink tracking-tight">0</p>
        <p class="text-sm text-muted mt-2">bpm</p>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import gsap from 'gsap'
import { ScrollTrigger } from 'gsap/ScrollTrigger'

gsap.registerPlugin(ScrollTrigger)

const ecg     = ref(null)
const ecgPath = ref(null)
const counter = ref(null)
const countEl = ref(null)

onMounted(async () => {
  await nextTick()

  // 1. Stagger cards: back.out ease with bounce
  gsap.from('.card', {
    opacity: 0, y: 50, scale: 0.9,
    duration: 0.7, stagger: 0.15, ease: 'back.out(1.5)',
  })

  // 2. ECG line draws on scroll
  if (ecgPath.value) {
    ScrollTrigger.create({
      trigger: ecg.value,
      start: 'top 80%',
      onEnter: () => gsap.to(ecgPath.value, { strokeDashoffset: 0, duration: 1.5, ease: 'power2.inOut' }),
    })
  }

  // 3. Count-up on scroll
  if (countEl.value) {
    ScrollTrigger.create({
      trigger: counter.value,
      start: 'top 80%',
      onEnter: () => gsap.to(countEl.value, {
        innerText: 82, duration: 1.5, snap: { innerText: 1 }, ease: 'power2.out',
      }),
    })
  }
})
</script>
