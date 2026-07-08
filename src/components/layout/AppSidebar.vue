<template>
  <aside
    :class="[
      'fixed left-0 top-0 z-99999 mt-16 flex h-screen flex-col border-r border-gray-200 bg-white px-5 text-gray-900 transition-all duration-200 ease-out dark:border-gray-800 dark:bg-gray-900 lg:mt-0',
      {
        'lg:w-[290px]': isExpanded || isMobileOpen || isHovered,
        'lg:w-[90px]': !isExpanded && !isHovered,
        'w-[290px] translate-x-0': isMobileOpen,
        '-translate-x-full': !isMobileOpen,
        'lg:translate-x-0': true,
      },
    ]"
    @mouseenter="!isExpanded && (isHovered = true)"
    @mouseleave="isHovered = false"
  >
    <div
      :class="[
        'flex py-7',
        !isExpanded && !isHovered ? 'lg:justify-center' : 'justify-start',
      ]"
    >
      <router-link to="/" class="flex items-center gap-3">
        <span
          class="flex size-10 items-center justify-center rounded-xl bg-brand-600 text-lg font-bold text-white shadow-theme-xs"
        >
          养
        </span>
        <span v-if="isExpanded || isHovered || isMobileOpen" class="min-w-0">
          <span class="block text-lg font-semibold text-gray-900 dark:text-white">智慧养老</span>
          <span class="block text-theme-xs text-gray-500 dark:text-gray-400">Smart Elder Care</span>
        </span>
      </router-link>
    </div>

    <div class="flex flex-1 flex-col overflow-y-auto duration-300 ease-linear no-scrollbar">
      <nav class="mb-6">
        <div class="flex flex-col gap-6">
          <div v-for="(menuGroup, groupIndex) in menuGroups" :key="menuGroup.title">
            <h2
              :class="[
                'mb-4 flex text-xs uppercase leading-5 text-gray-400',
                !isExpanded && !isHovered ? 'lg:justify-center' : 'justify-start',
              ]"
            >
              <template v-if="isExpanded || isHovered || isMobileOpen">
                {{ menuGroup.title }}
              </template>
              <HorizontalDots v-else />
            </h2>

            <ul class="flex flex-col gap-2">
              <li v-for="item in menuGroup.items" :key="item.name">
                <router-link
                  :to="item.path"
                  :class="[
                    'menu-item group',
                    {
                      'menu-item-active': isActive(item.path),
                      'menu-item-inactive': !isActive(item.path),
                    },
                    !isExpanded && !isHovered ? 'lg:justify-center' : 'lg:justify-start',
                  ]"
                  :aria-label="item.name"
                >
                  <span
                    :class="[
                      isActive(item.path)
                        ? 'menu-item-icon-active'
                        : 'menu-item-icon-inactive',
                    ]"
                  >
                    <component :is="item.icon" />
                  </span>
                  <span v-if="isExpanded || isHovered || isMobileOpen" class="menu-item-text">
                    {{ item.name }}
                  </span>
                </router-link>
              </li>
            </ul>
          </div>
        </div>
      </nav>

      <SidebarWidget v-if="isExpanded || isHovered || isMobileOpen" />
    </div>
  </aside>
</template>

<script setup lang="ts">
import { useRoute } from 'vue-router'
import {
  BarChartIcon,
  BellIcon,
  BoxCubeIcon,
  CalenderIcon,
  HorizontalDots,
  LayoutDashboardIcon,
  ListIcon,
  SettingsIcon,
  UserGroupIcon,
} from '@/icons'
import SidebarWidget from './SidebarWidget.vue'
import { useSidebar } from '@/composables/useSidebar'

const route = useRoute()
const { isExpanded, isMobileOpen, isHovered } = useSidebar()

const menuGroups = [
  {
    title: '监护工作台',
    items: [
      {
        icon: LayoutDashboardIcon,
        name: '首页总览',
        path: '/',
      },
      {
        icon: UserGroupIcon,
        name: '老人档案',
        path: '/elderly',
      },
      {
        icon: BarChartIcon,
        name: '健康监测',
        path: '/health',
      },
      {
        icon: BellIcon,
        name: '告警中心',
        path: '/alerts',
      },
    ],
  },
  {
    title: '运营管理',
    items: [
      {
        icon: BoxCubeIcon,
        name: '设备管理',
        path: '/devices',
      },
      {
        icon: ListIcon,
        name: '护理记录',
        path: '/care-records',
      },
      {
        icon: CalenderIcon,
        name: '服务预约',
        path: '/services',
      },
      {
        icon: SettingsIcon,
        name: '系统设置',
        path: '/settings',
      },
    ],
  },
]

const isActive = (path: string) => {
  if (path === '/') {
    return route.path === '/'
  }

  return route.path.startsWith(path)
}
</script>
