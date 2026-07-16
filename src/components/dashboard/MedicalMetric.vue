<template>
  <article
    class="rounded-[18px] border border-gray-200 bg-white p-5 shadow-theme-xs dark:border-gray-800 dark:bg-white/[0.03] md:p-6"
  >
    <div class="flex items-start justify-between gap-4">
      <div
        class="flex size-12 items-center justify-center rounded-xl"
        :class="toneClassMap[tone].iconWrap"
      >
        <component :is="icon" class="size-6" :class="toneClassMap[tone].icon" />
      </div>
      <span
        class="rounded-full px-2.5 py-1 text-theme-xs font-medium tabular-nums"
        :class="toneClassMap[tone].badge"
      >
        {{ trend }}
      </span>
    </div>
    <div class="mt-5">
      <p class="text-sm text-gray-500 dark:text-gray-400">{{ label }}</p>
      <div class="mt-2 flex items-end gap-1">
        <strong class="text-title-sm font-bold text-gray-800 tabular-nums dark:text-white/90">
          {{ value }}
        </strong>
        <span class="mb-1 text-sm font-medium text-gray-500 dark:text-gray-400">{{ unit }}</span>
      </div>
      <p class="mt-2 text-theme-xs text-gray-500 dark:text-gray-400 text-pretty">{{ detail }}</p>
    </div>
  </article>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { Activity, AlertTriangle, ClipboardList, Users } from 'lucide-vue-next'

type Tone = 'brand' | 'success' | 'warning' | 'error'

const props = withDefaults(
  defineProps<{
    label: string
    value: string
    unit?: string
    detail: string
    trend: string
    tone: Tone
  }>(),
  { unit: '' },
)

const icon = computed(() => {
  if (props.label.includes('复测')) return ClipboardList
  if (props.label.includes('异常')) return AlertTriangle
  if (props.label.includes('在管')) return Users
  return Activity
})

const toneClassMap: Record<Tone, { iconWrap: string; icon: string; badge: string }> = {
  brand: {
    iconWrap: 'bg-brand-50 dark:bg-brand-500/15',
    icon: 'text-brand-700 dark:text-brand-300',
    badge: 'bg-brand-50 text-brand-700 dark:bg-brand-500/15 dark:text-brand-300',
  },
  success: {
    iconWrap: 'bg-success-50 dark:bg-success-500/15',
    icon: 'text-success-700 dark:text-success-400',
    badge: 'bg-success-50 text-success-700 dark:bg-success-500/15 dark:text-success-400',
  },
  warning: {
    iconWrap: 'bg-warning-50 dark:bg-warning-500/15',
    icon: 'text-warning-700 dark:text-warning-400',
    badge: 'bg-warning-50 text-warning-700 dark:bg-warning-500/15 dark:text-warning-400',
  },
  error: {
    iconWrap: 'bg-error-50 dark:bg-error-500/15',
    icon: 'text-error-700 dark:text-error-400',
    badge: 'bg-error-50 text-error-700 dark:bg-error-500/15 dark:text-error-400',
  },
}
</script>
