<template>
  <div class="relative" ref="dropdownRef">
    <button
      type="button"
      class="flex items-center text-gray-700 dark:text-gray-400"
      aria-label="打开用户菜单"
      :aria-expanded="dropdownOpen"
      @click.prevent="toggleDropdown"
    >
      <span
        class="mr-3 grid size-11 place-items-center overflow-hidden rounded-full text-base font-semibold text-white"
        :class="avatarBgClass"
      >
        <img
          v-if="avatarUrl"
          :src="avatarUrl"
          :alt="displayName + ' 头像'"
          class="size-full object-cover"
        />
        <span v-else>{{ avatarInitial }}</span>
      </span>

      <span class="mr-1 hidden flex-col items-start sm:flex">
        <span class="font-medium text-theme-sm text-gray-800 dark:text-white/90">
          {{ displayName }}
        </span>
        <span class="text-theme-xs text-gray-500 dark:text-gray-400">{{ roleName }}</span>
      </span>

      <ChevronDownIcon :class="{ 'rotate-180': dropdownOpen }" />
    </button>

    <div
      v-if="dropdownOpen"
      class="absolute right-0 mt-[17px] flex w-[260px] flex-col rounded-[18px] border border-gray-200 bg-white p-3 shadow-theme-lg dark:border-gray-800 dark:bg-gray-dark"
    >
      <div class="flex items-center gap-3">
        <span
          class="grid size-12 place-items-center overflow-hidden rounded-full text-lg font-semibold text-white"
          :class="avatarBgClass"
        >
          <img
            v-if="avatarUrl"
            :src="avatarUrl"
            :alt="displayName + ' 头像'"
            class="size-full object-cover"
          />
          <span v-else>{{ avatarInitial }}</span>
        </span>
        <div>
          <span class="block font-medium text-gray-700 text-theme-sm dark:text-gray-300">
            {{ displayName }}
          </span>
          <span class="mt-0.5 block text-theme-xs text-gray-500 dark:text-gray-400">
            {{ roleName }} · {{ roleScope }}
          </span>
        </div>
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
      <button
        type="button"
        class="mt-3 flex items-center gap-3 rounded-lg px-3 py-2 font-medium text-gray-700 group text-theme-sm hover:bg-gray-100 hover:text-gray-700 dark:text-gray-400 dark:hover:bg-white/5 dark:hover:text-gray-300"
        @click="signOut"
      >
        <LogoutIcon class="text-gray-500 group-hover:text-gray-700 dark:group-hover:text-gray-300" />
        退出登录
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { InfoCircleIcon, LogoutIcon, SettingsIcon, UserCircleIcon, ChevronDownIcon } from '@/icons'
import { logout } from '@/api/auth'
import { clearAuthSession, getStoredUser } from '@/api/http'
import { canAccessPath, getRoleOption, type StaffRole } from '@/config/roles'

const dropdownOpen = ref(false)
const dropdownRef = ref<HTMLElement | null>(null)
const currentUser = getStoredUser()
const displayName =
  currentUser?.realName?.trim() || currentUser?.username?.trim() || '工作人员'

const roleOption = getRoleOption(currentUser?.roleName)
const roleName = roleOption.name
const roleScope = roleOption.scope

// 头像：按用户名哈希映射到 public/images/user/ 下的随机一张
const AVATAR_FILES = [
  'user-01.jpg', 'user-02.jpg', 'user-03.jpg', 'user-04.jpg', 'user-05.jpg',
  'user-06.jpg', 'user-07.jpg', 'user-08.jpg', 'user-09.jpg', 'user-10.jpg',
  'user-11.jpg', 'user-12.jpg', 'user-13.jpg', 'user-14.jpg', 'user-15.jpg',
  'user-16.jpg', 'user-17.jpg', 'user-18.jpg', 'user-19.jpg', 'user-20.jpg',
  'user-21.jpg', 'user-22.jpg', 'user-23.jpg', 'user-24.jpg', 'user-25.jpg',
  'user-26.jpg', 'user-27.jpg', 'user-28.jpg', 'user-29.jpg', 'user-30.jpg',
  'user-31.jpg', 'user-32.jpg', 'user-33.jpg', 'user-34.jpg', 'user-35.jpg',
  'user-36.jpg', 'user-37.jpg',
]

const hashUsername = (name: string) => {
  let hash = 0
  for (let i = 0; i < name.length; i += 1) {
    hash = (hash * 31 + name.charCodeAt(i)) >>> 0
  }
  return hash
}

const avatarUrl = computed(() => {
  const seed = currentUser?.username || currentUser?.id?.toString() || 'default'
  const file = AVATAR_FILES[hashUsername(seed) % AVATAR_FILES.length]
  return `/images/user/${file}`
})

const avatarInitial = computed(() => displayName.slice(0, 1).toUpperCase())

const avatarBgClass = computed(() => {
  const tone: Record<StaffRole, string> = {
    系统管理员: 'bg-brand-600',
    护理管理员: 'bg-success-600',
    医生: 'bg-error-600',
  }
  return tone[roleOption.name]
})

const allMenuItems = [
  { href: '/profile', icon: UserCircleIcon, text: '个人中心' },
  { href: '/settings', icon: SettingsIcon, text: '系统设置' },
  { href: '/alerts', icon: InfoCircleIcon, text: '待处理告警' },
]

const menuItems = allMenuItems.filter((item) => canAccessPath(roleName, item.href))

const toggleDropdown = () => {
  dropdownOpen.value = !dropdownOpen.value
}

const closeDropdown = () => {
  dropdownOpen.value = false
}

const signOut = () => {
  closeDropdown()
  void logout().catch(() => undefined)
  clearAuthSession()
  window.location.href = '/signin'
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
