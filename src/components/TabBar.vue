<template>
  <!-- Apple Liquid Glass floating tab bar -->
  <nav
    class="fixed bottom-3 left-1/2 -translate-x-1/2 z-50 flex items-center justify-around rounded-full w-[calc(100%-32px)] max-w-[448px]"
    :style="{
      isolation: 'isolate',
      background: `linear-gradient(135deg, rgba(255,255,255,0.22), rgba(255,255,255,0.06)), rgba(245,245,247,0.10)`,
      backdropFilter: 'blur(24px) saturate(180%) contrast(1.05)',
      WebkitBackdropFilter: 'blur(24px) saturate(180%) contrast(1.05)',
      border: '1px solid rgba(255,255,255,0.22)',
      boxShadow: `
        inset 0 1px 0 rgba(255,255,255,0.32),
        inset 0 -1px 0 rgba(255,255,255,0.06),
        0 12px 40px rgba(0,0,0,0.08)
      `,
      padding: '4px',
    }"
  >
    <!-- ::before - radial highlight overlay -->
    <div
      class="absolute inset-0 rounded-full pointer-events-none"
      :style="{
        background: `
          radial-gradient(circle at 20% 0%, rgba(255,255,255,0.40), transparent 34%),
          linear-gradient(90deg, rgba(255,255,255,0.12), transparent 42%, rgba(255,255,255,0.08))
        `,
      }"
    />
    <!-- ::after - inner hairline -->
    <div
      class="absolute rounded-full pointer-events-none"
      :style="{
        inset: '1px',
        border: '1px solid rgba(255,255,255,0.10)',
        borderRadius: 'inherit',
      }"
    />

    <!-- Sliding active pill -->
    <div
      class="absolute rounded-full z-10"
      :style="{
        width: `calc(${pct}% - 28px)`,
        height: 'calc(100% - 12px)',
        top: '6px',
        left: `calc(${activeIndex * pct}% + 14px)`,
        transition: 'left 0.35s cubic-bezier(0.22, 1, 0.36, 1)',
      }"
    >
      <div
        :key="bounceKey"
        class="w-full h-full rounded-full bg-primary shadow-[0_2px_8px_rgba(0,102,204,0.45)]"
        :style="{
          animation: bounceKey > 0 ? `pill-scale 0.35s ease-out` : 'none',
        }"
      />
    </div>

    <!-- Tab items -->
    <button
      v-for="(tab, i) in tabs"
      :key="tab.path"
      @click="navigate(tab.path)"
      class="relative flex flex-col items-center justify-center gap-0.5 rounded-full z-20"
      :style="{
        flex: '1',
        height: '52px',
        padding: '0 4px',
        cursor: 'pointer',
      }"
      :aria-label="tab.label"
    >
      <component
        :is="tab.icon"
        :size="22"
        :weight="activeIndex === i ? 'fill' : 'regular'"
        :style="{
          color: activeIndex === i ? '#ffffff' : '#999999',
          transition: 'color 0.3s ease',
        }"
      />
      <span
        class="text-[10px] font-medium"
        :style="{
          color: activeIndex === i ? '#ffffff' : '#999999',
          transition: 'color 0.3s ease',
        }"
      >{{ tab.label }}</span>
    </button>
  </nav>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const props = defineProps({
  tabs: { type: Array, required: true },
})

const router = useRouter()
const route = useRoute()

const activeIndex = computed(() => {
  const idx = props.tabs.findIndex((t) => {
    if (t.exact) return route.path === t.path
    return route.path.startsWith(t.path)
  })
  return idx === -1 ? 0 : idx
})

const pct = computed(() => 100 / props.tabs.length)

const bounceKey = ref(0)
watch(activeIndex, () => {
  bounceKey.value++
})

function navigate(path) {
  router.push(path)
}
</script>

<style scoped>
@keyframes pill-scale {
  0%   { transform: scale(1); }
  40%  { transform: scale(1.13); }
  100% { transform: scale(1); }
}
</style>
