<template>
  <div class="min-h-[100dvh] bg-canvas">
    <router-view v-slot="{ Component }">
      <transition name="page" mode="out-in">
        <component :is="Component" :key="$route.fullPath" />
      </transition>
    </router-view>

    <!-- Global loading overlay: outside transition, always fixed to viewport -->
    <LoadingSpinner v-if="pageLoading" />

    <!-- Persistent floating tab bar — hidden on guest pages (login/register) -->
    <TabBar v-if="!isGuestRoute" :tabs="mainTabs" />
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { pageLoading } from './loading.js'
import LoadingSpinner from './components/LoadingSpinner.vue'
import TabBar from './components/TabBar.vue'
import { mainTabs } from './tabs.js'

const route = useRoute()
const isGuestRoute = computed(() => route.meta.guest)
</script>

<style>
/* ── Spring-like page transition ── */
.page-enter-active {
  transition: transform 0.45s cubic-bezier(0.22, 0.61, 0.36, 1), opacity 0.35s ease;
}
.page-leave-active {
  transition: transform 0.3s cubic-bezier(0.4, 0, 0.6, 1), opacity 0.25s ease;
  position: absolute;  /* so enter & leave overlap for out-in */
  width: 100%;
}
.page-enter-from {
  opacity: 0;
  transform: translateX(28px);
}
.page-leave-to {
  opacity: 0;
  transform: translateX(-28px);
}
</style>
