<template>
  <div class="pb-24">
    <header class="sticky top-0 z-10 bg-[#f5f5f7]/80 backdrop-blur-md">
      <div class="flex items-center h-14 px-5">
        <h1 class="font-semibold text-[21px] text-ink tracking-tight">个人中心</h1>
      </div>
    </header>

    <!-- User Info -->
    <div class="px-5 pt-6">
      <div class="flex items-center gap-4">
        <div class="w-16 h-16 rounded-full bg-primary/15 flex items-center justify-center">
          <PhUser :size="28" class="text-primary" />
        </div>
        <div>
          <p class="text-xl font-semibold text-ink">{{ user.name }}</p>
        </div>
      </div>
    </div>

    <!-- Menu -->
    <section class="px-5 mt-8 space-y-1">
      <router-link to="/bind" class="glass-card flex items-center justify-between px-4 py-3.5 rounded-[18px] active:scale-[0.98] transition-transform">
        <div class="flex items-center gap-3">
          <PhHeart :size="20" class="text-primary" />
          <span class="text-sm font-medium text-ink">绑定管理</span>
        </div>
        <PhCaretRight :size="16" class="text-muted-soft" />
      </router-link>

      <router-link to="/appointments" class="glass-card flex items-center justify-between px-4 py-3.5 rounded-[18px] active:scale-[0.98] transition-transform">
        <div class="flex items-center gap-3">
          <PhCalendarCheck :size="20" class="text-primary" />
          <span class="text-sm font-medium text-ink">我的预约</span>
        </div>
        <PhCaretRight :size="16" class="text-muted-soft" />
      </router-link>
    </section>

    <!-- Danger Zone -->
    <section class="px-5 mt-8">
      <button
        @click="handleLogout"
        class="w-full flex items-center justify-center gap-2 px-4 py-3.5 rounded-full bg-error/5 text-error text-sm font-medium active:bg-error/10 transition-colors"
      >
        <PhSignOut :size="18" />
        退出登录
      </button>
    </section>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { PhUser, PhHeart, PhCalendarCheck, PhCaretRight, PhSignOut } from '@phosphor-icons/vue'

const router = useRouter()
const user = ref(JSON.parse(localStorage.getItem('user') || '{}'))

function handleLogout() {
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  router.replace('/login')
}
</script>
