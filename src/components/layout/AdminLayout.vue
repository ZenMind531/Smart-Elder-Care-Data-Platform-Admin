<template>
  <div class="min-h-screen xl:flex">
    <app-sidebar />
    <Backdrop />
    <div
      class="flex-1 transition-all duration-300 ease-in-out"
      :class="[isExpanded || isHovered ? 'lg:ml-[290px]' : 'lg:ml-[90px]']"
    >
      <app-header />
      <div class="p-4 mx-auto max-w-(--breakpoint-2xl) md:p-6">
        <p
          v-if="securityNotice"
          class="mb-4 rounded-lg border border-warning-200 bg-warning-50 px-4 py-3 text-theme-sm font-medium text-warning-700 shadow-theme-xs dark:border-warning-500/30 dark:bg-warning-500/10 dark:text-warning-300"
        >
          {{ securityNotice }}
        </p>
        <slot></slot>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, onUnmounted, ref } from 'vue'
import AppSidebar from './AppSidebar.vue'
import AppHeader from './AppHeader.vue'
import { useSidebar } from '@/composables/useSidebar'
import Backdrop from './Backdrop.vue'

const { isExpanded, isHovered } = useSidebar()
const securityNotice = ref('')
let noticeTimer: number | undefined

const showSecurityNotice = (message: string) => {
  securityNotice.value = message
  window.clearTimeout(noticeTimer)
  noticeTimer = window.setTimeout(() => {
    securityNotice.value = ''
  }, 5000)
}

const handleForbidden = (event: Event) => {
  const detail = (event as CustomEvent<{ message?: string }>).detail
  showSecurityNotice(detail?.message || '当前账号没有操作权限')
}

onMounted(() => {
  window.addEventListener('auth:forbidden', handleForbidden)
})

onUnmounted(() => {
  window.removeEventListener('auth:forbidden', handleForbidden)
  window.clearTimeout(noticeTimer)
})
</script>
