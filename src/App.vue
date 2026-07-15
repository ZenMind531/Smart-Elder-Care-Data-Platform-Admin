<template>
  <div class="min-h-[100dvh] bg-canvas">
    <router-view v-slot="{ Component }">
      <transition name="page" mode="out-in">
        <component :is="Component" />
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
.page-enter-active,
.page-leave-active {
  transition: opacity 0.25s ease, transform 0.25s ease;
}
.page-enter-from {
  opacity: 0;
  transform: translateX(20px);
}
.page-leave-to {
  opacity: 0;
  transform: translateX(-20px);
}
</style>
