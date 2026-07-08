<template>
  <div class="relative" ref="dropdownRef">
    <button
      type="button"
      class="flex items-center text-gray-700 dark:text-gray-400"
      aria-label="打开用户菜单"
      :aria-expanded="dropdownOpen"
      @click.prevent="toggleDropdown"
    >
      <span class="mr-3 size-11 overflow-hidden rounded-full">
        <img src="/images/user/owner.jpg" alt="护理管理员头像" />
      </span>

      <span class="mr-1 hidden font-medium text-theme-sm sm:block">护理管理员</span>

      <ChevronDownIcon :class="{ 'rotate-180': dropdownOpen }" />
    </button>

    <div
      v-if="dropdownOpen"
      class="absolute right-0 mt-[17px] flex w-[260px] flex-col rounded-2xl border border-gray-200 bg-white p-3 shadow-theme-lg dark:border-gray-800 dark:bg-gray-dark"
    >
      <div>
        <span class="block font-medium text-gray-700 text-theme-sm dark:text-gray-300">
          刘晓兰
        </span>
        <span class="mt-0.5 block text-theme-xs text-gray-500 dark:text-gray-400">
          护理管理员 · A 区护理组
        </span>
      </div>

      <ul class="flex flex-col gap-1 border-b border-gray-200 pb-3 pt-4 dark:border-gray-800">
        <li v-for="item in menuItems" :key="item.href">
          <router-link
            :to="item.href"
            class="flex items-center gap-3 rounded-lg px-3 py-2 font-medium text-gray-700 group text-theme-sm hover:bg-gray-100 hover:text-gray-700 dark:text-gray-400 dark:hover:bg-white/5 dark:hover:text-gray-300"
            @click="closeDropdown"
          >
            <component
              :is="item.icon"
              class="text-gray-500 group-hover:text-gray-700 dark:group-hover:text-gray-300"
            />
            {{ item.text }}
          </router-link>
        </li>
      </ul>
      <router-link
        to="/signin"
        @click="signOut"
        class="mt-3 flex items-center gap-3 rounded-lg px-3 py-2 font-medium text-gray-700 group text-theme-sm hover:bg-gray-100 hover:text-gray-700 dark:text-gray-400 dark:hover:bg-white/5 dark:hover:text-gray-300"
      >
        <LogoutIcon class="text-gray-500 group-hover:text-gray-700 dark:group-hover:text-gray-300" />
        退出登录
      </router-link>
    </div>
  </div>
</template>

<script setup lang="ts">
import { InfoCircleIcon, LogoutIcon, SettingsIcon, UserCircleIcon, ChevronDownIcon } from '@/icons'
import { ref, onMounted, onUnmounted } from 'vue'

const dropdownOpen = ref(false)
const dropdownRef = ref<HTMLElement | null>(null)

const menuItems = [
  { href: '/profile', icon: UserCircleIcon, text: '个人中心' },
  { href: '/settings', icon: SettingsIcon, text: '系统设置' },
  { href: '/alerts', icon: InfoCircleIcon, text: '待处理告警' },
]

const toggleDropdown = () => {
  dropdownOpen.value = !dropdownOpen.value
}

const closeDropdown = () => {
  dropdownOpen.value = false
}

const signOut = () => {
  closeDropdown()
}

const handleClickOutside = (event: MouseEvent) => {
  if (dropdownRef.value && !dropdownRef.value.contains(event.target as Node)) {
    closeDropdown()
  }
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})
</script>
